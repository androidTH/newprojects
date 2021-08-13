package com.d6.android.app.fragments

import android.Manifest
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.*
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.*
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.LOGIN_FOR_POINT_NEW
import com.d6.android.app.utils.Const.User.ISNOTLOCATION
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.holder.Holder
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.subscribers.DisposableSubscriber
import kotlinx.android.synthetic.main.fragment_homefind.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.startActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.toList

/**
 * 主页
 */
class HomeFindFragment : BaseFragment(){

    private var mTAG = HomeFindFragment::class.java.simpleName

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val devicetoken by lazy{
        SPUtils.instance().getString(Const.User.DEVICETOKEN)
    }

    private var mDefualtSex = -1
    var province = Province(Const.LOCATIONCITYCODE, "不限/定位")

    private val cityJson by lazy{
        DiskFileUtils.getDiskLruCacheHelper(context).getAsString(Const.PROVINCE_DATAOFFIND)
    }

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTTIMEOFPROVINCEINFIND)
    }

    private lateinit var mFindDateInfo:FindDateInfo

    override fun contentViewId() = R.layout.fragment_homefind

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mSwipeRefreshLayout.isRefreshing = false

        if (TextUtils.equals("0", getUserSex())) {
            mDefualtSex = 1
        } else {
            mDefualtSex = 0
        }

        rl_rgservice.setOnClickListener {
            startActivity<RecommendDateActivity>()
        }

        rl_datepage.setOnClickListener {
            startActivity<InviteDateActivity>()
        }

        rl_bangdanpage.setOnClickListener {
            startActivity<D6LoveHeartListActivity>()
        }

        rl_groupchat.setOnClickListener {
            startActivity<FindGroupListActivity>()
        }

        mSwipeRefreshLayout.setOnRefreshListener {
            mSwipeRefreshLayout.isRefreshing = false
            getPeoples()
        }

        iv_publish.setOnClickListener {
            activity.isAuthUser() {
                startActivity<PublishChooseActivity>()
            }
        }

        viewpagerbanner.postDelayed(object : Runnable {
            override fun run() {
                getProvinceData()
            }
        }, 200)

        loginforPoint()
        checkLocation()

        getPeoples()
        getData()
    }

    private fun initBanner(){
        if (TextUtils.equals(getUserSex(), "0")) {
            viewpagerbanner.setPages(
                    object : CBViewHolderCreator {
                        override fun createHolder(itemView: View): Holder<FindDate> {
                            return FindDateMenCardHolder(itemView, context)
                        }

                        override fun getLayoutId(): Int {
                            return R.layout.item_finddatecard
                        }
                    }, mDates)
        } else {
            viewpagerbanner.setPages(
                    object : CBViewHolderCreator {
                        override fun createHolder(itemView: View): Holder<FindDate> {
                            return FindDateWoMenCardHolder(itemView, context)
                        }

                        override fun getLayoutId(): Int {
                            return R.layout.item_finddate_womencard
                        }
                    }, mDates)
        }

        viewpagerbanner.setOnItemClickListener {
            startActivity<FindDateActivity>()
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
                SPUtils.instance().put(ISNOTLOCATION, false).apply()
            }else{
                SPUtils.instance().put(ISNOTLOCATION, true).apply()
            }
        }

        locationClient.setLocationListener {
            if (it != null) {
                locationClient.stopLocation()
                SPUtils.instance().put(USER_ADDRESS, it.city).apply() //it.city
                SPUtils.instance().put(USER_PROVINCE, it.province).apply()
                getUserLocation(it.city, it.province, it.country, "${it.latitude}", "${it.longitude}")
            }
        }
    }

    /**
     * 经纬度提交给服务端
     */
    private fun getUserLocation(city: String, sProvince: String, sCountry: String, lat: String, lon: String){
        Request.updateUserPosition(getLocalUserId(), sProvince, sCountry, city, lat, lon).request(this, false, success = { _, data ->
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
                    ProvinceData!!.add(0, province)
//                    mPopupArea.setData(ProvinceData)
                }
            }
        }catch (e: Exception){
            getServiceProvinceData()
        }
    }

    private fun getServiceProvinceData(){
        Request.getProvinceAll("1").request(this) { _, data ->
            data?.let {
                DiskFileUtils.getDiskLruCacheHelper(context).put(Const.PROVINCE_DATAOFFIND, GsonHelper.getGson().toJson(it))
                SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND, getTodayTime()).apply()
                setLocationCity()
                it.add(0, province)
            }
        }
    }

    fun refresh(sex: String, pageSelected: Int){
        mSwipeRefreshLayout.isEnabled = true
        mSwipeRefreshLayout.isRefreshing = true
    }

    //设置定位城市
    private fun setLocationCity(){
        var sameCity = SPUtils.instance().getString(USER_PROVINCE)
        var city = City("", getReplace(sameCity))
        city.isSelected = true
        province.lstDicts.add(city)
    }

    private fun loginforPoint(){
        Request.loginForPoint(getLoginToken(), userId, devicetoken).request(this, false, success = { msg, data ->
            if (data != null) {
                var sLoginToken = data.optString("sLoginToken")
                var lstTask = GsonHelper.jsonToList(data.optJsonArray("lstTask"), TaskBean::class.java)
                SPUtils.instance().put(Const.User.SLOGINTOKEN, sLoginToken).apply()
                if (lstTask != null && lstTask.size > 0) {
                    SPUtils.instance().put(Const.LASTDAYTIME, "").apply()
//                    SPUtils.instance().put(Const.LASTLONGTIMEOFProvince,"").apply()
                    SPUtils.instance().put(Const.LASTTIMEOFPROVINCEINFIND, "").apply()
                    var today = getTodayTime()
                    var yesterday = SPUtils.instance().getString(LOGIN_FOR_POINT_NEW + getLocalUserId(), "")
                    if (!TextUtils.equals(today, yesterday)) {
                        var mCheckInPointsDialog = CheckInPointsDialog()
                        mCheckInPointsDialog.arguments = bundleOf("beans" to lstTask)
                        mCheckInPointsDialog.show(childFragmentManager, "rewardtips")
                        mCheckInPointsDialog.setDialogListener { p, s ->
                            mCheckInPointsDialog.dismissAllowingStateLoss()
                        }
                        SPUtils.instance().put(LOGIN_FOR_POINT_NEW + getLocalUserId(), getTodayTime()).apply()
                    }
                }
            }
        })
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
        }
    }

    private fun getPeoples(){
        Request.findAppointmentList(userId, "", "", "${mDefualtSex}", 1).request(this) { _, data ->
            tv_datecount.text = "已有${data?.iAllAppointCount}人邀约成功"
        }
        getLatestNews()
    }


    private fun getLatestNews(){
        Request.queryLatestNews().request(this){ _, data->
            data?.let {
//                var rongGroup_name = it.optString("rongGroup_name")
                mFindDateInfo = it
                if(mFindDateInfo!=null){
                    Log.i("topInfo", "信息：${it.rongGroup_name}")
                    sv_finddate01.setImageURI(it.lookabout_picurl)

                    sv_finddate02.setImageURI("${it.appointment_picurl}")
                    tv_02.text = "发布了约会"

                    sv_finddate03.setImageURI(it.userpoint_picUrl)
                    tv_03.text = "收到${mFindDateInfo.userpoint_allLovePoint} [img src=redheart_small/]"
                    hideTopInfoFindDate()
                }
            }
        }

    }

    private fun hideTopInfoFindDate(){
        var annotation = AnimationUtils.loadAnimation(context, R.anim.hide_anim)
        var annotation1 = AnimationUtils.loadAnimation(context, R.anim.hide_anim)
        var annotation2 = AnimationUtils.loadAnimation(context, R.anim.hide_anim)
        var annotation3 = AnimationUtils.loadAnimation(context, R.anim.hide_anim)
        annotation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationEnd(animation: Animation?) {
                sv_finddate01.visibility = View.VISIBLE
                tv_01.text = "觅约"
                showTopInfoFindDate()
            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        ll_finddate01.startAnimation(annotation)

        annotation1.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationEnd(animation: Animation?) {
                ll_finddate02.visibility = View.VISIBLE
                var annotation1 = AnimationUtils.loadAnimation(context, R.anim.show_anim)
                ll_finddate02.startAnimation(annotation1)
            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        tv_datecount.startAnimation(annotation1)

        annotation2.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationEnd(animation: Animation?) {
                ll_finddate03.visibility = View.VISIBLE
                var annotation2 = AnimationUtils.loadAnimation(context, R.anim.show_anim)
                ll_finddate03.startAnimation(annotation2)
            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        tv_finddate_tips.startAnimation(annotation2)

        annotation3.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationEnd(animation: Animation?) {
                tv_finddate_02.text = "新人报道群(${mFindDateInfo.rongGroup_count}）"
                var annotation3 = AnimationUtils.loadAnimation(context, R.anim.show_anim)
                tv_finddate_02.startAnimation(annotation3)
            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        tv_finddate_02.startAnimation(annotation3)
    }

    private fun showTopInfoFindDate(){
        var annotation = AnimationUtils.loadAnimation(context, R.anim.show_anim)
        ll_finddate01.startAnimation(annotation)
    }

    private var mDates = ArrayList<FindDate>()
    private var pageNum = 1
    /**
     * 搜索
     */
    fun getData(city: String = "", userclassesid: String = "", agemin: String = "", agemax: String = "", lat: String = "", lon: String = "") {
        Request.findAccountCardListPage(userId, city, "", userclassesid, agemin, agemax, lat, lon, pageNum).request(this) { _, data ->
            Log.i("DateFragment", "${data?.list?.totalPage}---${pageNum}")
            if (data?.list?.results == null || data.list.results.isEmpty()) {

            } else {
                if (pageNum == 1) {
                    mDates.clear()
                }
                data.list.results?.let {
                    mDates.addAll(it.subList(0, 3))
                    var h = LinkedHashSet<FindDate>(mDates)
                    mDates.clear()
                    mDates.addAll(h.toList())
                }
                initBanner()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewpagerbanner.startTurning()
    }

    override fun onPause() {
        super.onPause()
        viewpagerbanner.stopTurning()
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                HomeFindFragment().apply {
                    arguments = Bundle().apply {
//                        putParcelable(ARG_PARAM1,param1)
                    }
                }
    }
}