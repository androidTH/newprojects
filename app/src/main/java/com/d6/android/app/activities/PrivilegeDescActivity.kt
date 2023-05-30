package com.d6.android.app.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.ClipboardManager
import android.text.TextUtils
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_privilegedesc_layout.*
import org.jetbrains.anko.toast



/**
 * 积分说明
 */
class PrivilegeDescActivity : BaseActivity() {

    private val sex by lazy {
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private var wechatnumber =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privilegedesc_layout)
        tv_back.setOnClickListener {
            mKeyboardKt.hideKeyboard(it)
            finish()
        }

        tv_copy_wechat.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.text = wechatnumber
            toast("已复制客服微信号")
        }

        tv_wechat_service.setOnClickListener {
            getWechatApi()
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.text = wechatnumber
        }

        getData()
        initView()
    }

    private fun getWechatApi() {
        try {
            val intent = Intent()
            val cmp = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setComponent(cmp)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // TODO: handle exception
            showToast("检查到您手机没有安装微信，请安装后使用该功能")
        }

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
        Request.getInfo(Const.SERVICE_WECHAT_CODE).request(this) { _, data ->
            data?.let {
                val womanWeChat = data.optString("ext5")
                val manWeChat = data.optString("ext6")
                if(TextUtils.equals(sex,"0")){
                    wechatnumber = womanWeChat
                }else{
                    wechatnumber = manWeChat
                }
                tv_wechat_number.text = "客服微信号：${wechatnumber}"
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
