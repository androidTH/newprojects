package com.d6.android.app.adapters

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.utils.Const
import com.d6.android.app.widget.test.CategoryBean

/**
 * jinjiarui
 */
class CityOfProvinceAdapter(data: List<CategoryBean.DataBean>) : BaseQuickAdapter<CategoryBean.DataBean, BaseViewHolder>(R.layout.item_area_menu_right, data) {

    override fun convert(helper: BaseViewHolder, data:CategoryBean.DataBean) {
         var item_menu_title = helper.getView<TextView>(R.id.tv_menu_title)
         var rv_menu_right = helper.getView<RecyclerView>(R.id.rv_menu_right)
         rv_menu_right.setHasFixedSize(true)
         rv_menu_right.layoutManager = GridLayoutManager(mContext, 3)

         var mSelectCityQuickAdapter = SelectCityQuickAdapter(data.dataList,data.moduleTitle)
         rv_menu_right.adapter = mSelectCityQuickAdapter
         item_menu_title.text = data.moduleTitle

//         mSelectCityQuickAdapter.setOnItemClickListener { adapter, view, position ->
//              if(mOnSelected !=null){
//                  mSelectCityQuickAdapter.selectPosition = position
//                  mOnSelected.onSelectedCityListener(position, mSelectCityQuickAdapter.data.get(position).title)
//              }
//         }

        mSelectCityQuickAdapter.setOnItemChildClickListener { adapter, view, position ->
            if(view.id == R.id.tv_city_name){
                if(mOnSelected !=null){
//                    mSelectCityQuickAdapter.selectPosition = position
                    Const.selectCategoryType = view.getTag().toString()
                    mOnSelected.onSelectedCityListener(position, mSelectCityQuickAdapter.data.get(position).title)
                    mSelectCityQuickAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private lateinit var mOnSelected:onSelectCityOfProvinceListenerInterface;

    public fun setOnSelectCityOfProvince(onSelected:onSelectCityOfProvinceListenerInterface){
        this.mOnSelected = onSelected
    }

    interface onSelectCityOfProvinceListenerInterface{
        fun onSelectedCityListener(pos:Int,name:String)
    }
}
