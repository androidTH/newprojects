package com.d6zone.android.app.adapters

import android.text.TextUtils
import android.view.View
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6zone.android.app.R
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.BlackListBean
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.getLevelDrawable
import com.d6zone.android.app.utils.getLocalUserId
import com.d6zone.android.app.widget.CustomToast
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.JsonPrimitive
import org.jetbrains.anko.backgroundDrawable

/**
 * jinjiarui
 */
class BlackListQuickAdapter(data: List<BlackListBean>) : BaseQuickAdapter<BlackListBean, BaseViewHolder>(R.layout.item_list_black, data),View.OnClickListener {
    override fun onClick(v: View?) {
        var blackList= (v as TextView).tag as BlackListBean
        removeBlackList(blackList)
    }

    private fun removeBlackList(blackList:BlackListBean){
        if (mContext is BaseActivity) {
            (mContext as BaseActivity).dialog("",canCancel = false,visibility = false)
        }
        Request.removeBlackList(getLocalUserId(),blackList.iBlackUserid.toString(),blackList.iIsAnonymous!!.toInt()).request((mContext as BaseActivity),false,success={ s: String?, jsonObject: JsonPrimitive? ->
            mData.remove(blackList)
            notifyDataSetChanged()
            CustomToast.showToast("已从黑名单中移除")
        })
    }

    override fun convert(helper: BaseViewHolder, data: BlackListBean) {
        helper.setText(R.id.tv_name,data.sUserName)
        val headView = helper.getView<SimpleDraweeView>(R.id.user_headView)
        headView.setImageURI(data.sPicUrl)
        val tv_userinfo = helper.getView<TextView>(R.id.tv_userinfo)

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
        val tv_sex = helper.getView<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        tv_sex.text = data.nianling
        val tv_vip = helper.getView<TextView>(R.id.tv_vip)
        tv_vip.backgroundDrawable = getLevelDrawable(data.userclassesid.toString(),mContext)

//        tv_vip.text = String.format("%s", data.userclassesname)
        var mTvRemove = helper.getView<TextView>(R.id.tv_remove)
//        mTvRemove.setOnClickListener(this)
        mTvRemove.setTag(data)

        mTvRemove.setOnClickListener(this)
    }
}
