package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.SquareTypeBean
import com.d6.android.app.utils.setLeftDrawable

/**
 *
 */
class SquareTypeAdapter(mData:ArrayList<SquareTypeBean>): BaseRecyclerAdapter<SquareTypeBean>(mData, R.layout.item_square_type) {
    override fun onBind(holder: ViewHolder, position: Int, data: SquareTypeBean) {
        val contentView = holder.bind<TextView>(R.id.tv_square_type)
        contentView.text = data.getmName()
        var drawable = ContextCompat.getDrawable(context,data.getmResId())
        setLeftDrawable(drawable,contentView)
    }
}