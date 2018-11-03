package com.d6.android.app.fragments

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.RecommendDateAdapter
import com.d6.android.app.adapters.SpeedDateAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.dialogs.FilterCityDialog
import com.d6.android.app.dialogs.FilterDateTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.models.NewDateBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.isAuthUser
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.startActivity


/**
 * 主页
 */
class HomeFragment : BaseFragment() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var type: Int = 0
    private var cityType: Int = -2
    private var city: String? = ""
    private var outCity: String? = ""

    private val mSpeedDates = ArrayList<MyDate>()
    private val speedDateAdapter by lazy {
        RecommendDateAdapter(mSpeedDates)
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
                if(date.iType == 1){
                    startActivity<FindDateDetailActivity>("data" to date)
                }else if(date.iType == 2){
                    startActivity<SpeedDateDetailActivity>("data" to date)
                }
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
//        mTabLayout.setViewPager(mViewPager)

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
//            startActivity<SpeedDateActivity>()
            startActivity<RecommendDateActivity>()
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

        tv_date_city.setOnClickListener {
            val filterCityDialog = FilterCityDialog()
            filterCityDialog.hidleCancel(TextUtils.isEmpty(city) && TextUtils.isEmpty(outCity))
            filterCityDialog.setCityValue(cityType, tv_date_city.text.toString())
            filterCityDialog.show(childFragmentManager, "fcd")
            filterCityDialog.setDialogListener { p, s ->
                if (p == 1 || p == 0) {
                    city = s
                    outCity = ""
                } else if (p == 2) {
                    city = ""
                    outCity = s
                } else if (p == -2) {//取消选择
                    city = ""
                    outCity = ""
                }
                cityType = p
                tv_date_city.text = s
                getFragment()
            }
        }

        tv_datetype.setOnClickListener {
            val filterDateTypeDialog = FilterDateTypeDialog()
            filterDateTypeDialog.show(childFragmentManager, "ftd")
            filterDateTypeDialog.setDialogListener { p, s ->
                type = p
                tv_datetype.text = s
//                getData(1)
                getFragment()
            }
        }
    }

    private fun getFragment(){
        val fragments = childFragmentManager.fragments
        fragments?.forEach {
            if (it != null && !it.isDetached) {
                if (it is SelfPullDateFragment) {
//                    if(!TextUtils.isEmpty(city)){
//                        it.refresh(city,type.toString())
//                    }else if(!TextUtils.isEmpty(outCity)){
//                        it.refresh(outCity,type.toString())
//                    }else {
//                        it.refresh("",type.toString())
//                    }
                  var area = if(!TextUtils.isEmpty(city)) city else if(!TextUtils.isEmpty(outCity)) outCity else ""
                    var dateType = if(type == 6){
                        ""
                    }else{
                        type.toString()
                    }
                   it.refresh(area ,dateType)
                }
            }
        }
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
        Request.findLookAboutList(userId).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            mSpeedDates.clear()
            data?.let {
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

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}