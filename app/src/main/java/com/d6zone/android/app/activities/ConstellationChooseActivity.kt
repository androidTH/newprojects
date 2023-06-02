package com.d6zone.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.*
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.XinZuoQuickDateAdapter
import com.d6zone.android.app.base.BaseActivity
import kotlinx.android.synthetic.main.activity_costellation_layout.*

class ConstellationChooseActivity : BaseActivity() {

    private val constellations = listOf<String>("白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座")

    private val mAdapter by lazy {
        XinZuoQuickDateAdapter(constellations)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_costellation_layout)
        immersionBar.fitsSystemWindows(true).init()
        tv_back.setOnClickListener {
            onBackPressed()
        }

        rv_choose_constelation.setHasFixedSize(true)
        rv_choose_constelation.layoutManager = LinearLayoutManager(this, VERTICAL,false)
        rv_choose_constelation.adapter = mAdapter

        mAdapter.setOnItemClickListener { adapter, view, position ->
            var intent = Intent()
            intent.putExtra("xinzuo",mAdapter.getItem(position))
            setResult(Activity.RESULT_OK,intent)
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.dd_menu_out)
    }
}
