package com.d6.android.app.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.DateBean
import com.d6.android.app.models.FindDate
import com.d6.android.app.models.UserTag
import com.facebook.drawee.view.SimpleDraweeView

class DateCardAdapter(mData: ArrayList<FindDate>) : BaseRecyclerAdapter<FindDate>(mData, R.layout.item_date_newcard) {

    private var mImages = ArrayList<String>()
    private val mTags = ArrayList<UserTag>()

    override fun onBind(holder: ViewHolder, position: Int, data: FindDate) {
        val rv_mydate_images = holder.bind<RecyclerView>(R.id.rv_mydate_images)
        val rv_mydate_tags = holder.bind<RecyclerView>(R.id.rv_mydate_tags)

        data.userpics?.let {
            mImages = it.split(",") as ArrayList<String>
            if (mImages.isNotEmpty()) {
                rv_mydate_images.visibility = View.GONE
            } else {
                rv_mydate_images.visibility = View.VISIBLE
                rv_mydate_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                rv_mydate_images.setHasFixedSize(true)
                mImages.clear()
                rv_mydate_images.adapter = DatelmageAdapter(mImages, 1)
            }
        }

        rv_mydate_tags.setHasFixedSize(true)
        rv_mydate_tags.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        mTags.clear()
        if (!data.shengao.isNullOrEmpty()) {
            mTags.add(UserTag("身高:${data.shengao}", R.drawable.shape_tag_bg_1))
        }

        if (!data.tizhong.isNullOrEmpty()) {
            mTags.add(UserTag("体重:${data.tizhong}", R.drawable.shape_tag_bg_2))
        }

        if (!data.zhiye.isNullOrEmpty()) {
            mTags.add(UserTag(data.zhiye ?: "", R.drawable.shape_tag_bg_3))
        }

        if (!data.nianling.isNullOrEmpty()) {
            mTags.add(UserTag(data.nianling ?: "", R.drawable.shape_tag_bg_4))
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

        rv_mydate_tags.adapter = UserTagAdapter(mTags)

        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.picUrl)

        holder.setText(R.id.tv_name, data.name)
        val tv_age = holder.bind<TextView>(R.id.tv_age)
        if (TextUtils.equals("0", data.nianling.toString())) {
            tv_age.text = ""
        } else {
            tv_age.text = data.nianling
        }
        tv_age.isSelected = TextUtils.equals("0", data.sex)
//        holder.setText(R.id.tv_type, array[(data.egagementtype ?: 0)])
        if (!data.egagementtext.isNullOrEmpty()) {
            holder.setText(R.id.tv_content, data.egagementtext)
        } else if (!(data.gexingqianming.isNullOrEmpty())) {
            holder.setText(R.id.tv_content, data.gexingqianming)
        } else {
            var sb = StringBuffer()
            if (!data.shengao.isNullOrEmpty()) {
                sb.append("身高：${data.shengao}")
            }
            if (!data.tizhong.isNullOrEmpty()) {
                sb.append("体重:${data.tizhong}")
            }
            if (!data.zhiye.isNullOrEmpty()) {
                sb.append("职业:${data.zhiye}")
            }
            if (!data.xingzuo.isNullOrEmpty()) {
                sb.append("星座:${data.xingzuo}")
            }
            holder.setText(R.id.tv_content, sb.toString())
        }

        Log.i("DateCardAdapter", "${data.name},${data.egagementtext},${data.gexingqianming},身高：${data.shengao} 体重:${data.tizhong}职业:${data.zhiye}星座:${data.xingzuo}")

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