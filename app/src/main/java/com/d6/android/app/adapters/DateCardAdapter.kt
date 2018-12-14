package com.d6.android.app.adapters

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.FindDate
import com.d6.android.app.models.UserTag
import com.d6.android.app.utils.AppUtils
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getTodayTime
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.flexbox.FlexboxLayoutManager

class DateCardAdapter(mData: ArrayList<FindDate>) : BaseRecyclerAdapter<FindDate>(mData, R.layout.item_date_newcard) {

    private var mImages = ArrayList<String>()
    private val mTags = ArrayList<UserTag>()
    private var iDataComplete = SPUtils.instance().getInt(Const.User.USER_DATACOMPLETION)

    private var lastTime = SPUtils.instance().getString(Const.LASTDAYTIME)

    override fun onBind(holder: ViewHolder, position: Int, data: FindDate) {
        val rv_mydate_images = holder.bind<RecyclerView>(R.id.rv_mydate_images)
        val rv_mydate_tags = holder.bind<RecyclerView>(R.id.rv_mydate_tags)
        val nomg_line = holder.bind<View>(R.id.noimg_line)
        if (!TextUtils.equals(data.userpics, "null")) {
            if (TextUtils.isEmpty(data.userpics)) {
                mImages.clear()
                rv_mydate_images.visibility = View.VISIBLE
                nomg_line.visibility = View.VISIBLE
            } else {
                var imglist = data.userpics.split(",")
                if (imglist.size == 0) {
                    mImages.clear()
                    rv_mydate_images.visibility = View.VISIBLE
                    nomg_line.visibility = View.VISIBLE
                } else {
                    nomg_line.visibility = View.GONE
                    rv_mydate_images.visibility = View.VISIBLE
                    rv_mydate_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    rv_mydate_images.setHasFixedSize(true)
                    mImages.clear()
                    if (imglist.size >= 4) {
                        mImages.addAll(imglist.toList().subList(0, 4))
                    } else {
                        mImages.addAll(imglist.toList())
                    }
                    rv_mydate_images.adapter = DatelmageAdapter(mImages, 1)
                }
            }
        } else {
            mImages.clear()
            rv_mydate_images.visibility = View.VISIBLE
            nomg_line.visibility = View.VISIBLE
        }

        rv_mydate_tags.setHasFixedSize(true)
        rv_mydate_tags.layoutManager = GridLayoutManager(context, 2)
        rv_mydate_tags.isNestedScrollingEnabled = false

        mTags.clear()
        if (!data.shengao.isNullOrEmpty()) {
            mTags.add(UserTag("身高 ${data.shengao}", R.mipmap.boy_stature_icon))
        }

        if (!data.tizhong.isNullOrEmpty()) {
            mTags.add(UserTag("体重 ${data.tizhong}", R.mipmap.boy_weight_icon))
        }

        if (!data.xingzuo.isNullOrEmpty()) {
            mTags.add(UserTag("星座 ${data.xingzuo}", R.mipmap.boy_constellation_icon))
        }

        if (!data.city.isNullOrEmpty()) {
            mTags.add(UserTag(data.city, R.mipmap.boy_area_icon))
        }

        rv_mydate_tags.adapter = CardManTagAdapter(mTags)

        var tv_job = holder.bind<TextView>(R.id.tv_job)
        if (!data.zhiye.isNullOrEmpty()) {
            AppUtils.setTvTag(context, "职业 ${data.zhiye}", 0, 2, tv_job)
        }

        var tv_zuojia = holder.bind<TextView>(R.id.tv_zuojia)
        if (!data.zuojia.isNullOrEmpty()) {
            AppUtils.setTvTag(context, "座驾 ${data.zuojia}", 0, 2, tv_zuojia)
        }

        var tv_aihao = holder.bind<TextView>(R.id.tv_aihao)
        if (!data.xingquaihao.isNullOrEmpty()) {
            var sb = StringBuffer()
            sb.append("爱好 ")
            var mHobbies = data.xingquaihao?.replace("#", ",")?.split(",")
            if (mHobbies != null) {
                for (str in mHobbies) {
                    sb.append("${str} ")
                }
                AppUtils.setTvTag(context, sb.toString(), 0, 2, tv_aihao)
            }
        }

        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.picUrl)

        if (data.name.length >= 8) {
            holder.setText(R.id.tv_name, "${data.name.substring(0, 6)}...")
        } else {
            holder.setText(R.id.tv_name, data.name)
        }

        holder.setText(R.id.tv_vip, data.classesname)
        val tv_age = holder.bind<TextView>(R.id.tv_age)

//        val tv_vistorfollownums = holder.bind<TextView>(R.id.tv_vistorfollownums)
        var ll_vistor = holder.bind<LinearLayout>(R.id.ll_vistor)
        var ll_like = holder.bind<LinearLayout>(R.id.ll_like)
        var tv_vistor_count = holder.bind<TextView>(R.id.tv_vistor_count)
        var tv_like_count = holder.bind<TextView>(R.id.tv_like_count)

        if (data.iVistorCountAll > 50) {
            ll_like.visibility = View.VISIBLE
            ll_vistor.visibility = View.VISIBLE
            tv_vistor_count.text = "${data.iVistorCountAll}"
            tv_like_count.text = "${data.iFansCountAll}"
//            tv_vistorfollownums.text="访客·${data.iVistorCountAll} 喜欢·${data.iFansCountAll}人"
        } else {
            ll_like.visibility = View.VISIBLE
            ll_vistor.visibility = View.GONE
            tv_like_count.text = "${data.iFansCountAll}"
//            tv_vistorfollownums.text = "喜欢·${data.iFansCountAll}人"
        }

        if (!data.nianling.isNullOrEmpty()) {
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

        var rl_perfect_userinfo = holder.bind<RelativeLayout>(R.id.rl_perfect_userinfo)
        if ((!TextUtils.equals(getTodayTime(), lastTime)) && position == 5) {
            if (iDataComplete < 60) {
                SPUtils.instance().put(Const.LASTDAYTIME, getTodayTime()).apply()
                rl_perfect_userinfo.visibility = View.VISIBLE
            } else {
                rl_perfect_userinfo.visibility = View.GONE
            }
        } else {
            rl_perfect_userinfo.visibility = View.GONE
        }

        val onClickListener = View.OnClickListener {
            rl_perfect_userinfo.visibility = View.GONE
            mOnItemClickListener?.onItemClick(it, position)
        }
        holder.bind<View>(R.id.cardView).setOnClickListener(onClickListener)
        holder.bind<TextView>(R.id.tv_perfect_userinfo).setOnClickListener(onClickListener)
    }
}