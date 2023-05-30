package com.d6.android.app.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.ImageLocalPagerActivity
import com.d6.android.app.activities.SimplePlayer
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.AddImage
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.mLocalBlurMap
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.screenWidth
import com.d6.android.app.utils.visible
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

/**
 *
 */
class AddImageV2Adapter(mData:ArrayList<AddImage>): BaseRecyclerAdapter<AddImage>(mData, R.layout.item_grid_add_image) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        val rootLayout = holder.bind<View>(R.id.root_layout)
        val size = (context.screenWidth()-2*context.dip(16)-2*context.dip(12)-2*context.dip(6))/3
        rootLayout.layoutParams.width = size
        rootLayout.layoutParams.height = size
        rootLayout.requestLayout()
        return holder
    }

    override fun onBind(holder: ViewHolder, position: Int, data: AddImage) {
        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        val ivDeleteView = holder.bind<ImageView>(R.id.ivDeleteView)
        val ivVideoPlay = holder.bind<ImageView>(R.id.iv_video_play)
        imageView.visibility = View.VISIBLE
        var tv_firepic = holder.bind<TextView>(R.id.tv_firepic)
        if (data.type == 1) {//添加。
            ivDeleteView.gone()
            ivVideoPlay.gone()
            tv_firepic.visibility = View.GONE
            imageView.setImageResource(R.mipmap.comment_addphoto_icon)
        } else if(data.type==2){
            ivVideoPlay.visibility = View.VISIBLE
            ivDeleteView.visibility = View.VISIBLE
            tv_firepic.visibility = View.GONE
            imageView.setImageURI(data.imgUrl)
            ivDeleteView.setImageResource(R.mipmap.deleted_video_bg)
        }else {
            ivVideoPlay.visibility = View.GONE
            ivDeleteView.visible()
            ivDeleteView.setImageResource(R.mipmap.comment_photo_edit)
            ivVideoPlay.gone()
            if(data.mBluer){
                imageView.showBlur(data.imgUrl)
            }else{
                imageView.setImageURI(data.imgUrl)
            }

            if(data.mFirePic){
                tv_firepic.visibility = View.VISIBLE
            }else{
                tv_firepic.visibility = View.GONE
            }
        }

        imageView.setOnClickListener {
            if (data.type == 1) {
                listener?.onAddClick(data.type)
            }else if(data.type==2){
                startVideoActivity(data.path)
            }else{
                startActivity(mData,position)
            }
        }

        ivDeleteView.setOnClickListener {
            if(data.type==2){
                mData.remove(data)
                notifyDataSetChanged()
                listener?.onAddClick(data.type)
//                mData.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
            }else{
                startActivity(mData,position)
            }
        }
    }

    fun startActivity(mData:ArrayList<AddImage>,pos:Int){
        var resultList = ArrayList<String>()
        mLocalBlurMap.clear()
        Const.mLocalFirePicsMap.clear()
        mData.forEach {
            if(it.type!=1){
                var path = it.imgUrl.replace("file://","")
                resultList.add(path)
                mLocalBlurMap.put(path,it.mBluer)
                Const.mLocalFirePicsMap.put(path,it.mFirePic)
            }
        }

        (context as BaseActivity).startActivityForResult<ImageLocalPagerActivity>(1000, ImageLocalPagerActivity.TYPE to 0,
                ImageLocalPagerActivity.CURRENT_POSITION to pos,ImageLocalPagerActivity.URLS to resultList,
                "delete" to true,"paypoints" to true,"firepics" to true)
    }

    fun startVideoActivity(path:String){
        (context as BaseActivity).startActivity<SimplePlayer>("videoPath" to path,"videoType" to "0")
    }

    fun setOnAddClickListener(l:(type:Int)->Unit){
        listener = object : OnViewClickListener {
            override fun onAddClick(type:Int) {
                l(type)
            }
        }
    }

    private var listener: OnViewClickListener?=null

    interface OnViewClickListener{
        fun onAddClick(type:Int)
    }
}