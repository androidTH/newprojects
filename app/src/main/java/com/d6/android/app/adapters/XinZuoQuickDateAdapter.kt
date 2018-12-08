package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.application.D6Application
import com.d6.android.app.models.MyDate
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class XinZuoQuickDateAdapter(data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_default_drop_down, data) {

    override fun convert(helper: BaseViewHolder, data: String) {
         helper.setText(R.id.tv_constellation,data);
    }
}
