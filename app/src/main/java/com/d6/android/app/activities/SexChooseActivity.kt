package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import kotlinx.android.synthetic.main.activity_sex_layout.*

class SexChooseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sex_layout)
        immersionBar.fitsSystemWindows(true).init()
        tv_man.setOnClickListener {
            var intent = Intent()
            intent.putExtra("sex","1")
            setResult(Activity.RESULT_OK,intent)
            onBackPressed()
        }

        tv_woman.setOnClickListener {
            var intent = Intent()
            intent.putExtra("sex","0")
            setResult(Activity.RESULT_OK,intent)
            onBackPressed()
        }

        tv_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.dd_menu_out);
    }
}
