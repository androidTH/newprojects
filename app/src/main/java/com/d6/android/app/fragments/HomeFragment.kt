package com.d6.android.app.fragments

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.RecommendDateAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.dialogs.FilterDateTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.startActivity
import android.view.Gravity
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.dialogs.AreaSelectedPopup
import com.d6.android.app.models.City
import com.d6.android.app.models.Province
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * 主页
 */
class HomeFragment : BaseFragment() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var type: Int = 0
    private var city: String? = ""

    var province = Province(Const.LOCATIONCITYCODE,"不限/定位")

    private val cityJson by lazy{
        DiskFileUtils.getDiskLruCacheHelper(context).getAsString(Const.PROVINCE_DATAOFFIND)
    }

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND)
    }

    private val mSpeedDates = ArrayList<MyDate>()
    private val speedDateAdapter by lazy {
        RecommendDateAdapter(mSpeedDates)
    }

    override fun contentViewId() = R.layout.fragment_home

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            mSwipeRefreshLayout.isEnabled = verticalOffset >= 0
        }

        rvSpeedDate.setHasFixedSize(true)
        rvSpeedDate.isNestedScrollingEnabled = true
        rvSpeedDate.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
//        val snapHelper = LinearSnapHelper()
//        snapHelper.attachToRecyclerView(rvSpeedDate)
        rvSpeedDate.adapter = speedDateAdapter

        speedDateAdapter.setOnItemClickListener { _, position ->
                val date = mSpeedDates[position]
                if(date.iType == 1){
                    startActivity<FindDateDetailActivity>("data" to date)
                }else if(date.iType == 2){
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

        tv_speed_date_more.setOnClickListener {
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
//            activity.isAuthUser(){
                showArea()
//            }
        }

        tv_datetype.setOnClickListener {
//            activity.isCheckOnLineAuthUser(this,userId){
                val filterDateTypeDialog = FilterDateTypeDialog()
                filterDateTypeDialog.show(childFragmentManager, "ftd")
                filterDateTypeDialog.setDialogListener { p, s ->
                    type = p
                    tv_datetype.text = s
                    getFragment()
                }
//            }
        }

        mPopupArea = AreaSelectedPopup.create(activity)
                .setDimView(mSwipeRefreshLayout)
                .apply()
        loginforPoint()
        checkLocation()
        getProvinceData()
    }

    override fun onFirstVisibleToUser() {

    }


    private val locationClient by lazy {
        AMapLocationClient(activity)
    }

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }


    private fun checkLocation(){
        RxPermissions(activity).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                startLocation()
            }
        }

        locationClient.setLocationListener {
            if (it != null) {
                locationClient.stopLocation()
                SPUtils.instance().put(USER_ADDRESS,it.city).apply() //it.city
                SPUtils.instance().put(USER_PROVINCE,it.province).apply()
            }
        }
    }

    private fun getProvinceData() {
        if (cityJson.isNullOrEmpty()) {
            getServiceProvinceData()
        } else {
            if (!TextUtils.equals(getTodayTime(), lastTime)) {
                getServiceProvinceData()
            } else {
                var ProvinceData: MutableList<Province>? = GsonHelper.jsonToList(cityJson, Province::class.java)
                setLocationCity()
                ProvinceData!!.add(0,province)
                mPopupArea.setData(ProvinceData)
            }
        }
    }

    private fun getServiceProvinceData(){
        Request.getProvinceAll().request(this) { _, data ->
            data?.let {
                DiskFileUtils.getDiskLruCacheHelper(context).put(Const.PROVINCE_DATAOFFIND, GsonHelper.getGson().toJson(it))
                SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND,getTodayTime()).apply()
                setLocationCity()
                it.add(0,province)
                mPopupArea.setData(it)
            }
        }
    }

    fun refresh(){
        showDialog()
        city = ""
        type = 0
        getSpeedData()
        getFragment()
    }

    //设置定位城市
    private fun setLocationCity(){
        var sameCity = SPUtils.instance().getString(USER_PROVINCE)
        var city = City("", getReplace(sameCity))
        city.isSelected = true
        province.lstDicts.add(city)
    }

    lateinit var mPopupArea: AreaSelectedPopup

    private fun showArea(){
        mPopupArea.showAtLocation(mSwipeRefreshLayout,Gravity.NO_GRAVITY,0,resources.getDimensionPixelOffset(R.dimen.height_75))
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->

            if(position == -1){
                tv_date_city.text = "同城"
                city = string
//                setSearChUI(0,true)
            }else if(position == -2){
                //定位失败
                checkLocation()
            }else if(position == -3){
                city = ""
                tv_date_city.text = "地区"
//                setSearChUI(0,false)
            }else {
                city = string
                tv_date_city.text = string
//                setSearChUI(0,true)
            }
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
                    SPUtils.instance().put(Const.LASTLONGTIMEOFProvince,"").apply()
                    SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND,"").apply()
                }
            }
        }){code,msg->
//            var mg = JsonObject().getAsJsonObject(msg)
//            showTips(mg,"","")
        }
    }


//    private fun setSearChUI(clickIndex:Int,iconFlag:Boolean){
//        if(clickIndex == 0){
//            var drawable = if(iconFlag) ContextCompat.getDrawable(activity,R.mipmap.ic_arrow_up_orange)else ContextCompat.getDrawable(activity,R.mipmap.titlemore_icon)
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
//            tv_date_city.setCompoundDrawables(null,null,drawable,null)
//            tv_date_city.textColor = if(iconFlag) ContextCompat.getColor(context,R.color.color_F7AB00) else ContextCompat.getColor(context,R.color.color_black)
//        }else if(clickIndex == 1){
//            var drawable = if(iconFlag) ContextCompat.getDrawable(activity,R.mipmap.ic_arrow_up_orange)else ContextCompat.getDrawable(activity,R.mipmap.titlemore_icon)
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
//            tv_datetype.setCompoundDrawables(null,null,drawable,null)
//            tv_datetype.textColor = if(iconFlag) ContextCompat.getColor(context,R.color.color_F7AB00) else ContextCompat.getColor(context,R.color.color_black)
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