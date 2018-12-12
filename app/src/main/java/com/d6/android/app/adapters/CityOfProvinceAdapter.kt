package com.d6.android.app.adapters

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.Province
import com.d6.android.app.utils.Const

/**
 * jinjiarui
 */
class CityOfProvinceAdapter(data: List<Province>) : BaseQuickAdapter<Province, BaseViewHolder>(R.layout.item_area_menu_right, data) {

    override fun convert(helper: BaseViewHolder, data: Province) {
        var item_menu_title = helper.getView<TextView>(R.id.tv_menu_title)
        var rv_menu_right = helper.getView<RecyclerView>(R.id.rv_menu_right)
        var tv_arealocation = helper.getView<TextView>(R.id.tv_arealocation)
        tv_arealocation.visibility = View.GONE
        if(TextUtils.equals(data.ids,"100010")){
            tv_arealocation.visibility = View.VISIBLE
            if (TextUtils.isEmpty(data.lstDicts.get(0).name)) {
                tv_arealocation.text = mContext.getString(R.string.string_nolocation)
                tv_arealocation.setTag(Const.LOCATIONFAIL)
            } else {
                tv_arealocation.text = data.lstDicts.get(0).name
                tv_arealocation.setTag(Const.LOCATIONSUCCESS)
            }
        }
        item_menu_title.text = data.name
        rv_menu_right.setHasFixedSize(true)
        rv_menu_right.layoutManager = GridLayoutManager(mContext, 3) as RecyclerView.LayoutManager?

        var mSelectCityQuickAdapter = SelectCityQuickAdapter(data.lstDicts, data.name.toString())
        rv_menu_right.adapter = mSelectCityQuickAdapter
        item_menu_title.text = data.name

        mSelectCityQuickAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.tv_city_name) {
                if (mOnSelected != null) {
//                    mSelectCityQuickAdapter.selectPosition = position
                    Const.selectCategoryType = view.getTag().toString()
                    mOnSelected.onSelectedCityListener(position, mSelectCityQuickAdapter.data.get(position).name.toString())
                    mSelectCityQuickAdapter.notifyDataSetChanged()
                }
            }
        }

        helper.addOnClickListener(R.id.tv_arealocation)
    }

    private lateinit var mOnSelected: onSelectCityOfProvinceListenerInterface;

    fun setOnSelectCityOfProvince(onSelected: onSelectCityOfProvinceListenerInterface) {
        this.mOnSelected = onSelected
    }

    interface onSelectCityOfProvinceListenerInterface {
        fun onSelectedCityListener(pos: Int, name: String)
    }
}
