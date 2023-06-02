package com.d6zone.android.app.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6zone.android.app.R
import com.d6zone.android.app.models.UserGiftBeans
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class ReceiveGiftListQuickAdapter(data: List<UserGiftBeans>) : BaseQuickAdapter<UserGiftBeans, BaseViewHolder>(R.layout.layout_receivegiftitem, data) {

    override fun convert(helper: BaseViewHolder, data: UserGiftBeans) {
          var sampimgview_gift = helper.getView<SimpleDraweeView>(R.id.sampimgview_gift)
          sampimgview_gift.setImageURI(data.giftIcon)
          helper.setText(R.id.gift_name,data.giftName+"x"+data.giftNum)
    }
}
