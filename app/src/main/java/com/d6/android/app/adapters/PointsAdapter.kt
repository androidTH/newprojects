package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.UserPoints
import com.d6.android.app.utils.*
import org.jetbrains.anko.textColor

/**
 *粉丝
 */
class PointsAdapter(mData: ArrayList<UserPoints>) : HFRecyclerAdapter<UserPoints>(mData, R.layout.item_list_mypoints) {//R.layout.item_list_mypoints

    //1、充值积分  2、消费积分 3、充值购买小红花 4、提现小红花 5、收到小红花 6、赠送小红花  3、5是加  4、6是减
    override fun onBind(holder: ViewHolder, position: Int, data: UserPoints) {
        var tv_mypoints_content = holder.bind<TextView>(R.id.tv_mypoints_content)
        val tv_mypointstime = holder.bind<TextView>(R.id.tv_mypointstime)
        tv_mypointstime.text = data.dCreatetime.toTime("yyyy.MM.dd HH:mm")
        val tv_nums = holder.bind<TextView>(R.id.tv_nums)
        if (data.iPointtype == 1 || data.iPointtype == 3 || data.iPointtype == 5) {
            tv_nums.text = "+${data.iPoint.toString()}"
            tv_nums.textColor = ContextCompat.getColor(context, R.color.color_F7AB00)
        } else if (data.iPointtype == 2 || data.iPointtype == 4 || data.iPointtype == 6) {
            tv_nums.text = "-${data.iPoint.toString()}"
            tv_nums.textColor = ContextCompat.getColor(context, R.color.color_68BFFF)
        }

        if (data.iType == 16 || data.iType == 17 || data.iType == 18) {
            data.sPointdesc?.let {
                var index = it.indexOf("@${data.sSendUserName}")
                var str = it.replace("@${data.sSendUserName}", "${data.sSendUserName}")
                var span = SpannableStringBuilder(str)
                var len = (index + data.sSendUserName.length)
                span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_F7AB00)), index, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv_mypoints_content.text = span
            }
        }else{
            tv_mypoints_content.text = data.sPointdesc
        }
    }
}