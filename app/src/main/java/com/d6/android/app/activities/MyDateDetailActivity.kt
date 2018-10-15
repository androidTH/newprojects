package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.models.UserData
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_mydate_details.*

class MyDateDetailActivity : BaseActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mydate_details)
        titlebar_datedetails.titleView.setText("我的约会")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
        }
    }
}
