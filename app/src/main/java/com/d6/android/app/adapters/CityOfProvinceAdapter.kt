package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.Province
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getReplace
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor

/**
 * jinjiarui
 */
class CityOfProvinceAdapter(data: List<Province>) : BaseQuickAdapter<Province, BaseViewHolder>(R.layout.item_area_menu_right, data) {

//    private val sameCity by lazy{
//        getReplace(SPUtils.instance().getString(USER_PROVINCE))
//    }

    override fun convert(helper: BaseViewHolder, data: Province) {
        var item_menu_title = helper.getView<TextView>(R.id.tv_menu_title)
        var rv_menu_right = helper.getView<RecyclerView>(R.id.rv_menu_right)
        var ll_noarea = helper.getView<LinearLayout>(R.id.ll_noarea)
        ll_noarea.visibility = View.GONE
        if(TextUtils.equals(data.ids, Const.LOCATIONCITYCODE)){
            ll_noarea.visibility = View.VISIBLE
            var tv_arealocation = helper.getView<TextView>(R.id.tv_arealocation)
            var tv_no_limit_area = helper.getView<TextView>(R.id.tv_no_limit_area)

            if(TextUtils.equals(Const.NO_LIMIT_ERA,Const.selectCategoryType)){
                tv_no_limit_area.textColor= ContextCompat.getColor(mContext, R.color.white)
                tv_no_limit_area.backgroundDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_orange_city)
            }else{
                tv_no_limit_area.textColor = ContextCompat.getColor(mContext, R.color.color_333333)
                tv_no_limit_area.backgroundDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_f5_city)
            }
            var city = data.lstDicts.get(0);
            if(city.isValid != "2"){
                tv_no_limit_area.visibility = View.VISIBLE
            }else{
                tv_no_limit_area.visibility = View.GONE
            }
            tv_no_limit_area.setTag(Const.NO_LIMIT_ERA)

            if (TextUtils.isEmpty(data.lstDicts.get(0).name)) {
                var sameCity = getReplace(SPUtils.instance().getString(USER_PROVINCE))
                if(TextUtils.isEmpty(sameCity)){
                    tv_arealocation.text = mContext.getString(R.string.string_nolocation)
                    tv_arealocation.setTag(Const.LOCATIONFAIL)
                }else{
                    tv_arealocation.text = sameCity
                    tv_arealocation.setTag(Const.LOCATIONSUCCESS)
                }
            } else {
                var loc_city = data.lstDicts.get(0).name
                if(TextUtils.equals(loc_city,"不限地区")){
                    tv_arealocation.visibility = View.GONE
                }else{
                    tv_arealocation.text = data.lstDicts.get(0).name
                    tv_arealocation.setTag(Const.LOCATIONSUCCESS)
                }
            }
            var tv_noarea = helper.getView<TextView>(R.id.tv_noarea)
            if(city.isShowNoArea){
                tv_noarea.visibility = View.VISIBLE
            }else{
                tv_noarea.visibility = View.GONE
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
                    var city = mSelectCityQuickAdapter.data.get(position);
                    var name = if(!city.pParamName.isNullOrEmpty()){
                        city.pParamName
                    }else{
                        city.name
                    }
                    mOnSelected.onSelectedCityListener(position, name.toString())

                    mSelectCityQuickAdapter.notifyDataSetChanged()
                }
            }
        }

        helper.addOnClickListener(R.id.tv_arealocation)
        helper.addOnClickListener(R.id.tv_no_limit_area)
        helper.addOnClickListener(R.id.tv_noarea)
    }

    private lateinit var mOnSelected: onSelectCityOfProvinceListenerInterface;

    fun setOnSelectCityOfProvince(onSelected: onSelectCityOfProvinceListenerInterface) {
        this.mOnSelected = onSelected
    }

    interface onSelectCityOfProvinceListenerInterface {
        fun onSelectedCityListener(pos: Int, name: String)
    }
}
