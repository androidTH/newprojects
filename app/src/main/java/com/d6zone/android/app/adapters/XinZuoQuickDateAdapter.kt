package com.d6zone.android.app.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6zone.android.app.R

/**
 * jinjiarui
 */
class XinZuoQuickDateAdapter(data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_default_drop_down, data) {

    override fun convert(helper: BaseViewHolder, data: String) {
         helper.setText(R.id.tv_constellation,data);
    }
}
