package com.d6zone.android.app.adapters

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.d6zone.android.app.R
import com.d6zone.android.app.base.adapters.BaseRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.models.DateType
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.textColor

/**
 *
 */
class DateTypeAdapter(mData:ArrayList<DateType>): BaseRecyclerAdapter<DateType>(mData, R.layout.item_grid_datetype) {

    override fun onBind(holder: ViewHolder, position: Int, data: DateType) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.iv_datetype_img)
        val tv_dateTypeName = holder.bind<TextView>(R.id.tv_datetype_name)
        val iv_pressok = holder.bind<ImageView>(R.id.iv_pressok)
        tv_dateTypeName.text = data.dateTypeName
        if(data.isSelected){
            tv_dateTypeName.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)
//            imageView.isSelected = true
            iv_pressok.visibility = View.VISIBLE
            imageView.setImageURI(data.selectedimgUrl)
        }else{
            tv_dateTypeName.textColor = ContextCompat.getColor(context,R.color.color_666666)
//            imageView.isSelected = false
            iv_pressok.visibility = View.INVISIBLE
            imageView.setImageURI(data.imgUrl)
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