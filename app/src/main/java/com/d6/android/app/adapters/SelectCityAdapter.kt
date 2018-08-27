package com.d6.android.app.adapters

import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.City

/**
 *
 */

class SelectCityAdapter(mData: ArrayList<City>) : BaseRecyclerAdapter<City>(mData, R.layout.item_city) {

    override fun onBind(holder: ViewHolder, position: Int, data: City) {
        val tv_name = holder.bind<TextView>(R.id.tv_name)
        tv_name.isSelected = data.isSelected
        tv_name.text = data.name
    }
}
