package com.d6zone.android.app.activities

import android.content.Intent
import android.os.Bundle
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.SPUtils
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.push.RongPushClient
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.activity_about_us_main.*

/**
 * Created by Bob on 15/11/3.
 * 会话列表，需要做 2 件事
 * 1，push 重连，收到 push 消息的时候，做一下 connect 操作
 */
class ConversationListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        //push
        if (intent.data != null && intent.data!!.scheme != null
                && intent.data!!.scheme == "rong" && intent.data!!.getQueryParameter("isFromPush") != null
                && intent.data!!.getQueryParameter("isFromPush") == "true") {
            // 统计华为push点击事件
            RongPushClient.recordHWNotificationEvent(intent)
            enterActivity()
        } else {//通知过来
            //程序切到后台，收到消息后点击进入,会执行这里
            if (RongIM.getInstance().currentConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT
                    ||RongIM.getInstance().currentConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONN_USER_BLOCKED) {
                enterActivity()
            } else {
                startActivity(Intent(this@ConversationListActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 MainActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     *
     *
     * 作用：由于在 manifest 中 intent-filter 是配置在 ConversationListActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 MainActivity 为例：
     * 在 ConversationListActivity 收到消息后，选择进入 MainActivity，这样就把 MainActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * MainActivity 页面，而不是直接退回到 桌面。
     */
    private fun enterActivity() {
        val token = SPUtils.instance().getString(Const.User.RONG_TOKEN)
        if (token == "default") {
            startActivity(Intent(this@ConversationListActivity, SignInActivity::class.java))
            finish()
        } else {
            dialog()
            reconnect(token)
        }
    }


    private fun reconnect(token: String) {
        RongIM.connect(token, object : RongIMClient.ConnectCallback() {
            override fun onDatabaseOpened(p0: RongIMClient.DatabaseOpenStatus?) {
            }
            override fun onSuccess(s: String) {
                dismissDialog()
                startActivity(Intent(this@ConversationListActivity, MainActivity::class.java))
                finish()
            }

            override fun onError(p0: RongIMClient.ConnectionErrorCode?) {
            }
        })

    }

}
