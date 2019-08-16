package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.JsonObject
import org.jetbrains.anko.backgroundDrawable

/**
 *粉丝
 */
class FollowAdapter(mData:ArrayList<Fans>): HFRecyclerAdapter<Fans>(mData, R.layout.item_list_follows) ,View.OnClickListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: Fans) {
        holder.setText(R.id.tv_name,data.sUserName)
        val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
//        val tv_time =holder.bind<TextView>(R.id.tv_time)
//        tv_time.text = data.dJointime.toTime("MM.dd")
        headView.setImageURI(data.sPicUrl)
        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        if(!data.gexingqianming.isNullOrEmpty()){
            tv_userinfo.text = data.gexingqianming
            tv_userinfo.visibility = View.VISIBLE
        }else{
            tv_userinfo.visibility = View.GONE
        }

        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        tv_sex.text = data.nianling
        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
//        if (TextUtils.equals("1", sex)&& TextUtils.equals(data.sSex, "0")) {//0 女 1 男
//            tv_vip.text = String.format("%s", data.userclassesname)
//            tv_vip.visibility =View.GONE
//        } else {
//            tv_vip.text = String.format("%s", data.userclassesname)
//            tv_vip.visibility = View.VISIBLE
//        }

        tv_vip.backgroundDrawable = getLevelDrawable(data.userclassesid.toString(),context)

        var mTvFollow = holder.bind<TextView>(R.id.tv_follow)

        if(data.iIsFollow == 0){
            mTvFollow.setBackgroundResource(R.drawable.shape_10r_nofans);
            mTvFollow.setTextColor(context.resources.getColor(R.color.color_F7AB00))
            mTvFollow.setText("喜欢")
        }else{
            mTvFollow.setBackgroundResource(R.drawable.shape_10r_fans)
            mTvFollow.setTextColor(context.resources.getColor(R.color.color_DFE1E5))
            mTvFollow.setText("已喜欢")
        }

//        mTvFollow.setBackgroundResource(R.drawable.shape_10r_fans)
//        mTvFollow.setTextColor(context.resources.getColor(R.color.color_DFE1E5))
//        mTvFollow.setText("已喜欢")

        mTvFollow.setOnClickListener(this)
        mTvFollow.setTag(data)
        sysErr(data.toString()+"--------url----->"+data.sPicUrl.isNullOrEmpty())
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