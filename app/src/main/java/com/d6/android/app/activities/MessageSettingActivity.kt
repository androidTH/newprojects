package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import kotlinx.android.synthetic.main.activity_message_setting.*

class MessageSettingActivity : TitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_setting)
        immersionBar.init()
        title = "设置"
        val manager = NotificationManagerCompat.from(this)
        val isOpened = manager.areNotificationsEnabled()
        tv_state.text = if (isOpened) "已开启" else "已关闭"
    }
}
