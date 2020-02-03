package com.d6.android.app.utils

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView

import com.d6.android.app.R
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation

object RongUtils {

    private var userProvider: UserProvider? = null
    private var groupProvider: GroupProver? = null

    fun setUserProvider(userProvider: UserProvider) {
        RongUtils.userProvider = userProvider
    }

    fun setGroupProvider(groupProver: GroupProver){
        RongUtils.groupProvider = groupProver
    }

    interface UserProvider {
        fun getUser(userId: String, textView: TextView?, imageView: ImageView?)
    }

    interface  GroupProver{
        fun getGroupInfo(groupId:String,textView: TextView?, imageView: ImageView?)
    }

    fun getUser(username: String, textView: TextView?, imageView: ImageView?) {
        if (userProvider != null)
            userProvider!!.getUser(username, textView, imageView)
    }

    fun getGroup(groupId: String, textView: TextView?, imageView: ImageView?) {
        if (groupProvider != null)
            groupProvider!!.getGroupInfo(groupId, textView, imageView)
    }

    fun setUserInfo(userName: String, textView: TextView?, imageView: ImageView?) {
        textView?.setTag(R.id.view_tag, userName)
        imageView?.setTag(R.id.view_tag, userName)
        getUser(userName, textView, imageView)
    }

    fun setGroupInfo(groupId: String, textView: TextView?, imageView: ImageView?) {
        textView?.setTag(R.id.view_tag, groupId)
        imageView?.setTag(R.id.view_tag, groupId)
        getGroup(groupId, textView, imageView)
    }

    //会话列表置顶
    fun setConversationTop(context: Context, conversationType: Conversation.ConversationType, targetId: String, state: Boolean) {
        if (!TextUtils.isEmpty(targetId) && RongIM.getInstance() != null) {
            RongIM.getInstance().setConversationToTop(conversationType, targetId, state, object : RongIMClient.ResultCallback<Boolean>() {
                override fun onSuccess(aBoolean: Boolean?) {

                }

                override fun onError(errorCode: RongIMClient.ErrorCode) {
                    NToast.shortToast(context,"设置失败")
                }
            })
        }
    }

    fun getConnectCallback(): RongIMClient.ConnectCallback {
        return object : RongIMClient.ConnectCallback() {
            override fun onTokenIncorrect() {
            }

            override fun onSuccess(s: String) {

            }

            override fun onError(e: RongIMClient.ErrorCode) {
            }
        }
    }
}
