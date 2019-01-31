package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.net.API
import com.d6.android.app.utils.getD6VersionName
import kotlinx.android.synthetic.main.activity_about_us_main.*
import org.jetbrains.anko.startActivity

/**
 * 关于我们
 */
class AboutUsMainActivity : TitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us_main)
        immersionBar.init()
        title = "关于我们"

        tv_aboutUs.setOnClickListener {
            val about_us = API.STATIC_BASE_URL+"guanyuwomen?header=0"
            startActivity<WebViewActivity>("title" to "关于我们","url" to about_us)
        }

        tv_platform_service.setOnClickListener {
            val service = API.STATIC_BASE_URL+"pingtaifuwu?header=0"
            startActivity<WebViewActivity>("title" to "平台服务","url" to service)
        }

        tv_business.setOnClickListener {
            val business = API.STATIC_BASE_URL+"shangwuhezuo?header=0"
            startActivity<WebViewActivity>("title" to "商务合作","url" to business)
        }

        tv_versionname.text = getD6VersionName(this)
    }
}
