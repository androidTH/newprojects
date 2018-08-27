package com.d6.android.app.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.optString
import kotlinx.android.synthetic.main.activity_qr.*

class QRActivity : TitleActivity() {
    private val type by lazy {
        intent.getIntExtra("type", 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        title = "扫描二维码"

        tv_tip.text = if (type == 0) {
            "男生添加微信"
        } else if (type == 1) {
            "女生添加微信"
        } else {
            "平台二维码"
        }

        if (type == 0) {
//            tv_tip.setTextColor(ContextCompat.getColor(this,R.color.color_369))
            arrow.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_big_arrow,0,0)
        } else if (type == 1) {
//            tv_tip.setTextColor(Color.parseColor("#fd6899"))
            arrow.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_big_arrow_female,0,0)
        } else {
//            tv_tip.setTextColor(ContextCompat.getColor(this,R.color.orange_f6a))
            arrow.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_big_arrow_wechat,0,0)
        }

        dialog()
        getData()
    }

    private fun getData() {
        val mark = if (type == 0) {
            "qrcode-boy"
        } else if (type == 1) {
            "qrcode-girl"
        } else {
            "qrcode-weixin"
        }
        Request.getInfo(mark).request(this) { _, data ->
            data?.let {
                val url = data.optString("picUrl")
                imageView.setImageURI(url)
            }
        }
    }
}
