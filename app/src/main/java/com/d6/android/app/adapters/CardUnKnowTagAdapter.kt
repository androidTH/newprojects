package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.UserUnKnowTag

/**
 *
 */
class CardUnKnowTagAdapter(mData:ArrayList<UserUnKnowTag>): BaseRecyclerAdapter<UserUnKnowTag>(mData, R.layout.item_list_car_unknowtag) {
    override fun onBind(holder: ViewHolder, position: Int, data: UserUnKnowTag) {
        val titleView = holder.bind<TextView>(R.id.tv_title)
        var drawable = ContextCompat.getDrawable(context,data.color)
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
        titleView.setCompoundDrawables(drawable,null,null,null)
        titleView.text = data.title

        val contentView = holder.bind<TextView>(R.id.tv_content)
        contentView.text = data.content
    }
}