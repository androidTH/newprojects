package com.d6.android.app.adapters

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class NoticeFriendsQuickAdapter(data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_noticefriends, data) {

    override fun convert(helper: BaseViewHolder, data: String) {
        var mHeadView = helper.getView<SimpleDraweeView>(R.id.headView)
        mHeadView.setImageURI("http://p22l7xdxa.bkt.clouddn.com/1535603825725.jpg?imageMogr2/auto-orient/thumbnail/200x200/quality/100")
//        val mIvClear = helper.getView<ImageView>(R.id.iv_clear)
        helper.addOnClickListener(R.id.iv_clear)
    }
}
