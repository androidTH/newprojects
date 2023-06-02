package com.d6zone.android.app.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6zone.android.app.R

/**
 * jinjiarui
 */
class AuthTipsQuickAdapter(data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.auth_tips, data) {

    override fun convert(helper: BaseViewHolder, data: String) {
         helper.setText(R.id.tv_desc,data);
    }
}
