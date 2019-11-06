package com.d6.android.app.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.adapters.LoveHeartPageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.fragments.LoveHeartListQuickFragment
import kotlinx.android.synthetic.main.activity_d6loveheartlist.*
import org.jetbrains.anko.textColor

/**
 * 开通会员
 */
class D6LoveHeartListActivity : BaseActivity() {

    private var mTitles = ArrayList<String>()
    private var mFragments = ArrayList<LoveHeartListQuickFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_d6loveheartlist)
        immersionBar.init()

        tv_membership_back.setOnClickListener {
            onBackPressed()
        }

        mTitles.add("魅力榜")
        mTitles.add("土豪棒")

        mFragments.add(LoveHeartListQuickFragment.newInstance("魅力榜",2))
        mFragments.add(LoveHeartListQuickFragment.newInstance("土豪棒",1))
        viewpager_loveheart.adapter = LoveHeartPageAdapter(supportFragmentManager,mFragments,mTitles)
        viewpager_loveheart.offscreenPageLimit = mFragments.size

        tab_loveheartitle.setupWithViewPager(viewpager_loveheart)
//        tab_membership.addOnTabSelectedListener(object : com.d6.android.app.widget.tablayout.TabLayout.OnTabSelectedListener {
//            override fun onTabReselected(tab: com.d6.android.app.widget.tablayout.TabLayout.Tab?) {
//
//            }
//
//            override fun onTabSelected(tab: com.d6.android.app.widget.tablayout.TabLayout.Tab?) {
//                tab?.let {
//                    setTabSelected(true, it)
//                    viewpager_membership.setCurrentItem(it.getPosition())
//                }
//            }
//
//            override fun onTabUnselected(tab: com.d6.android.app.widget.tablayout.TabLayout.Tab?) {
//                tab?.let {
//                    setTabSelected(false, it)
//                }
//            }
//        })
    }

    private fun setTabSelected(flag: Boolean, tab: com.d6.android.app.widget.tablayout.TabLayout.Tab) {
        var tv = tab.getCustomView()!!.findViewById<TextView>(R.id.tv_tab)
        var paint = tv.getPaint()
        paint.setFakeBoldText(flag)
        if (flag) {
            tv.textColor = ContextCompat.getColor(this, R.color.color_F7AB00)
        } else {
            tv.textColor = ContextCompat.getColor(this, R.color.color_666666)
        }
    }


    private fun InitUIData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}