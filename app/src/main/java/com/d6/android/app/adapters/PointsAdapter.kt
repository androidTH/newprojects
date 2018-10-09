package com.d6.android.app.adapters

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
class PointsAdapter(mData:ArrayList<UserPoints>): HFRecyclerAdapter<UserPoints>(mData,  R.layout.item_list_mypoints){//R.layout.item_list_mypoints

    override fun onBind(holder: ViewHolder, position: Int, data: UserPoints) {
        holder.setText(R.id.tv_mypoints_content,data.sPointdesc)
        val tv_mypointstime = holder.bind<TextView>(R.id.tv_mypointstime)
        tv_mypointstime.text = data.dCreatetime.toTime("yyyy.MM.dd HH:mm")
        val tv_nums = holder.bind<TextView>(R.id.tv_nums)
        if(data.iPointtype == 1){
            tv_nums.text = "+${data.iPoint.toString()}"
            tv_nums.textColor = context.resources.getColor(R.color.color_F7AB00)
        }else if(data.iPointtype == 2){
            tv_nums.text = "-${data.iPoint.toString()}"
            tv_nums.textColor = context.resources.getColor(R.color.color_68BFFF)
        }
    }
}