package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.UserTag
import com.d6.android.app.utils.AppUtils

/**
 *
 */
class UserTagAdapter(mData:ArrayList<UserTag>): BaseRecyclerAdapter<UserTag>(mData, R.layout.item_list_card_user_tag) {//item_list_card_user_tag
    override fun onBind(holder: ViewHolder, position: Int, data: UserTag) {
        val contentView = holder.bind<TextView>(R.id.tv_content)
        contentView.text = data.content
        var drawable = ContextCompat.getDrawable(context,data.color)
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
        contentView.setCompoundDrawables(drawable,null,null,null)
        contentView.text = data.content
        AppUtils.setUserInfoTvTag(context,data.content,0,2,contentView)
    }
}