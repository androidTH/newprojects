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
import com.d6.android.app.widget.textinlineimage.TextInlineImage
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.JsonObject
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor

/**
 *粉丝
 */
class RecentlyFansAdapter(mData:ArrayList<LoveHeartFans>): HFRecyclerAdapter<LoveHeartFans>(mData, R.layout.item_list_fans) ,View.OnClickListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: LoveHeartFans) {
        if(data.iIsCode==1){
            holder.setText(R.id.tv_name,"匿名")
            val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
            headView.setImageURI("res:///"+R.mipmap.shenmiren_icon)
        }else{
            holder.setText(R.id.tv_name,data.sSendUserName)
            val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
            headView.setImageURI(data.sPicUrl)
        }
        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        if(!data.sPointdesc.isNullOrEmpty()){
            tv_userinfo.visibility = View.VISIBLE
            tv_userinfo.text = data.sPointdesc
        }else{
            tv_userinfo.visibility = View.GONE
        }
        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        tv_sex.text = data.nianling
        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        if (TextUtils.equals("1", sex)&& TextUtils.equals(data.sSex, "0")) {//0 女 1 男
            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility =View.GONE
        } else {
//            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility = View.VISIBLE
            tv_vip.backgroundDrawable = getLevelDrawable("${data.userclassesid}",context)
        }

        var tv_receivedliked = holder.bind<TextInlineImage>(R.id.tv_receivedliked)
        if(data.iPoint>=Const.iLovePointShow){
            tv_receivedliked.textColor = ContextCompat.getColor(context,R.color.color_FF4133)
            tv_receivedliked.text = "${data.iPoint} [img src=super_like_icon/] [img src=redheart_small/]"
        }else{
            tv_receivedliked.textColor = ContextCompat.getColor(context,R.color.color_black)
            tv_receivedliked.text = "${data.iPoint} [img src=redheart_small/]"
        }

//        redheart_small
//        var mTvFollow = holder.bind<TextView>(R.id.tv_follow)
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