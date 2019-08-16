package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.models.SysMessage
import com.d6.android.app.utils.toDefaultTime
import kotlinx.android.synthetic.main.activity_message_detail.*

class MessageDetailActivity : TitleActivity() {

    private val systemMsg by lazy {
        intent.getSerializableExtra("data") as SysMessage
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_detail)
        immersionBar.init()
        title = "消息详情"

        tv_title.text = systemMsg.title
        tv_time.text = systemMsg.createTime?.toDefaultTime()
        tv_content.text = systemMsg.content

    }
}
