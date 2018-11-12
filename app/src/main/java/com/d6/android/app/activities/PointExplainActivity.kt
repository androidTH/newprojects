package com.d6.android.app.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.activity_pointexplain_layout.*

/**
 * 积分说明
 */
class PointExplainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pointexplain_layout)
        tv_back.setOnClickListener {
            mKeyboardKt.hideKeyboard(it)
            finish()
        }
//        getData();
        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.defaultTextEncodingName = "utf-8"
        settings.domStorageEnabled = true
        webView.setWebChromeClient(object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String?) {
                super.onReceivedTitle(view, title)
            }
        })
        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                dismissDialog()
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
//                dialog()
            }
        })

        webView.loadUrl("http://www.d6-zone.com/JyPhone/static/integralexplain.html")
    }

    private fun getData() {

        Request.getInfo(Const.SCORE_EXPLAIN_CODE).request(this) { _, data ->
            data?.let {
                var str = data.optString("description")
//                tv_title0.text = str
            }
        }
    }

//    "ids": "45",
//    "piecesMark": "integral_explain",
//    "title": "积分说明",
//    "keywork": "积分说明",
//    "description": "",
//    "picUrl": "",
//    "sortId": "",
//    "userid": "2",
//    "content": null,
//    "createTime": 1541846231287,
//    "beginTime": null,
//    "endTime": null,
//    "ext1": "",
//    "ext2": "",
//    "ext3": "",
//    "ext4": "",
//    "ext5": "",
//    "ext6": "",
//    "ext7": "",
//    "ext8": ""
}
