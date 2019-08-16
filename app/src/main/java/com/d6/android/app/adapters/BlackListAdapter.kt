package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
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
        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        tv_sex.text = data.nianling
        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        tv_vip.backgroundDrawable = getLevelDrawable(data.userclassesid.toString(),context)

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
        Request.removeBlackList(getLocalUserId(),blackList.iBlackUserid.toString(),blackList.iIsAnonymous!!.toInt()).request((context as BaseActivity),false,success={ s: String?, jsonObject:JsonPrimitive? ->
            mData.remove(blackList)
            notifyDataSetChanged()
            CustomToast.showToast("已从黑名单中移除")
        })
    }
}