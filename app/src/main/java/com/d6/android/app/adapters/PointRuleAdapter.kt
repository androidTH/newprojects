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
import com.d6.android.app.models.PointRule
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.JsonObject

/**
 *粉丝
 */
class PointRuleAdapter(mData:ArrayList<PointRule>): HFRecyclerAdapter<PointRule>(mData, R.layout.item_list_pointrule) ,View.OnClickListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: PointRule) {
        holder.setText(R.id.tv_point_nums,"${data.iPoint.toString()}积分")
        var  mTvPointMoney= holder.bind<TextView>(R.id.tv_point_money)
        mTvPointMoney.text = "¥${data.iPrice.toString()}"
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
            tv_focus.setText("已关注")
            fans.iIsFollow = 1
        }
    }

    private fun delFollow(fans:Fans,tv_focus:TextView){
        Request.getDelFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
            tv_focus.setBackgroundResource(R.drawable.shape_10r_nofans)
            tv_focus.setTextColor(context.resources.getColor(R.color.color_F7AB00))
            tv_focus.text ="关注"
            fans.iIsFollow = 0
        }
    }
}