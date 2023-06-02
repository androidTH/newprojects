package com.d6zone.android.app.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.*
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.fragments.*
import com.d6zone.android.app.utils.isAuthUser
import kotlinx.android.synthetic.main.activity_invatedate.*
import org.jetbrains.anko.startActivity
/**
 * 邀约
 */
class InviteDateActivity : TitleActivity() {

    private var mFragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invatedate)
        immersionBar.init()
        setTitleBold("邀约")
        mFragments.add(InvateDatePageFragment.newInstance("",0))
        mViewPager.adapter = InvateDateFragmentAdapter(supportFragmentManager, mFragments)

        iv_publish.setOnClickListener {
            isAuthUser() {
                startActivity<PublishChooseActivity>()
            }
        }
    }
}
