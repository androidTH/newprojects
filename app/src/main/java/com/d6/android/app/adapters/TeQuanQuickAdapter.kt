package com.d6.android.app.adapters

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.Banner
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class TeQuanQuickAdapter(data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_men_auth, data) {

    override fun convert(helper: BaseViewHolder, data:String) {
        val iv_tqicon = helper.getView<ImageView>(R.id.iv_tqicon)
        val tv_tqname = helper.getView<TextView>(R.id.tv_tqname)
        val tv_tqtag = helper.getView<TextView>(R.id.tv_tqtag)
        val tv_tqdesc = helper.getView<TextView>(R.id.tv_tqdesc)
//        simpleDraweeView.setImageURI(data.picurl)
        tv_tqname.text = data
    }
}
