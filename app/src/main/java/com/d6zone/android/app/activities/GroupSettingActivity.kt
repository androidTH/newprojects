package com.d6zone.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.GroupUsersListAdapter
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.GroupUserBean
import com.d6zone.android.app.models.NewGroupBean
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.rong.RongD6Utils
import com.d6zone.android.app.utils.*
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_groupsetting.*
import me.nereo.multi_image_selector.utils.FinishActivityManager
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class GroupSettingActivity : TitleActivity() {

    private var pageNum = 1
    private var mGroupUserList = ArrayList<GroupUserBean>()

    private val mGroupUserListAdapter by lazy {
        GroupUsersListAdapter(mGroupUserList)
    }

    private lateinit var mGroupBean:NewGroupBean
    fun IsNotNullGroupBean()=::mGroupBean.isInitialized

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupsetting)
        immersionBar.init()
        setTitleBold("群设置")
        if(intent.hasExtra("bean")){
            mGroupBean = intent.getParcelableExtra("bean")

            tv_groupnum.text = "${mGroupBean.iGroupNum}"
            if(mGroupBean.iIsOwner==1){
                btn_group_leave.text = "解散群聊"
                tv_groupmore.visibility = View.VISIBLE
            }else if(mGroupBean.iIsManager==1||mGroupBean.iIsManager==2){
                btn_group_leave.text = "退出群聊"
                tv_groupmore.visibility = View.INVISIBLE
            }else{
                btn_group_leave.text = "解散群聊"
            }

            if(IsNotNullGroupBean()){
                if(mGroupBean.iIsManager==1){
                    tv_groupname.text = "${mGroupBean.sGroupName}(可修改)"
                }else{
                    tv_groupname.text = "${mGroupBean.sGroupName}"
                }
            }
        }
        rv_grouplist.setHasFixedSize(true)
        rv_grouplist.isNestedScrollingEnabled = true
        rv_grouplist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_grouplist.adapter = mGroupUserListAdapter

        sw_groupmessage_notfaction.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setNotifi(2)
            } else {
                setNotifi(1)
            }
        }

        ll_group.setOnClickListener {
            startActivity<GroupUsersActivity>("bean" to mGroupBean)
        }

        rl_groupname.setOnClickListener {
            if(IsNotNullGroupBean()){
                if(mGroupBean.iIsManager==1||mGroupBean.iIsOwner==1){
                    startActivity<CreateGroupActivity>("bean" to mGroupBean)
                }
            }
        }

        btn_group_leave.setOnClickListener {
            if (IsNotNullGroupBean()) {
                if (mGroupBean.iIsOwner == 1) {
                    dissGroup()
                } else {
                    quiteGroup()
                }
            }
        }

        rl_member_center.setOnClickListener {
            if(IsNotNullGroupBean()){
                startActivity<ShareGroupActivity>("groupBean" to mGroupBean)
//                if(mGroupBean.iIsOwner==1||mGroupBean.iIsManager==1){
//
//                }
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
        Request.getGroupMemberListByGroupId("${mGroupBean.sId}","",pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mGroupUserList.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {

            } else {
                data.list.results?.let { mGroupUserList.addAll(it) }
            }
            mGroupUserListAdapter.notifyDataSetChanged()
        }

        Request.getGroupByGroupId("${mGroupBean.sId}").request(this,false,success={msg,data->
            data?.let {
                mGroupBean = it
                tv_groupcount.text = "群成员(${mGroupBean.iMemberCount})"
                if(mGroupBean.iIsManager==1){
                    tv_groupname.text = "${mGroupBean.sGroupName}(可修改)"
                }else{
                    tv_groupname.text = "${mGroupBean.sGroupName}"
                }
//                if(it.iIsOwner==1){
//                    btn_group_leave.text = "解散群聊"
//                }else if(it.iIsManager==1){
//                    btn_group_leave.text = "退出群聊"
//                }
            }
        })

       if(RongIM.getInstance() != null){

           RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, "${mGroupBean.sId}", object : RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
               override fun onSuccess(conversationNotificationStatus: Conversation.ConversationNotificationStatus) {
                   if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                       sw_groupmessage_notfaction.isChecked = true
                   } else {
                       sw_groupmessage_notfaction.isChecked = false
                   }
               }

               override fun onError(errorCode: RongIMClient.ErrorCode) {

               }
           })
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

    private fun dissGroup(){
        if(IsNotNullGroupBean()){
            dialog()
            FinishActivityManager.getManager().finishActivity(ChatActivity::class.java)
            Request.dissGroup("${mGroupBean.sId}").request(this,false,success={msg,data->
                finish()
                toast("解散成功")
            }){code,msg->
                toast("${msg}")
            }
        }
    }

    private fun quiteGroup(){
        if(IsNotNullGroupBean()){
            dialog()
            FinishActivityManager.getManager().finishActivity(ChatActivity::class.java)
            Request.quiteGroup("${mGroupBean.sId}").request(this,false,success={msg,data->
                finish()
                toast("退出成功")
            }){code,msg->
                toast("${msg}")
            }
        }
    }

    //iIsNotification状态 1、提醒 2、不提醒
    private fun setNotifi(iIsNotification:Int){
        if(IsNotNullGroupBean()){
            Request.updateMemberNotification("${mGroupBean.sId}",iIsNotification).request(this,false,success = {msg,data->
                if (iIsNotification==2) {
                    RongD6Utils.setConverstionNotif(this, Conversation.ConversationType.GROUP, "${mGroupBean.sId}", true)
                } else {
                    RongD6Utils.setConverstionNotif(this, Conversation.ConversationType.GROUP, "${mGroupBean.sId}", false)
                }
            }){code,msg->
                toast(msg)
            }
        }
    }
}
