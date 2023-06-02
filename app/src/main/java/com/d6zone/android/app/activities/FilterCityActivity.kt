package com.d6zone.android.app.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.d6zone.android.app.R
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.fragments.SelectCityFragment
import kotlinx.android.synthetic.main.activity_filter_city.*

class FilterCityActivity : TitleActivity() {

    private val type by lazy {//1多选，其他单选
        intent.getIntExtra("type",0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_city)
        title = "选择地区"

        mViewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            val titles = arrayOf( "全国","海外")
            override fun getItem(position: Int): Fragment {
                val p = if (position == 0) {
                    1
                } else {
                    0
                }
                return SelectCityFragment.instance(p,type)
            }

            override fun getCount() = 2

            override fun getPageTitle(position: Int): CharSequence {
                return titles[position]
            }
        }
        mTabLayout.setViewPager(mViewPager)
    }

}
