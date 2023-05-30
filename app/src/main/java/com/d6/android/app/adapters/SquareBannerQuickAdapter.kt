package com.d6.android.app.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.Banner
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class SquareBannerQuickAdapter(data: List<Banner>) : BaseQuickAdapter<Banner, BaseViewHolder>(R.layout.item_banner, data) {

    override fun convert(helper: BaseViewHolder, data:Banner) {
        val simpleDraweeView = helper.getView<SimpleDraweeView>(R.id.imageView)
        simpleDraweeView.setImageURI(data.picurl)
    }
}
