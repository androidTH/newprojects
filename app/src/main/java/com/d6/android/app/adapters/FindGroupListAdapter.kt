package com.d6.android.app.adapters

import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.NewGroupBean
import com.facebook.drawee.view.SimpleDraweeView

/**
 *
 */
class FindGroupListAdapter(mData: ArrayList<NewGroupBean>) : BaseRecyclerAdapter<NewGroupBean>(mData, R.layout.item_findgroup) {
    override fun onBind(holder: ViewHolder, position: Int, data: NewGroupBean) {
        val group_headView = holder.bind<SimpleDraweeView>(R.id.group_headView)
        group_headView.setImageURI(data.sGroupPic)
        var tv_groupname = holder.bind<TextView>(R.id.tv_groupname)
        tv_groupname.text = "${data.sGroupName}"

        var tv_groupnum = holder.bind<TextView>(R.id.tv_groupnums)
        tv_groupnum.text = "${data.iMemberCount}人"

        var tv_more = holder.bind<TextView>(R.id.tv_more)
        tv_more.setOnClickListener {

        }
    }
}