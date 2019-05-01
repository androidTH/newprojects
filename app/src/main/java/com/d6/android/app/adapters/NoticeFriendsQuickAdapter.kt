package com.d6.android.app.adapters

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.Fans
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class NoticeFriendsQuickAdapter(data: List<Fans>) : BaseQuickAdapter<Fans, BaseViewHolder>(R.layout.item_noticefriends, data) {

    override fun convert(helper: BaseViewHolder, data: Fans) {
        var mHeadView = helper.getView<SimpleDraweeView>(R.id.headView)
        mHeadView.setImageURI(data.sPicUrl)
//        val mIvClear = helper.getView<ImageView>(R.id.iv_clear)
        helper.addOnClickListener(R.id.iv_clear)
    }
}
