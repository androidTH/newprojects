package com.d6zone.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6zone.android.app.R
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.base.adapters.HFRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.extentions.showBlur
import com.d6zone.android.app.models.Fans
import com.d6zone.android.app.models.LoveHeartFans
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.*
import com.d6zone.android.app.utils.Const.BLUR_60
import com.d6zone.android.app.utils.Const.D6_WWW_TAG
import com.d6zone.android.app.widget.textinlineimage.TextInlineImage
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.JsonObject
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor

/**
 *粉丝
 */
class FollowAdapter(mData:ArrayList<LoveHeartFans>): HFRecyclerAdapter<LoveHeartFans>(mData, R.layout.item_list_follows) ,View.OnClickListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: LoveHeartFans) {
        val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
//        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        if(data.iIsCode==1){
            holder.setText(R.id.tv_name,"匿名")
//            holder.bind<TextView>(R.id.tv_name).textColor = ContextCompat.getColor(context,R.color.color_8F5A5A)
            if(data.sPicUrl.isNullOrEmpty()){
                headView.setImageURI("res:///"+R.mipmap.mask_fenhui_bg)
            }else{
                if("${data.sPicUrl}".contains(D6_WWW_TAG)){
                    headView.showBlur(data.sPicUrl,40,60)
                }else{
                    headView.setImageURI("${data.sPicUrl}${BLUR_60}")
                }
//                headView.showBlur(data.sPicUrl,20)
            }
//            headView.setImageURI("res:///"+R.mipmap.shenmiren_icon)
//            tv_userinfo.text = "对方送的[img src=redheart_small/]较少，支付积分即可查看身份 "
//            tv_userinfo.visibility = View.VISIBLE
        }else{
            holder.setText(R.id.tv_name,data.sSendUserName)
//            holder.bind<TextView>(R.id.tv_name).textColor = ContextCompat.getColor(context,R.color.color_black)
//        val tv_time =holder.bind<TextView>(R.id.tv_time)
//        tv_time.text = data.dJointime.toTime("MM.dd")

            headView.setImageURI(data.sPicUrl)
//            if(!data.gexingqianming.isNullOrEmpty()){
//                tv_userinfo.text = data.gexingqianming
//                tv_userinfo.visibility = View.VISIBLE
//            }else if(!data.ziwojieshao.isNullOrEmpty()){
//                tv_userinfo.text = data.ziwojieshao
//                tv_userinfo.visibility = View.VISIBLE
//            }else{
//                tv_userinfo.visibility = View.GONE
//            }

        }

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
//        if (TextUtils.equals("1", sex)&& TextUtils.equals(data.sSex, "0")) {//0 女 1 男
//            tv_vip.text = String.format("%s", data.userclassesname)
//            tv_vip.visibility =View.GONE
//        } else {
//            tv_vip.text = String.format("%s", data.userclassesname)
//            tv_vip.visibility = View.VISIBLE
//        }

        tv_vip.backgroundDrawable = getLevelDrawable("${data.userclassesid}",context)

        var tv_likedtype = holder.bind<TextView>(R.id.tv_likedtype)
        tv_likedtype.setText("送TA")
        var  tv_sendliked= holder.bind<TextInlineImage>(R.id.tv_sendliked)
        if(data.iPoint>=Const.iLovePointShow){
            tv_sendliked.textColor = ContextCompat.getColor(context,R.color.color_FF4133)
            tv_sendliked.text ="${data.iPoint} [img src=super_like_icon/] [img src=redheart_small/]"
        }else{
            tv_sendliked.textColor = ContextCompat.getColor(context,R.color.color_FF4133)
            tv_sendliked.text =  "${data.iPoint} [img src=redheart_small/]"
        }
//        tv_sendliked.textColor = ContextCompat.getColor(context,R.color.color_FF4133)
//        tv_sendliked.text ="${data.iPoint} [img src=super_like_icon/] [img src=redheart_small/]"

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
            tv_info.visibility = View.VISIBLE
            tv_info.text = "${mInfo}"
        }
        var tv_job = holder.bind<TextView>(R.id.tv_job)

        if(data.zhiye.isNullOrEmpty()){
            tv_job.visibility = View.GONE
        }else{
            tv_job.visibility = View.VISIBLE
            tv_job.text = "职业：${data.zhiye}"
        }

//        var mTvFollow = holder.bind<TextView>(R.id.tv_follow)
//
//        if(data.iIsFollow == 0){
//            mTvFollow.setBackgroundResource(R.drawable.shape_10r_nofans);
//            mTvFollow.setTextColor(context.resources.getColor(R.color.color_F7AB00))
//            mTvFollow.setText("喜欢")
//        }else{
//            mTvFollow.setBackgroundResource(R.drawable.shape_10r_fans)
//            mTvFollow.setTextColor(context.resources.getColor(R.color.color_DFE1E5))
//            mTvFollow.setText("已喜欢")
//        }
//        mTvFollow.setOnClickListener(this)
//        mTvFollow.setTag(data)
    }

    override fun onClick(v: View?) {
        var fans= (v as TextView).tag as Fans
//        delFollow(fans,v)

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