package com.d6.android.app.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.d6.android.app.R
import com.d6.android.app.adapters.SelectCityAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.City
import com.d6.android.app.net.Request
import com.d6.android.app.utils.CityFilter
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.invisible
import com.d6.android.app.utils.visible
import kotlinx.android.synthetic.main.fragment_select_city.*
import org.jetbrains.anko.collections.forEachWithIndex

/**
 *
 */
class SelectCityFragment : BaseFragment() {

    companion object {

        fun instance(position: Int, type: Int): Fragment {
            val fragment = SelectCityFragment()
            val b = Bundle()
            b.putInt("position", position)
            b.putInt("type", type)
            fragment.arguments = b
            return fragment
        }
    }

    private val mCities = ArrayList<City>()
    private val mHotCities = ArrayList<City>()
    private val hotCityAdapter by lazy {
        SelectCityAdapter(mHotCities)
    }

    private val cityAdapter by lazy {
        SelectCityAdapter(mCities)
    }

    private val filter by lazy {
        CityFilter()
    }

    private val mPosition by lazy {
        arguments.getInt("position")
    }

    private val type by lazy {
        arguments.getInt("type")
    }

    private val selectedCity = ArrayList<City>()

    override fun contentViewId() = R.layout.fragment_select_city

    override fun onFirstVisibleToUser() {
        rv_hot_city.setHasFixedSize(true)
        rv_hot_city.isNestedScrollingEnabled = false
        rv_hot_city.layoutManager = GridLayoutManager(context, 4)
        rv_hot_city.adapter = hotCityAdapter
        rv_city.setHasFixedSize(true)
        rv_city.isNestedScrollingEnabled = false
        rv_city.layoutManager = GridLayoutManager(context, 4)
        rv_city.adapter = cityAdapter

        if (type == 1) {
            tv_action.visible()
        } else {
            tv_action.gone()
        }

        hotCityAdapter.setOnItemClickListener { _, position ->
            val data = mHotCities[position]
            val cityStr = data.name
            val intent = Intent()
            intent.putExtra("type",mPosition)
            intent.putExtra("data",cityStr)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
        }

        cityAdapter.setOnItemClickListener { _, position ->
            val data = mCities[position]
            val isSelected = data.isSelected
            if (type == 1) {//多选
                if (isSelected) {
                    if (TextUtils.equals("全选", data.name)) {
                        selectedCity.clear()
                        mCities.forEach {
                            it.isSelected = false
                        }
                    } else {
                        if (selectedCity.contains(data)) {
                            selectedCity.remove(data)
                        }
                    }
                } else {
                    if (TextUtils.equals("全选", data.name)) {
                        mCities.forEachWithIndex { i, city ->
                            if (i > 0) {
                                city.isSelected = true
                                if (!selectedCity.contains(city)) {
                                    selectedCity.add(city)
                                }
                            }
                        }
                    } else {
                        if (!selectedCity.contains(data)) {
                            selectedCity.add(data)
                        }
                    }
                }
                data.isSelected = !isSelected
                cityAdapter.notifyDataSetChanged()
            } else {//单选
                val cityStr = data.name
                val intent = Intent()
                intent.putExtra("type",mPosition)
                intent.putExtra("data",cityStr)
                activity.setResult(Activity.RESULT_OK,intent)
                activity.finish()
            }
        }

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                search(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        tv_action.setOnClickListener {

            val cityStr = if (selectedCity.isEmpty()) {
                ""
            } else {
                val b = StringBuilder()
                selectedCity.forEach {
                    b.append(it.name).append(",")
                }
                if (b.endsWith(",")) {
                    b.deleteCharAt(b.length-1)
                }
                b.toString()
            }

            val intent = Intent()
            intent.putExtra("type",mPosition)
            intent.putExtra("data",cityStr)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
        }

        filter.setCallBack {
            mCities.clear()
            mCities.addAll(it)
            cityAdapter.notifyDataSetChanged()
            if (mCities.isEmpty()) {
                tv_tip.visible()
            } else {
                tv_tip.invisible()
            }
        }

        showDialog()
        getData()

    }

    private fun getData() {
        val key = if (mPosition == 0) "1" else "0"
        Request.getCities(key).request(this) { _, data ->
            mHotCities.clear()
            mCities.clear()
            data?.let {
                if (type == 1) {//多选情况，也就是筛选时使用
                    mCities.add(City("","全选"))
                }
                mCities.addAll(it)
                mCities.removeAt(0)
                it.forEach {
                    //isValid 1 热门地市，0 普通地市
                    if (TextUtils.equals(it.isValid, "1")) {
                        mHotCities.add(it)
                    }
                }
                cityAdapter.notifyDataSetChanged()
                hotCityAdapter.notifyDataSetChanged()
                filter.update(mCities)
            }
        }
    }

    private fun search(keyword: String) {
        if (keyword.isEmpty()) {
            ll_hot.visible()
        } else {
            ll_hot.gone()
        }
        filter.filter(keyword)
    }
}