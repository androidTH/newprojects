package com.d6.android.app.adapters

import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.UserTag

/**
 *
 */
class CardTagAdapter(mData:ArrayList<UserTag>): BaseRecyclerAdapter<UserTag>(mData, R.layout.item_list_card_otheruser_tag) {
    override fun onBind(holder: ViewHolder, position: Int, data: UserTag) {
        val contentView = holder.bind<TextView>(R.id.tv_content)
        contentView.text = data.content
    }
}