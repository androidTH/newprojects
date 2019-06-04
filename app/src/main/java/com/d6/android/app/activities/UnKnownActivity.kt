package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.d6.android.app.R
import com.d6.android.app.adapters.BlackListAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.BlackListBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import kotlinx.android.synthetic.main.activity_blacklist.*
import kotlinx.android.synthetic.main.activity_unknow.*
import org.jetbrains.anko.startActivity

/**
 * 匿名身份
 */
class UnKnownActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unknow)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.trans_parent).statusBarDarkFont(true).init()
        tv_unknow_back.setOnClickListener {
            finish()
        }

        tv_unknow_square.setOnClickListener {

        }

        tv_unknow_date.setOnClickListener {

        }

        tv_unknow_start.setOnClickListener {

        }

    }

}
