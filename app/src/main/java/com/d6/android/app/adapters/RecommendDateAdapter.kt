package com.d6.android.app.adapters

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.MyDate
import com.d6.android.app.models.NewDateBean
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.view_trend_view.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource

/**
 *
 */
class RecommendDateAdapter(mData: ArrayList<MyDate>) : BaseRecyclerAdapter<MyDate>(mData, R.layout.item_home_speed_date) {
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
                tv_audio_auth.backgroundDrawable=ContextCompat.getDrawable(context,R.mipmap.video_big)
//                tv_audio_auth.text = "视频认证"
//                var drawable = ContextCompat.getDrawable(AppUtils.context,R.mipmap.video_small_authentication_icon)
//                drawable.setBounds(0,0,  drawable.getMinimumWidth(), drawable.getMinimumHeight())//这句一定要加
//                tv_audio_auth.setCompoundDrawables(drawable,null,null,null);
            } else if(TextUtils.equals("0", data.screen)){
                tv_audio_auth.visibility = View.GONE
            }else if(TextUtils.equals("3",data.screen)){
                tv_audio_auth.visibility = View.GONE
                tv_audio_auth.backgroundDrawable=ContextCompat.getDrawable(context,R.mipmap.renzheng_big)
//                tv_audio_auth.text = "已认证"
//                var drawable = ContextCompat.getDrawable(AppUtils.context,R.mipmap.small_authentication_icon)
//                drawable.setBounds(0, 0,  drawable.getMinimumWidth(), drawable.getMinimumHeight())//这句一定要加
//                tv_audio_auth.setCompoundDrawables(drawable,null,null,null);
            }
        }else{
            tv_audio_auth.visibility = View.GONE
            tv_audio_level.visibility = View.VISIBLE

            if (data.classesname.toString().startsWith("普通")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
            } else if (data.classesname.toString().startsWith("白银")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
            } else if (data.classesname.toString().startsWith("黄金")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
            } else if (data.classesname.toString().startsWith( "钻石")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
            } else if (data.classesname.toString().startsWith("私人")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_private)
            }
        }
        val typeView = holder.bind<TextView>(R.id.tv_type)
        if(data.iType==1){
            typeView.text = "觅约"
        }else if(data.iType==2){
//            typeView.text = "速约"
            typeView.text =  data.getSpeedStateStr()
        }
    }
}