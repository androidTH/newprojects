package com.d6.android.app.adapters

import android.util.Log
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.SquareTag
import com.d6.android.app.models.UserTag
import com.facebook.drawee.view.SimpleDraweeView

/**
 *
 */
class UserTagAdapter(mData:ArrayList<UserTag>): BaseRecyclerAdapter<UserTag>(mData, R.layout.item_list_user_tag) {
    override fun onBind(holder: ViewHolder, position: Int, data: UserTag) {
        val contentView = holder.bind<TextView>(R.id.tv_content)
        contentView.text = data.content
//        contentView.setBackgroundResource(data.color)
    }
}