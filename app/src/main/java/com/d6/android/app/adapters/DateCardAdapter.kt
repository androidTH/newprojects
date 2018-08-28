package com.d6.android.app.adapters

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.DateBean
import com.facebook.drawee.view.SimpleDraweeView
import com.tencent.open.utils.Util.subString

class DateCardAdapter(mData: ArrayList<DateBean>) : BaseRecyclerAdapter<DateBean>(mData, R.layout.item_date_card) {
    private val array by lazy {
        arrayOf("不限", "救火", "征求", "急约", "旅行约")
    }

//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
//        val h = super.onCreateViewHolder(parent, viewType)
//        val imageView = h.bind<Banner>(R.id.imageView)
//        imageView.setImageLoader(BannerLoader2(1.25f))
//        imageView.layoutParams.height = (( context.screenWidth()-2*context.dip(30))/1.25f).toInt()
//        imageView.isAutoPlay(false)
//        imageView.requestLayout()
////        imageView.setOnTouchListener { _, motionEvent ->
////            if (motionEvent.action == MotionEvent.ACTION_MOVE){
////                if (imageView.mImageViewPager.adapter != null) {
////                    if (imageView.mImageViewPager.currentItem==)
////                }
////            }
////            return@setOnTouchListener false
////        }
//        return h
//    }

    override fun onBind(holder: ViewHolder, position: Int, data: DateBean) {
//        val imageView = holder.bind<Banner>(R.id.imageView)
        val imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        data.userpics?.let {
            val images = it.split(",")
            if (images.isNotEmpty()) {
                imageView.setImageURI(images[0])
//                imageView.update(images)
            }
        }

        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.picUrl)

        holder.setText(R.id.tv_name, data.name)
        val tv_age = holder.bind<TextView>(R.id.tv_age)
        if(TextUtils.equals("0",data.nianling.toString())){
            tv_age.text = ""
        }else{
            tv_age.text = data.nianling
        }
        tv_age.isSelected = TextUtils.equals("0", data.sex)
        holder.setText(R.id.tv_type, array[(data.egagementtype ?: 0)])
        if(!data.egagementtext.isNullOrEmpty()){
            holder.setText(R.id.tv_content, data.egagementtext)
        }else if(!(data.gexingqianming.isNullOrEmpty())){
            holder.setText(R.id.tv_content, data.gexingqianming)
        }else{
            var sb:StringBuffer = StringBuffer()
            if(!data.height.isNullOrEmpty()){
               sb.append("身高：${data.height}")
            }
            if(!data.tizhong.isNullOrEmpty()){
                sb.append("体重:${data.tizhong}")
            }
            if(!data.zhiye.isNullOrEmpty()){
                sb.append("职业:${data.zhiye}")
            }
            if(!data.xinzuo.isNullOrEmpty()){
                sb.append("星座:${data.xinzuo}")
            }
            holder.setText(R.id.tv_content,sb.toString())
        }

        Log.i("DateCardAdapter", "${data.name},${data.egagementtext},${data.gexingqianming},身高：${data.height} 体重:${data.tizhong}职业:${data.zhiye}星座:${data.xinzuo}")

        if (TextUtils.equals("null", data.userlookwhere.toString())) {
            data.userlookwhere=""
        }
        if (TextUtils.equals("null", data.userhandlookwhere.toString())) {
            data.userhandlookwhere=""
        }
        var a: String? = ""
        if ((data.userlookwhere + data.userhandlookwhere).length > 5) {
            a = ((data.userlookwhere + data.userhandlookwhere).subSequence(0, 5).toString()) + "..."
        }
        holder.setText(R.id.tv_city, a)
        val onClickListener = View.OnClickListener {
            mOnItemClickListener?.onItemClick(it, position)
        }
        holder.bind<View>(R.id.cardView).setOnClickListener(onClickListener)

        //val tv_limit = holder.bind<TextView>(R.id.tv_limit)
//        val s= when(data.screen){
//            1->"白银及以上可约"
//            2->"黄金及以上可约"
//            else->"普通及以上可约"
//        }
//        tv_limit.text  = s
    }
}