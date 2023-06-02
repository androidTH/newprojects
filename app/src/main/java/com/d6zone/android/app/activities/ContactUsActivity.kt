package com.d6zone.android.app.activities

import android.content.Context
import android.os.Bundle
import android.text.ClipboardManager
import com.d6zone.android.app.R
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.SpanBuilder
import com.d6zone.android.app.utils.optString
import kotlinx.android.synthetic.main.activity_contact_us.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * 联系我们
 */
class ContactUsActivity : TitleActivity() {
    private var manWeChat="000000"
    private var womanWeChat="000000"
    private var p:Float = 1.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        immersionBar.init()
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
//            p = p+1.0f
//            var angle = (360/100)*p
//            circlebarview.setProgressNum(angle,0)
        }

        tv_man_tip.text = SpanBuilder(String.format("男生专属客服\n点击复制微信号:%s","000000"))
                .bold(0,6)
                .build()

        tv_woman_tip.text = SpanBuilder(String.format("女生专属客服\n点击复制微信号:%s","000000"))
                .bold(0,6)
                .build()
        dialog()
        getData()

        circlebarview.setMaxNum(360.0f)
    }

    private fun getData() {
        Request.getInfo(Const.SERVICE_WECHAT_CODE).request(this) { _, data ->
            data?.let {
                val womanWeChat = data.optString("ext1")
                val manWeChat = data.optString("ext2")
                tv_man_tip.text = SpanBuilder(String.format("男生专属客服\n点击复制微信号:%s", manWeChat))
                        .bold(0, 6)
                        .build()
                tv_woman_tip.text = SpanBuilder(String.format("女生专属客服\n点击复制微信号:%s", womanWeChat))
                        .bold(0, 6)
                        .build()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
