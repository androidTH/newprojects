package com.d6.android.app.adapters

import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.application.D6Application
import com.d6.android.app.models.MyDate
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.imageBitmap

/**
 * jinjiarui
 */
class RecommentAllQuickDateAdapter(data: List<MyDate>) : BaseQuickAdapter<MyDate, BaseViewHolder>(R.layout.item_list_recommend_date, data) {

    override fun convert(helper: BaseViewHolder, data: MyDate) {
        val imageView = helper.getView<SimpleDraweeView>(R.id.imageView)
        data.lookpics?.let {
            imageView.setImageURI(it)
//            FrescoUtils.loadImage(mContext,it,object:IResult<Bitmap>{
//                override fun onResult(result: Bitmap?) {
//                    result?.let {
//                        imageView.imageBitmap = it
//                    }
//                }
//            })
            Log.i("recoment","${it}")
        }
        val nameView = helper.getView<TextView>(R.id.tv_name)
        nameView.text = String.format("%s", data.looknumber) //String.format("%s%s", data.speedcity, data.speednumber)
        nameView.isSelected = TextUtils.equals(data.sex, "0")
        var tv_info = helper.getView<TextView>(R.id.tv_info)
        if(TextUtils.equals("0",data.sex)){
            tv_info.visibility = View.GONE
            tv_info.text = String.format("%s岁·%s·%s", data.age, data.height, data.weight)
        }else{
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
                tv_info.visibility = View.GONE
                tv_info.text = sb.toString()
            }// String.format("职业:%s 座驾:%s", data.job, data.zuojia)
        }

        helper.setText(R.id.tv_content, data.lookfriendstand)
        var mCity = helper.getView<TextView>(R.id.tv_address)
        if(!data.city.isNullOrEmpty()){
            mCity.visibility = View.VISIBLE
            mCity.text = data.city
        }else{
           mCity.visibility = View.GONE
        }

        val tv_audio_auth = helper.getView<TextView>(R.id.tv_auth_state)
        val tv_audio_level = helper.getView<TextView>(R.id.tv_auth_level)

        if(TextUtils.equals("0",data.sex)){
            tv_audio_auth.visibility = View.VISIBLE
            tv_audio_level.visibility = View.GONE
            if (TextUtils.equals("1", data.screen)) {
                tv_audio_auth.backgroundDrawable=ContextCompat.getDrawable(mContext,R.mipmap.video_big)
//                tv_audio_auth.text = "视频认证"
//                var drawable = ContextCompat.getDrawable(AppUtils.context,R.mipmap.video_small_authentication_icon)
//                drawable.setBounds(0, 0,drawable.getMinimumWidth(), drawable.getMinimumHeight());//这句一定要加
//                tv_audio_auth.setCompoundDrawables(drawable,null,null,null);
            } else if(TextUtils.equals("0", data.screen)){
                tv_audio_auth.visibility = View.GONE
            }else if(TextUtils.equals("3",data.screen)){
                tv_audio_auth.visibility = View.GONE
                tv_audio_auth.backgroundDrawable=ContextCompat.getDrawable(mContext,R.mipmap.renzheng_big)
//                tv_audio_auth.text = "已认证"
//                var drawable = ContextCompat.getDrawable(AppUtils.context,R.mipmap.small_authentication_icon)
//                drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight())//这句一定要加
//                tv_audio_auth.setCompoundDrawables(drawable,null,null,null);
            }
        }else{
            tv_audio_auth.visibility = View.GONE
            tv_audio_level.visibility = View.VISIBLE
//            tv_audio_level.text = data.classesname

            if (data.classesname.toString().startsWith("普通")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_ordinary)
            } else if (data.classesname.toString().startsWith("白银")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_silver)
            } else if (data.classesname.toString().startsWith("黄金")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_gold)
            } else if (data.classesname.toString().startsWith( "钻石")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_zs)
            } else if (data.classesname.toString().startsWith("私人")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_private)
            }else if (data.classesname.toString().startsWith("入群")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.ruqun_icon)
            }else if (data.classesname.toString().startsWith("APP")) {
                tv_audio_level.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.app_vip)
            }
        }

        Log.i("viptype","会员类型${data.classesname}")
//        val endTime = data.createTime.parserTime().toTime("yyyy-MM-dd")
        val cTime = if (D6Application.systemTime <= 0) {
            System.currentTimeMillis()
        } else {
            D6Application.systemTime
        }

        val typeView = helper.getView<TextView>(R.id.tv_type)
        if(data.iType==1){
            typeView.text = "觅约"
        }else if(data.iType==2){
//            typeView.text = "速约"
            typeView.text =  data.getSpeedStateStr()
        }
    }
}
