package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.d6.android.app.R
import com.d6.android.app.activities.ImagePagerActivity
import com.d6.android.app.activities.TrendDetailActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.Square
import com.d6.android.app.utils.getLocalUserId
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.startActivity

/**
 *
 */
class SquareImageAdapter(mData: ArrayList<String>,val type:Int = 0) : HFRecyclerAdapter<String>(mData, R.layout.item_list_square_image) {
    private var mSquare: Square = Square()
    private val mImages = ArrayList<String>()
    private var mBlurIndex = ArrayList<String>()
    override fun onBind(holder: ViewHolder, position: Int, data: String) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        var iv_lock = holder.bind<ImageView>(R.id.iv_lock)
        var iv_unlock = holder.bind<ImageView>(R.id.iv_unlock)
//        imageView.postDelayed(object:Runnable{
//            override fun run() {
                if(mBlurIndex!=null&&mBlurIndex.size>position){
                    var blurType = mBlurIndex[position]
                    if(TextUtils.equals("2",blurType)){
                        if(TextUtils.equals("${mSquare!!.userid}", getLocalUserId())){
                            iv_lock.visibility = View.GONE
                            iv_unlock.visibility = View.GONE
                            imageView.showBlur(data)
                        }else{
                            iv_lock.visibility = View.VISIBLE
                            iv_unlock.visibility = View.GONE
                            imageView.showBlur(data)
                        }
                    }else if(TextUtils.equals("3",blurType)){
                        if(TextUtils.equals("${mSquare!!.userid}", getLocalUserId())){
                            iv_lock.visibility = View.GONE
                            iv_unlock.visibility = View.GONE
                            imageView.showBlur(data)
                        }else{
                            iv_unlock.visibility = View.VISIBLE
                            iv_lock.visibility = View.GONE
                            imageView.setImageURI(data)
                        }
                    }else{
                        iv_unlock.visibility = View.GONE
                        iv_lock.visibility = View.GONE
                        imageView.setImageURI(data)
                    }
                }else{
                    iv_lock.visibility = View.GONE
                    iv_unlock.visibility = View.GONE
                    imageView.setImageURI(data)
                }
//            }
//        },200)

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
                            ImagePagerActivity.SIfLovePics to "${mSquare?.sIfLovePics}",ImagePagerActivity.SOURCEID to "${mSquare?.id}",ImagePagerActivity.ISANONYMOUS to "${mSquare.iIsAnonymous}")
                }
            } else {
                context.startActivity<TrendDetailActivity>(TrendDetailActivity.URLS to mData, TrendDetailActivity.CURRENT_POSITION to position,
                        "data" to (mSquare ?: Square()))
            }
        }
    }

    fun bindSquare(square: Square) {
        this.mSquare = square
        this.mSquare.sIfLovePics?.split(",")?.toList()?.let {
            this.mBlurIndex.clear()
            this.mBlurIndex.addAll(it)
        }
    }
}