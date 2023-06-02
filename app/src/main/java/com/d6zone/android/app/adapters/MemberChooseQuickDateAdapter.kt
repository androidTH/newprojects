package com.d6zone.android.app.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6zone.android.app.R
import com.d6zone.android.app.models.MemberChoose

/**
 * jinjiarui
 */
class MemberChooseQuickDateAdapter(data: List<MemberChoose>) : BaseQuickAdapter<MemberChoose, BaseViewHolder>(R.layout.item_default_drop_down, data) {

    override fun convert(helper: BaseViewHolder, data: MemberChoose) {
         helper.setText(R.id.tv_constellation,data.title);
    }
}
