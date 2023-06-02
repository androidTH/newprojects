package com.d6zone.android.app.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6zone.android.app.R
import com.facebook.drawee.view.SimpleDraweeView

/**
 *
 */
class ChatDatelmageAdapter(mPics: ArrayList<String>, val type:Int = 0) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_chat_date_image,mPics) {
    override fun convert(helper: BaseViewHolder?, item: String?) {
        helper?.let {
            val imageView = it.getView<SimpleDraweeView>(R.id.imageView)
            imageView.setImageURI(item)
        }
    }

}