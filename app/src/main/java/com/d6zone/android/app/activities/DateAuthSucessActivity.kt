package com.d6zone.android.app.activities

import android.os.Bundle
import android.view.View
import com.d6zone.android.app.R
import com.d6zone.android.app.base.BaseActivity
import kotlinx.android.synthetic.main.activity_date_auth_success.*


/**
 * 约会认证情况
 */
class DateAuthSucessActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_auth_success)
        immersionBar
                .fitsSystemWindows(false)
                .titleBar(tv_back)
                .init()
        tv_back.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}