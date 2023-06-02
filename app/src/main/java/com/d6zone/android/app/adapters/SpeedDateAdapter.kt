package com.d6zone.android.app.adapters

import android.graphics.Color
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.d6zone.android.app.R
import com.d6zone.android.app.application.D6Application
import com.d6zone.android.app.base.adapters.BaseRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.models.MyDate
import com.d6zone.android.app.utils.gone
import com.d6zone.android.app.utils.parserTime
import com.d6zone.android.app.utils.toTime
import com.d6zone.android.app.utils.visible
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundColor

/**
 *
 */
class SpeedDateAdapter(mData: ArrayList<MyDate>) : BaseRecyclerAdapter<MyDate>(mData, R.layout.item_home_speed_date) {
    override fun onBind(holder: ViewHolder, position: Int, data: MyDate) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        imageView.setImageURI(data.speedpics)
        val nameView = holder.bind<TextView>(R.id.tv_name)
//        nameView.text = String.format("%s%s", data.speedwhere + data.handspeedwhere, data.speednumber)
        nameView.text = String.format("%s", data.speednumber) //String.format("%s%s", data.speedcity, data.speednumber)
        nameView.isSelected = TextUtils.equals(data.sex, "0")
        holder.setText(R.id.tv_info, String.format("%s岁·%s·%s", data.age, data.height, data.weight))
        holder.setText(R.id.tv_content, data.speedcontent)
        holder.setText(R.id.tv_type, data.getSpeedStateStr())
        holder.setText(R.id.tv_address,data.speedcity)
        val tv_audio_auth = holder.bind<ImageView>(R.id.tv_audio_auth)
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
        val typeView = holder.bind<TextView>(R.id.tv_type)
        typeView.backgroundColor = Color.parseColor("#cc562BFF")
        if (current > endTime) {//已过期
            typeView.text = "已过期"
            typeView.backgroundColor = Color.parseColor("#94000000")
        }

//        val line = holder.bind<View>(R.id.line)
//        if (position == mData.size - 1) {
//            line.gone()
//        } else {
//            line.visible()
//        }
    }
}