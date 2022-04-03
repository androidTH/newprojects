package com.d6.android.app.adapters

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.FindGroupBean
import com.d6.android.app.models.GiftBeans
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class ReceiveGiftListQuickAdapter(data: List<GiftBeans>) : BaseQuickAdapter<GiftBeans, BaseViewHolder>(R.layout.layout_receivegiftitem, data) {

    override fun convert(helper: BaseViewHolder, data: GiftBeans) {
          var sampimgview_gift = helper.getView<SimpleDraweeView>(R.id.sampimgview_gift)
          sampimgview_gift.setImageURI(data.icon)
          helper.setText(R.id.gift_name,data.name+"x"+data.loveNum)
    }
}
