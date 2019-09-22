package com.d6.android.app.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class VipPicsInfoQuickAdapter(data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_pic_info, data) {

    override fun convert(helper: BaseViewHolder, data: String) {
        var pic_simpledraweeview = helper.getView<SimpleDraweeView>(R.id.pic_simpledraweeview)
        pic_simpledraweeview.setImageURI(data)
        helper.setGone(R.id.iv_video_play,true)
        helper.addOnClickListener(R.id.iv_video_play)
    }
}
