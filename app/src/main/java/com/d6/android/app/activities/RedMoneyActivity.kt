package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_redmoney.*

class RedMoneyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redmoney)
        immersionBar.init()

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        tv_redmoney_tips.text = "红包总额不能低于1000 [img src=taren_gray_icon/]，10个 [img src=taren_gray_icon/] =1元"

        btn_create_redmoney.setOnClickListener {

        }
    }

    override fun onResume() {
        super.onResume()
    }
}
