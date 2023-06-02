package com.d6zone.android.app.adapters

import com.d6zone.android.app.R
import com.d6zone.android.app.base.adapters.HFRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.models.MyDate
import com.d6zone.android.app.widget.SelfReleaseView

/**
 *
 */
class SelfReleaseAdapter(mData:ArrayList<MyDate>): HFRecyclerAdapter<MyDate>(mData, R.layout.item_list_self_release) {
    override fun onBind(holder: ViewHolder, position: Int, data: MyDate) {
        val view = holder.bind<SelfReleaseView>(R.id.srv_view)
        view.update(data)
    }
}