package com.d6zone.android.app.utils

import android.content.Context
import android.widget.ImageView
import com.facebook.drawee.view.SimpleDraweeView
import com.youth.banner.loader.ImageLoader

/**
 * Created on 2018/1/9.
 */
class BannerLoader2(val aspectRatio: Float = 1.25f) : ImageLoader() {
    override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
        imageView?.let {
            if (it is SimpleDraweeView) {
                it.setImageURI(path.toString())
            }
        }
    }

    override fun createImageView(context: Context?): ImageView {
        val siv = SimpleDraweeView(context)
        siv.aspectRatio = aspectRatio
        return siv
    }
}