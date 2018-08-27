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
import kotlinx.android.synthetic.main.activity_contact_us.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * 联系我们
 */
class ContactUsActivity : TitleActivity() {
    private var manWeChat="000000"
    private var womanWeChat="000000"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        title = "联系我们"

        btn_man.setOnClickListener {
//            startActivity<QRActivity>("type" to 0)
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = manWeChat
            toast("微信号已复制到剪切板")
        }
        btn_woman.setOnClickListener {
//            startActivity<QRActivity>("type" to 1)
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = womanWeChat
            toast("微信号已复制到剪切板")
        }
        btn_weChat.setOnClickListener {
            startActivity<QRNewActivity>("type" to 2)
        }

        tv_man_tip.text = SpanBuilder(String.format("男生专属客服\n点击复制微信号:%s","000000"))
                .bold(0,6)
                .build()

        tv_woman_tip.text = SpanBuilder(String.format("女生专属客服\n点击复制微信号:%s","000000"))
                .bold(0,6)
                .build()
        dialog()
        getData(0)
        getData(1)
    }

    private fun getData(type:Int) {
        val mark = if (type == 0) {
            "qrcode-boy"
        } else if (type == 1) {
            "qrcode-girl"
        } else {
            "qrcode-weixin"
        }
        Request.getInfo(mark).request(this) { _, data ->
            data?.let {
                val ext1 = data.optString("ext1")
                if (type == 0) {
                    manWeChat = ext1
                    tv_man_tip.text = SpanBuilder(String.format("男生专属客服\n点击复制微信号:%s",manWeChat))
                            .bold(0,6)
                            .build()
                }else if (type == 1) {
                    womanWeChat = ext1
                    tv_woman_tip.text = SpanBuilder(String.format("女生专属客服\n点击复制微信号:%s",womanWeChat))
                            .bold(0,6)
                            .build()
                }
            }
        }
    }

}
