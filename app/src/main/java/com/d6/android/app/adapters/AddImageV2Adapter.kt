package com.d6.android.app.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.d6.android.app.R
import com.d6.android.app.activities.BLBeautifyImageActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.AddImage
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.screenWidth
import com.d6.android.app.utils.visible
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam

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
        if (data.type == 1) {//添加。
            ivDeleteView.gone()
            imageView.setImageResource(R.mipmap.comment_addphoto_icon)
        } else {
            ivDeleteView.visible()
            imageView.setImageURI(data.imgUrl)
        }

        imageView.setOnClickListener {
            if (data.type == 1) {
                listener?.onAddClick()
            }
        }
        ivDeleteView.setOnClickListener {
//            mData.remove(data)
//            notifyDataSetChanged()
            startActivity(mData,position)
        }
    }

    fun startActivity(mData:ArrayList<AddImage>,pos:Int){
        var param:BLBeautifyParam = BLBeautifyParam()//data.imgUrl.replace("file://","")
        param.index = pos
        mData.forEach {
            if(it.type == 0){
                param.images.add(it.imgUrl.replace("file://",""))
            }
        }
        (context as BaseActivity).startActivityForResult<BLBeautifyImageActivity>(BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE,BLBeautifyParam.KEY to param);
    }

    fun setOnAddClickListener(l:()->Unit){
        listener = object : OnViewClickListener {
            override fun onAddClick() {
                l()
            }
        }
    }

    private var listener: OnViewClickListener?=null

    interface OnViewClickListener{
        fun onAddClick()
    }
}