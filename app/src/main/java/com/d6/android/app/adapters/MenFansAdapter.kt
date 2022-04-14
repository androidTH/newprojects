package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout
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
 * 男用户关注
 */
class MenFansAdapter(mData:ArrayList<LoveHeartFans>): HFRecyclerAdapter<LoveHeartFans>(mData, R.layout.item_list_menfans) ,View.OnClickListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: LoveHeartFans) {
        val headView = holder.bind<SimpleDraweeView>(R.id.user_menheadView)
        if(data.iIsCode==1){
            if(data.sPicUrl.isNullOrEmpty()){
                headView.setImageURI("res:///"+R.mipmap.mask_fenhui_bg)
            }else{
                if("${data.sPicUrl}".contains(D6_WWW_TAG)){
                    headView.showBlur(data.sPicUrl,40,60)
                }else{
                    headView.setImageURI("${data.sPicUrl}${BLUR_60}")
                }
            }
            holder.setText(R.id.tv_menname,"匿名")

        }else{
            headView.setImageURI(data.sPicUrl)
            holder.setText(R.id.tv_menname,data.sSendUserName)
        }
        Log.i("menfansAdapter","${data.sPicUrl}数量,名字：${data.sSendUserName},身高${data.shengao},位置：${data.sPosition}")

        val tv_sex = holder.bind<TextView>(R.id.tv_mensex)
        val tv_age = holder.bind<TextView>(R.id.tv_menage)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        if(data.nianling.isNullOrEmpty()){
            tv_age.visibility = View.GONE
        }else{
            tv_age.isSelected = TextUtils.equals("0", "${data.sSex}")
            tv_age.visibility = View.VISIBLE
            tv_age.text = "${data.nianling}岁"
        }

        val tv_vip = holder.bind<TextView>(R.id.tv_menvip)
        if (TextUtils.equals("1", sex)&& TextUtils.equals(data.sSex, "0")) {//0 女 1 男
//            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility =View.GONE
        } else {
//            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility = View.VISIBLE
            tv_vip.backgroundDrawable = getLevelDrawable("${data.userclassesid}",context)
        }

//        var tv_likedtype = holder.bind<TextView>(R.id.tv_likedtype)
//        tv_likedtype.setText("送你")

        var tv_info = holder.bind<TextView>(R.id.tv_menuserinfo)
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

        var tv_job = holder.bind<TextView>(R.id.tv_menjob)
        if(data.zhiye.isNullOrEmpty()){
            tv_job.visibility = View.GONE
        }else{
            tv_job.visibility = View.GONE
            tv_job.text = "职业：${data.zhiye}"
        }

        var tv_receivedliked = holder.bind<TextInlineImage>(R.id.tv_receivedliked)
        var tv_showgift = holder.bind<TextInlineImage>(R.id.tv_showgift)
        var ll_receivedheart = holder.bind<LinearLayout>(R.id.ll_receivedheart)
        tv_showgift.visibility = View.VISIBLE
        if(data.iAllLovePoint>=Const.iLovePointShow){
            tv_receivedliked.visibility = View.GONE
            tv_receivedliked.textColor = ContextCompat.getColor(context,R.color.color_666666)
            tv_receivedliked.text = "送你${data.iAllLovePoint}颗 [img src=super_like_icon/] [img src=redheart_small/]"
        }else{
            tv_receivedliked.visibility = View.GONE
            tv_receivedliked.text = "送你${data.iAllLovePoint}颗 [img src=redheart_small/]"
        }

        if(data.giftName.isNullOrEmpty()){
//            ll_receivedheart.visibility = View.INVISIBLE
            tv_showgift.visibility = View.GONE
        }else{
            tv_showgift.visibility = View.VISIBLE
//            ll_receivedheart.visibility = View.VISIBLE
            tv_showgift.text = "送你礼物\n${data.giftName}(${data.giftLoveNum}颗[img src=redheart_small/])"
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