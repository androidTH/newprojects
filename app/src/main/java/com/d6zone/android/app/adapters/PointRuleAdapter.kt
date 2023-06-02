package com.d6zone.android.app.adapters

import android.graphics.Paint
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6zone.android.app.R
import com.d6zone.android.app.base.adapters.HFRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.models.PointRule

/**
 *粉丝
 */
class PointRuleAdapter(mData:ArrayList<PointRule>): HFRecyclerAdapter<PointRule>(mData, R.layout.item_list_pointrule){

    override fun onBind(holder: ViewHolder, position: Int, data: PointRule) {
        holder.setText(R.id.tv_point_nums,"${data.iPoint.toString()}积分")
        var  mTvPointMoney= holder.bind<TextView>(R.id.tv_point_money)
        mTvPointMoney.text = "¥${data.iPrice.toString()}"
        var tv_point_nomoney = holder.bind<TextView>(R.id.tv_point_nomoney)
        if(TextUtils.equals(data.iPrice.toString(),data.iDiscount.toString())){
            tv_point_nomoney.visibility = View.GONE
        }else{
            tv_point_nomoney.visibility = View.VISIBLE
            tv_point_nomoney.text="¥${data.iDiscount.toString()}"
            tv_point_nomoney.paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG)
            tv_point_nomoney.paint.isAntiAlias = true
        }
    }
}