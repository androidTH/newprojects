package com.d6zone.android.app.adapters

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6zone.android.app.R
import com.d6zone.android.app.application.D6Application
import com.d6zone.android.app.base.adapters.HFRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.models.MyDate
import com.d6zone.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundResource

/**
 * Created on 2017/12/16.
 */
class DateAdapter(mData: ArrayList<MyDate>) : HFRecyclerAdapter<MyDate>(mData, R.layout.item_list_speed_date) {
    override fun onBind(holder: ViewHolder, position: Int, data: MyDate) {
        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.speedpics)
        val typeView = holder.bind<TextView>(R.id.tv_type)
        typeView.text = data.getSpeedStateStr()
        val titleView = holder.bind<TextView>(R.id.tv_title)
//        titleView.text = String.format("%s%s", data.speedwhere+data.handspeedwhere, data.speednumber)
        titleView.text = String.format("%s%s", "", data.speednumber)
        titleView.isSelected = TextUtils.equals(data.sex, "0")
        val ageView = holder.bind<TextView>(R.id.tv_age)
        ageView.text = data.age
        if (data.age.isNullOrEmpty()) {
            ageView.gone()
        } else {
            ageView.visible()
        }
        val heightView = holder.bind<TextView>(R.id.tv_height)
        heightView.text = data.height
        if (data.height.isNullOrEmpty()) {
            heightView.gone()
        } else {
            heightView.visible()
        }
        val weightView = holder.bind<TextView>(R.id.tv_weight)
        weightView.text = data.weight
        if (data.weight.isNullOrEmpty()) {
            weightView.gone()
        } else {
            weightView.visible()
        }
//        val l1 = data.speedcity?.length ?: 0
//        val l2 = data.getSpeedStateStr().length
//        val l3 = data.handspeedwhere?.length ?: 0
//        holder.setText(R.id.tv_content, SpanBuilder(String.format("%s%s-%s", data.speedcity, data.getSpeedStateStr(), data.speedcontent))
//                .color(context, 0, l1 + l2 + 1, R.color.textColor)
//                .build())
        val start = data.beginTime?.parserTime()
        val end = data.endTime?.parserTime()
        holder.setText(R.id.tv_deadline_time, String.format("速约时间:%s-%s",start?.toTime("MM.dd") , end?.toTime("MM.dd")))

        val tv_audio_auth= holder.bind<View>(R.id.tv_audio_auth)
        if (TextUtils.equals("1", data.screen)) {
            tv_audio_auth.visible()
        } else {
            tv_audio_auth.gone()
        }
        val endTime = data.endTime.parserTime().toTime("yyyy-MM-dd")
        val cTime = if (D6Application.systemTime <= 0) {
            System.currentTimeMillis()
        } else {
            D6Application.systemTime
        }
        val current = cTime.toTime("yyyy-MM-dd")
        typeView.backgroundResource = R.drawable.shape_6r_orange
        if (current > endTime) {//已过期
            typeView.text = "已过期"
            typeView.backgroundResource = R.drawable.shape_6r_gray
        }
        holder.setText(R.id.tv_time, String.format("%s",data.createTime?.interval()))
    }
}