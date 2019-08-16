package com.d6.android.app.adapters

import android.os.Build
import android.text.Html
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.Fans
import com.d6.android.app.models.FriendBean
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class AuthTipsQuickAdapter(data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.auth_tips, data) {

    override fun convert(helper: BaseViewHolder, data: String) {
         helper.setText(R.id.tv_desc,data);
    }
}
