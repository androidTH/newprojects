package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.utils.Const
import com.d6.android.app.widget.test.CategoryBean
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor

/**
 *
 */
class SelectCityQuickAdapter constructor(data: List<CategoryBean.DataBean.DataListBean>?, categoryType: String) : BaseQuickAdapter<CategoryBean.DataBean.DataListBean, BaseViewHolder>(R.layout.item_area_menu_right_subclass, data) {

    var selectPosition = 0
    var typeName:String=""
    init {
        typeName = categoryType
    }

    override fun convert(helper: BaseViewHolder, item: CategoryBean.DataBean.DataListBean) {
        val tv_city_name = helper.getView<TextView>(R.id.tv_city_name)
        tv_city_name.text = item.title
        if (TextUtils.equals(typeName+item.title, Const.selectCategoryType)) {
            tv_city_name.textColor = ContextCompat.getColor(mContext, R.color.white)
            tv_city_name.backgroundDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_orange_city)
        } else {
            tv_city_name.textColor = ContextCompat.getColor(mContext, R.color.color_333333)
            tv_city_name.backgroundDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_f5_city)
        }
        tv_city_name.setTag(typeName+item.title)
        helper.addOnClickListener(R.id.tv_city_name)
//        if(TextUtils.equals(getItem(selectPosition)?.title,item.title)){
//            tv_city_name.textColor = ContextCompat.getColor(mContext, R.color.white)
//            tv_city_name.backgroundDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_orange_city)
//        }else{
//            tv_city_name.textColor = ContextCompat.getColor(mContext, R.color.color_333333)
//            tv_city_name.backgroundDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_f5_city)
//        }
//        tv_city_name.textColor = ContextCompat.getColor(mContext, R.color.color_333333)
//        tv_city_name.backgroundDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_f5_city)
    }
}
