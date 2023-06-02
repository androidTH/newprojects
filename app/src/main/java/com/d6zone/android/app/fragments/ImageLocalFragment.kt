package com.d6zone.android.app.fragments

import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.d6zone.android.app.R
import com.d6zone.android.app.activities.ImageLocalPagerActivity
import com.d6zone.android.app.base.BaseNoBarFragment
import com.d6zone.android.app.utils.AppUtils
import com.d6zone.android.app.utils.BitmapUtils
import com.d6zone.android.app.widget.ScreenUtil
import com.d6zone.android.app.widget.frescohelper.FrescoUtils
import com.d6zone.android.app.widget.frescohelper.IResult
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.fragment_image.*
import java.lang.Exception

/**
 * 图片Fragment
 */
class ImageLocalFragment : BaseNoBarFragment() {
    companion object {
        fun newInstance(url: String, isBlur: Boolean): Fragment {
            val imageFragment = ImageLocalFragment()
            val bundle = Bundle()
            bundle.putString("url", url)
            bundle.putBoolean("isBlur", isBlur)
            imageFragment.arguments = bundle
            return imageFragment
        }
    }

    override fun contentViewId() = R.layout.fragment_image

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val url: String?
        var isBlur = false
        if (arguments != null) {
            url = arguments.getString("url")
        } else {
            url = ""
        }
        setNewPic(url,isBlur)
    }

    fun setNewPic(path:String?,isBlur:Boolean){
        sampimgview.setMaxScale(15f)
        sampimgview.setZoomEnabled(true)
        FrescoUtils.loadImage(context,"file://"+path,object: IResult<Bitmap> {
            override fun onResult(result: Bitmap?) {
                try{
                    result?.let {
                        var mlistWH = BitmapUtils.getWidthHeight("${path}")
                        val width = ScreenUtil.getScreenWidth(AppUtils.context)
                        val scaleW = width / mlistWH[0].toFloat()
                        if (BitmapUtils.isLongImage(context,mlistWH)) {
                            sampimgview.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START)
                            sampimgview.setImage(ImageSource.uri("${path}"))
                            sampimgview.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER_IMMEDIATE)
                        } else {
                            sampimgview.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
                            sampimgview.setImage(ImageSource.bitmap(it), ImageViewState(scaleW, PointF(0f, 0f), 0))
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        })

        sampimgview.setOnClickListener {
//            var mImagelocals = Imagelocals(urls,1,0)
//            ObserverManager.getInstance().notifyObservers(mImagelocals)
            Fresco.getImagePipeline().clearMemoryCaches()
            (activity as ImageLocalPagerActivity).closeImageLocalPager()
        }
    }

    override fun onFirstVisibleToUser() {

    }

    override fun onVisibleToUser() {
        Log.i("FirstVisibleToUser","onVisibleToUser")
    }
    override fun onInvisibleToUser() {}
}
