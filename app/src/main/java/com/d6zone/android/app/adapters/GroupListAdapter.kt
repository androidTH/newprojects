package com.d6zone.android.app.adapters

import android.widget.TextView
import com.d6zone.android.app.R
import com.d6zone.android.app.base.adapters.BaseRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.models.NewGroupBean
import com.facebook.drawee.view.SimpleDraweeView

/**
 *
 */
class GroupListAdapter(mData: ArrayList<NewGroupBean>) : BaseRecyclerAdapter<NewGroupBean>(mData, R.layout.item_group) {
    override fun onBind(holder: ViewHolder, position: Int, data: NewGroupBean) {
        val group_headView = holder.bind<SimpleDraweeView>(R.id.group_headView)
        group_headView.setImageURI(data.sGroupPic)
        var tv_groupname = holder.bind<TextView>(R.id.tv_groupname)
        tv_groupname.text = "${data.sGroupName}"
    }
}