package com.d6.android.app.adapters

import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.DateType
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.textColor

/**
 *
 */
class DateTypeAdapter(mData:ArrayList<DateType>): BaseRecyclerAdapter<DateType>(mData, R.layout.item_grid_datetype) {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBind(holder: ViewHolder, position: Int, data: DateType) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.iv_datetype_img)
        val tv_dateTypeName = holder.bind<TextView>(R.id.tv_datetype_name)
        if(data.type == 2){
            tv_dateTypeName.textColor = context.getColor(R.color.color_F7AB00)
            tv_dateTypeName.text = data.dateTypeName
            imageView.isSelected = true
        }else{
            tv_dateTypeName.textColor = context.getColor(R.color.color_666666)
            tv_dateTypeName.text = data.dateTypeName
            imageView.isSelected = false
        }
    }

    fun setOnAddClickListener(l:()->Unit){
        listener = object : OnViewClickListener {
            override fun onAddClick() {
                l()
            }
        }
    }

    private var listener: OnViewClickListener?=null

    interface OnViewClickListener{
        fun onAddClick()
    }
}