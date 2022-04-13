package com.d6.android.app.fragments

import android.Manifest
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import org.jetbrains.anko.support.v4.startActivity
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.adapters.RecommentDatePageAdapter
import com.d6.android.app.dialogs.*
import com.d6.android.app.models.*
import com.d6.android.app.rong.bean.RecommentType
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.ISNOTLOCATION
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_service.*
import java.lang.Exception

/**
 * 主页
 */
class ServiceFragment : BaseFragment() ,ViewPager.OnPageChangeListener{

    private var mTAG = ServiceFragment::class.java.simpleName

    private var city: String = ""
    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND+getLocalUserId())
    }
    private val cityJson by lazy{
        DiskFileUtils.getDiskLruCacheHelper(context).getAsString(Const.PROVINCE_DATAOFFIND+getLocalUserId())
    }

    var province = Province(Const.LOCATIONCITYCODE,"不限/定位")

    private val mRecommentTypes = ArrayList<RecommentType>()
    private var mFragments = ArrayList<RecommendDateQuickFragment>()

    private val userclassId by lazy{
        SPUtils.instance().getString(Const.User.USER_CLASS_ID)
    }

    private var userJson = SPUtils.instance().getString(Const.USERINFO)
    private var mUserInfo = GsonHelper.getGson().fromJson(userJson, UserData::class.java)

    private val showRGDialog by lazy{
        SPUtils.instance().getBoolean(Const.User.IS_FIRST_SHOW_RGDIALOG +getLocalUserId(),true)
    }

    override fun contentViewId() = R.layout.fragment_service

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        immersionBar.statusBarColor(R.color.color_black).statusBarDarkFont(false).init()

        tv_service_city.setOnClickListener {
            activity.isAuthUser(){
                showArea()
            }
        }

        ll_userlevel.setOnClickListener {
            activity.isAuthUser(){
                startActivity<MemberActivity>()
            }
        }

        tv_recomendtitle.setOnClickListener {
            var mRgDateDialog = RenGongDateDialog()
            mRgDateDialog.show(childFragmentManager,"RgDateDailog")
        }

        mRecommentTypes.add(RecommentType("全部",""))
        mRecommentTypes.add(RecommentType("觅约","5"))
        mRecommentTypes.add(RecommentType("救火","1"))
        mRecommentTypes.add(RecommentType("征求","2"))
        mRecommentTypes.add(RecommentType("急约","3"))
        mRecommentTypes.add(RecommentType("旅行","4"))

        mFragments.add(RecommendDateQuickFragment.newInstance("",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("5",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("1",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("2",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("3",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("4",""))

        viewpager_recommenddate.adapter = RecommentDatePageAdapter(childFragmentManager,mFragments,mRecommentTypes)
        viewpager_recommenddate.offscreenPageLimit = mFragments.size
        tab_recommentdate.setupWithViewPager(viewpager_recommenddate)
        viewpager_recommenddate.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                pageSelected = position
            }
        })

        if(TextUtils.equals(userclassId,"7")){
            ll_userlevel.visibility = View.GONE
        }else{
            ll_userlevel.visibility = View.GONE
            recomend_level.setImageURI(getLocalUserHeadPic())
            if(TextUtils.equals(userclassId,"29")){
                tv_userlevel.text = "高级会员"
            }else if(TextUtils.equals(userclassId,"27")){
                tv_userlevel.text = "初级会员"
            }else{
                tv_userlevel.text = mUserInfo.classesname
            }
        }

        if(showRGDialog){
            var mRgDateDialog = RenGongDateDialog()
            mRgDateDialog.show(childFragmentManager,"RgDateDailog")
            SPUtils.instance().put(Const.User.IS_FIRST_SHOW_RGDIALOG +getLocalUserId(),false).apply()
        }

        mPopupArea = AreaSelectedPopup.create(activity)
                .setDimView(ll_recomment_root)
                .apply()
        checkLocation()
        getProvinceData()
    }

    override fun onFirstVisibleToUser() {

    }

    lateinit var mPopupArea: AreaSelectedPopup
    private var pageSelected = 0

    private fun showArea(){
        mPopupArea.showAsDropDown(ll_rgchoose,0,resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->
            if(position == -3){
                city = ""
                tv_service_city.text = getString(R.string.string_area_city)
            }else{
                city = string
                tv_service_city.text = string
            }
            mFragments.get(pageSelected).getFindRecommend(mRecommentTypes.get(pageSelected).type,city)
        }
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
            }
        }
    }

    private fun getProvinceData() {
        try{
            if (TextUtils.isEmpty(cityJson)) {
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
                DiskFileUtils.getDiskLruCacheHelper(context).put(Const.PROVINCE_DATAOFFIND+getLocalUserId(), GsonHelper.getGson().toJson(it))
                SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND+getLocalUserId(),getTodayTime()).apply()
                setLocationCity()
                it.add(0,province)
                mPopupArea.setData(it)
            }
        }
    }

    fun refresh(){
        if(mFragments.size>0){
            mFragments.get(pageSelected).getFindRecommend(mRecommentTypes.get(pageSelected).type,city)
        }
//        viewpager_recommenddate.postDelayed(object:Runnable{
//            override fun run() {
//                mFragments.get(pageSelected).getFindRecommend(mRecommentTypes.get(pageSelected).type,city)
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

    private fun getFragment(){

    }

    private fun getSpeedData() {

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

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}