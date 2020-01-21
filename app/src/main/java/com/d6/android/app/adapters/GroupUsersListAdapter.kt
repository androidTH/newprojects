package com.d6.android.app.adapters

import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.Fans
import com.facebook.drawee.view.SimpleDraweeView

/**
 *
 */
class GroupUsersListAdapter(mData: ArrayList<Fans>) : BaseRecyclerAdapter<Fans>(mData, R.layout.item_groupuser) {
    override fun onBind(holder: ViewHolder, position: Int, data: Fans) {
        val groupuser_headView = holder.bind<SimpleDraweeView>(R.id.groupuser_headView)
        groupuser_headView.setImageURI(data.sPicUrl)
        var tv_groupusername = holder.bind<TextView>(R.id.tv_groupusername)
        tv_groupusername.text = data.sUserName
    }
}