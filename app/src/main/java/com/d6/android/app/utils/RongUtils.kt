package com.d6.android.app.utils

import android.widget.ImageView
import android.widget.TextView

import com.d6.android.app.R

object RongUtils {

    private var userProvider: UserProvider? = null

    fun setUserProvider(userProvider: UserProvider) {
        RongUtils.userProvider = userProvider
    }

    interface UserProvider {
        fun getUser(userId: String, textView: TextView?, imageView: ImageView?)
    }

    fun getUser(username: String, textView: TextView?, imageView: ImageView?) {
        if (userProvider != null)
            userProvider!!.getUser(username, textView, imageView)
    }


    fun setUserInfo(userName: String, textView: TextView?, imageView: ImageView?) {
        textView?.setTag(R.id.view_tag, userName)
        imageView?.setTag(R.id.view_tag, userName)
        getUser(userName, textView, imageView)
    }
}
