package com.d6.android.app.fragments

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager
import com.d6.android.app.R
import com.d6.android.app.activities.MainActivity
import com.d6.android.app.activities.NewestFindDateActivity
import com.d6.android.app.activities.SpeedDateActivity
import com.d6.android.app.activities.SpeedDateDetailActivity
import com.d6.android.app.adapters.SpeedDateAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.isAuthUser
import com.d6.android.app.utils.sysErr
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.gyf.barlibrary.ImmersionBar
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity


/**
 * 主页
 */
class HomeFragment : BaseFragment() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val mSpeedDates = ArrayList<MyDate>()
    private val speedDateAdapter by lazy {
        SpeedDateAdapter(mSpeedDates)
    }

    override fun contentViewId() = R.layout.fragment_home

    override fun onFirstVisibleToUser() {
        immersionBar.statusBarColor(R.color.colorPrimaryDark).init()

        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            mSwipeRefreshLayout.isEnabled = verticalOffset >= 0
        }

        rvSpeedDate.setHasFixedSize(true)
        rvSpeedDate.isNestedScrollingEnabled = true
        rvSpeedDate.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        rvSpeedDate.adapter = speedDateAdapter

        speedDateAdapter.setOnItemClickListener { _, position ->
            activity?.isAuthUser {
                val date = mSpeedDates[position]
                startActivity<SpeedDateDetailActivity>("data" to date)
            }
        }

        mViewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            val titles = arrayOf("官方推荐")
            override fun getItem(position: Int): Fragment {
                if (position == 0) {
//                    return HomeFindDateFragment.instance(position)
                    return SelfPullDateFragment.instance(position)
                }
                return SelfPullDateFragment.instance(position)
            }

            override fun getCount() = titles.size

            override fun getPageTitle(position: Int) = titles[position]

        }
        mTabLayout.setViewPager(mViewPager)

//        bannerAdapter.setOnItemClickListener { view, position ->
//            val banner = mBanners[position]
//            val ids = banner.newsid ?: ""
//            startActivity<SquareTrendDetailActivity>("id" to ids)
//        }

        tv_speed_date_more.setOnClickListener {
//            activity?.let {
//                if (it is MainActivity) {
//                    it.changeTab(1)
//                }
//            }
            startActivity<SpeedDateActivity>()
        }

        tv_newest_date_more.setOnClickListener {
            startActivity<NewestFindDateActivity>()
        }

        mSwipeRefreshLayout.setOnRefreshListener {
//            getBanner()
            getSpeedData()
            val fragments = childFragmentManager.fragments
            fragments?.forEach {
                if (it != null && !it.isDetached) {
                    if (it is SelfPullDateFragment) {
                        it.refresh()
                    }
//                    else if (it is HomeSelfReleaseFragment) {
//                        it.refresh()
//                    }
                }
            }
        }

        showDialog()
//        getBanner()
        getSpeedData()
    }

//    private fun getBanner() {
//        Request.getBanners().request(this, success = { _, data ->
//            mSwipeRefreshLayout.isRefreshing = false
//            if (data?.list?.results != null) {
//                mBanners.clear()
//                mBanners.addAll(data.list.results)
//                bannerAdapter.notifyDataSetChanged()
//            }
//        }) { _, _ ->
//            mSwipeRefreshLayout.isRefreshing = false
//        }
//    }

    private fun getSpeedData() {
        Request.getSpeedDateList(1, 1, speedhomepage = "1", pageSize = 2).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            mSpeedDates.clear()
            data?.list?.results?.let {
                mSpeedDates.addAll(it)
            }
            speedDateAdapter.notifyDataSetChanged()
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
        }
    }
//    override fun onVisibleToUser() {
//        super.onVisibleToUser()
//        if (!mHidden) {
//            immersionBar.init()
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}