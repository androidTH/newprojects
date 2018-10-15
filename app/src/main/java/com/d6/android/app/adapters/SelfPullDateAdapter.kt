package com.d6.android.app.adapters

import com.d6.android.app.R
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.models.MyDate
import com.d6.android.app.models.Square
import com.d6.android.app.widget.SelfPullDateView
import com.d6.android.app.widget.SelfReleaseView
import com.d6.android.app.widget.TrendView

/**
 *
 */
class SelfPullDateAdapter(mData:ArrayList<MyAppointment>): HFRecyclerAdapter<MyAppointment>(mData, R.layout.item_list_pull_date) {
    override fun onBind(holder: ViewHolder, position: Int, data: MyAppointment) {
        val view = holder.bind<SelfPullDateView>(R.id.srv_view)
        view.update(data)
    }
}