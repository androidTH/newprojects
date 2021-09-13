package com.d6.android.app.fragments

import android.Manifest
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.RecommendDateAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import org.jetbrains.anko.support.v4.startActivity
import android.view.Gravity
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.adapters.HomeDatePageAdapter
import com.d6.android.app.dialogs.*
import com.d6.android.app.models.*
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.LOGIN_FOR_POINT_NEW
import com.d6.android.app.utils.Const.User.ISNOTLOCATION
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.utils.Const.dateTypes
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.d6.android.app.widget.popup.blur.MoreWindow
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_recommend_date.*
import kotlinx.android.synthetic.main.fragment_invatedatepage.*
import kotlinx.android.synthetic.main.fragment_invatedatepage.tv_date_city
import org.jetbrains.anko.bundleOf
import java.lang.Exception

/**
 * 主页
 */
class InvateDatePageFragment : BaseFragment() ,SelfPullDateFragment.RenGongBackground,ViewPager.OnPageChangeListener{

    private var mTAG = InvateDatePageFragment::class.java.simpleName

    private var onPageSelected:Int = 0

//    var showDateTypes:Array<DateType> = arrayOf(DateType(0),DateType(6),DateType(VoiceChatType),DateType(2),DateType(1),DateType(3),DateType(7),DateType(8),DateType(4))
    var showDateTypes:Array<DateType> = arrayOf(DateType(0),DateType(6),DateType(2),DateType(1),DateType(3),DateType(7),DateType(8),DateType(4))

    private val mSelfDateTypes = ArrayList<DateType>()

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val devicetoken by lazy{
        SPUtils.instance().getString(Const.User.DEVICETOKEN)
    }

    private var type: Int = 0
    private var city: String? = ""
    private var mDefualtSex = -1

    lateinit var mPopupSex:SelectedSexPopup
    fun IsNotNullPopupSex()=::mPopupSex.isInitialized

    private var mSelfPullDateFragment:SelfPullDateFragment?=null
//    private var mFragments = ArrayList<SelfPullDateFragment>()

    var province = Province(Const.LOCATIONCITYCODE,"不限/定位")

    private val cityJson by lazy{
        DiskFileUtils.getDiskLruCacheHelper(context).getAsString(Const.PROVINCE_DATAOFFIND)
    }

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND)
    }

    override fun contentViewId() = R.layout.fragment_invatedatepage

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (TextUtils.equals("0", getUserSex())) {
            tv_date_sex.text = "男生"
            mDefualtSex = 1
        } else {
            tv_date_sex.text = "女生"
            mDefualtSex = 0
        }
        var mFragments = listOf(
                SelfPullDateFragment.instance("", mDefualtSex),
                SelfPullDateFragment.instance("6",mDefualtSex),
//                SelfPullDateFragment.instance("9",mDefualtSex),
                SelfPullDateFragment.instance("2",mDefualtSex),
                SelfPullDateFragment.instance("1",mDefualtSex),
                SelfPullDateFragment.instance("3",mDefualtSex),
                SelfPullDateFragment.instance("7",mDefualtSex),
                SelfPullDateFragment.instance("8",mDefualtSex),
                SelfPullDateFragment.instance("4",mDefualtSex)
        )

        mFragments[0].setRenGongBackGround(this)
        mFragments[1].setRenGongBackGround(this)
//        mFragments[2].setRenGongBackGround(this)
        mFragments[2].setRenGongBackGround(this)
        mFragments[3].setRenGongBackGround(this)
        mFragments[4].setRenGongBackGround(this)
        mFragments[5].setRenGongBackGround(this)
        mFragments[6].setRenGongBackGround(this)
        mFragments[7].setRenGongBackGround(this)

        for (i in 0..(showDateTypes.size-1)) {
            var dt = showDateTypes[i]
            if(i==0){
                dt.dateTypeName = "全部"
            }else if(i==-1){//2
                dt.dateTypeName = "连麦"
            }else{
                dt.dateTypeName = dateTypes[dt.type-1]
            }
            mSelfDateTypes.add(dt)
        }

        mViewPager.offscreenPageLimit = mFragments.size
        mViewPager.adapter = HomeDatePageAdapter(childFragmentManager,mFragments,mSelfDateTypes)

        tab_home_date.setupWithViewPager(mViewPager)
        mViewPager.addOnPageChangeListener(this)

        tv_date_sex.setOnClickListener {
            activity.isAuthUser(){
                if(IsNotNullPopupSex()){
                    showSex()
                }
            }
        }

        tv_date_city.setOnClickListener {
            activity.isAuthUser(){
                showArea()
            }
        }

        mViewPager.postDelayed(object:Runnable{
            override fun run() {
                mPopupArea = AreaSelectedPopup.create(activity)
                        .setDimView(mSwipeRefreshLayout)
                        .apply()

                mPopupSex = SelectedSexPopup.create(activity)
                        .setDimView(mSwipeRefreshLayout)
                        .apply()
                getProvinceData()
            }
        },200)

//        loginforPoint()
        checkLocation()
    }

    private var mMoreWindow: MoreWindow? = null

    private fun showMoreWindow(view: View) {
        if (null == mMoreWindow) {
            mMoreWindow = MoreWindow(activity)
            mMoreWindow?.init()
        }
        mMoreWindow?.let {
            it.showMoreWindow(view, 100)
        }
    }

    private fun showSex() {
        mPopupSex.showAtLocation(mSwipeRefreshLayout,Gravity.NO_GRAVITY,0,resources.getDimensionPixelOffset(R.dimen.height_73))
        mPopupSex.setOnPopupItemClick { basePopup, position, string ->
            mDefualtSex = position
            if (mDefualtSex == -1) {
                tv_date_sex.text = getString(R.string.string_sex)
            }else{
                tv_date_sex.text = string
            }
            getFragment()
        }

        mPopupSex.setOnDismissListener {

        }
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
                SPUtils.instance().put(ISNOTLOCATION,false).apply()
            }else{
                SPUtils.instance().put(ISNOTLOCATION,true).apply()
            }
        }

        locationClient.setLocationListener {
            if (it != null) {
                locationClient.stopLocation()
                SPUtils.instance().put(USER_ADDRESS,it.city).apply() //it.city
                SPUtils.instance().put(USER_PROVINCE,it.province).apply()
                getUserLocation(it.city,it.province,it.country,"${it.latitude}","${it.longitude}")

                city = getReplace(it.province)
                tv_date_city.text = city
                getFragment()
                if(mPopupArea!=null){
                    mPopupArea.updateCityOfProvice()
                }
            }
        }
    }

    /**
     * 经纬度提交给服务端
     */
    private fun getUserLocation(city:String,sProvince:String,sCountry:String,lat:String,lon:String){
        Request.updateUserPosition(getLocalUserId(),sProvince,sCountry,city,lat,lon).request(this,false,success={_,data->
        })
    }

    private fun getProvinceData() {
        try{
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
        }catch (e:Exception){
            getServiceProvinceData()
        }
    }

    private fun getServiceProvinceData(){
        Request.getProvinceAll("1").request(this) { _, data ->
            data?.let {
                DiskFileUtils.getDiskLruCacheHelper(context).put(Const.PROVINCE_DATAOFFIND, GsonHelper.getGson().toJson(it))
                SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND,getTodayTime()).apply()
                setLocationCity()
                it.add(0,province)
                mPopupArea.setData(it)
            }
        }
    }

    fun refresh(sex:String,pageSelected:Int){
//        mSwipeRefreshLayout.isEnabled = true
//        mSwipeRefreshLayout.isRefreshing = true
//        mSwipeRefreshLayout.scrollTo(0,0)
//        if(pageSelected==0){
//            onPageSelected = pageSelected
//            mViewPager.setCurrentItem(onPageSelected)
//        }
//        mSwipeRefreshLayout.postDelayed(object:Runnable{
//            override fun run() {
//                city = ""
//                if (TextUtils.equals("-1",sex)) {
//                    tv_date_sex.text = getString(R.string.string_sex)
////                    mDefualtSex = -1
//                }
//                tv_date_city.text = "地区"
//                getFragment()
//            }
//        },600)
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
        mPopupArea.showAtLocation(mSwipeRefreshLayout,Gravity.NO_GRAVITY,0,resources.getDimensionPixelOffset(R.dimen.height_73))
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->
            if(position == -2){
                //定位失败
               checkLocation()
            }else{
                if(position == -1){
                    tv_date_city.text = "同城"
                    city = string
//                setSearChUI(0,true)
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
        }

        mPopupArea.setOnDismissListener {
        }
    }

    private fun getFragment(){
        val fragments = childFragmentManager.fragments
        var mSelfPullDateFragment:SelfPullDateFragment = fragments.get(onPageSelected) as SelfPullDateFragment
        mSelfPullDateFragment?.let {
            var area = if(!TextUtils.isEmpty(city)) city else ""
            type = showDateTypes.get(onPageSelected).type
            var dateType = if(type==0){//type == 6||
                ""
            }else{
                type.toString()
            }
            it.refresh(area ,dateType,mDefualtSex)
        }
    }

    private fun loginforPoint(){
        Request.loginForPoint(getLoginToken(),userId,devicetoken).request(this,false,success = {msg,data->
            if (data != null) {
                var sLoginToken = data.optString("sLoginToken")
                var lstTask = GsonHelper.jsonToList(data.optJsonArray("lstTask"),TaskBean::class.java)
                SPUtils.instance().put(Const.User.SLOGINTOKEN,sLoginToken).apply()
                if (lstTask!=null&&lstTask.size>0) {
                    SPUtils.instance().put(Const.LASTDAYTIME, "").apply()
//                    SPUtils.instance().put(Const.LASTLONGTIMEOFProvince,"").apply()
                    SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND,"").apply()
                    var today = getTodayTime()
                    var yesterday = SPUtils.instance().getString(LOGIN_FOR_POINT_NEW+getLocalUserId(),"")
                    if(!TextUtils.equals(today,yesterday)){
                        var mCheckInPointsDialog = CheckInPointsDialog()
                        mCheckInPointsDialog.arguments = bundleOf("beans" to lstTask)
                        mCheckInPointsDialog.show(childFragmentManager,"rewardtips")
                        mCheckInPointsDialog.setDialogListener { p, s ->
                            mCheckInPointsDialog.dismissAllowingStateLoss()
                        }
                        SPUtils.instance().put(LOGIN_FOR_POINT_NEW+getLocalUserId(), getTodayTime()).apply()
                    }
                }
            }
        })
    }

//    private fun getSpeedData() {
//        Request.findLookAboutList(userId).request(this,success = { _, data ->
//            mSwipeRefreshLayout.isRefreshing = false
//            mSpeedDates.clear()
//            data?.let {
//                mSpeedDates.addAll(it)
//            }
//            speedDateAdapter.notifyDataSetChanged()
//        }) { code, msg ->
//            toast(msg)
//            mSwipeRefreshLayout.isRefreshing = false
//        }
//    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        onPageSelected = position
        Log.i("selfpulldate","onPageSelected${onPageSelected}")
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

    override fun showBackground(mUpDown: Boolean) {

    }

    override fun showAllDateNums(type: String, count: Int) {
//        tv_datacount.text = "已有${count}人邀约成功"
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                InvateDatePageFragment().apply {
                    arguments = Bundle().apply {
//                        putParcelable(ARG_PARAM1,param1)
                    }
                }
    }
}