package com.d6.android.app.adapters

import android.graphics.Bitmap
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class VipPicsInfoQuickAdapter(data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_pic_info, data) {

    override fun convert(helper: BaseViewHolder, data: String) {
        var pic_simpledraweeview = helper.getView<ImageView>(R.id.pic_simpledraweeview)
        var iv_bg = helper.getView<ImageView>(R.id.iv_bg)
        FrescoUtils.loadImage(mContext,data,object: IResult<Bitmap> {
            override fun onResult(result: Bitmap?) {
                result?.let {
                    pic_simpledraweeview.setImageBitmap(it)
//                    var layoutparams = iv_bg.layoutParams
//                    layoutparams.height = it.height
//                    iv_bg.layoutParams = layoutparams
                }
            }
        })
//        pic_simpledraweeview.setImageURI(data)
        helper.setGone(R.id.iv_video_play,false)
//        helper.addOnClickListener(R.id.iv_video_play)
    }
}
