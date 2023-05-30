package com.d6.android.app.adapters

import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.Banner
import com.d6.android.app.utils.screenWidth
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.dip

/**
 *
 */
class BannerAdapter(mData: ArrayList<Banner>) : BaseRecyclerAdapter<Banner>(mData, R.layout.item_banner) {
    var width:Int = 0
    var space = 0
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val holder =  super.onCreateViewHolder(parent, viewType)
        width = (context.screenWidth()*0.95f).toInt()
//        width = (context.screenWidth())
        space = (context.screenWidth()-width)/2
        val imageView = holder.bind<View>(R.id.imageView)
        imageView.layoutParams.width = width
        imageView.requestLayout()
        return holder
    }

    override fun onBind(holder: ViewHolder, position: Int, data: Banner) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        val params = imageView.layoutParams
        if (position == 0) {
            if (params is ViewGroup.MarginLayoutParams) {
                params.leftMargin = space
                params.rightMargin = context.dip(2.5f)
            }
        }else if (position == mData.size - 1 && position > 0) {
            if (params is ViewGroup.MarginLayoutParams) {
                params.rightMargin = space
                params.leftMargin = context.dip(2.5f)
            }
        } else {
            if (params is ViewGroup.MarginLayoutParams) {
                params.leftMargin = context.dip(2.5f)
                params.rightMargin = context.dip(2.5f)
            }
        }
        imageView.layoutParams = params
        imageView.setImageURI(data.picurl)
    }
}