package com.d6.android.app.extentions

import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequestBuilder


fun SimpleDraweeView?.showBlur(path: String?, iterations: Int = 3, blurRadius: Int = 5) {
    if (path == null || this == null) {
        return
    }
    val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(path))
            .setPostprocessor(IterativeBoxBlurPostProcessor(iterations, blurRadius))
            .build()
    val controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .setOldController(this.controller)
            .build()
    this.controller = controller
}