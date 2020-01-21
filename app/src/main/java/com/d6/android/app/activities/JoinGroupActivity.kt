package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_joingroup.*


/**
 * 会员页面
 */
class JoinGroupActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joingroup)
        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .init()

        if(intent.hasExtra("groupId")){
            tv_groupnumber.text = intent.getStringExtra("groupId")
        }

        iv_back_close.setOnClickListener {
            finish()
        }
        tv_wxshare.setOnClickListener {

        }

        tv_pengyougroupshare.setOnClickListener {

        }

        tv_save_local.setOnClickListener {

        }

    }

    override fun onResume() {
        super.onResume()
        getUserInfo()
    }

    private fun getUserInfo() {
        Request.getUserInfo("", getLocalUserId()).request(this, success = { _, data ->
             data?.let {

             }
        }) { _, _ ->
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}