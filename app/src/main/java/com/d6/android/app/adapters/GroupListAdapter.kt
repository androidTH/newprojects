package com.d6.android.app.adapters

import android.text.TextUtils
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.MyDate
import com.facebook.drawee.view.SimpleDraweeView

/**
 *
 */
class GroupListAdapter(mData: ArrayList<MyDate>) : BaseRecyclerAdapter<MyDate>(mData, R.layout.item_group) {
    override fun onBind(holder: ViewHolder, position: Int, data: MyDate) {
        val group_headView = holder.bind<SimpleDraweeView>(R.id.group_headView)
        group_headView.setImageURI(data.lookpics)
        var tv_groupname = holder.bind<TextView>(R.id.tv_groupname)
        tv_groupname.text = data.name
    }
}