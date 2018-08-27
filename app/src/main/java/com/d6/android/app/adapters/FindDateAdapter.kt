package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.MyDate
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.visible
import com.facebook.drawee.view.SimpleDraweeView

/**
 * Created on 2017/12/16.
 */
class FindDateAdapter(mData: ArrayList<MyDate>) : HFRecyclerAdapter<MyDate>(mData, R.layout.item_list_find_date) {
    override fun onBind(holder: ViewHolder, position: Int, data: MyDate) {

        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        imageView.setImageURI(data.lookpics)
        val vipView =holder.bind<View>(R.id.tv_vip)
        if (TextUtils.equals(data.sex, "0")) vipView.gone() else vipView.visible()
        holder.setText(R.id.tv_vip, String.format("%s", data.classesname))
        val tv_name = holder.bind<TextView>(R.id.tv_name)
        tv_name.text = data.looknumber
        tv_name.isSelected = TextUtils.equals(data.sex, "0")
        val s = if (data.zuojia.isNullOrEmpty()) {
            String.format("%s岁  %s  %s", data.age, data.height, data.weight)
        } else {
            String.format("%s岁  %s  %s  %s", data.age, data.height, data.weight,data.zuojia)
        }
        holder.setText(R.id.tv_info, s)
//        val area1 = if (data.handlookwhere.isNullOrEmpty()) "" else data.handlookwhere
//        val area2 = if (data.lookwhere.isNullOrEmpty()) "" else data.lookwhere
//        val city = when {
//            (area1 + area2).isEmpty() -> "暂无"
//            area1.isNullOrEmpty() -> area2
//            area2.isNullOrEmpty() -> area1
//            else -> area1 + "," + area2
//        }
        holder.setText(R.id.tv_city, data.city)
        holder.setText(R.id.tv_content, data.lookfriendstand)
    }
}