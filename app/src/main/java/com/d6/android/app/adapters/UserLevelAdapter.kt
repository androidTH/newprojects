package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.UserLevel

/**
 *
 */
class UserLevelAdapter(mData:ArrayList<UserLevel>): BaseRecyclerAdapter<UserLevel>(mData, R.layout.item_list_type) {
    override fun onBind(holder: ViewHolder, position: Int, data: UserLevel) {
        val contentView = holder.bind<TextView>(R.id.tv_content)
        contentView.text = String.format("%s",data.classesname)
        if (data.isSelected) {
            contentView.setTextColor(ContextCompat.getColor(context,R.color.orange_f6a))
        } else {
            contentView.setTextColor(ContextCompat.getColor(context,R.color.textColor))
        }

        contentView.setOnClickListener {
            data.isSelected = !data.isSelected
            notifyDataSetChanged()
        }

    }
}