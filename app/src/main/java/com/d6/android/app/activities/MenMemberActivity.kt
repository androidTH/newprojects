package com.d6.android.app.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.ClipboardManager
import android.text.TextUtils
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.API.BASE_URL
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.share.utils.ShareUtils
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.activity_pointexplain_layout.*
import org.jetbrains.anko.toast

/**
 * 男性会员说明
 */
class MenMemberActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menmember_layout)
        immersionBar.init()
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
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if(request!=null){
                   var url = request.url.toString()
                   if(TextUtils.equals("d6://openwechat",url)){
//                       var intent = Intent()
//                       var cmp = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
//                       intent.setAction(Intent.ACTION_MAIN)
//                       intent.addCategory(Intent.CATEGORY_LAUNCHER)
//                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                       intent.setComponent(cmp)
//                       startActivity(intent)
                       var localUserId =getLocalUserId()
                       pushCustomerMessage(this@MenMemberActivity,localUserId,1,localUserId,next = {
                             chatService(this@MenMemberActivity)
                       })
                   }else{
                       val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                       cm.text = request.url.toString().split("d6://copywechat/")[1]
                       toast("微信号已复制到剪切板")
                   }
                   return true
                }
                return false;
            }

            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                dismissDialog()
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }
        })

        //http://47.105.50.76/JyPhone/static/member/index_new.html
        webView.loadUrl(BASE_URL+"static/member/index_new.html")
    }
}
