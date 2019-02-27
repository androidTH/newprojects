package com.d6.android.app.adapters

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

/**
 *粉丝
 */
class BlackListAdapter(mData:ArrayList<Fans>): HFRecyclerAdapter<Fans>(mData, R.layout.item_list_black) ,View.OnClickListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: Fans) {
        holder.setText(R.id.tv_name,data.sUserName)
        val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
        headView.setImageURI(data.sPicUrl)
        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)

        tv_userinfo.text = data.gexingqianming

        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        tv_sex.text = data.nianling
        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        val sex = SPUtils.instance().getString(Const.User.USER_SEX)
        if (TextUtils.equals("1", sex)&& TextUtils.equals(data.sSex, "0")) {//0 女 1 男
            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility =View.GONE
        } else {
            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility = View.VISIBLE
        }

        var mTvRemove = holder.bind<TextView>(R.id.tv_remove)
        mTvRemove.setOnClickListener(this)
        mTvRemove.setTag(data)
    }

    override fun onClick(v: View?) {
        var fans= (v as TextView).tag as Fans
        if(fans.iIsFollow == 0){
            addFollow(fans)
        }else {
            delFollow(fans)
        }
    }

    private fun addFollow(fans:Fans){
        if (context is BaseActivity) {
            (context as BaseActivity).dialog("",canCancel = false,visibility = false)
        }
        Request.getAddFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
             mData.remove(fans)
             notifyDataSetChanged()
        }
    }

    private fun delFollow(fans:Fans){
        if (context is BaseActivity) {
            (context as BaseActivity).dialog("",canCancel = false,visibility = false)
        }
        Request.getDelFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
            mData.remove(fans)
            notifyDataSetChanged()
        }
    }
}