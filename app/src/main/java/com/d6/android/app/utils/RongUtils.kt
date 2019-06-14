package com.d6.android.app.utils

import android.widget.ImageView
import android.widget.TextView

import com.d6.android.app.R

object RongUtils {

    private var userProvider: UserProvider? = null
    private var groupProvider: GroupProver? = null

    fun setUserProvider(userProvider: UserProvider) {
        RongUtils.userProvider = userProvider
    }

    fun setGroupProvider(groupProver: GroupProver){
        RongUtils.groupProvider = groupProvider
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

}
