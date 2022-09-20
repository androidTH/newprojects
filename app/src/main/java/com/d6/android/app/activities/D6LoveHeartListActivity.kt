package com.d6.android.app.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.adapters.LoveHeartPageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.fragments.CharmBangdanFragment
import com.d6.android.app.fragments.LoveHeartListQuickFragment
import com.d6.android.app.fragments.ManyLoveHeartListQuickFragment
import kotlinx.android.synthetic.main.activity_d6loveheartlist.*
import org.jetbrains.anko.textColor

/**
 * 开通会员
 */
class D6LoveHeartListActivity : BaseActivity() {

    private var mTitles = ArrayList<String>()
    private var mFragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_d6loveheartlist)
        immersionBar.init()

        tv_membership_back.setOnClickListener {
            onBackPressed()
        }

        mTitles.add("魅力榜")
        mTitles.add("土豪榜")

        mFragments.add(CharmBangdanFragment.newInstance("魅力榜",2))
        mFragments.add(ManyLoveHeartListQuickFragment.newInstance("土豪榜",1))
        viewpager_loveheart.adapter = LoveHeartPageAdapter(supportFragmentManager,mFragments,mTitles)
        viewpager_loveheart.offscreenPageLimit = mFragments.size

        tab_loveheartitle.setupWithViewPager(viewpager_loveheart)
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