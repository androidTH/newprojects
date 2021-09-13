package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.FindDate
import com.d6.android.app.models.UserTag
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.BLUR_50
import com.d6.android.app.utils.Const.D6_WWW_TAG
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivity
import kotlin.collections.ArrayList

class FindDateCardAdapter(mData: ArrayList<FindDate>) : BaseRecyclerAdapter<FindDate>(mData, R.layout.item_finddatecard) {

    private val mImages = ArrayList<String>()
    private val mTags = ArrayList<UserTag>()

    private var mLayoutNormal = AppUtils.getWHRatio() //0 大布局 1 小布局

    override fun onBind(holder: ViewHolder, position: Int, data: FindDate) {
        var rl_man_card = holder.bind<RelativeLayout>(R.id.rl_man_card)
        var imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
        var index = data.picUrl.indexOf("?")
        var url = if (index != -1) {
            data.picUrl.subSequence(0, index)
        } else {
            data.picUrl
        }
        if (url.contains(D6_WWW_TAG)) {
            imageView.showBlur("${data.picUrl}")
        } else {
            imageView.setImageURI("${url}${BLUR_50}")
        }
        Log.i("DateCardAdapter", "${data.name}----${data.picUrl}")
        rl_man_card.visibility = View.VISIBLE
        val rl_small_mendate_layout = holder.bind<RelativeLayout>(R.id.rl_small_mendate_layout)
        val rl_big_mendate_layout = holder.bind<RelativeLayout>(R.id.rl_big_mendate_layout)
        if (mLayoutNormal > 2.0f) {
            rl_small_mendate_layout.visibility = View.GONE
            rl_big_mendate_layout.visibility = View.VISIBLE
            mShowBigLayout(holder, position, data)
        } else {
            rl_small_mendate_layout.visibility = View.VISIBLE
            rl_big_mendate_layout.visibility = View.GONE
            val rv_mydate_images = holder.bind<RecyclerView>(R.id.rv_mydate_images)
            val rv_mydate_tags = holder.bind<RecyclerView>(R.id.rv_mydate_tags)
            mImages.clear()
            if (!TextUtils.equals(data.userpics, "null")) {
                if (TextUtils.isEmpty(data.userpics)) {
                    mImages.add(data.picUrl)
                    rv_mydate_images.visibility = View.VISIBLE
//                        nomg_line.visibility = View.GONE
                } else {
                    var imglist = data.userpics.split(",")
                    if (imglist.size == 0) {
                        mImages.add(data.picUrl)
                        rv_mydate_images.visibility = View.VISIBLE
//                            nomg_line.visibility = View.GONE
                    } else {
//                            nomg_line.visibility = View.GONE
                        mImages.clear()
                        if (imglist.size >= 4) {
                            mImages.addAll(imglist.toList().subList(0, 4))
                        } else {
                            mImages.addAll(imglist.toList())
                        }
                    }
                }
            } else {
                mImages.add(data.picUrl)
                rv_mydate_images.visibility = View.VISIBLE
//                    nomg_line.visibility = View.VISIBLE
            }

            rv_mydate_images.visibility = View.VISIBLE
            rv_mydate_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rv_mydate_images.setHasFixedSize(true)

            rv_mydate_images.adapter = DatelmageAdapter(mImages, 1)
            (rv_mydate_images.adapter as DatelmageAdapter).setOnItemClickListener { adapter, view, p ->
                context.startActivity<UserInfoActivity>("id" to data.accountId.toString())
            }
            rv_mydate_tags.setHasFixedSize(true)
            rv_mydate_tags.layoutManager = GridLayoutManager(context, 2)
            rv_mydate_tags.isNestedScrollingEnabled = false

            mTags.clear()
//                if (!data.shengao.isNullOrEmpty()) {
            mTags.add(UserTag("身高 ${data.shengao}", R.mipmap.boy_stature_whiteicon))
//                }

//                if (!data.tizhong.isNullOrEmpty()) {
            mTags.add(UserTag("体重 ${data.tizhong}", R.mipmap.boy_weight_whiteicon))
//                }

//                if (!data.xingzuo.isNullOrEmpty()) {
            mTags.add(UserTag("星座 ${data.xingzuo}", R.mipmap.boy_constellation_whiteicon))
//                }

            if (!data.city.isNullOrEmpty()) {
                mTags.add(UserTag("地区 ${data.sPosition}", R.mipmap.boy_area_whiteicon))
            } else {
                mTags.add(UserTag("地区", R.mipmap.boy_area_whiteicon))
            }

            if (!data.zhiye.isNullOrEmpty()) {
                if (data.zhiye.length > 6) {
                    mTags.add(UserTag("职业 ${data.zhiye.subSequence(0, 6)}...", R.mipmap.boy_profession_whiteicon))
                } else {
                    mTags.add(UserTag("职业 ${data.zhiye}", R.mipmap.boy_profession_whiteicon))
                }
            } else {
                mTags.add(UserTag("职业", R.mipmap.boy_profession_whiteicon))
            }

            if (!data.city.isNullOrEmpty()) {
                mTags.add(UserTag("约会地 ${data.city}", R.mipmap.boy_datearea_whiteicon, 3))
            } else {
                mTags.add(UserTag("约会地 ", R.mipmap.boy_datearea_whiteicon, 3))
            }

            rv_mydate_tags.adapter = CardManTagAdapter(mTags)

            var tv_zuojia = holder.bind<TextView>(R.id.tv_zuojia)
            if (!data.zuojia.isNullOrEmpty()) {
                AppUtils.setTvNewTag(context, "座驾 ${data.zuojia}", 0, 2, tv_zuojia)
            } else {
                AppUtils.setTvNewTag(context, "座驾 ", 0, 2, tv_zuojia)
                tv_zuojia.visibility = View.VISIBLE
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
                    if (sb.length > 14) {
                        AppUtils.setTvNewTag(context, "${sb.substring(0, 14)}...", 0, 2, tv_aihao)
                    } else {
                        AppUtils.setTvNewTag(context, sb.toString(), 0, 2, tv_aihao)
                    }
                }
            } else {
                AppUtils.setTvNewTag(context, "爱好 ", 0, 2, tv_aihao)
                tv_aihao.visibility = View.VISIBLE
            }

            holder.bind<SimpleDraweeView>(R.id.headView).also {
                it.setImageURI(data.picUrl)
            }

            val img_date_menauther = holder.bind<ImageView>(R.id.img_date_menauther)

            if (TextUtils.equals("0", data!!.screen) || data!!.screen.isNullOrEmpty()) {
                img_date_menauther.visibility = View.GONE
            } else if (TextUtils.equals("1", data!!.screen)) {
                img_date_menauther.setImageResource(R.mipmap.video_small)
            } else if (TextUtils.equals("3", data!!.screen)) {
                img_date_menauther.visibility = View.GONE
                img_date_menauther.setImageResource(R.mipmap.renzheng_small)
            } else {
                img_date_menauther.visibility = View.GONE
            }

            if (data.name.length >= 7) {
                holder.setText(R.id.tv_name, "${data.name.substring(0, 6)}...")
            } else {
                holder.setText(R.id.tv_name, data.name)
            }

            var tv_vip = holder.bind<TextView>(R.id.tv_vip)
            tv_vip.visibility = View.VISIBLE
            if (TextUtils.equals(data.userclassesid.toString(), "22")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
            } else if (TextUtils.equals(data.userclassesid, "23")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
            } else if (TextUtils.equals(data.userclassesid, "24")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
            } else if (TextUtils.equals(data.userclassesid, "25")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
            } else if (TextUtils.equals(data.userclassesid, "26")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_private)
            } else if (TextUtils.equals(data.userclassesid, "7")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.youke_icon)
            } else if (TextUtils.equals(data.userclassesid, "30")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.ruqun_icon)
            } else if (TextUtils.equals(data.userclassesid, "31")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.app_vip)
            } else {
                tv_vip.backgroundDrawable = null
                tv_vip.visibility = View.GONE
            }

            val tv_sex = holder.bind<TextView>(R.id.tv_sex)
            tv_sex.isSelected = TextUtils.equals("0", data.sex)

            var ll_like = holder.bind<LinearLayout>(R.id.ll_like)
            var tv_linetime = holder.bind<TextView>(R.id.tv_vistor_count)
            var tv_loveheart_vistor = holder.bind<TextView>(R.id.tv_like_count)

            var sblove = StringBuffer()
            if (data.iReceiveLovePoint >= 10) {
                sblove.append("送出 [img src=redheart_small/] · ${data.iReceiveLovePoint}     ")
            }

            if (data.iVistorCountAll >= 10) {
                sblove.append("访客·${data.iVistorCountAll}")
            }

            if (sblove.toString().length > 0) {
                ll_like.visibility = View.VISIBLE
                tv_loveheart_vistor.visibility = View.VISIBLE
                tv_loveheart_vistor.text = sblove.toString()
            } else {
                ll_like.visibility = View.GONE
                tv_loveheart_vistor.visibility = View.GONE
            }

            if (data.iOnline == 1) {
                tv_linetime.visibility = View.GONE
                ll_like.visibility = View.GONE
            } else {
                tv_linetime.visibility = View.VISIBLE
                ll_like.visibility = View.VISIBLE
                if (data.iOnline == 2) {
                    setLeftDrawable(ContextCompat.getDrawable(context, R.drawable.shape_dot_online), tv_linetime)
                    tv_linetime.text = "当前状态·${data.sOnlineMsg}"
                } else {
                    setLeftDrawable(ContextCompat.getDrawable(context, R.drawable.shape_dot_translate), tv_linetime)
                    tv_linetime.text = "在线时间·${data.sOnlineMsg}"
                }
            }

            val tv_age = holder.bind<TextView>(R.id.tv_age)
            if (!data.nianling.isNullOrEmpty()) {
                if (TextUtils.equals("0", data.nianling)) {
                    tv_age.visibility = View.GONE
                } else {
                    tv_age.isSelected = TextUtils.equals("0", data.sex)
                    tv_age.visibility = View.VISIBLE
                    tv_age.text = "${data.nianling}岁"
                }
            } else {
                tv_age.visibility = View.GONE
            }


            if (!data.egagementtext.isNullOrEmpty()) {
                if (!TextUtils.equals("null", data.egagementtext)) {
                    holder.setText(R.id.tv_content, data.egagementtext)
                } else {
                    holder.setText(R.id.tv_content, "")
                }
            } else if (!(data.gexingqianming.isNullOrEmpty())) {
                if (!TextUtils.equals("null", data.gexingqianming)) {
                    holder.setText(R.id.tv_content, data.gexingqianming)
                } else {
                    holder.setText(R.id.tv_content, "")
                }
            } else if (!data.ziwojieshao.isNullOrEmpty()) {
                if (!TextUtils.equals("null", data.ziwojieshao)) {
                    holder.setText(R.id.tv_content, data.ziwojieshao)
                } else {
                    holder.setText(R.id.tv_content, "")
                }
            } else {
                holder.setText(R.id.tv_content, "")
            }
        }
        val onClickListener = View.OnClickListener {
            mOnItemClickListener?.onItemClick(it, position)
        }
        holder.bind<View>(R.id.cardView).setOnClickListener(onClickListener)
        holder.bind<View>(R.id.rl_small_mendate_layout).setOnClickListener(onClickListener)
        holder.bind<View>(R.id.imageViewbg).setOnClickListener(onClickListener)
        holder.bind<View>(R.id.rl_big_mendate_layout).setOnClickListener(onClickListener)
    }

    fun mShowBigLayout(holder: ViewHolder, position: Int, data: FindDate) {

        val newheadView = holder.bind<SimpleDraweeView>(R.id.newheadView)
        newheadView.setImageURI(data.picUrl)

        val rv_mydate_newtags = holder.bind<RecyclerView>(R.id.rv_mydate_newtags)
        rv_mydate_newtags.setHasFixedSize(true)
        rv_mydate_newtags.layoutManager = GridLayoutManager(context, 2)
        rv_mydate_newtags.isNestedScrollingEnabled = false
        mTags.clear()
//        if (!data.shengao.isNullOrEmpty()) {
        mTags.add(UserTag("身高 ${data.shengao}", R.mipmap.boy_stature_whiteicon))
//        }

//        if (!data.tizhong.isNullOrEmpty()) {
        mTags.add(UserTag("体重 ${data.tizhong}", R.mipmap.boy_weight_whiteicon))
//        }

//        if (!data.xingzuo.isNullOrEmpty()) {
        mTags.add(UserTag("星座 ${data.xingzuo}", R.mipmap.boy_constellation_whiteicon))
//        }

        if (!data.city.isNullOrEmpty()) {
            mTags.add(UserTag("地区 ${data.sPosition}", R.mipmap.boy_area_whiteicon))
        } else {
            mTags.add(UserTag("地区", R.mipmap.boy_area_whiteicon))
        }

        if (!data.zhiye.isNullOrEmpty()) {
            if (data.zhiye.length > 6) {
                mTags.add(UserTag("职业 ${data.zhiye.subSequence(0, 6)}...", R.mipmap.boy_profession_whiteicon))
            } else {
                mTags.add(UserTag("职业 ${data.zhiye}", R.mipmap.boy_profession_whiteicon))
            }
        } else {
            mTags.add(UserTag("职业", R.mipmap.boy_profession_whiteicon))
        }

        if (!data.city.isNullOrEmpty()) {
            mTags.add(UserTag("约会地 ${data.city}", R.mipmap.boy_datearea_whiteicon, 3))
        } else {
            mTags.add(UserTag("约会地", R.mipmap.boy_datearea_whiteicon, 3))
        }

        rv_mydate_newtags.adapter = CardManTagAdapter(mTags)

        var tv_newzuojia = holder.bind<TextView>(R.id.tv_newzuojia)
        if (!data.zuojia.isNullOrEmpty()) {
            AppUtils.setTvNewTag(context, "座驾 ${data.zuojia}", 0, 2, tv_newzuojia)
        } else {
            AppUtils.setTvNewTag(context, "座驾", 0, 2, tv_newzuojia)
            tv_newzuojia.visibility = View.VISIBLE
        }

        var tv_newaihao = holder.bind<TextView>(R.id.tv_newaihao)
        if (!data.xingquaihao.isNullOrEmpty()) {
            var sb = StringBuffer()
            sb.append("爱好 ")
            var mHobbies = data.xingquaihao?.replace("#", ",")?.split(",")
            if (mHobbies != null) {
                for (str in mHobbies) {
                    sb.append("${str} ")
                }
                if (sb.length > 14) {
                    AppUtils.setTvNewTag(context, "${sb.substring(0, 14)}...", 0, 2, tv_newaihao)
                } else {
                    AppUtils.setTvNewTag(context, "${sb}", 0, 2, tv_newaihao)
                }
            }
        } else {
            AppUtils.setTvNewTag(context, "爱好", 0, 2, tv_newaihao)
            tv_newaihao.visibility = View.VISIBLE
        }

        val img_date_newmenauther = holder.bind<ImageView>(R.id.img_date_newmenauther)

        if (TextUtils.equals("0", data!!.screen) || data!!.screen.isNullOrEmpty()) {
            img_date_newmenauther.visibility = View.GONE
        } else if (TextUtils.equals("1", data!!.screen)) {
            img_date_newmenauther.setImageResource(R.mipmap.video_small)
        } else if (TextUtils.equals("3", data!!.screen)) {
            img_date_newmenauther.visibility = View.GONE
            img_date_newmenauther.setImageResource(R.mipmap.renzheng_small)
        } else {
            img_date_newmenauther.visibility = View.GONE
        }

        holder.setText(R.id.tv_newname, data.name)

        var tv_newvip = holder.bind<TextView>(R.id.tv_newvip)
        tv_newvip.visibility = View.VISIBLE
        var drawable = getLevelDrawable(data.userclassesid, context)
        if (drawable == null) {
            tv_newvip.backgroundDrawable = null
            tv_newvip.visibility = View.GONE
        } else {
            tv_newvip.backgroundDrawable = drawable
        }

        var tv_linetime = holder.bind<TextView>(R.id.tv_linetime)
        if (data.iOnline == 1) {
            tv_linetime.visibility = View.GONE
        } else {
            tv_linetime.visibility = View.VISIBLE
            if (data.iOnline == 2) {
                setLeftDrawable(ContextCompat.getDrawable(context, R.drawable.shape_dot_online), tv_linetime)
                tv_linetime.text = "当前状态·${data.sOnlineMsg}"
            } else {
                setLeftDrawable(ContextCompat.getDrawable(context, R.drawable.shape_dot_translate), tv_linetime)
                tv_linetime.text = "在线时间·${data.sOnlineMsg}"
            }
        }

        var tv_loveheart_vistor = holder.bind<TextView>(R.id.tv_loveheart_vistor)

        var sblove = StringBuffer()
        if (data.iReceiveLovePoint >= 10) {
            sblove.append("送出 [img src=redheart_small/] · ${data.iReceiveLovePoint}     ")
        }

        if (data.iVistorCountAll >= 10) {
            sblove.append("访客·${data.iVistorCountAll}")
        }

        if (sblove.toString().length > 0) {
            tv_loveheart_vistor.visibility = View.VISIBLE
            tv_loveheart_vistor.text = "${sblove}"
        } else {
            tv_loveheart_vistor.visibility = View.GONE
        }

        val tv_newage = holder.bind<TextView>(R.id.tv_newage)
        val tv_newsex = holder.bind<TextView>(R.id.tv_newsex)
        if (!data.nianling.isNullOrEmpty()) {
            if (TextUtils.equals("0", data.nianling)) {
                tv_newage.visibility = View.GONE
            } else {
                tv_newage.isSelected = TextUtils.equals("0", data.sex)
                tv_newage.visibility = View.VISIBLE
                tv_newage.text = "${data.nianling}岁"
            }
        } else {
            tv_newage.visibility = View.GONE
        }

        tv_newsex.isSelected = TextUtils.equals("0", data.sex)

        var tv_newcontent = holder.bind<TextView>(R.id.tv_newcontent)
        if (!data.egagementtext.isNullOrEmpty()) {
            if (!TextUtils.equals("null", data.egagementtext)) {
                tv_newcontent.text = "${data.egagementtext}"
            } else {
                tv_newcontent.text = ""
            }
        } else if (!(data.gexingqianming.isNullOrEmpty())) {
            if (!TextUtils.equals("null", data.gexingqianming)) {
                tv_newcontent.text = "${data.gexingqianming}"
            } else {
                tv_newcontent.text = ""
            }
        } else if (!data.ziwojieshao.isNullOrEmpty()) {
            if (!TextUtils.equals("null", data.ziwojieshao)) {
                tv_newcontent.text = "${data.ziwojieshao}"
            } else {
                tv_newcontent.text = ""
            }
        } else {
            tv_newcontent.text = ""
        }
    }
}