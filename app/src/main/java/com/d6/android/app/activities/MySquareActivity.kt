package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.fragments.MySquareFragment
import kotlinx.android.synthetic.main.activity_my_square.*

class MySquareActivity : TitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_square)
        title = "我的广场"

        mViewPager.adapter = object :FragmentStatePagerAdapter(supportFragmentManager){

            val titles = arrayOf("我发布的","我点赞的","我评论的")
            override fun getItem(position: Int): Fragment {
                return MySquareFragment.instance(position)
            }

            override fun getCount() = titles.size

            override fun getPageTitle(position: Int)= titles[position]
        }

        mTabLayout.setViewPager(mViewPager)
    }
}
