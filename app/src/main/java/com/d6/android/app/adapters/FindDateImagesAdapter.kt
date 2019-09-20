package com.d6.android.app.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.support.annotation.Nullable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.d6.android.app.R
import com.d6.android.app.activities.ImagePagerActivity
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.widget.ScreenUtil
import com.d6.android.app.widget.SingleImageControllerListener
import com.d6.android.app.widget.ZoomableDraweeView
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import org.jetbrains.anko.startActivity

/**
 *
 */
class FindDateImagesAdapter(mData: ArrayList<String>) : BaseRecyclerAdapter<String>(mData, R.layout.item_find_date_image) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
//        width = (context.screenWidth()*0.9f).toInt()
//        space = (context.screenWidth()-width)/2
//        val imageView = holder.bind<View>(R.id.imageView)
//        imageView.layoutParams.width = width
//        imageView.requestLayout()
//        var layoutParams = imageView.getLayoutParams()
        return holder
    }

    override fun onBind(holder: ViewHolder, position: Int, data: String) {
        val imageView = holder.bind<ImageView>(R.id.imageView)
//        val layoutParams = imageView.getLayoutParams()
//        var viewWidth = ScreenUtil.getScreenWidth(context)
//
//        var controllerListener =object: BaseControllerListener<ImageInfo>() {
//            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
//                if (imageInfo == null) {
//                    return
//                }
//                var height = imageInfo.getHeight()
//
//                layoutParams.width = viewWidth
//                layoutParams.height = height
//                imageView.setLayoutParams(layoutParams);
//            }
//
//            override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
//                super.onIntermediateImageSet(id, imageInfo)
//            }
//
//            override fun onFailure(id: String?, throwable: Throwable?) {
//                super.onFailure(id, throwable)
//                throwable?.printStackTrace();
//            }
//        }
//
//        var controller = Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener)
//                .setUri(data).build()
//        imageView.setController(controller)


//        var controllerListener = SingleImageControllerListener(imageView)
//
//        var imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(data))
//        imageRequestBuilder.setRotationOptions(RotationOptions.autoRotate())
//
////        imageRequestBuilder.resizeOptions = ResizeOptions(reqWidth, reqHeight)
//        // 不支持图片渐进式加载，理由：https://github.com/facebook/fresco/issues/1204
//        imageRequestBuilder.setProgressiveRenderingEnabled(false);
//
//        var imageRequest = imageRequestBuilder.build();
//
//        var draweeControllerBuilder = Fresco.newDraweeControllerBuilder()
//        draweeControllerBuilder.setOldController(imageView.getController());
//        draweeControllerBuilder.setImageRequest(imageRequest);
//
//        if (controllerListener != null) {
//            draweeControllerBuilder.setControllerListener(controllerListener);
//        }
//
//        draweeControllerBuilder.setTapToRetryEnabled(false); // 在加载失败后，禁用点击重试功能
//        draweeControllerBuilder.setAutoPlayAnimations(true); // 自动播放gif动画
//        var draweeController = draweeControllerBuilder.build()
//        imageView.setController(draweeController)

//        imageView.setImageURI(data)


        FrescoUtils.loadImage(context,data,object:IResult<Bitmap>{
            override fun onResult(result: Bitmap?) {
                result?.let {
                    imageView.setImageBitmap(it)
                }
            }
        })

        imageView.setOnClickListener {
            context.startActivity<ImagePagerActivity>(ImagePagerActivity.URLS to mData, ImagePagerActivity.CURRENT_POSITION to position)
        }
    }

    override fun onViewRecycled(holder: ViewHolder?) {
        super.onViewRecycled(holder)

    }

    fun releaseImageViewResouce(imageView:ImageView) {
        if (imageView == null)
            return;
        var drawable = imageView.getDrawable()
        if (drawable != null) {
            var bitmapDrawable = drawable as BitmapDrawable
            var bitmap = bitmapDrawable.getBitmap()
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle()
            }
        }
    }
}