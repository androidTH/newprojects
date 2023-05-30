package com.d6.android.app.adapters

import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.SquareTag
import com.facebook.drawee.view.SimpleDraweeView

/**
 * Created on 2017/12/17.
 */
class SquareTagAdapter(mData:ArrayList<SquareTag>):BaseRecyclerAdapter<SquareTag>(mData, R.layout.item_list_square_tag) {
    override fun onBind(holder: ViewHolder, position: Int, data: SquareTag) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        imageView.setImageURI(data.imgUrl)
        holder.setText(R.id.tv_content,data.content)
        holder.setSelected(R.id.tv_content,data.isSelected)
    }
}