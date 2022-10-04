package com.d6.android.app.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.adapters.LoveHeartPageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.BangDanListQuickRichFragment
import com.d6.android.app.fragments.CharmBangdanFragment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_d6loveheartlist.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

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
        var mPageIndex = intent.getIntExtra("pageIndex",0)

        mFragments.add(CharmBangdanFragment.newInstance("魅力榜",mPageIndex))
        mFragments.add(BangDanListQuickRichFragment.newInstance("土豪榜",2))
        viewpager_loveheart.adapter = LoveHeartPageAdapter(supportFragmentManager,mFragments,mTitles)
        viewpager_loveheart.offscreenPageLimit = mFragments.size

        tab_loveheartitle.setupWithViewPager(viewpager_loveheart)
        viewpager_loveheart.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageSelected(index: Int) {
//                    if(TextUtils.equals("1", getUserSex())){
//                        ll_self_bangdan_order.visibility = View.VISIBLE
//                        tv_click_bangdan.visibility = View.GONE
//                    }else{
//                        ll_self_bangdan_order.visibility = View.GONE
//                    }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
        })

//        if(TextUtils.equals("1", getUserSex())){
//            ll_self_bangdan_order.visibility = View.GONE
//            tv_click_bangdan.visibility = View.GONE
//        }
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