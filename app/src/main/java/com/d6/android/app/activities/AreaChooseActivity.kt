package com.d6.android.app.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.d6.android.app.R
import com.d6.android.app.adapters.CityOfProvinceAdapter
import com.d6.android.app.adapters.ProvinceAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.City
import com.d6.android.app.net.Request
import com.d6.android.app.utils.GsonHelper
import com.d6.android.app.widget.test.CategoryBean
import com.d6.android.app.widget.test.ConvertUtils
import kotlinx.android.synthetic.main.activity_area_choose_layout.*

class AreaChooseActivity : BaseActivity() {

    private var mCities = ArrayList<String>()
    private var mHotCities = ArrayList<City>()
    private var mHomeList = ArrayList<CategoryBean.DataBean>()
    private var mShowTitles = ArrayList<Int>()
    private var currentItem: Int = 0

    private val mCityOfProviceAdapter by lazy {
        CityOfProvinceAdapter(mHomeList)
    }

    private val mProciceAdapter by lazy {
        ProvinceAdapter(mCities)
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

//        getData("0")

        loadData()

        mProciceAdapter.setOnItemChildClickListener { adapter, view, position ->
            if(view.id == R.id.item_name){
               mProciceAdapter.selectItem = position
               mProciceAdapter.notifyDataSetChanged()
                (rv_menu_right.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(mShowTitles.get(position),0)
            }
        }

        rv_menu_right.addOnScrollListener(object: RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentPos = (rv_menu_right.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (currentPos != currentItem && currentPos >= 0) {
                    currentItem = currentPos
                    mProciceAdapter.selectItem = currentItem
                    mProciceAdapter.notifyDataSetChanged()
                }
            }
        })

        mCityOfProviceAdapter.setOnSelectCityOfProvince(object : CityOfProvinceAdapter.onSelectCityOfProvinceListenerInterface {
            override fun onSelectedCityListener(pos: Int, name: String) {
                mCityOfProviceAdapter.notifyDataSetChanged()
                var intent = Intent()
                intent.putExtra("area",name)
                setResult(RESULT_OK,intent)
                onBackPressed()
            }
        })
    }

    private fun getData(key:String) {
        Request.getCities(key).request(this) { _, data ->
            mHotCities.clear()
            mCities.clear()
            data?.let {
//                if (type == 1) {//多选情况，也就是筛选时使用
//                    mCities.add(City("","全选"))
//                }
//               dd mCities.addAll(it)
//                dd mCities.removeAt(0)
//                it.forEach {
                    //isValid 1 热门地市，0 普通地市
//                    if (TextUtils.equals(it.isValid, "1")) {
//                        mHotCities.add(it)
//                    }
//                }
                mProciceAdapter.setNewData(mCities)
//                cityAdapter.notifyDataSetChanged()
//                hotCityAdapter.notifyDataSetChanged()
//                filter.update(mCities)
            }
        }
    }

    fun loadData(){
            val json = ConvertUtils.toString(getAssets().open("category.json"))
            val categoryBean = GsonHelper.GsonToBean(json, CategoryBean::class.java)
            for (i in 0 until categoryBean.data.size) {
                val dataBean = categoryBean.data.get(i)
                mCities.add(dataBean.moduleTitle)
                mShowTitles.add(i)
                mHomeList.add(dataBean)
            }
        mProciceAdapter.setNewData(mCities)
        mCityOfProviceAdapter.setNewData(mHomeList)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.dd_menu_out);
    }
}
