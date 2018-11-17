package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.MyDate
import com.facebook.drawee.view.SimpleDraweeView

/**
 *
 */
class RecommendAllDateAdapter(mData: ArrayList<MyDate>) : HFRecyclerAdapter<MyDate>(mData, R.layout.item_list_recommend_date) {
    override fun onBind(holder: ViewHolder, position: Int, data: MyDate) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        imageView.setImageURI(data.lookpics)
        val nameView = holder.bind<TextView>(R.id.tv_name)
//        nameView.text = String.format("%s%s", data.speedwhere + data.handspeedwhere, data.speednumber)
        nameView.text = String.format("%s", data.looknumber) //String.format("%s%s", data.speedcity, data.speednumber)
        nameView.isSelected = TextUtils.equals(data.sex, "0")

        if(TextUtils.equals("0",data.sex)){
            holder.setText(R.id.tv_info, String.format("%s岁·%s·%s", data.age, data.height, data.weight))
        }else{
            var tv_info = holder.bind<TextView>(R.id.tv_info)
            var sb = StringBuffer()
            if(!data.job.isNullOrEmpty()){
                sb.append("职业:${data.job}")
            }
            if(!data.zuojia.isNullOrEmpty()){
                sb.append("座驾:${data.zuojia}")
            }
            if(sb.toString().isNullOrEmpty()){
                tv_info.visibility = View.GONE
            }else{
                tv_info.visibility = View.VISIBLE
                tv_info.text = sb.toString()
            }// String.format("职业:%s 座驾:%s", data.job, data.zuojia)
        }

        holder.setText(R.id.tv_content, data.lookfriendstand)
        holder.setText(R.id.tv_address,data.city)
        val tv_audio_auth = holder.bind<TextView>(R.id.tv_auth_state)
        val tv_audio_level = holder.bind<TextView>(R.id.tv_auth_level)

        if(TextUtils.equals("0",data.sex)){
            tv_audio_auth.visibility = View.VISIBLE
            tv_audio_level.visibility = View.GONE
            if (TextUtils.equals("1", data.screen)) {
                tv_audio_auth.isSelected = true
                tv_audio_auth.text = "视频认证"
            } else if(TextUtils.equals("0", data.screen)){
                tv_audio_auth.isSelected = false
                tv_audio_auth.text = "未认证"
            }else if(TextUtils.equals("3",data.screen)){
                tv_audio_auth.isSelected = false
                tv_audio_auth.text = "初级认证"
            }
        }else{
            tv_audio_auth.visibility = View.GONE
            tv_audio_level.visibility = View.VISIBLE
            tv_audio_level.text = data.classesname
        }
//        val endTime = data.createTime.parserTime().toTime("yyyy-MM-dd")
        val cTime = if (D6Application.systemTime <= 0) {
            System.currentTimeMillis()
        } else {
            D6Application.systemTime
        }

        val typeView = holder.bind<TextView>(R.id.tv_type)
        if(data.iType==1){
            typeView.text = "觅约"
        }else if(data.iType==2){
            typeView.text = "速约"
            typeView.text =  data.getSpeedStateStr()
        }

//        val current = cTime.toTime("yyyy-MM-dd")
//        val typeView = holder.bind<TextView>(R.id.tv_type)
//        typeView.backgroundColor = Color.parseColor("#cc562BFF")
//        if (current > endTime) {//已过期
//            typeView.text = "已过期"
//            typeView.backgroundColor = Color.parseColor("#94000000")
//        }

//        val line = holder.bind<View>(R.id.line)
//        if (position == mData.size - 1) {
//            line.gone()
//        } else {
//            line.visible()
//        }
    }
}