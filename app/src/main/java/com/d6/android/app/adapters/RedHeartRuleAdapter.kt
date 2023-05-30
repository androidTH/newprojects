package com.d6.android.app.adapters

import android.graphics.Paint
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.LoveHeartRule
import com.d6.android.app.models.PointRule

/**
 *粉丝
 */
class RedHeartRuleAdapter(mData:ArrayList<LoveHeartRule>): HFRecyclerAdapter<LoveHeartRule>(mData, R.layout.item_list_redheartrule){

    override fun onBind(holder: ViewHolder, position: Int, data: LoveHeartRule) {
        holder.setText(R.id.tv_readheart_nums,"${data.iLoveCount.toString()}")
        var  mTvPointMoney= holder.bind<TextView>(R.id.tv_readheart_money)
        mTvPointMoney.text = "¥${data.iPrice.toString()}"
//        var tv_point_nomoney = holder.bind<TextView>(R.id.tv_readheart_nomoney)
//        if(TextUtils.equals(data.iPrice.toString(),data.iDiscount.toString())){
//            tv_point_nomoney.visibility = View.GONE
//        }else{
//            tv_point_nomoney.visibility = View.VISIBLE
//            tv_point_nomoney.text="¥${data.iDiscount.toString()}"
//            tv_point_nomoney.paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG)
//            tv_point_nomoney.paint.isAntiAlias = true
//        }
    }
}