package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.GroupUsersListAdapter
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.ApplayJoinGroupDialog
import com.d6.android.app.dialogs.JoinGroupDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.FindGroupBean
import com.d6.android.app.models.GroupUserBean
import com.d6.android.app.models.NewGroupBean
import com.d6.android.app.net.Request
import com.d6.android.app.rong.RongD6Utils
import com.d6.android.app.utils.*
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_groupjoin.*
import kotlinx.android.synthetic.main.activity_groupjoin.tv_groupname
import kotlinx.android.synthetic.main.activity_joingroup.*
import me.nereo.multi_image_selector.utils.FinishActivityManager
import org.jetbrains.anko.imageURI
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

class GroupJoinActivity : TitleActivity() {

    private var pageNum = 1
    private var mGroupUserList = ArrayList<GroupUserBean>()

    private val mGroupUserListAdapter by lazy {
        GroupUsersListAdapter(mGroupUserList)
    }

    private lateinit var mGroupBean:NewGroupBean
    private lateinit var mFindGroupBean: FindGroupBean
    fun IsNotNullGroupBean()=::mGroupBean.isInitialized

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupjoin)
        immersionBar.init()
        if(intent.hasExtra("bean")){
            mFindGroupBean = intent.getParcelableExtra("bean")
            setTitleBold(mFindGroupBean.sGroupName)
            tv_groupnum.text = "${mFindGroupBean.iGroupNum}"
//            if(mGroupBean.iIsOwner==1){
//                btn_group_leave.text = "解散群聊"
//                tv_groupmore.visibility = View.VISIBLE
//            }else if(mGroupBean.iIsManager==1||mGroupBean.iIsManager==2){
//                btn_group_leave.text = "退出群聊"
//                tv_groupmore.visibility = View.INVISIBLE
//            }else{
//                btn_group_leave.text = "解散群聊"
//            }

            btn_group_leave.text = "申请入群"

            if(IsNotNullGroupBean()){
                tv_groupname.text = "${mFindGroupBean.sGroupName}"
                group_headView.setImageURI("${mFindGroupBean.sGroupPic}")
            }
        }
        rv_grouplist.setHasFixedSize(true)
        rv_grouplist.isNestedScrollingEnabled = true
        rv_grouplist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_grouplist.adapter = mGroupUserListAdapter

        ll_group.setOnClickListener {
            startActivity<GroupUsersActivity>("bean" to mGroupBean)
        }

        btn_group_leave.setOnClickListener {
            if (IsNotNullGroupBean()) {
                var mApplayJoinGroupDialog = ApplayJoinGroupDialog()
//                mApplayJoinGroupDialog.arguments = bundleOf("groupId" to "${mGroupId}")
                mApplayJoinGroupDialog.setDialogListener { p, s ->
                    if(p==2){
                        s?.let {
                            applayToGroup("${s}")
                        }
                    }
                }
                mApplayJoinGroupDialog.show(supportFragmentManager,"joingroup")
            }
        }

        rl_member_center.setOnClickListener {
            if(IsNotNullGroupBean()){
                startActivity<ShareGroupActivity>("groupBean" to mGroupBean)
            }
        }

        getUserInfo()
        mGroupUserListAdapter.setOnItemClickListener { _, position ->
            val id = mGroupUserList[position].iUserid
            startActivity<UserInfoActivity>("id" to "${id}")
        }
    }

    override fun onResume() {
        super.onResume()
        getGroupUsersData()
    }

    private fun getGroupUsersData() {
        Request.getGroupMemberListByGroupId("${mFindGroupBean.sId}","",pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mGroupUserList.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {

            } else {
                data.list.results?.let { mGroupUserList.addAll(it) }
            }
            mGroupUserListAdapter.notifyDataSetChanged()
        }

        Request.getGroupByGroupId("${mFindGroupBean.sId}").request(this,false,success={msg,data->
            data?.let {
                mGroupBean = it
                tv_groupcount.text = "群成员(${mGroupBean.iMemberCount})"
                tv_groupname.text = "${mGroupBean.sGroupName}"
                group_headView.setImageURI("${mGroupBean.sGroupPic}")
//                if(mGroupBean.iIsManager==1){
//                    tv_groupname.text = "${mGroupBean.sGroupName}(可修改)"
//                }else{
//                    tv_groupname.text = "${mGroupBean.sGroupName}"
//                }
//                if(it.iIsOwner==1){
//                    btn_group_leave.text = "解散群聊"
//                }else if(it.iIsManager==1){
//                    btn_group_leave.text = "退出群聊"
//                }
            }
        })

    }

    fun applayToGroup(content:String){
        Request.applyToGroup("${mFindGroupBean.sId}","${content}").request(this,false,success={msg,data->
            toast("${msg}")
        }){code,msg->
            toast(msg)
        }
    }


    private fun getUserInfo() {
        Request.getUserInfo("", getLocalUserId()).request(this, success = { _, data ->
            SPUtils.instance().put(Const.USERINFO, GsonHelper.getGson().toJson(data)).apply()
            saveUserInfo(data)
            data?.let {
            }
        })
    }
}
