package com.d6.android.app.adapters

import com.d6.android.app.R
import com.d6.android.app.activities.ImagePagerActivity
import com.d6.android.app.activities.TrendDetailActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.Square
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.startActivity

/**
 *
 */
class SquareImageAdapter(mData: ArrayList<String>,val type:Int = 0) : HFRecyclerAdapter<String>(mData, R.layout.item_list_square_image) {
    private var square: Square? = null
    override fun onBind(holder: ViewHolder, position: Int, data: String) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        imageView.setImageURI(data)
        imageView.setOnClickListener {
            if (type == 1) {
                context.startActivity<ImagePagerActivity>(TrendDetailActivity.URLS to mData, TrendDetailActivity.CURRENT_POSITION to position)
            } else {
                context.startActivity<TrendDetailActivity>(TrendDetailActivity.URLS to mData, TrendDetailActivity.CURRENT_POSITION to position,
                        "data" to (square ?: Square()))
            }
        }
    }

    fun bindSquare(square: Square) {
        this.square = square
    }
}