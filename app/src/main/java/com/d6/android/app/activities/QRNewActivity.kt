package com.d6.android.app.activities

import android.content.Context
import android.os.Bundle
import android.text.ClipboardManager
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.SpanBuilder
import com.d6.android.app.utils.optString
import kotlinx.android.synthetic.main.activity_qr_new.*
import org.jetbrains.anko.toast

class QRNewActivity : TitleActivity() {

    private var weChat = "000000"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_new)
        btn_weChat.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = weChat
            toast("微信号已复制到剪切板")
        }

        tv_tip.text = SpanBuilder(String.format("微信公众号\n点击复制微信号:%s", "000000"))
                .bold(0, 5)
                .build()

        dialog()
        getData()
    }

    private fun getData() {
        val mark = "qrcode-weixin"

        Request.getInfo(mark).request(this) { _, data ->
            data?.let {
                val ext1 = data.optString("ext1")
                weChat = ext1
                tv_tip.text = SpanBuilder(String.format("微信公众号\n点击复制微信号:%s", ext1))
                        .bold(0, 5)
                        .build()
            }
        }
    }
}