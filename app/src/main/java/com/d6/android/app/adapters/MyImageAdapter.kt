package com.d6.android.app.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.d6.android.app.R
import com.d6.android.app.activities.ImagePagerActivity
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.AddImage
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.screenWidth
import com.d6.android.app.utils.visible
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

/**
 *
 */
class MyImageAdapter(mData: ArrayList<AddImage>) : BaseRecyclerAdapter<AddImage>(mData, R.layout.item_list_my_image) {

    var mRes:Int=R.mipmap.ic_add_v2bg

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        val rootLayout = holder.bind<View>(R.id.root_layout)
        val size = (context.screenWidth() - 2 * context.dip(11) - 2 * context.dip(11) - 3 * context.dip(6)) / 4
        rootLayout.layoutParams.width = size
        rootLayout.layoutParams.height = size
        rootLayout.requestLayout()
        return holder
    }

    override fun onBind(holder: ViewHolder, position: Int, data: AddImage) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        if (data.type == 1) {//添加。
            imageView.setImageURI("res:///" + mRes)
        } else {
            imageView.setImageURI(data.imgUrl)
        }
    }

    fun setOnAddClickListener(l: () -> Unit) {
        listener = object : OnViewClickListener {
            override fun onAddClick() {
                l()
            }
        }
    }

    private var listener: OnViewClickListener? = null

    interface OnViewClickListener {
        fun onAddClick()
    }
}