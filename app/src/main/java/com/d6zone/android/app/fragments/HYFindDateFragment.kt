package com.d6zone.android.app.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.d6zone.android.app.R
import com.d6zone.android.app.activities.*
import com.d6zone.android.app.adapters.DateCardAdapter
import com.d6zone.android.app.adapters.DateWomanCardAdapter
import com.d6zone.android.app.adapters.FindDateCardAdapter
import com.d6zone.android.app.base.BaseFragment
import com.d6zone.android.app.base.adapters.BaseRecyclerAdapter
import com.d6zone.android.app.dialogs.*
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.*
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.*
import com.d6zone.android.app.utils.Const.User.IS_FIRST_SHOW_FINDLASTDAYNOTICEDIALOG
import com.d6zone.android.app.utils.Const.User.USER_ADDRESS
import com.d6zone.android.app.utils.Const.User.USER_PROVINCE
import com.d6zone.android.app.widget.diskcache.DiskFileUtils
import com.d6zone.android.app.widget.gallery.DSVOrientation
import com.d6zone.android.app.widget.gallery.transform.ScaleTransformer
import com.d6zone.android.app.widget.gift.GiftControl
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_hyfinddate.*
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import org.jetbrains.anko.support.v4.startActivity
import java.io.InputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * 约会
 */
class HYFindDateFragment : BaseFragment(), BaseRecyclerAdapter.OnItemClickListener {

    override fun onItemClick(view: View?, position: Int) {
        if (view?.id == R.id.cardView||view?.id==R.id.rl_small_mendate_layout||view?.id==R.id.imageViewbg||view?.id==R.id.rl_big_mendate_layout) {
            startActivity<FindDateActivity>()
        }
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy {
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private val heardPic by lazy {
        SPUtils.instance().getString(Const.User.USER_HEAD)
    }

    private var mTotalPages = 3

    private val locationClient by lazy {
        AMapLocationClient(activity)
    }


    private val lastTime by lazy {
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND+getLocalUserId())
    }

    private val cityJson by lazy {
        DiskFileUtils.getDiskLruCacheHelper(context).getAsString(Const.PROVINCE_DATAOFFIND+getLocalUserId())
    }

    private var IsNotFastClick: Boolean = false
    private var pageNum = 1
    private var DANMU_pageNum = 1
    private var mDates = ArrayList<FindDate>()
    private var scrollPosition = 0
    private var province = Province(Const.LOCATIONCITYCODE, "不限/定位")
    //礼物
    private var giftControl: GiftControl? = null

    override fun contentViewId() = R.layout.fragment_hyfinddate

    override fun onFirstVisibleToUser() {
        mRecyclerView.setOrientation(DSVOrientation.HORIZONTAL)
        setAdapter()
        mRecyclerView.setSlideOnFling(false)
        mRecyclerView.setItemTransitionTimeMillis(150)
        mRecyclerView.setItemTransformer(ScaleTransformer.Builder()
                .setMinScale(1.0f)
                .setMaxScale(1.0f)
                .build())

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrollPosition = mRecyclerView.currentItem + 1
                }
            }
        })

        setLoadingShow()
        getData(city, userclassesid, agemin, agemax)
        checkLocation()

        mPopupAges = AgeSelectedPopup.create(activity)
                .setDimView(rl_date)
                .apply()

        mPopupConstellation = ConstellationSelectedPopup.create(activity)
                .setDimView(rl_date)
                .apply()

        mPopupArea = AreaSelectedPopup.create(activity)
                .setDimView(rl_date)
                .apply()

        getProvinceData()

        loading_headView.setImageURI(heardPic)

    }

    private var mDanmakuContext: DanmakuContext? = null
    private var mParser: BaseDanmakuParser? = null

    private fun createParser(stream: InputStream?): BaseDanmakuParser {
        return object : BaseDanmakuParser() {

            override fun parse(): Danmakus {
                return Danmakus()
            }
        }
    }

    fun refresh() {
//        showDialog()
        pageNum = 1
        if (pageNum == 1) {
            mDates.clear()
        }
        setLoadingShow()
        getData(city, userclassesid, agemin, agemax)
    }

    fun setAdapter() {
        if (TextUtils.equals(sex, "0")) {
            mRecyclerView.adapter = FindDateCardAdapter(mDates)
        } else {
            mRecyclerView.adapter = DateWomanCardAdapter(mDates)
        }
        (mRecyclerView.adapter as BaseRecyclerAdapter<*>).setOnItemClickListener(this)
    }

    private fun checkLocation() {
        RxPermissions(activity).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                startLocation()
                SPUtils.instance().put(Const.User.ISNOTLOCATION,false).apply()
            }else{
                SPUtils.instance().put(Const.User.ISNOTLOCATION,true).apply()
            }
        }

        locationClient.setLocationListener {
            if (it != null) {
                locationClient.stopLocation()
                mLat = it.latitude.toString()
                mLon = it.longitude.toString()
                SPUtils.instance().put(USER_PROVINCE, it.province).apply()
                SPUtils.instance().put(USER_ADDRESS, it.city).apply() //it.city
                getUserLocation(it.city,it.province,it.country,"${it.latitude}","${it.longitude}")
            }
        }
    }

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }

    /**
     * 经纬度提交给服务端
     */
    private fun getUserLocation(city:String,sProvince:String,sCountry:String,lat:String,lon:String){
        Request.updateUserPosition(getLocalUserId(),sProvince,sCountry,city,lat,lon).request(this,false,success={_,data->
        })
    }


    private fun setLoadingShow(){
        rl_loading.visibility = View.VISIBLE
        mRecyclerView.visibility = View.GONE
        find_waveview.start()
    }
    /**
     * 搜索
     */
    fun getData(city: String = "", userclassesid: String = "", agemin: String = "", agemax: String = "", lat: String = "", lon: String = "") {
        if (mDates.size == 0) {
            tv_main_card_bg_im_id.gone()
            tv_main_card_Bg_tv_id.gone()
            if (TextUtils.equals(sex, "1")) {
                mReceiveLoveHearts.clear()
            }
        }
        Request.findAccountCardListPage(userId, city, "", userclassesid, agemin, agemax, lat, lon, pageNum).request(this) { _, data ->
                rl_loading.visibility = View.GONE
                find_waveview.stop()
                mRecyclerView.visibility = View.VISIBLE
//                mTotalPages = data?.list?.totalPage!!
                Log.i("DateFragment", "${data?.list?.totalPage}---${pageNum}")
                if (data?.list?.results == null || data.list.results.isEmpty()) {
                    if (pageNum == 1) {
                        mRecyclerView.visibility = View.GONE
//                    tv_tip.gone()
                        tv_main_card_bg_im_id.visible()
                        tv_main_card_Bg_tv_id.visible()
                    } else {
                        mRecyclerView.visibility = View.VISIBLE
                        tv_main_card_bg_im_id.gone()
                        tv_main_card_Bg_tv_id.gone()

                    }
                } else {
                    mRecyclerView.visibility = View.VISIBLE
                    tv_main_card_bg_im_id.gone()
                    tv_main_card_Bg_tv_id.gone()
                    if (pageNum == 1) {
                        mDates.clear()
                    }

                    data.list.results?.let {
                        mDates.addAll(it.subList(0,3))
                        var h = LinkedHashSet<FindDate>(mDates)
                        mDates.clear()
                        mDates.addAll(h.toList())
                    }
//                    if (pageNum == 1) {
//                        var findDate = mDates.get(0)
//                        if (TextUtils.equals(sex, "1")) {
////                            getFindReceiveLoveHeart(findDate.accountId.toString())
//                        }
//                    }
                    mRecyclerView.adapter.notifyDataSetChanged()
                    if(pageNum!=1){
                        try{
                            scrollPosition = mRecyclerView.currentItem + 1
                            if (mRecyclerView.layoutManager.itemCount > scrollPosition) {
                                mRecyclerView.postDelayed(object : Runnable {
                                    override fun run() {
                                        mRecyclerView.smoothScrollToPosition(scrollPosition)
                                    }
                                }, 200)
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
    }

    fun doNextCard() {
        scrollPosition = mRecyclerView.currentItem + 1
        if (mDates.isNotEmpty() && (mDates.size - scrollPosition) >= 0) {
            if ((mDates.size - scrollPosition) ==0) {
                pageNum++
                setLoadingShow()
                getData(city, userclassesid, agemin, agemax)
            }else{
                mRecyclerView.smoothScrollToPosition(scrollPosition)
            }
        }
    }

    fun showNotice(){
        Request.getUserInfo("", getLocalUserId()).request(this,false,success = { _, data ->
            data?.let {
                if(getOneDay(SPUtils.instance().getLong(IS_FIRST_SHOW_FINDLASTDAYNOTICEDIALOG + getLocalUserId(), System.currentTimeMillis()))){
                    if(it.iLastDayExposureCount>0){
//                        iv_mycade_newnotice.visibility = View.VISIBLE
                    }else{
//                        iv_mycade_newnotice.visibility = View.GONE
                    }
                }else{
//                    iv_mycade_newnotice.visibility = View.GONE
                }
            }
        })
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
            immersionBar.navigationBarColor("#FFFFFF")
        }
    }

    override fun onResume() {
        super.onResume()
        showNotice()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
        locationClient.onDestroy()
    }

    private val mImages = ArrayList<AddImage>()
    private var mUserInfoData: UserData? = null

    private fun setFindDate(mFindDate: FindDate, it: UserData) {
        mFindDate.name = it.name.toString()
        mFindDate.classesname = it.classesname.toString()
        mFindDate.nianling = it.age.toString()
        mFindDate.sex = it.sex.toString()
        mFindDate.gexingqianming = it.intro.toString()
        mFindDate.picUrl = it.picUrl.toString()
        mFindDate.userpics = it.userpics.toString()
        mFindDate.shengao = it.height.toString()
        mFindDate.tizhong = it.weight.toString()
        mFindDate.xingzuo = it.constellation.toString()
        mFindDate.city = it.city.toString()
        mFindDate.zhiye = it.job.toString()
        mFindDate.zuojia = it.zuojia.toString()
        mFindDate.xingquaihao = it.hobbit.toString()
        mFindDate.iVistorCountAll = it.iVistorCountAll
        mFindDate.iFansCountAll = it.iFansCountAll
        mFindDate.sOnlineMsg = it.sOnlineMsg
        mFindDate.iOnline = it.iOnline
        mFindDate.sPosition = "${it.area}"
        mImages.clear()
        if (!it.userpics.isNullOrEmpty()) {
            val images = it.userpics!!.split(",")
            images.forEach {
                mImages.add(AddImage(it))
            }
        }
        mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
    }

    private fun getProvinceData() {
        try{
            if (!TextUtils.isEmpty(cityJson)) {
                getServiceProvinceData()
            } else {
                if (!TextUtils.equals(getTodayTime(), lastTime)) {
                    getServiceProvinceData()
                } else {
                    var ProvinceData: MutableList<Province>? = GsonHelper.jsonToList(cityJson, Province::class.java)
                    setLocationCity()
                    ProvinceData?.add(0, province)
                    mPopupArea.setData(ProvinceData)
                }
            }
        }catch(e:Exception){
            e.printStackTrace()
            getServiceProvinceData()
        }
    }

    private fun getServiceProvinceData() {
        Request.getProvinceAll("1").request(this) { _, data ->
            data?.let {
                DiskFileUtils.getDiskLruCacheHelper(context).put(Const.PROVINCE_DATAOFFIND+getLocalUserId(), GsonHelper.getGson().toJson(it))
                SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND+getLocalUserId(), getTodayTime()).apply()
                setLocationCity()
                it.add(0, province)
                mPopupArea.setData(it)
            }
        }
    }

    //设置定位城市
    private fun setLocationCity() {
        var sameCity = SPUtils.instance().getString(USER_PROVINCE)
        var city = City("", getReplace(sameCity))
        city.isSelected = true
        province.lstDicts.add(city)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.DOUPDATEUSERINFOCODE && resultCode == Activity.RESULT_OK) {
            var bundle = data?.extras
            mUserInfoData = (bundle?.getSerializable("userinfo") as? UserData)
            mUserInfoData?.let {
                if (TextUtils.equals(sex, "0")) {
                    (mRecyclerView.adapter as DateCardAdapter).iDateComlete = it.iDatacompletion
                } else {
                    (mRecyclerView.adapter as DateWomanCardAdapter).iDateComlete = it.iDatacompletion
                }
                setFindDate(mDates.get(4), it)
            }
            mRecyclerView.adapter.notifyItemChanged(4)
            SPUtils.instance().put(Const.USERINFO, GsonHelper.getGson().toJson(mUserInfoData)).apply()
        }
    }

    lateinit var mPopupAges: AgeSelectedPopup
    lateinit var mPopupConstellation: ConstellationSelectedPopup
    lateinit var mPopupArea: AreaSelectedPopup
    var userclassesid: String = ""
    var agemin: String = ""
    var agemax: String = ""
    var city: String = ""
    var mLat: String = ""
    var mLon: String = ""

    private var mReceiveLoveHearts = ArrayList<LoveHeartFans>()

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                HYFindDateFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}