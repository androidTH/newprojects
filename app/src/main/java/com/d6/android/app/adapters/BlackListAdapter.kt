package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.request
import com.d6.android.app.models.BlackListBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.JsonPrimitive
import org.jetbrains.anko.backgroundDrawable

/**
 *粉丝
 */
class BlackListAdapter(mData:ArrayList<BlackListBean>): HFRecyclerAdapter<BlackListBean>(mData, R.layout.item_list_black) ,View.OnClickListener{

    override fun onBind(holder: ViewHolder, position: Int, data: BlackListBean) {
        holder.setText(R.id.tv_name,data.sUserName)
        val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
        headView.setImageURI(data.sPicUrl)
        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)

        if (!(data.gexingqianming.isNullOrEmpty())) {
            if(!TextUtils.equals("null",data.gexingqianming)){
                tv_userinfo.text = data.gexingqianming
            }else{
                tv_userinfo.visibility = View.GONE
            }
        }else if(!data.ziwojieshao.isNullOrEmpty()){
            if(!TextUtils.equals("null",data.ziwojieshao)){
                tv_userinfo.text =  data.ziwojieshao
            }else{
                tv_userinfo.visibility = View.GONE
            }
        }else{
            tv_userinfo.visibility = View.GONE
        }

        val img_blacklist_auther = holder.bind<ImageView>(R.id.img_blacklist_auther)
//        if(TextUtils.equals("3",data.screen)){
//            img_blacklist_auther.visibility=View.GONE
//            img_blacklist_auther.setImageResource(R.mipmap.renzheng_small)
//        }else if(TextUtils.equals("1",data.screen)){
//            img_blacklist_auther.visibility=View.VISIBLE
//            img_blacklist_auther.setImageResource(R.mipmap.video_small)
//        }else{
//            img_blacklist_auther.visibility=View.GONE
//        }

        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        tv_sex.text = data.nianling
        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        if (TextUtils.equals(data.userclassesid, "27")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_cj)
        } else if (TextUtils.equals(data.userclassesid, "28")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_zj)
        } else if (TextUtils.equals(data.userclassesid, "29")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_gj)
        } else if (TextUtils.equals(data.userclassesid.toString(), "22")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
        } else if (TextUtils.equals(data.userclassesid, "23")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
        } else if (TextUtils.equals(data.userclassesid, "24")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
        } else if (TextUtils.equals(data.userclassesid, "25")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
        } else if (TextUtils.equals(data.userclassesid, "26")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_private)
        }else if (TextUtils.equals(data.userclassesid, "7")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.youke_icon)
        }

//        tv_vip.text = String.format("%s", data.userclassesname)
        var mTvRemove = holder.bind<TextView>(R.id.tv_remove)
        mTvRemove.setOnClickListener(this)
        mTvRemove.setTag(data)
    }

    override fun onClick(v: View?) {
        var blackList= (v as TextView).tag as BlackListBean
        removeBlackList(blackList)
    }

    private fun removeBlackList(blackList:BlackListBean){
        if (context is BaseActivity) {
            (context as BaseActivity).dialog("",canCancel = false,visibility = false)
        }
        Request.removeBlackList(blackList.sId).request((context as BaseActivity),false,success={ s: String?, jsonObject:JsonPrimitive? ->
            mData.remove(blackList)
            notifyDataSetChanged()
            CustomToast.showToast("已从黑名单中移除")
        })
    }
}