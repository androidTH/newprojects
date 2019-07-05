package com.d6.android.app.adapters

import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.ImagePagerActivity
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.startActivity

/**
 *
 */
class FindDateImagesAdapter(mData: ArrayList<String>) : BaseRecyclerAdapter<String>(mData, R.layout.item_find_date_image) {
    var width:Int = 0
    var space = 0
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val holder =  super.onCreateViewHolder(parent, viewType)
//        width = (context.screenWidth()*0.9f).toInt()
//        space = (context.screenWidth()-width)/2
//        val imageView = holder.bind<View>(R.id.imageView)
//        imageView.layoutParams.width = width
//        imageView.requestLayout()
        return holder
    }

    override fun onBind(holder: ViewHolder, position: Int, data: String) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
//        val params = imageView.layoutParams
//        if (position == 0) {
//            if (params is ViewGroup.MarginLayoutParams) {
//                params.leftMargin = space
//                params.rightMargin = context.dip(2)
//            }
//        }else if (position == mData.size - 1 && position > 0) {
//            if (params is ViewGroup.MarginLayoutParams) {
//                params.rightMargin = space
//                params.leftMargin = context.dip(2)
//            }
//        } else {
//            if (params is ViewGroup.MarginLayoutParams) {
//                params.leftMargin = context.dip(2)
//                params.rightMargin = context.dip(2)
//            }
//        }
//        imageView.layoutParams = params
        imageView.setImageURI(data)
        imageView.setOnClickListener {
            context.startActivity<ImagePagerActivity>(ImagePagerActivity.URLS to mData, ImagePagerActivity.CURRENT_POSITION to position)
        }
    }
}