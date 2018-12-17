package com.d6.android.app.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.RecommendDateAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.dialogs.FilterCityDialog
import com.d6.android.app.dialogs.FilterDateTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.startActivity
import android.support.v7.widget.LinearSnapHelper
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.d6.android.app.dialogs.AreaSelectedPopup
import com.d6.android.app.models.City
import com.d6.android.app.models.Province
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.diskcache.DiskFileUtils
import kotlinx.android.synthetic.main.fragment_date.*

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

    private val cityJson by lazy{
//        SPUtils.instance().getString(Const.PROVINCE_DATA)
        DiskFileUtils.getDiskLruCacheHelper(context).getAsString(Const.PROVINCE_DATA)
    }

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTLONGTIME)
    }

    private val mSpeedDates = ArrayList<MyDate>()
    private val speedDateAdapter by lazy {
        RecommendDateAdapter(mSpeedDates)
    }

    override fun contentViewId() = R.layout.fragment_home

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        immersionBar.statusBarColor(R.color.colorPrimaryDark).init()

        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            mSwipeRefreshLayout.isEnabled = verticalOffset >= 0
        }

        rvSpeedDate.setHasFixedSize(true)
        rvSpeedDate.isNestedScrollingEnabled = true
        rvSpeedDate.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rvSpeedDate)
        rvSpeedDate.adapter = speedDateAdapter

//        dzsticknavlayout.setOnStartActivity {
//            startActivity<RecommendDateActivity>()
//        }

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
                }
            }
        }

        showDialog()
        getSpeedData()

        tv_date_city.setOnClickListener {
//            val filterCityDialog = FilterCityDialog()
//            filterCityDialog.hidleCancel(TextUtils.isEmpty(city) && TextUtils.isEmpty(outCity))
//            filterCityDialog.setCityValue(cityType, tv_date_city.text.toString())
//            filterCityDialog.show(childFragmentManager, "fcd")
//            filterCityDialog.setDialogListener { p, s ->
//                if (p == 1 || p == 0) {
//                    city = s
//                    outCity = ""
//                } else if (p == 2) {
//                    city = ""
//                    outCity = s
//                } else if (p == -2) {//取消选择
//                    city = ""
//                    outCity = ""
//                }
//                cityType = p
//                tv_date_city.text = s
//                getFragment()
//            }

            showArea()
        }

        tv_datetype.setOnClickListener {
            val filterDateTypeDialog = FilterDateTypeDialog()
            filterDateTypeDialog.show(childFragmentManager, "ftd")
            filterDateTypeDialog.setDialogListener { p, s ->
                type = p
                tv_datetype.text = s
                getFragment()
            }
        }

        mPopupArea = AreaSelectedPopup.create(activity)
                .setDimView(mSwipeRefreshLayout)
                .apply()
        loginforPoint()
        getProvinceData()
    }

    override fun onFirstVisibleToUser() {

    }

    private fun getProvinceData() {
        if (cityJson.isNullOrEmpty()) {
            getServiceProvinceData()
        } else {
            if (!TextUtils.equals(getTodayTime(), lastTime)) {
                getServiceProvinceData()
            } else {
                var ProvinceData: MutableList<Province>? = GsonHelper.jsonToList(cityJson, Province::class.java)
                mPopupArea.setData(ProvinceData)
            }
        }
    }

    private fun getServiceProvinceData(){
        Request.getProvince().request(this) { _, data ->
            data?.let {
                mPopupArea.setData(it)
                DiskFileUtils.getDiskLruCacheHelper(context).put(Const.PROVINCE_DATA, GsonHelper.getGson().toJson(it))
                SPUtils.instance().put(Const.LASTLONGTIME, getTodayTime()).apply()
            }
        }
    }

    lateinit var mPopupArea: AreaSelectedPopup

    private fun showArea(){
        mPopupArea.showAtLocation(mSwipeRefreshLayout,Gravity.NO_GRAVITY,0,resources.getDimensionPixelOffset(R.dimen.height_75))
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->
            city = string
            getFragment()
        }

        mPopupArea.setOnDismissListener {
        }
    }

    private fun getFragment(){
        val fragments = childFragmentManager.fragments
        fragments?.forEach {
            if (it != null && !it.isDetached) {
                if (it is SelfPullDateFragment) {
//                  var area = if(!TextUtils.isEmpty(city)) city else if(!TextUtils.isEmpty(outCity)) outCity else ""
                  var area = if(!TextUtils.isEmpty(city)) city else ""
                    var dateType = if(type == 6||type==0){
                        ""
                    }else{
                        type.toString()
                    }
                   it.refresh(area ,dateType)
                }
            }
        }
    }

    private fun loginforPoint(){
        Request.loginForPoint(userId).request(this,false,success = {msg,data->
            showTips(data,"","")
            if (data != null) {
                var pointDesc = data.optString("sAddPointDesc")
                if (!TextUtils.isEmpty(pointDesc)) {
                    SPUtils.instance().put(Const.LASTDAYTIME, "").apply()
                    SPUtils.instance().put(Const.LASTDAYTIME,"").apply()
                }
            }
        }){code,msg->
//            var mg = JsonObject().getAsJsonObject(msg)
//            showTips(mg,"","")
        }
    }


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