package com.d6.android.app.utils

import android.content.Context
import android.widget.ImageView
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.youth.banner.loader.ImageLoader
import org.jetbrains.anko.dip

/**
 * Created on 2018/1/9.
 */
class BannerLoader(val aspectRatio: Float = 1.456f) : ImageLoader() {
    override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
        imageView?.let {
            if (it is SimpleDraweeView) {
                it.setImageURI(path.toString())
            }
        }
    }

    override fun createImageView(context: Context?): ImageView {
        val siv = SimpleDraweeView(context)
        context?.let {
            siv.hierarchy.roundingParams = RoundingParams.fromCornersRadius(context.dip(6).toFloat())
        }
        siv.aspectRatio = aspectRatio
        return siv
    }
}