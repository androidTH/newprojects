package com.d6.android.app.adapters

import android.content.Context
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
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.FindDate
import com.d6.android.app.models.UserTag
import com.d6.android.app.utils.AppUtils
import com.d6.android.app.utils.AppUtils.Companion.getWHRatio
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.getLevelDrawable
import com.d6.android.app.utils.setLeftDrawable
import com.d6.android.app.widget.convenientbanner.holder.Holder
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundDrawable
import java.util.*

/**
 * author : jinjiarui
 * time   : 2019/01/03
 * desc   :
 * version:
 */
class FindDateMenCardHolder(itemView: View?) : Holder<FindDate>(itemView) {

    private lateinit var rl_man_card: RelativeLayout
    private lateinit var imageView: SimpleDraweeView
    private lateinit var headView: SimpleDraweeView
    private lateinit var rl_small_mendate_layout: RelativeLayout
    private lateinit var rl_big_mendate_layout: RelativeLayout
    private lateinit var rv_mydate_images: RecyclerView
    private lateinit var rv_mydate_tags: RecyclerView
    private lateinit var tv_zuojia: TextView
    private lateinit var tv_aihao: TextView
    private lateinit var tv_name: TextView
    private lateinit var img_date_menauther: ImageView
    private lateinit var tv_vip: TextView
    private lateinit var tv_sex: TextView
    private lateinit var tv_linetime: TextView
    private lateinit var tv_loveheart_vistor: TextView
    private lateinit var tv_age: TextView
    private lateinit var tv_content: TextView
    private lateinit var ll_like: LinearLayout
    private val mLayoutNormal = getWHRatio() //0 大布局 1 小布局

    private val mImages = ArrayList<String>()
    private val mTags = ArrayList<UserTag>()
    private lateinit var mContext: Context
    private lateinit var holder:View;

    constructor(itemView: View?, context: Context) : this(itemView) {
        suspend { itemView }
        mContext = context
    }


    override fun initView(itemView: View) {
        holder = itemView
        rl_man_card = itemView.findViewById(R.id.rl_man_card)
        imageView = itemView.findViewById(R.id.imageView)//item_finddatecard  layout_men_finddate_big
        headView = itemView.findViewById(R.id.headView)
        rl_small_mendate_layout = itemView.findViewById(R.id.rl_small_mendate_layout)
        rl_big_mendate_layout = itemView.findViewById(R.id.rl_big_mendate_layout)
        rv_mydate_images = itemView.findViewById(R.id.rv_mydate_images)
        rv_mydate_tags = itemView.findViewById(R.id.rv_mydate_tags)
        tv_zuojia = itemView.findViewById(R.id.tv_zuojia)
        tv_aihao = itemView.findViewById(R.id.tv_aihao)
        img_date_menauther = itemView.findViewById(R.id.img_date_menauther)
        tv_name = itemView.findViewById(R.id.tv_name)
        tv_vip = itemView.findViewById(R.id.tv_vip)
        tv_sex = itemView.findViewById(R.id.tv_sex)
        tv_vip = itemView.findViewById(R.id.tv_vip)
        ll_like = itemView.findViewById(R.id.ll_like)
        tv_linetime = itemView.findViewById(R.id.tv_vistor_count)
        tv_loveheart_vistor = itemView.findViewById(R.id.tv_like_count)
        tv_age = itemView.findViewById(R.id.tv_age)
        tv_content = itemView.findViewById(R.id.tv_content)
    }


    override fun updateUI(data: FindDate, position: Int, total: Int) {
        var index = data?.picUrl.indexOf("?")
        var url = if (index != -1) {
            data.picUrl.subSequence(0, index)
        } else {
            data.picUrl
        }
        if (url.contains(Const.D6_WWW_TAG)) {
            imageView.showBlur("${data.picUrl}")
        } else {
            imageView?.setImageURI("${url}${Const.BLUR_50}")
        }
        Log.i("DateCardAdapter", "${data.name}----${data.picUrl}")
        rl_man_card.visibility = View.VISIBLE
        if (mLayoutNormal > 2.0f) {
            rl_small_mendate_layout.visibility = View.GONE
            rl_big_mendate_layout.visibility = View.VISIBLE
            mShowBigLayout(holder, position, data)
        } else {
            rl_small_mendate_layout?.visibility = View.VISIBLE
            rl_big_mendate_layout?.visibility = View.GONE
            mImages.clear()
            if (!TextUtils.equals(data.userpics, "null")) {
                if (TextUtils.isEmpty(data.userpics)) {
                    mImages.add(data.picUrl)
                    rv_mydate_images.visibility = View.GONE
//                        nomg_line.visibility = View.GONE
                } else {
                    var imglist = data.userpics.split(",")
                    if (imglist.size == 0) {
                        mImages.add(data.picUrl)
                        rv_mydate_images.visibility = View.GONE
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
                rv_mydate_images.visibility = View.GONE
//                    nomg_line.visibility = View.VISIBLE
            }

            rv_mydate_images.visibility = View.GONE
            rv_mydate_images.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            rv_mydate_images.setHasFixedSize(true)

            rv_mydate_images.adapter = DatelmageAdapter(mImages, 1)
            rv_mydate_tags.setHasFixedSize(true)
            rv_mydate_tags.layoutManager = GridLayoutManager(mContext, 2)
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

            if (!data.zuojia.isNullOrEmpty()) {
                AppUtils.setTvNewTag(mContext, "座驾 ${data.zuojia}", 0, 2, tv_zuojia)
            } else {
                AppUtils.setTvNewTag(mContext, "座驾 ", 0, 2, tv_zuojia)
                tv_zuojia.visibility = View.VISIBLE
            }

            if (!data.xingquaihao.isNullOrEmpty()) {
                var sb = StringBuffer()
                sb.append("爱好 ")
                var mHobbies = data.xingquaihao?.replace("#", ",")?.split(",")
                if (mHobbies != null) {
                    for (str in mHobbies) {
                        sb.append("${str} ")
                    }
                    if (sb.length > 14) {
                        AppUtils.setTvNewTag(mContext, "${sb.substring(0, 14)}...", 0, 2, tv_aihao)
                    } else {
                        AppUtils.setTvNewTag(mContext, sb.toString(), 0, 2, tv_aihao)
                    }
                }
            } else {
                AppUtils.setTvNewTag(mContext, "爱好 ", 0, 2, tv_aihao)
                tv_aihao.visibility = View.VISIBLE
            }

            headView.also {
                it.setImageURI(data.picUrl)
            }

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
                tv_name.text = "${data.name.substring(0, 6)}..."
            } else {
                tv_name.text = data.name
            }

            tv_vip.visibility = View.VISIBLE
            if (TextUtils.equals(data.userclassesid.toString(), "22")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_ordinary)
            } else if (TextUtils.equals(data.userclassesid, "23")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_silver)
            } else if (TextUtils.equals(data.userclassesid, "24")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_gold)
            } else if (TextUtils.equals(data.userclassesid, "25")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_zs)
            } else if (TextUtils.equals(data.userclassesid, "26")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_private)
            } else if (TextUtils.equals(data.userclassesid, "7")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.youke_icon)
            } else if (TextUtils.equals(data.userclassesid, "30")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.ruqun_icon)
            } else if (TextUtils.equals(data.userclassesid, "31")) {
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(mContext, R.mipmap.app_vip)
            } else {
                tv_vip.backgroundDrawable = null
                tv_vip.visibility = View.GONE
            }

            tv_sex.isSelected = TextUtils.equals("0", data.sex)

//            var ll_like = holder.bind<LinearLayout>(R.id.ll_like)
//            var tv_linetime = holder.bind<TextView>(R.id.tv_vistor_count)
//            var tv_loveheart_vistor = holder.bind<TextView>(R.id.tv_like_count)

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
                    setLeftDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape_dot_online), tv_linetime)
                    tv_linetime.text = "当前状态·${data.sOnlineMsg}"
                } else {
                    setLeftDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape_dot_translate), tv_linetime)
                    tv_linetime.text = "在线时间·${data.sOnlineMsg}"
                }
            }

//            val tv_age = holder.bind<TextView>(R.id.tv_age)
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
                    tv_content.text = "${data.egagementtext}"
                } else {
                    tv_content.text = ""
                }
            } else if (!(data.gexingqianming.isNullOrEmpty())) {
                if (!TextUtils.equals("null", data.gexingqianming)) {
                    tv_content.text = "${data.gexingqianming}"
                } else {
                    tv_content.text = ""
                }
            } else if (!data.ziwojieshao.isNullOrEmpty()) {
                if (!TextUtils.equals("null", data.ziwojieshao)) {
                    tv_content.text = "${data.ziwojieshao}"
                } else {
                    tv_content.text = ""
                }
            } else {
                tv_content.text = ""
            }
        }
    }

    fun mShowBigLayout(holder: View, position: Int, data: FindDate) {

        val newheadView = holder.findViewById<SimpleDraweeView>(R.id.newheadView)
        newheadView.setImageURI(data.picUrl)

        val rv_mydate_newtags = holder.findViewById<RecyclerView>(R.id.rv_mydate_newtags)
        rv_mydate_newtags.setHasFixedSize(true)
        rv_mydate_newtags.layoutManager = GridLayoutManager(mContext, 2)
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

        var tv_newzuojia = holder.findViewById<TextView>(R.id.tv_newzuojia)
        if (!data.zuojia.isNullOrEmpty()) {
            AppUtils.setTvNewTag(mContext, "座驾 ${data.zuojia}", 0, 2, tv_newzuojia)
        } else {
            AppUtils.setTvNewTag(mContext, "座驾", 0, 2, tv_newzuojia)
            tv_newzuojia.visibility = View.VISIBLE
        }

        var tv_newaihao = holder.findViewById<TextView>(R.id.tv_newaihao)
        if (!data.xingquaihao.isNullOrEmpty()) {
            var sb = StringBuffer()
            sb.append("爱好 ")
            var mHobbies = data.xingquaihao?.replace("#", ",")?.split(",")
            if (mHobbies != null) {
                for (str in mHobbies) {
                    sb.append("${str} ")
                }
                if (sb.length > 14) {
                    AppUtils.setTvNewTag(mContext, "${sb.substring(0, 14)}...", 0, 2, tv_newaihao)
                } else {
                    AppUtils.setTvNewTag(mContext, "${sb}", 0, 2, tv_newaihao)
                }
            }
        } else {
            AppUtils.setTvNewTag(mContext, "爱好", 0, 2, tv_newaihao)
            tv_newaihao.visibility = View.VISIBLE
        }

        val img_date_newmenauther = holder.findViewById<ImageView>(R.id.img_date_newmenauther)

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

        var tv_newname = holder.findViewById<TextView>(R.id.tv_newname)
        tv_newname.text = data.name
        var tv_newvip = holder.findViewById<TextView>(R.id.tv_newvip)
        tv_newvip.visibility = View.VISIBLE
        var drawable = getLevelDrawable(data.userclassesid, mContext)
        if (drawable == null) {
            tv_newvip.backgroundDrawable = null
            tv_newvip.visibility = View.GONE
        } else {
            tv_newvip.backgroundDrawable = drawable
        }

        var tv_linetime = holder.findViewById<TextView>(R.id.tv_linetime)
        if (data.iOnline == 1) {
            tv_linetime.visibility = View.GONE
        } else {
            tv_linetime.visibility = View.VISIBLE
            if (data.iOnline == 2) {
                setLeftDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape_dot_online), tv_linetime)
                tv_linetime.text = "当前状态·${data.sOnlineMsg}"
            } else {
                setLeftDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape_dot_translate), tv_linetime)
                tv_linetime.text = "在线时间·${data.sOnlineMsg}"
            }
        }

        var tv_loveheart_vistor = holder.findViewById<TextView>(R.id.tv_loveheart_vistor)

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

        val tv_newage = holder.findViewById<TextView>(R.id.tv_newage)
        val tv_newsex = holder.findViewById<TextView>(R.id.tv_newsex)
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

        var tv_newcontent = holder.findViewById<TextView>(R.id.tv_newcontent)
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