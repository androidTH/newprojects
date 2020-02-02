package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.d6.android.app.R
import com.d6.android.app.adapters.GroupUsersListAdapter
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.models.GroupUserBean
import com.d6.android.app.models.NewGroupBean
import com.d6.android.app.net.Request
import com.d6.android.app.rong.RongD6Utils
import com.d6.android.app.utils.*
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_groupsetting.*
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

            tv_groupname.text = "${mGroupBean.sGroupName}"
            tv_groupnum.text = "${mGroupBean.iGroupNum}"
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

        btn_group_leave.setOnClickListener {
            quiteGroup()
        }

        getUserInfo()
        mGroupUserListAdapter.setOnItemClickListener { _, position ->
            val id = mGroupUserList[position].iUserid
            startActivity<UserInfoActivity>("id" to "${id}")
        }

        getGroupUsersData()
    }

    private fun getGroupUsersData() {
        Request.getGroupMemberListByGroupId("${mGroupBean.sId}", pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mGroupUserList.clear()
                tv_groupcount.text = "群成员(${data?.list?.totalRecord})"
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {

            } else {
                data.list.results?.let { mGroupUserList.addAll(it) }
            }
            mGroupUserListAdapter.notifyDataSetChanged()
        }

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
