package com.d6.android.app.fragments

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import com.d6.android.app.R
import com.d6.android.app.activities.MainActivity
import com.d6.android.app.activities.NewestFindDateActivity
import com.d6.android.app.activities.SpeedDateDetailActivity
import com.d6.android.app.adapters.BannerAdapter
import com.d6.android.app.adapters.SpeedDateAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Banner
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.FullyLinearLayoutManager
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.optString
import kotlinx.android.synthetic.main.fragment_home_new.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * 主页
 */
class HomeNewFragment : BaseFragment() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private val mBanners = ArrayList<Banner>()
    private val bannerAdapter by lazy {
        BannerAdapter(mBanners)
    }
    private val mSpeedDates = ArrayList<MyDate>()
    private val speedDateAdapter by lazy {
        SpeedDateAdapter(mSpeedDates)
    }

    override fun contentViewId() = R.layout.fragment_home_new

    override fun onFirstVisibleToUser() {
        mBanner.setHasFixedSize(true)
        mBanner.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBanner.adapter = bannerAdapter
        val helper = PagerSnapHelper()
        helper.attachToRecyclerView(mBanner)

        rvSpeedDate.isNestedScrollingEnabled = false
        rvSpeedDate.layoutManager = FullyLinearLayoutManager(context)
        rvSpeedDate.adapter = speedDateAdapter

        speedDateAdapter.setOnItemClickListener { _, position ->
            val date = mSpeedDates[position]
            startActivity<SpeedDateDetailActivity>("data" to date)
        }

        mViewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            val titles = arrayOf("官方推荐", "自主发布")
            override fun getItem(position: Int): Fragment {
                if (position == 0) {
                    return HomeFindDateFragment.instance(position)
                }
                return HomeSelfReleaseFragment.instance(position)
            }

            override fun getCount() = titles.size

            override fun getPageTitle(position: Int) = titles[position]

        }
        mTabLayout.setViewPager(mViewPager)

        tv_speed_date_more.setOnClickListener {
            activity?.let {
                if (it is MainActivity) {
                    it.changeTab(1)
                }
            }
        }
        tv_newest_date_more.setOnClickListener {
            startActivity<NewestFindDateActivity>()
        }
        showDialog()
        getBanner()
        getSpeedData()
    }

    private fun getBanner() {
//        Request.getBanners().request(this) { _, data ->
//            data?.let {
//                val picUrl = data.optString("picUrl")
//                val pics = picUrl.split(",")
//                mBanners.clear()
//                mBanners.addAll(pics.toList())
//                bannerAdapter.notifyDataSetChanged()
//            }
//        }
    }

    private fun getSpeedData() {
        Request.getSpeedDateList(1,1,pageSize = 2).request(this){_,data->
            mSpeedDates.clear()
            data?.list?.results?.let {
                mSpeedDates.addAll(it)
            }
            speedDateAdapter.notifyDataSetChanged()
        }
    }
}