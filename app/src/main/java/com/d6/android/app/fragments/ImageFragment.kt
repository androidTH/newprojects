package com.d6.android.app.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.d6.android.app.R
import com.d6.android.app.activities.ImagePagerActivity
import com.d6.android.app.base.BaseNoBarFragment
import com.d6.android.app.utils.BitmapUtils
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.facebook.binaryresource.FileBinaryResource
import com.facebook.cache.common.SimpleCacheKey
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.fragment_image.*
import java.lang.Exception
/**
 * 图片Fragment
 */
class ImageFragment : BaseNoBarFragment() {

    companion object {
        fun newInstance(url: String, isBlur: Boolean,isFire:Boolean): Fragment {
            val imageFragment = ImageFragment()
            val bundle = Bundle()
            bundle.putString("url", url)
            bundle.putBoolean("isBlur", isBlur)
            bundle.putBoolean("isFire", isFire)
            imageFragment.arguments = bundle
            return imageFragment
        }
    }

    var url: String?=null
    override fun contentViewId() = R.layout.fragment_image

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var isBlur = false
        var isFire = false
        if (arguments != null) {
            url = arguments.getString("url")
            isBlur = arguments.getBoolean("isBlur")
            isFire = arguments.getBoolean("isFire")
        } else {
            url = ""
        }
        try{
            if(isFire){
                doFirePics(isFire)
            }else{
                iv_firepic_gb.visibility = View.GONE
                updatePicUrl(activity,"${url}",isBlur)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        sampimgview.setOnClickListener {
            if(sampimgview!=null){
                sampimgview.recycle()
            }
            if(activity!=null){
                (activity as ImagePagerActivity).onBackPressed()
            }
        }

        sampimgview.setOnLongClickListener(object:View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                if(activity!=null){
                    (activity as ImagePagerActivity).startCountDownTimer()
                }
                Log.i("setOnTouchListener","开始了")
                return true
            }
        })

        sampimgview.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_UP-> {
                    if (activity != null) {
                        (activity as ImagePagerActivity).cancelTimer()
                    }
                    Log.i("setOnTouchListener", "结束了")
                }
            }
            false
        }

        iv_firepic_gb.setOnClickListener {
            if(sampimgview!=null){
                sampimgview.recycle()
            }
            if(activity!=null){
                (activity as ImagePagerActivity).onBackPressed()
            }
        }
    }

    fun updatePicUrl(mActivity: Activity, url:String, isBlur:Boolean,isFirePic:Boolean = false){
//        tv_tag.text = "${url}"
        sampimgview.setMaxScale(15f)
        sampimgview.setZoomEnabled(true)
        try{
            FrescoUtils.loadImage(context,url,object: IResult<Bitmap> {
                override fun onResult(result: Bitmap?) {
                    try{
                        result?.let {
                            var  resource = Fresco.getImagePipelineFactory().getMainFileCache().getResource(SimpleCacheKey(url))
                            if(resource!=null){
                                var fileResouce= resource as FileBinaryResource
                                var mlistWH = BitmapUtils.getWidthHeight(fileResouce.getFile().path)
//                            var width = AppScreenUtils.getScreenWidth(activity)
//                            var scaleW = width / mlistWH[0].toFloat()
                                if (BitmapUtils.isLongImage(mActivity,mlistWH)) {
                                    try{
                                        sampimgview.setImage(ImageSource.uri(fileResouce.getFile().path))
                                        sampimgview.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START)
                                        sampimgview.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER_IMMEDIATE)
                                    }catch (e: Exception){
                                        e.printStackTrace()
                                        if(sampimgview!=null){
                                            sampimgview.recycle()
                                        }
                                        if(activity!=null){
                                            (activity as ImagePagerActivity).onBackPressed()
                                        }
                                    }
                                } else {
                                    sampimgview.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
                                    sampimgview.setImage(ImageSource.uri(fileResouce.getFile().path))
//                                sampimgview.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
//                                sampimgview.setImage(ImageSource.bitmap(it), ImageViewState(scaleW, PointF(0f, 0f), 0))
                                }
                            }else{
                                if(it!=null){
                                    sampimgview.setImage(ImageSource.bitmap(it))
                                }else{
                                    sampimgview.visibility = View.GONE
                                    zoomDrawee.visibility = View.VISIBLE
                                    setZoomableDraweeView("${url}",isBlur)
                                }
                            }
                        }
                    }catch(e:Exception){
                        e.printStackTrace()
                        if(activity!=null){
                            (activity as ImagePagerActivity).onBackPressed()
                        }
                    }
                }
            })
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    fun doFirePics(isFirePic:Boolean = false){
        if(isFirePic){
            Log.e("smallsoho", "swatch为空:${url}")
            sampimgview.visibility = View.GONE
            iv_firepic_gb.visibility = View.VISIBLE
//            Glide.with(context).asBitmap().load(url).into(object : SimpleTarget<Bitmap>() {
//                override fun onResourceReady(p0: Bitmap?, p1: Transition<in Bitmap>?) {
//                    p0?.let {
//                        BitmapUtils.setVibraiteCanvasBitmap(p0,iv_firepic_gb,drawview_firepicbg)
//                    }
//                }
//            })
        }
    }


    private fun setZoomableDraweeView(url:String,isBlur:Boolean){
        val hierarchy = GenericDraweeHierarchyBuilder(resources)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_START)
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
        zoomDrawee.setOnClickListener {
            Fresco.getImagePipeline().clearMemoryCaches()
            if(activity!=null){
                (activity as ImagePagerActivity).onBackPressed()
            }
        }
    }

    override fun onFirstVisibleToUser() {

    }

    override fun onVisibleToUser() {
        Log.i("FirstVisibleToUser","onVisibleToUser")
    }
    override fun onInvisibleToUser() {}
}
