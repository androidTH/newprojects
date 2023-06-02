package com.d6zone.android.app.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6zone.android.app.R
import com.d6zone.android.app.models.FriendBean
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class NoticeFriendsQuickAdapter(data: List<FriendBean>) : BaseQuickAdapter<FriendBean, BaseViewHolder>(R.layout.item_noticefriends, data) {

    override fun convert(helper: BaseViewHolder, data: FriendBean) {
        var mHeadView = helper.getView<SimpleDraweeView>(R.id.headView)
        mHeadView.setImageURI(data.sPicUrl)
//        val mIvClear = helper.getView<ImageView>(R.id.iv_clear)
        helper.addOnClickListener(R.id.iv_clear)
        helper.addOnClickListener(R.id.ll_clear)
    }
}
