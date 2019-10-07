package com.d6.android.app.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.widget.TextView
import com.amap.api.location.AMapLocationClient
import com.chad.library.adapter.base.BaseQuickAdapter
import com.d6.android.app.R
import com.d6.android.app.adapters.CityOfProvinceAdapter
import com.d6.android.app.adapters.ProvinceAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.City
import com.d6.android.app.models.Province
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.PROVINCE_DATA
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_area_choose_layout.*

class AreaChooseActivity : BaseActivity() {

//    https://github.com/kongzue/StackLabel

    private var mCities = ArrayList<Province>()
    private var mProvinces = ArrayList<Province>()
//    private var mHomeList = ArrayList<CategoryBean.DataBean>()
    private var currentItem: Int = 0

    private val locationCity= getReplace(SPUtils.instance().getString(USER_ADDRESS))

    var province = Province(Const.LOCATIONCITYCODE, "定位")

    private val lastTime by lazy{
        SPUtils.instance().getString(Const.LASTLONGTIMEOFProvince)
    }

    private val mCityOfProviceAdapter by lazy {
        CityOfProvinceAdapter(mCities)
    }

    private val mProciceAdapter by lazy {
        ProvinceAdapter(mProvinces)
    }

    private val cityJson by lazy{
        DiskFileUtils.getDiskLruCacheHelper(this).getAsString(Const.PROVINCE_DATA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_choose_layout)
        immersionBar.fitsSystemWindows(true).init()

        tv_back.setOnClickListener {
            onBackPressed()
        }

        rv_menu.setHasFixedSize(true)
        rv_menu.layoutManager = LinearLayoutManager(this)
        rv_menu.adapter = mProciceAdapter

        rv_menu_right.setHasFixedSize(true)
        rv_menu_right.layoutManager = LinearLayoutManager(this)
        rv_menu_right.adapter = mCityOfProviceAdapter

        mProciceAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.item_name) {
                mProciceAdapter.selectItem = position
                tv_menu_toptitle.text = mProvinces.get(position).name
                mProciceAdapter.notifyDataSetChanged()
                (rv_menu_right.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
            }
        }

        rv_menu_right.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentPos = (rv_menu_right.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (currentPos != currentItem && currentPos >= 0) {
                    currentItem = currentPos
                    mProciceAdapter.selectItem = currentItem
                    tv_menu_toptitle.text = mProvinces.get(currentPos).name
                    mProciceAdapter.notifyDataSetChanged()
                    rv_menu.getLayoutManager().scrollToPosition(currentPos)
                }
            }
        })

        mCityOfProviceAdapter.setOnItemChildClickListener(BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.tv_arealocation) {
                var mTvLocation = (view as TextView);
                if(TextUtils.equals(mTvLocation.tag.toString(),Const.LOCATIONFAIL)){
                    checkLocation()
                }else{
                    var intent = Intent()
                    intent.putExtra("area", (view as TextView).text.toString())
                    setResult(RESULT_OK, intent)
                    onBackPressed()
                }
            }
        })

        mCityOfProviceAdapter.setOnSelectCityOfProvince(object : CityOfProvinceAdapter.onSelectCityOfProvinceListenerInterface {
            override fun onSelectedCityListener(pos: Int, name: String) {
                mCityOfProviceAdapter.notifyDataSetChanged()
                var intent = Intent()
                intent.putExtra("area", name)
                setResult(RESULT_OK, intent)
                onBackPressed()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        if (cityJson.isNullOrEmpty()) {
            getServiceProvinceData()
        } else {
            if (TextUtils.equals(getTodayTime(), lastTime)) {
                getServiceProvinceData()
            } else {
                var data: MutableList<Province>? = GsonHelper.jsonToList(cityJson,Province::class.java)
                mProvinces.clear()
                setLocationCity(locationCity)
                data?.let {
                    it.add(0, province)
                    mProvinces.addAll(it)
                    mCities.addAll(it)
                    mProciceAdapter.setNewData(mProvinces)
                    mCityOfProviceAdapter.setNewData(mCities)
                    tv_menu_toptitle.text = mProvinces.get(0).name
                }
            }
        }
    }

    private fun getServiceProvinceData(){
        Request.getProvince().request(this) { _, data ->
            data?.let {
                DiskFileUtils.getDiskLruCacheHelper(this).put(PROVINCE_DATA, GsonHelper.getGson().toJson(it))
                mProvinces.clear()
                setLocationCity(locationCity)
                it.add(0,province)
                mProvinces.addAll(it)
                mCities.addAll(it)
                mProciceAdapter.setNewData(mProvinces)
                mCityOfProviceAdapter.setNewData(mCities)
                tv_menu_toptitle.text = mProvinces.get(0).name
                SPUtils.instance().put(Const.LASTLONGTIMEOFProvince, getTodayTime()).apply()
            }
        }
    }

    //设置定位城市
    private fun setLocationCity(city:String){
        var city = City("",city)
        city.isSelected = true
        city.isValid ="2"
        province.lstDicts.add(city)
    }

    private val locationClient by lazy {
        AMapLocationClient(applicationContext)
    }

    private fun checkLocation(){
        RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe {
            if (it) {
                locationClient.setLocationListener {
                    if (it != null) {
                        setLocationCity(getReplace(it.city))
                        locationClient.stopLocation()
                        mCityOfProviceAdapter.notifyItemChanged(0)
                        SPUtils.instance().put(USER_ADDRESS,it.city).apply() //it.city
                        SPUtils.instance().put(USER_PROVINCE,it.province).apply()
                    }
                }
                startLocation()
            }
        }
    }

    private fun startLocation() {
        locationClient.stopLocation()
        locationClient.startLocation()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.dd_menu_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(locationClient!=null){
            locationClient.onDestroy()
        }
        immersionBar.destroy()
    }
}
