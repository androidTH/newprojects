package com.d6.android.app.adapters

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.FindDate
import com.d6.android.app.models.UserTag
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.flexbox.FlexboxLayoutManager

class DateWomanCardAdapter(mData: ArrayList<FindDate>) : BaseRecyclerAdapter<FindDate>(mData, R.layout.item_date_womennewcard) {

    private val mTags = ArrayList<UserTag>()

    override fun onBind(holder: ViewHolder, position: Int, data: FindDate) {
        val rv_mydate_tags = holder.bind<RecyclerView>(R.id.rv_mydate_tags)

        rv_mydate_tags.setHasFixedSize(true)
        rv_mydate_tags.layoutManager = FlexboxLayoutManager(context)
        rv_mydate_tags.isNestedScrollingEnabled = false

        mTags.clear()
        if (!data.shengao.isNullOrEmpty()) {
            mTags.add(UserTag("身高:${data.shengao}", R.drawable.shape_tag_bg_1))
        }

        if (!data.tizhong.isNullOrEmpty()) {
            mTags.add(UserTag("体重:${data.tizhong}", R.drawable.shape_tag_bg_2))
        }

        if (!data.zhiye.isNullOrEmpty()) {
            mTags.add(UserTag(data.zhiye, R.drawable.shape_tag_bg_3))
        }

        if (!data.xingzuo.isNullOrEmpty()) {
            mTags.add(UserTag(data.xingzuo ?: "", R.drawable.shape_tag_bg_5))
        }

        if (!data.xingquaihao.isNullOrEmpty()) {
            var mHobbies = data.xingquaihao?.replace("#", ",")?.split(",")
            if (mHobbies != null) {
                for (str in mHobbies) {
                    mTags.add(UserTag(str, R.drawable.shape_tag_bg_6))
                }
            }
        }

        rv_mydate_tags.adapter = CardTagAdapter(mTags)

        val bigImgView = holder.bind<SimpleDraweeView>(R.id.imageView)
        if(!TextUtils.equals(data.userpics,"null")){
            if(TextUtils.isEmpty(data.userpics)){
                bigImgView.setImageURI(data.picUrl)
            }else{
                var images = data.userpics.split(",")
                if (images.size>0) {
                    bigImgView.setImageURI(images[0])
                }
            }
        }else{
            bigImgView.setImageURI(data.picUrl)
        }

        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.picUrl)

        if(data.name.length>=8){
            holder.setText(R.id.tv_name, "${data.name.substring(0,6)}...")
        }else{
            holder.setText(R.id.tv_name, data.name)
        }

//        holder.setText(R.id.tv_vip, data.classesname)
        val tv_age = holder.bind<TextView>(R.id.tv_age)

        if(!data.nianling.isNullOrEmpty()){
            if (TextUtils.equals("0", data.nianling)) {
                tv_age.text = ""
            } else {
                tv_age.text = data.nianling
            }
        }

        tv_age.isSelected = TextUtils.equals("0", data.sex)
        if (!data.egagementtext.isNullOrEmpty()) {
            holder.setText(R.id.tv_content, data.egagementtext)
        } else if (!(data.gexingqianming.isNullOrEmpty())) {
            holder.setText(R.id.tv_content, data.gexingqianming)
        }

        if (TextUtils.equals("null", data.userlookwhere)) {
            data.userlookwhere = ""
        }
        if (TextUtils.equals("null", data.userhandlookwhere)) {
            data.userhandlookwhere = ""
        }
        var a: String? = ""
        if ((data.userlookwhere + data.userhandlookwhere).length > 5) {
            a = ((data.userlookwhere + data.userhandlookwhere).subSequence(0, 5).toString()) + "..."
        }
        holder.setText(R.id.tv_city, a)

        val tv_vistorfollownums = holder.bind<TextView>(R.id.tv_vistorfollownums)
        if(data.iVistorCountAll>=50){
            tv_vistorfollownums.text="访客·${data.iVistorCountAll} 喜欢·${data.iFansCountAll}人"
        }else{
            tv_vistorfollownums.text = "喜欢·${data.iFansCountAll}人"
        }
        val onClickListener = View.OnClickListener {
            mOnItemClickListener?.onItemClick(it, position)
        }

        holder.bind<View>(R.id.cardView).setOnClickListener(onClickListener)
    }
}