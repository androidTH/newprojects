package com.d6.android.app.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.FindDate
import com.d6.android.app.models.UserTag
import com.d6.android.app.widget.gallery.CardAdapterHelper
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.flexbox.FlexboxLayoutManager

class DateCardAdapter(mData: ArrayList<FindDate>) : BaseRecyclerAdapter<FindDate>(mData, R.layout.item_date_newcard) {

    private var mImages = ArrayList<String>()
    private val mTags = ArrayList<UserTag>()

    private val mCardAdapterHelper = CardAdapterHelper()
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        context = parent!!.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_newcard, parent, false)
        mCardAdapterHelper.onCreateViewHolder(parent, view)
        return ViewHolder(view)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: FindDate) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, itemCount)
        val rv_mydate_images = holder.bind<RecyclerView>(R.id.rv_mydate_images)
        val rv_mydate_tags = holder.bind<RecyclerView>(R.id.rv_mydate_tags)

        data.userpics?.let {
            var imglist = it.split(",")
            if (mImages.isNotEmpty()) {
                rv_mydate_images.visibility = View.GONE
            } else {
                rv_mydate_images.visibility = View.VISIBLE
                rv_mydate_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                rv_mydate_images.setHasFixedSize(true)
                mImages.clear()
                if(imglist.size>=4){
                    mImages.addAll(imglist.toList().subList(0,4))
                }else {
                    mImages.addAll(imglist.toList())
                }
                rv_mydate_images.adapter = DatelmageAdapter(mImages, 1)
            }
        }

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

        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.picUrl)

        holder.setText(R.id.tv_name, data.name)
        val tv_age = holder.bind<TextView>(R.id.tv_age)

        val tv_vistorfollownums = holder.bind<TextView>(R.id.tv_vistorfollownums)

        if(data.iVistorCountAll>=50){
            tv_vistorfollownums.text="访客·${data.iVistorCountAll} 喜欢·${data.iFansCountAll}人"
        }else{
            tv_vistorfollownums.text = "喜欢·${data.iFansCountAll}人"
        }

        if(!data.nianling.isNullOrEmpty()){
            if (TextUtils.equals("0", data.nianling.toString())) {
                tv_age.text = ""
            } else {
                tv_age.text = data.nianling
            }
        }

        tv_age.isSelected = TextUtils.equals("0", data.sex)
//        holder.setText(R.id.tv_type, array[(data.egagementtype ?: 0)])
        if (!data.egagementtext.isNullOrEmpty()) {
            holder.setText(R.id.tv_content, data.egagementtext)
        } else if (!(data.gexingqianming.isNullOrEmpty())) {
            holder.setText(R.id.tv_content, data.gexingqianming)
        }

        if (TextUtils.equals("null", data.userlookwhere.toString())) {
            data.userlookwhere = ""
        }
        if (TextUtils.equals("null", data.userhandlookwhere.toString())) {
            data.userhandlookwhere = ""
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
    }
}