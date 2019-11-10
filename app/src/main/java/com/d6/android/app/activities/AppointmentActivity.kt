package com.d6.android.app.activities

import android.Manifest
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.adapters.HomeDatePageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.AreaSelectedPopup
import com.d6.android.app.dialogs.RenGongDateDialog
import com.d6.android.app.dialogs.SelectedSexPopup
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.SelfPullDateFragment
import com.d6.android.app.models.City
import com.d6.android.app.models.DateType
import com.d6.android.app.models.Province
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.ISNOTLOCATION
import com.d6.android.app.utils.Const.User.IS_FIRST_SHOW_RGDIALOG
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_CLASS_ID
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.facebook.drawee.backends.pipeline.Fresco
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_appointment.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

/**
 * 约会
 */
class AppointmentActivity : BaseActivity() {

    private var city: String = ""

    var province = Province(Const.LOCATIONCITYCODE,"不限/定位")

    private var mFragments = ArrayList<SelfPullDateFragment>()

    private var pageSelected = 0

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND)
    }

    private val cityJson by lazy{
        DiskFileUtils.getDiskLruCacheHelper(this).getAsString(Const.PROVINCE_DATAOFFIND)
    }

    private var userJson = SPUtils.instance().getString(Const.USERINFO)
    private var mUserInfo = GsonHelper.getGson().fromJson(userJson, UserData::class.java)

    private val userclassId by lazy{
        SPUtils.instance().getString(USER_CLASS_ID)
    }

    private val showRGDialog by lazy{
        SPUtils.instance().getBoolean(IS_FIRST_SHOW_RGDIALOG+getLocalUserId(),true)
    }

    private val mSelfDateTypes = ArrayList<DateType>()
    private var showDateTypes:Array<DateType> = arrayOf(DateType(0),DateType(6),DateType(2),DateType(1),DateType(3),DateType(7),DateType(8))
    private var type: Int = 0

    lateinit var mPopupArea: AreaSelectedPopup
    fun IsNotNullPopupArea()=::mPopupArea.isInitialized
    lateinit var mPopupSex:SelectedSexPopup
    fun IsNotNullPopupSex()=::mPopupSex.isInitialized

    private var mSelectedSex = -1
    private var from = "SquareFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)
        immersionBar.init()

        from = intent.getStringExtra("from")

        if (TextUtils.equals(from, "SquareFragment")) {
            if (TextUtils.equals("0", getUserSex())) {
                tv_date_sex.text = "男生"
                mSelectedSex = 1
            } else {
                tv_date_sex.text = "女生"
                mSelectedSex = 0
            }
        } else if (TextUtils.equals(from, "PublishFindDateActivity")) {
            tv_date_sex.text = getString(R.string.string_sex)
            mSelectedSex = -1
        }

        var mFragments = listOf(
                SelfPullDateFragment.instance("", mSelectedSex),
                SelfPullDateFragment.instance("6", mSelectedSex),
                SelfPullDateFragment.instance("2", mSelectedSex),
                SelfPullDateFragment.instance("1", mSelectedSex),
                SelfPullDateFragment.instance("3", mSelectedSex),
                SelfPullDateFragment.instance("7", mSelectedSex),
                SelfPullDateFragment.instance("8", mSelectedSex)
        )

        for (i in 0..(showDateTypes.size - 1)) {
            var dt = showDateTypes[i]
            if (i == 0) {
                dt.dateTypeName = "全部"
            } else {
                dt.dateTypeName = Const.dateTypes[dt.type - 1]
            }
            mSelfDateTypes.add(dt)
        }

        iv_back_close.setOnClickListener {
            finish()
        }

        tv_date_city.setOnClickListener {
            isAuthUser(){
                if(IsNotNullPopupArea()){
                    showArea()
                }
            }
        }

        tv_date_sex.setOnClickListener {
            isAuthUser(){
                if(IsNotNullPopupSex()){
                    showSex()
                }
            }
        }

        tv_add_date.setOnClickListener {
            isCheckOnLineAuthUser(this, getLocalUserId()){
                startActivityForResult<PublishFindDateActivity>(10)
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

        viewpager_appointment.adapter = HomeDatePageAdapter(supportFragmentManager,mFragments,mSelfDateTypes)
        viewpager_appointment.offscreenPageLimit = mFragments.size
        tab_home_date.setupWithViewPager(viewpager_appointment)
        viewpager_appointment.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
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
//            recomend_level.visibility = View.GONE
//            tv_userlevel.text = "联系客服"
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
            mRgDateDialog.show(supportFragmentManager,"RgDateDailog")
            SPUtils.instance().put(IS_FIRST_SHOW_RGDIALOG+getLocalUserId(),false).apply()
        }

        checkLocation()

        viewpager_appointment.postDelayed(object:Runnable{
            override fun run() {
                mPopupArea = AreaSelectedPopup.create(this@AppointmentActivity)
                        .setDimView(viewpager_appointment)
                        .apply()

                mPopupSex = SelectedSexPopup.create(this@AppointmentActivity)
                        .setDimView(viewpager_appointment)
                        .apply()

                if(!TextUtils.equals(getTodayTime(),lastTime)){
                    getProvinceData()
                }else{
                    try{
                        var ProvinceData: MutableList<Province>? = GsonHelper.jsonToList(cityJson, Province::class.java)
                        setLocationCity()
                        ProvinceData?.add(0,province)
                        if(IsNotNullPopupArea()){
                            mPopupArea.setData(ProvinceData)
                        }
                    }catch(e:Exception){
                        e.printStackTrace()
                        getProvinceData()
                    }
                }
            }
        },200)
    }

    private fun getProvinceData() {
        Request.getProvinceAll().request(this) { _, data ->
            data?.let {
                DiskFileUtils.getDiskLruCacheHelper(this).put(Const.PROVINCE_DATAOFFIND, GsonHelper.getGson().toJson(it))
                setLocationCity()
                it.add(0,province)
                if(IsNotNullPopupArea()){
                    mPopupArea.setData(it)
                }
                SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND, getTodayTime()).apply()
            }
        }
    }

    //设置不限
    private fun setLocationCity(){
//        var city = City("","不限地区")
        var sameCity = SPUtils.instance().getString(USER_PROVINCE)
        var city = City("", getReplace(sameCity))
        city.isSelected = true
        city.isSelected = true
        province.lstDicts.add(city)
    }


    private fun showArea(){
        mPopupArea.showAsDropDown(rl_appointment_title,0,0)
        mPopupArea.setOnPopupItemClick { basePopup, position, string ->
            if(position == -1){
                tv_date_city.text = "同城"
                city = string
            }else if(position == -2){
                //定位失败
                checkLocation()
            }else if(position == -3){
                city = ""
                tv_date_city.text = "地区"
            }else {
                city = string
                tv_date_city.text = string
            }
            getFragment()
        }

        mPopupArea.setOnDismissListener {

        }
    }

    private fun showSex() {
        mPopupSex.showAsDropDown(rl_appointment_title, 0,0)
        mPopupSex.setOnPopupItemClick { basePopup, position, string ->
            mSelectedSex = position
            if (mSelectedSex == -1) {
                tv_date_sex.text = getString(R.string.string_sex)
            }else{
                tv_date_sex.text = string
            }
            getFragment()
        }

        mPopupSex.setOnDismissListener {

        }
    }

    private fun getFragment(){
        var mSelfPullDateFragment:SelfPullDateFragment = supportFragmentManager.fragments[pageSelected] as SelfPullDateFragment
        mSelfPullDateFragment?.let {
            var area = if(!TextUtils.isEmpty(city)) city else ""
            type = showDateTypes.get(pageSelected).type
            var dateType = if(type==0){//type == 6||
                ""
            }else{
                type.toString()
            }
            it.refresh(area ,dateType,mSelectedSex)
        }
    }


    private val locationClient by lazy {
        AMapLocationClient(this)
    }

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }


    private fun checkLocation(){
        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
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

    override fun onDestroy() {
        super.onDestroy()
        mFragments.clear()
        Fresco.getImagePipeline().clearMemoryCaches()
        immersionBar.destroy()
    }
}
