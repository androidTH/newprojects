package com.d6zone.android.app.adapters

import com.d6zone.android.app.R
import com.d6zone.android.app.base.adapters.BaseRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.models.Image

/**
 * Created on 2017/12/21.
 */
class ChooseImageAdapter(mData:ArrayList<Image>):BaseRecyclerAdapter<Image>(mData, R.layout.item_choose_image) {
    override fun onBind(holder: ViewHolder, position: Int, data: Image) {

    }
}