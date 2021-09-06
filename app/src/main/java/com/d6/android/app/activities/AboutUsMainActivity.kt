package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.PRIVACY_POLICY
import com.d6.android.app.utils.Const.USER_AGREEMENT
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.diyUpdate
import com.d6.android.app.utils.optString
import com.vector.update_app.utils.AppUpdateUtils
import kotlinx.android.synthetic.main.activity_about_us_main.*
import org.jetbrains.anko.startActivity

/**
 * 关于我们
 */
class AboutUsMainActivity : TitleActivity() {

    private var TAG:String? = AboutUsMainActivity::class.java.simpleName

    private var private_url = "http://www.d6-zone.com/JyPhone/static/privacy/index.html"
    private var user_agreement = "file:///android_asset/yonghuxieyi.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us_main)
        immersionBar.init()
        title = "关于我们"

        tv_privacy.setOnClickListener {
            val url = "http://www.d6-zone.com/JyPhone/static/privacy/index.html"
            startActivity<WebViewActivity>("title" to "隐私政策", "url" to private_url)
        }

        tv_userxieyi.setOnClickListener {
            val url = "file:///android_asset/yonghuxieyi.html"
            startActivity<WebViewActivity>("title" to "用户协议", "url" to user_agreement)
        }

        tv_aboutUs.setOnClickListener {
            val about_us = API.STATIC_BASE_URL + "us"
            startActivity<WebViewActivity>("title" to "关于我们", "url" to about_us)
        }

        tv_platform_service.setOnClickListener {
            val service = API.STATIC_BASE_URL + "paas"
            startActivity<WebViewActivity>("title" to "平台服务", "url" to service)
        }

//        tv_business.setOnClickListener {
//            val business = API.STATIC_BASE_URL + "business"
//            startActivity<WebViewActivity>("title" to "商务合作", "url" to business)
//        }

        rl_checkversion.setOnClickListener {
            diyUpdate(this, TAG)
//               checkVersion()
        }

        getPrivicy()

    }


    private fun getPrivicy(){
        Request.getInfo(PRIVACY_POLICY).request(this, success = { _, data ->
            data?.let {
                private_url = data.optString("privacy_policy")

            }
        }){ code, msg->
            SPUtils.instance().put(Const.User.ISNOTFREECHATTAG, false).apply()
        }

        Request.getInfo(USER_AGREEMENT).request(this, success = { _, data ->
            data?.let {
                user_agreement = data.optString("user agreement")

            }
        }){ code, msg->
            SPUtils.instance().put(Const.User.ISNOTFREECHATTAG, false).apply()
        }
    }

    private fun checkVersion() {
        Request.getByVersion(AppUpdateUtils.getVersionName(this), "2").request(this, false, success = { msg, data ->
            data?.let {
                diyUpdate(this, TAG)
            }
        }) { code, msg ->
            showToast("已是最新版本")
        }
    }

    //    private var updateurl ="https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt"
}
