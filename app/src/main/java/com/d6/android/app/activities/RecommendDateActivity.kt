package com.d6.android.app.activities

import android.Manifest
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.adapters.RecommentDatePageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.AreaSelectedPopup
import com.d6.android.app.dialogs.RenGongDateDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.RecommendDateQuickFragment
import com.d6.android.app.models.City
import com.d6.android.app.models.Province
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.rong.bean.RecommentType
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.IS_FIRST_SHOW_RGDIALOG
import com.d6.android.app.utils.Const.User.USER_CLASS_ID
import com.d6.android.app.utils.Const.User.USER_CLASS_NAME
import com.d6.android.app.utils.Const.User.USER_HEAD
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.facebook.drawee.backends.pipeline.Fresco
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_recommend_date.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * 全部人工推荐
 */
class RecommendDateActivity : BaseActivity() {

    val fragment = RecommendDateQuickFragment()
    private var city: String = ""

    private val mRecommentTypes = ArrayList<RecommentType>()

    var province = Province(Const.LOCATIONCITYCODE,"不限/定位")

    private var mFragments = ArrayList<RecommendDateQuickFragment>()

    private var pageSelected = 1

    private val locationClient by lazy {
        AMapLocationClient(this)
    }

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND+getLocalUserId())
    }

    private val cityJson by lazy{
        DiskFileUtils.getDiskLruCacheHelper(this).getAsString(Const.PROVINCE_DATAOFFIND+getLocalUserId())
    }

    private val userclassId by lazy{
        SPUtils.instance().getString(USER_CLASS_ID)
    }

    private val userclassName by lazy{
        SPUtils.instance().getString(USER_CLASS_NAME)
    }

    private val showRGDialog by lazy{
        SPUtils.instance().getBoolean(IS_FIRST_SHOW_RGDIALOG+getLocalUserId(),true)
    }

    private val headerUrl by lazy{
        SPUtils.instance().getString(USER_HEAD)
    }

    lateinit var mPopupArea: AreaSelectedPopup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_date)
        immersionBar.statusBarColor(R.color.color_black).statusBarDarkFont(false).init()

        iv_back_close.setOnClickListener {
            finish()
        }

        tv_date_city.setOnClickListener {
            isAuthUser(){
               showArea()
            }
        }

        ll_userlevel.setOnClickListener {
            isAuthUser(){
                startActivity<MemberActivity>()
            }
        }

        tv_recomendtitle.setOnClickListener {
            var mRgDateDialog = RenGongDateDialog()
            mRgDateDialog.show(supportFragmentManager,"RgDateDailog")
        }

//        tv_datetype.setOnClickListener {
//                val filterDateTypeDialog = FilterDateTypeDialog()
//                filterDateTypeDialog.setDateType(false)
//                filterDateTypeDialog.show(supportFragmentManager, "ftd")
//                filterDateTypeDialog.setDialogListener { p, s ->
//                    if (p == 6) {
//                        iLookType = ""
//                        tv_datetype.text = "类型"
//                    } else {
//                        iLookType = p.toString()
//                        tv_datetype.text = s
//                    }
//                    fragment.getFindRecommend(iLookType,city)
//                }
//        }

//        fragment.userVisibleHint = true
//        supportFragmentManager.beginTransaction()
//                .replace(R.id.container,fragment,"s")
//                .commitAllowingStateLoss()

        mPopupArea = AreaSelectedPopup.create(this)
                .setDimView(ll_recomment_root)
                .apply()

        if(!TextUtils.equals(getTodayTime(),lastTime)){
            getProvinceData()
        }else{
            try{
                var ProvinceData: MutableList<Province>? = GsonHelper.jsonToList(cityJson, Province::class.java)
                ProvinceData?.let {
                    setLocationCity()
                    it.add(0,province)
                    mPopupArea.setData(ProvinceData)
                }
            }catch(e:Exception){
                e.printStackTrace()
                getProvinceData()
            }
        }

        mRecommentTypes.add(RecommentType("全部",""))
        mRecommentTypes.add(RecommentType("觅约","5"))
//        mRecommentTypes.add(RecommentType("救火","1"))
        mRecommentTypes.add(RecommentType("征求","2"))
        mRecommentTypes.add(RecommentType("急约","3"))
        mRecommentTypes.add(RecommentType("旅行","4"))

        mFragments.add(RecommendDateQuickFragment.newInstance("",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("5",""))
//        mFragments.add(RecommendDateQuickFragment.newInstance("1",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("2",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("3",""))
        mFragments.add(RecommendDateQuickFragment.newInstance("4",""))

        viewpager_recommenddate.adapter = RecommentDatePageAdapter(supportFragmentManager,mFragments,mRecommentTypes)
        viewpager_recommenddate.offscreenPageLimit = mFragments.size
        viewpager_recommenddate.currentItem = pageSelected
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

        if(TextUtils.equals("${userclassId}","7")){
            ll_userlevel.visibility = View.GONE
//            recomend_level.visibility = View.GONE
//            tv_userlevel.text = "联系客服"
        }else{
            ll_userlevel.visibility = View.VISIBLE
            recomend_level.setImageURI(headerUrl)
            if(TextUtils.equals("${userclassId}","29")){
                tv_userlevel.text = "高级会员"
            }else if(TextUtils.equals("${userclassId}","27")){
                tv_userlevel.text = "初级会员"
            }else{
                tv_userlevel.text = "${userclassName}"
            }
        }

        if(showRGDialog){
            var mRgDateDialog = RenGongDateDialog()
            mRgDateDialog.show(supportFragmentManager,"RgDateDailog")
            SPUtils.instance().put(IS_FIRST_SHOW_RGDIALOG+getLocalUserId(),false).apply()
        }

    }

    private fun getProvinceData() {
        Request.getProvinceAll("1").request(this) { _, data ->
            data?.let {
                DiskFileUtils.getDiskLruCacheHelper(this).put(Const.PROVINCE_DATAOFFIND, GsonHelper.getGson().toJson(it))
                setLocationCity()
                it.add(0,province)
                mPopupArea.setData(it)
                SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND+getLocalUserId(), getTodayTime()).apply()
            }
        }
    }

    //设置不限
    private fun setLocationCity(){
//        var city = City("","不限地区")

        var sameCity = SPUtils.instance().getString(Const.User.USER_PROVINCE)
        var city = City("", getReplace(sameCity))
        city.isSelected = true
        city.isSelected = true
        province.lstDicts.add(city)
    }


    private fun showArea(){
        mPopupArea.showAsDropDown(ll_rgchoose,0,resources.getDimensionPixelOffset(R.dimen.margin_1))
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->
            if (position == -2) {
                checkLocation()
            }else{
                if(position == -3){
                    city = ""
                    tv_date_city.text = getString(R.string.string_area_city)
                }else{
                    city = string
                    tv_date_city.text = string
                }
                mFragments.get(0).getFindRecommend("",city)

                mFragments.get(1).getFindRecommend("5",city)

                mFragments.get(2).getFindRecommend("2",city)

                mFragments.get(3).getFindRecommend("3",city)

                mFragments.get(4).getFindRecommend("4",city)

            }
        }

        mPopupArea.setOnDismissListener {

        }
    }

    private fun checkLocation(){
        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                startLocation()
                SPUtils.instance().put(Const.User.ISNOTLOCATION,false).apply()
            }else{
                toast("请前往系统设置开启定位权限")
                SPUtils.instance().put(Const.User.ISNOTLOCATION,true).apply()
            }
        }

        locationClient.setLocationListener {
            if (it != null) {
                locationClient.stopLocation()
                SPUtils.instance().put(Const.User.USER_ADDRESS,it.city).apply() //it.city
                SPUtils.instance().put(Const.User.USER_PROVINCE,it.province).apply()
                getUserLocation(it.city,it.province,it.country,"${it.latitude}","${it.longitude}")

                tv_date_city.text = getReplace(it.province)
//                mFragments.get(pageSelected).getFindRecommend(mRecommentTypes.get(pageSelected).type,"${tv_date_city.text}")


//                mFragments.get(0).getFindRecommend("","${tv_date_city.text}")
//
//                mFragments.get(1).getFindRecommend("5","${tv_date_city.text}")
//
//                mFragments.get(2).getFindRecommend("2","${tv_date_city.text}")
//
//                mFragments.get(3).getFindRecommend("3","${tv_date_city.text}")
//
//                mFragments.get(4).getFindRecommend("4","${tv_date_city.text}")

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

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRecommentTypes.clear()
        mFragments.clear()
        Fresco.getImagePipeline().clearMemoryCaches()
        immersionBar.destroy()
    }
}
