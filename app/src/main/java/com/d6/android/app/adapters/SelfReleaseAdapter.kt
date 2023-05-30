package com.d6.android.app.adapters

import com.d6.android.app.R
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.MyDate
import com.d6.android.app.models.Square
import com.d6.android.app.widget.SelfReleaseView
import com.d6.android.app.widget.TrendView

/**
 *
 */
class SelfReleaseAdapter(mData:ArrayList<MyDate>): HFRecyclerAdapter<MyDate>(mData, R.layout.item_list_self_release) {
    override fun onBind(holder: ViewHolder, position: Int, data: MyDate) {
        val view = holder.bind<SelfReleaseView>(R.id.srv_view)
        view.update(data)
    }
}