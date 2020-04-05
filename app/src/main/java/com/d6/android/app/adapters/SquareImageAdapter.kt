package com.d6.android.app.adapters

import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.transition.Transition
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.d6.android.app.R
import com.d6.android.app.activities.ImagePagerActivity
import com.d6.android.app.activities.TrendDetailActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.startActivity

/**
 *
 */
class SquareImageAdapter(mData: ArrayList<String>,val type:Int = 0) : HFRecyclerAdapter<String>(mData, R.layout.item_list_square_image) {
    private var mSquare: Square = Square()
    private val mImages = ArrayList<String>()
    private var mBlurIndex = ArrayList<String>()
    private var mSeeFireIndex = ArrayList<String>()
    override fun onBind(holder: ViewHolder, position: Int, data: String) {
        val imageView = with(holder) { bind<SimpleDraweeView>(R.id.imageView) }
        var iv_lock = holder.bind<ImageView>(R.id.iv_lock)
        var iv_unlock = holder.bind<ImageView>(R.id.iv_unlock)
        var mIvBg = holder.bind<ImageView>(R.id.iv_firepic_bg)
        var iv_fire = holder.bind<TextView>(R.id.iv_fire)
        if (mBlurIndex != null && mBlurIndex.size > position) {
            var blurType = mBlurIndex[position]
            if (TextUtils.equals("2", blurType)) {
                if (TextUtils.equals("${mSquare!!.userid}", getLocalUserId())) {
                    iv_lock.visibility = View.GONE
                    iv_unlock.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    imageView.showBlur(data)
                    updateFirePics(position,mIvBg,imageView,iv_fire,data)
                } else {
                    iv_lock.visibility = View.VISIBLE
                    iv_unlock.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    imageView.showBlur(data)

                    iv_fire.visibility = View.GONE
                    mIvBg.visibility = View.GONE
                }
            } else if (TextUtils.equals("3", blurType)) {
                if (TextUtils.equals("${mSquare!!.userid}", getLocalUserId())) {
                    iv_lock.visibility = View.GONE
                    iv_unlock.visibility = View.GONE
                    imageView.showBlur(data)
                } else {
                    iv_unlock.visibility = View.VISIBLE
                    iv_lock.visibility = View.GONE
                    imageView.setImageURI(data)
                }
                updateFirePics(position,mIvBg,imageView,iv_fire,data)
            } else {
                iv_unlock.visibility = View.GONE
                iv_lock.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                imageView.setImageURI(data)
                updateFirePics(position,mIvBg,imageView,iv_fire,data)
            }
        } else {
            iv_lock.visibility = View.GONE
            iv_unlock.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            imageView.setImageURI(data)
            updateFirePics(position,mIvBg,imageView,iv_fire,data)
        }
        imageView.setOnClickListener {
            if (type == 1) {
                mSquare?.let {
                    mImages.clear()
                    val images = it.sSourceSquarePics?.split(",")
                    if (images != null) {
                        mImages.addAll(images.toList())
                    }
                    context.startActivity<ImagePagerActivity>(ImagePagerActivity.URLS to mImages, ImagePagerActivity.CURRENT_POSITION to position,
                            ImagePagerActivity.USERID to "${mSquare!!.userid}", ImagePagerActivity.mBEAN to mSquare,
                            ImagePagerActivity.SIfLovePics to "${mSquare?.sIfLovePics}",ImagePagerActivity.SIfSeePics to "${mSquare?.sIfSeePics}",ImagePagerActivity.SOURCEID to "${mSquare?.id}",ImagePagerActivity.ISANONYMOUS to "${mSquare.iIsAnonymous}")
                }
            } else {
                context.startActivity<TrendDetailActivity>(TrendDetailActivity.URLS to mData, TrendDetailActivity.CURRENT_POSITION to position,
                        "data" to (mSquare ?: Square()))
            }
        }
    }

    private fun updateFirePics(position:Int,mIvBg:ImageView,imageView:SimpleDraweeView,iv_fire:TextView,data: String){
        if (mSeeFireIndex != null && mSeeFireIndex.size > position) {
            var seeFireIndex = mSeeFireIndex[position]
            Log.i("squareImage","阅后即焚：${seeFireIndex}")
            if(TextUtils.equals("3",seeFireIndex)){
                mIvBg.visibility = View.GONE
                iv_fire.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                imageView.setImageURI("res:///"+R.mipmap.mask_fenhui_bg)
//                Glide.with(context).asBitmap().load(data).into(object : SimpleTarget<Bitmap>() {
//                    override fun onResourceReady(p0: Bitmap?, p1: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
//                        p0?.let {
//                            BitmapUtils.setVibraite(it, mIvBg)
//                        }
//                    }
//                })
//                iv_fire.text = "照片已焚毁"
//                var drawable = ContextCompat.getDrawable(context, R.mipmap.pic_firepic_icon)
//                setTopDrawable(drawable, iv_fire)
            }else if(TextUtils.equals("2",seeFireIndex)){
                iv_fire.visibility = View.VISIBLE
                mIvBg.visibility = View.GONE
                imageView.visibility = View.VISIBLE

                iv_fire.text = "阅后即焚"
                var drawable = ContextCompat.getDrawable(context, R.mipmap.pic_fire_icon)
                setTopDrawable(drawable, iv_fire)
                imageView.showBlur(data)
            }else{
                iv_fire.visibility = View.GONE
                mIvBg.visibility = View.GONE
                imageView.visibility = View.VISIBLE
            }
        }
    }

    fun bindSquare(square: Square) {
        this.mSquare = square
        this.mSquare.sIfLovePics?.split(",")?.toList()?.let {
            this.mBlurIndex.clear()
            this.mBlurIndex.addAll(it)
        }

        this.mSquare.sIfSeePics?.split(",")?.toList()?.let {
            this.mSeeFireIndex.clear()
            this.mSeeFireIndex.addAll(it)
        }
        Log.i("squareImage","内容：${square.content},打赏可见：${this.mSquare.sIfLovePics},阅后即焚：${this.mSquare.sIfSeePics}")
    }
}