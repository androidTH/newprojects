package com.d6zone.android.app.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.d6zone.android.app.R
import com.d6zone.android.app.base.BaseFragment
import com.d6zone.android.app.adapters.CharmBangDanPageAdapter
import kotlinx.android.synthetic.main.fragment_service.*

/**
 * 主页
 */
class CharmBangdanFragment : BaseFragment() ,ViewPager.OnPageChangeListener{

    override fun contentViewId() = R.layout.fragment_charmbangdan
    private val mRecommentTypes = ArrayList<String>()
    private var mFragments = ArrayList<Fragment>()
    private var mPageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ARG_PARAM2)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        immersionBar.statusBarColor(R.color.color_black).statusBarDarkFont(false).init()

        mRecommentTypes.add("月榜")
        mRecommentTypes.add("年榜")
        mRecommentTypes.add("总榜")

        mFragments.add(BangDanListQuickFragment.newInstance("月榜",1,1))
        mFragments.add(BangDanListQuickFragment.newInstance("年榜",1,2))
        mFragments.add(BangDanListQuickFragment.newInstance("总榜",1,3))

        viewpager_recommenddate.adapter = CharmBangDanPageAdapter(childFragmentManager, mFragments, mRecommentTypes)
        viewpager_recommenddate.offscreenPageLimit = mFragments.size
        tab_recommentdate.setupWithViewPager(viewpager_recommenddate)
        viewpager_recommenddate.setCurrentItem(mPageIndex)
        viewpager_recommenddate.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }
        })

    }

    override fun onFirstVisibleToUser() {

    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.statusBarColor(R.color.color_black).statusBarDarkFont(false).init()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                CharmBangdanFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1,param1)
                        putInt(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"