package com.d6.android.app.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.d6.android.app.R
import com.d6.android.app.adapters.CityOfProvinceAdapter
import com.d6.android.app.adapters.ProvinceAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.City
import com.d6.android.app.models.Province
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.GsonHelper
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.widget.diskcache.DiskFileUtils
import kotlinx.android.synthetic.main.activity_area_choose_layout.*

class AreaChooseActivity : BaseActivity() {

    private var mCities = ArrayList<Province>()
    private var mProvinces = ArrayList<Province>()
//    private var mHomeList = ArrayList<CategoryBean.DataBean>()
    private var currentItem: Int = 0

    private val locationCity by lazy{
        SPUtils.instance().getString(USER_ADDRESS)
    }

    var province = Province(Const.LOCATIONCITYCODE, "定位")

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
                    mProciceAdapter.notifyDataSetChanged()
                    rv_menu.getLayoutManager().scrollToPosition(currentPos)
                }
            }
        })

        mCityOfProviceAdapter.setOnItemChildClickListener(BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.tv_arealocation) {
                var intent = Intent()
                intent.putExtra("area", (view as TextView).text.toString())
                setResult(RESULT_OK, intent)
                onBackPressed()
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
        if(!TextUtils.isEmpty(cityJson)){
            var data: MutableList<Province>? = GsonHelper.jsonToList(cityJson,Province::class.java)
            mProvinces.clear()
//            setLocationCity()
            data?.let {
//                it.add(0, province)
                mProvinces.addAll(it)
                mCities.addAll(it)
                mProciceAdapter.setNewData(mProvinces)
                mCityOfProviceAdapter.setNewData(mCities)
            }
        }else{
            Request.getProvince().request(this) { _, data ->
                data?.let {
                    DiskFileUtils.getDiskLruCacheHelper(this).put(Const.PROVINCE_DATA, GsonHelper.getGson().toJson(it))
                    mProvinces.clear()
//                    setLocationCity()
//                    it.add(0,province)
                    mProvinces.addAll(it)
                    mCities.addAll(it)
                    mProciceAdapter.setNewData(mProvinces)
                    mCityOfProviceAdapter.setNewData(mCities)
                }
            }
        }
    }

    //设置定位城市
    private fun setLocationCity(){
        var city = City("",locationCity)
        city.isSelected = true
        province.lstDicts.add(city)
    }

    fun loadData() {
//        val json = ConvertUtils.toString(getAssets().open("province.json"))
//        val categoryBean = GsonHelper.GsonToBean(json, CategoryBean::class.java)
//        for (i in 0 until categoryBean.data.size) {
//            val dataBean = categoryBean.data.get(i)
//               mProvinces.add(dataBean.moduleTitle)
//            mShowTitles.add(i)
//            mHomeList.add(dataBean)
//        }
//        mProciceAdapter.setNewData(mCities)
//        mCityOfProviceAdapter.setNewData(mHomeList)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.dd_menu_out);
    }
}
