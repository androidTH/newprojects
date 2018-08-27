package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import io.rong.imkit.RongIM
import io.rong.imkit.fragment.ConversationFragment
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import java.util.*


class ChatActivity : TitleActivity() {
    private val mTargetId by lazy {
        intent.data.getQueryParameter("targetId")
    }
    private val mTitle by lazy {
        intent.data.getQueryParameter("title")
    }
    private val mConversationType: Conversation.ConversationType by lazy {
        Conversation.ConversationType.valueOf(intent.data.lastPathSegment.toUpperCase(Locale.US))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val info = RongUserInfoManager.getInstance().getUserInfo(mTargetId)
        title = if (info == null || info.name.isNullOrEmpty()) {
            mTitle
        } else {
            info.name
        }

        enterActivity()

    }

    private fun enterActivity() {

        val token = SPUtils.instance().getString(Const.User.RONG_TOKEN)
        if (token == "default") {

        } else {
            if (RongIM.getInstance().currentConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                enterFragment(mConversationType, mTargetId)
            } else {
                reconnect(token)
            }
        }
    }

    private fun reconnect(token: String) {
        RongIM.connect(token, object : RongIMClient.ConnectCallback() {
            override fun onTokenIncorrect() {}

            override fun onSuccess(s: String) {
                enterFragment(mConversationType, mTargetId)
            }

            override fun onError(e: RongIMClient.ErrorCode) {
                enterFragment(mConversationType, mTargetId)
            }
        })
    }

    private var fragment: ConversationFragment? = null
    private fun enterFragment(mConversationType: Conversation.ConversationType, mTargetId: String) {
        if (isDestroy) {
            return
        }
        fragment = ConversationFragment()

        val uri = Uri.parse("rong://" + applicationInfo.packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId)
                .appendQueryParameter("title", mTitle)
                .build()

        fragment!!.uri = uri

        val transaction = supportFragmentManager.beginTransaction()
        //xxx 涓轰綘瑕佸姞杞界殑 id
        transaction.add(R.id.rong_content, fragment)
        transaction.commitAllowingStateLoss()
    }

}
