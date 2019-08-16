package com.d6.android.app.fragments

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import com.d6.android.app.R
import com.d6.android.app.base.BaseNoBarFragment
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.fragment_image.*

/**
 * 图片Fragment
 */
class ImageFragment : BaseNoBarFragment() {
    companion object {
        fun newInstance(url: String, isBlur: Boolean): Fragment {
            val imageFragment = ImageFragment()
            val bundle = Bundle()
            bundle.putString("url", url)
            bundle.putBoolean("isBlur", isBlur)
            imageFragment.arguments = bundle
            return imageFragment
        }
    }

    override fun contentViewId() = R.layout.fragment_image
    override fun onFirstVisibleToUser() {
        val url: String?
        var isBlur = false
        if (arguments != null) {
            url = arguments.getString("url")
            isBlur = arguments.getBoolean("isBlur")
        } else {
            url = ""
        }
        val hierarchy = GenericDraweeHierarchyBuilder(resources)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(ProgressBarDrawable())
                .build()
        val uri = Uri.parse(url)
        val request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(IterativeBoxBlurPostProcessor(3, 5))
                .build()

        val ctrl: DraweeController
        //模糊
        ctrl = if (isBlur) {
            Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setTapToRetryEnabled(true)
                    .build()
        } else {
            Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setTapToRetryEnabled(true)
                    .build()
        }
        zoomDrawee.hierarchy = hierarchy
        zoomDrawee.controller = ctrl
        zoomDrawee.setOnClickListener { activity.onBackPressed() }
    }

    override fun onVisibleToUser() {}
    override fun onInvisibleToUser() {}
}
