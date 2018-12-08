package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.City
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

/**
 * jinjiarui
 */
class ProvinceAdapter(data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_menu, data) {

    var selectItem = 0

    override fun convert(helper: BaseViewHolder, data: String) {
         var item_name = helper.getView<TextView>(R.id.item_name)
         var viewLeft = helper.getView<View>(R.id.view_left)
         if(TextUtils.equals(getItem(selectItem),data)){
             item_name.textColor = ContextCompat.getColor(mContext, R.color.color_F7AB00)
             item_name.backgroundColor = ContextCompat.getColor(mContext, R.color.white)
             viewLeft.visibility = View.VISIBLE
         }else{
             item_name.textColor = ContextCompat.getColor(mContext, R.color.color_666666)
             item_name.backgroundColor = ContextCompat.getColor(mContext, R.color.color_F5F5F5)
             viewLeft.visibility = View.GONE
         }
         item_name.text = data
         helper.addOnClickListener(R.id.item_name)
    }
}
