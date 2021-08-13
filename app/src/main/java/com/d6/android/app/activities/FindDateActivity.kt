package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import com.d6.android.app.R
import com.d6.android.app.adapters.*
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.fragments.*
import kotlinx.android.synthetic.main.activity_finddate.*

/**
 * 遇见
 */
class FindDateActivity : TitleActivity() {

    private var mFragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finddate)
        immersionBar.init()
        setTitleBold("遇见")
        mFragments.add(DateFragment.newInstance("",0))
        mViewPager.adapter = InvateDateFragmentAdapter(supportFragmentManager,mFragments)
    }
}
