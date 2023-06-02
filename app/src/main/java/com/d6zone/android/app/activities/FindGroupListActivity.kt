package com.d6zone.android.app.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.*
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.fragments.FindGroupQuickFragment
import kotlinx.android.synthetic.main.activity_invatedate.*
import kotlinx.android.synthetic.main.activity_userslist.*

/**
 * 群列表
 */
class FindGroupListActivity : BaseActivity(){

    private var mFragments = ArrayList<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findgrouplist)
        immersionBar.init()
        tv_userlist_back.setOnClickListener {
            finish()
        }
        mFragments.add(FindGroupQuickFragment.newInstance("",0))
        mViewPager.adapter = InvateDateFragmentAdapter(supportFragmentManager, mFragments)
    }
}
