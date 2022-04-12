package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.request
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.Fans
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.BLUR_60
import com.d6.android.app.utils.Const.D6_WWW_TAG
import com.d6.android.app.widget.textinlineimage.TextInlineImage
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.JsonObject
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor

/**
 *粉丝
 */
class FansAdapter(mData:ArrayList<LoveHeartFans>): HFRecyclerAdapter<LoveHeartFans>(mData, R.layout.item_list_fans) ,View.OnClickListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: LoveHeartFans) {
//        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)

        if(data.iIsCode==1){
//            headView.setImageURI("res:///"+R.mipmap.shenmiren_icon)
            if(data.sPicUrl.isNullOrEmpty()){
                headView.setImageURI("res:///"+R.mipmap.mask_fenhui_bg)
            }else{
                if("${data.sPicUrl}".contains(D6_WWW_TAG)){
                    headView.showBlur(data.sPicUrl,40,60)
                }else{
                    headView.setImageURI("${data.sPicUrl}${BLUR_60}")
                }
//                headView.showBlur(data.sPicUrl)
            }
            holder.setText(R.id.tv_name,"匿名")

        }else{
            headView.setImageURI(data.sPicUrl)
            holder.setText(R.id.tv_name,data.sSendUserName)
//            holder.bind<TextView>(R.id.tv_name).textColor = ContextCompat.getColor(context,R.color.color_black)

//            if(!data.gexingqianming.isNullOrEmpty()){
//                tv_userinfo.visibility = View.VISIBLE
//                tv_userinfo.text = data.gexingqianming
//            }else if(!data.ziwojieshao.isNullOrEmpty()){
//                tv_userinfo.text = data.ziwojieshao
//                tv_userinfo.visibility = View.VISIBLE
//            }else{
//                tv_userinfo.visibility = View.GONE
//            }
        }
        Log.i("fansAdapter","${data.sPicUrl}数量,名字：${data.sSendUserName},身高${data.shengao},位置：${data.sPosition}")

        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        val tv_age = holder.bind<TextView>(R.id.tv_age)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        if(data.nianling.isNullOrEmpty()){
            tv_age.visibility = View.GONE
        }else{
            tv_age.isSelected = TextUtils.equals("0", "${data.sSex}")
            tv_age.visibility = View.VISIBLE
            tv_age.text = "${data.nianling}岁"
        }

        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        if (TextUtils.equals("1", sex)&& TextUtils.equals(data.sSex, "0")) {//0 女 1 男
//            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility =View.GONE
        } else {
//            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility = View.VISIBLE
            tv_vip.backgroundDrawable = getLevelDrawable("${data.userclassesid}",context)
        }

        var tv_likedtype = holder.bind<TextView>(R.id.tv_likedtype)
        tv_likedtype.setText("送你")

        var tv_info = holder.bind<TextView>(R.id.tv_userinfo)
        var mInfo = ""
        if(!data.shengao.isNullOrEmpty()){
            mInfo = "${data.shengao}"
        }
        if(!data.sPosition.isNullOrEmpty()){
            if(data.shengao.isNullOrEmpty()){
                mInfo = "${data.sPosition}"
            }else{
                mInfo = "${data.shengao}·${data.sPosition}"
            }
        }

        if(mInfo.isNullOrEmpty()){
            tv_info.visibility = View.GONE
        }else{
            tv_info.visibility = View.GONE
            tv_info.text = "${mInfo}"
        }

        var tv_job = holder.bind<TextView>(R.id.tv_job)
        if(data.zhiye.isNullOrEmpty()){
            tv_job.visibility = View.GONE
        }else{
            tv_job.visibility = View.GONE
            tv_job.text = "职业：${data.zhiye}"
        }

        var tv_receivedliked = holder.bind<TextInlineImage>(R.id.tv_receivedliked)
        var tv_showgift = holder.bind<TextInlineImage>(R.id.tv_showgift)
        tv_showgift.visibility = View.VISIBLE
        if(data.iAllLovePoint>=Const.iLovePointShow){
            tv_receivedliked.textColor = ContextCompat.getColor(context,R.color.color_666666)
            tv_receivedliked.text = "${data.iAllLovePoint}颗 [img src=super_like_icon/] [img src=redheart_small/]"
            if(data.giftName.isNullOrEmpty()){
                tv_showgift.visibility = View.GONE
            }else{
                tv_showgift.visibility = View.VISIBLE
                tv_showgift.text = "TA送了你\n ${data.giftLoveNum}(${data.giftLoveNum}颗[img src=redheart_small/])"
            }
        }else{
            tv_receivedliked.text = "${data.iAllLovePoint}颗 [img src=redheart_small/]"
            if(data.giftName.isNullOrEmpty()){
                tv_showgift.visibility = View.GONE
            }else{
                tv_showgift.visibility = View.VISIBLE
                tv_showgift.text = "TA送了你\n ${data.giftName}(${data.giftLoveNum}颗[img src=redheart_small/])"
            }
        }

    }

    override fun onClick(v: View?) {
        var fans= (v as TextView).tag as Fans
        if(fans.iIsFollow == 0){
            addFollow(fans,v)
        }else {
            delFollow(fans,v)
        }
//        notifyDataSetChanged()
    }

    private fun addFollow(fans:Fans,tv_focus:TextView){
        Request.getAddFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
            tv_focus.setBackgroundResource(R.drawable.shape_10r_fans)
            tv_focus.setTextColor(context.resources.getColor(R.color.color_DFE1E5))
            tv_focus.setText("已喜欢")
            fans.iIsFollow = 1
        }
    }

    private fun delFollow(fans:Fans,tv_focus:TextView){
        Request.getDelFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
            tv_focus.setBackgroundResource(R.drawable.shape_10r_nofans)
            tv_focus.setTextColor(context.resources.getColor(R.color.color_F7AB00))
            tv_focus.text ="喜欢"
            fans.iIsFollow = 0
        }
    }
}