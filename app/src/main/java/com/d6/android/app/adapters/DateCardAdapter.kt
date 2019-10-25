package com.d6.android.app.adapters

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
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
import com.d6.android.app.utils.BitmapUtils.clearBitmap
import com.d6.android.app.widget.blurry.Blurry
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivity

class DateCardAdapter(mData: ArrayList<FindDate>) : BaseRecyclerAdapter<FindDate>(mData, R.layout.item_date_newcard) {

    private val mImages = ArrayList<String>()
    private val mTags = ArrayList<UserTag>()
    private var userId = SPUtils.instance().getString(Const.User.USER_ID)//35598

    var iDateComlete: Int = 0
    private var mLayoutNormal = 0 //0 大布局 1 小布局

    override fun onBind(holder: ViewHolder, position: Int, data: FindDate) {
        var rl_man_card = holder.bind<RelativeLayout>(R.id.rl_man_card)
        var rl_women_perfect = holder.bind<RelativeLayout>(R.id.rl_women_perfect)
        if (position == 4 && TextUtils.equals(data.accountId, userId)) {
            rl_man_card.visibility = View.GONE
            rl_women_perfect.visibility = View.VISIBLE
            showUserPerfect(holder, position, data)
        } else {
            rl_man_card.visibility = View.VISIBLE
            rl_women_perfect.visibility = View.GONE
            val rl_small_mendate_layout = holder.bind<RelativeLayout>(R.id.rl_small_mendate_layout)
            val rl_big_mendate_layout = holder.bind<RelativeLayout>(R.id.rl_big_mendate_layout)
            if (mLayoutNormal == 0) {
                rl_small_mendate_layout.visibility = View.GONE
                rl_big_mendate_layout.visibility = View.VISIBLE
                mShowBigLayout(holder, position, data)
            } else {
                rl_small_mendate_layout.visibility = View.VISIBLE
                rl_big_mendate_layout.visibility = View.GONE
                val rv_mydate_images = holder.bind<RecyclerView>(R.id.rv_mydate_images)
                val rv_mydate_tags = holder.bind<RecyclerView>(R.id.rv_mydate_tags)
                val nomg_line = holder.bind<View>(R.id.noimg_line)
                if (!TextUtils.equals(data.userpics, "null")) {
                    if (TextUtils.isEmpty(data.userpics)) {
                        mImages.clear()
                        rv_mydate_images.visibility = View.INVISIBLE
                        nomg_line.visibility = View.VISIBLE
                    } else {
                        var imglist = data.userpics.split(",")
                        if (imglist.size == 0) {
                            mImages.clear()
                            rv_mydate_images.visibility = View.INVISIBLE
                            nomg_line.visibility = View.GONE
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
                            (rv_mydate_images.adapter as DatelmageAdapter).setOnItemClickListener { adapter, view, p ->
                                //                            var index=  mData.indexOf(data)
//                            var mFindDate=  mData.get(index)
                                context.startActivity<UserInfoActivity>("id" to data.accountId.toString())
//                            var mShowPics = data.userpics.split(",")
//                            CustomToast.showToast("${data.name}=${position}=${mFindDate.name}")
//                            context.startActivity<ImagePagerActivity>(ImagePagerActivity.URLS to mShowPics, ImagePagerActivity.CURRENT_POSITION to p)
                            }
                        }
                    }
                } else {
                    mImages.clear()
                    rv_mydate_images.visibility = View.INVISIBLE
                    nomg_line.visibility = View.GONE
                }

                rv_mydate_tags.setHasFixedSize(true)
                rv_mydate_tags.layoutManager = GridLayoutManager(context, 2)
                rv_mydate_tags.isNestedScrollingEnabled = false

                mTags.clear()
                if (!data.shengao.isNullOrEmpty()) {
                    mTags.add(UserTag("身高 ${data.shengao}", R.mipmap.boy_stature_whiteicon))
                }

                if (!data.tizhong.isNullOrEmpty()) {
                    mTags.add(UserTag("体重 ${data.tizhong}", R.mipmap.boy_weight_whiteicon))
                }

                if (!data.xingzuo.isNullOrEmpty()) {
                    mTags.add(UserTag("星座 ${data.xingzuo}", R.mipmap.boy_constellation_whiteicon))
                }

                if (!data.city.isNullOrEmpty()) {
                    mTags.add(UserTag("地区 ${data.city}", R.mipmap.boy_area_whiteicon))
                }

                if(!data.zhiye.isNullOrEmpty()){
                    mTags.add(UserTag("职业 ${data.zhiye}", R.mipmap.boy_profession_whiteicon))
                }

                if(!data.city.isNullOrEmpty()){
                    mTags.add(UserTag("约会地 ${data.city}", R.mipmap.boy_datearea_whiteicon,3))
                }

                rv_mydate_tags.adapter = CardManTagAdapter(mTags)

//                var tv_job = holder.bind<TextView>(R.id.tv_job)
//                if (!data.zhiye.isNullOrEmpty()) {
//                    AppUtils.setTvNewTag(context, "职业 ${data.zhiye}", 0, 2, tv_job)
//                } else {
//                    tv_job.visibility = View.GONE
//                }

                var tv_zuojia = holder.bind<TextView>(R.id.tv_zuojia)
                if (!data.zuojia.isNullOrEmpty()) {
                    AppUtils.setTvNewTag(context, "座驾 ${data.zuojia}", 0, 2, tv_zuojia)
                } else {
                    tv_zuojia.visibility = View.GONE
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
                        AppUtils.setTvNewTag(context, sb.toString(), 0, 2, tv_aihao)
                    }
                } else {
                    tv_aihao.visibility = View.GONE
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
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_ordinary))
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
                } else if (TextUtils.equals(data.userclassesid, "23")) {
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_silver))
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
                } else if (TextUtils.equals(data.userclassesid, "24")) {
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_gold))
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
                } else if (TextUtils.equals(data.userclassesid, "25")) {
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_diamonds))
                    tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
                } else if (TextUtils.equals(data.userclassesid, "26")) {
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_private))
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

                val tv_age = holder.bind<TextView>(R.id.tv_age)

                var tv_linetime = holder.bind<TextView>(R.id.tv_vistor_count)
                var tv_loveheart_vistor = holder.bind<TextView>(R.id.tv_like_count)

                if(data.sOnlineMsg.isNullOrEmpty()){
                    tv_linetime.visibility = View.GONE
                }else{
                    tv_linetime.visibility = View.VISIBLE
                    if(data.sOnlineMsg.indexOf(context.getString(R.string.string_nowonline))!=-1){
                        setLeftDrawable(ContextCompat.getDrawable(context,R.drawable.shape_dot_online),tv_linetime)
                    }else{
                        setLeftDrawable(ContextCompat.getDrawable(context,R.drawable.shape_dot_translate),tv_linetime)
                    }
                    tv_linetime.text = "${data.sOnlineMsg}"
                }

                if (data.iVistorCountAll >= 10) {
                    tv_loveheart_vistor.text = "送出 [img src=redheart_small/] · ${data.iReceiveLovePoint}     访客·${data.iVistorCountAll}"
                } else {
                    tv_loveheart_vistor.text = "送出 [img src=redheart_small/] · ${data.iReceiveLovePoint}"
                }

                if (!data.nianling.isNullOrEmpty()) {
                    if (TextUtils.equals("0", data.nianling.toString())) {
                        tv_age.text = ""
                    } else {
                        tv_age.text = data.nianling
                    }
                }

                tv_age.isSelected = TextUtils.equals("0", data.sex)
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

            var rl_perfect_womanuserinfo = holder.bind<RelativeLayout>(R.id.rl_perfect_womenuserinfo)
            if (position == 4 && TextUtils.equals(data.accountId, userId)) {
                if (iDateComlete < 60) {
                    rl_perfect_womanuserinfo.visibility = View.VISIBLE
                } else {
                    rl_perfect_womanuserinfo.visibility = View.GONE
                }
            } else {
                rl_perfect_womanuserinfo.visibility = View.GONE
            }
        }

        var imageView = holder.bind<SimpleDraweeView>(R.id.imageView)
//            var imageViewbg = holder.bind<ImageView>(R.id.imageViewbg)
        imageView.showBlur(data.picUrl)

        val onClickListener = View.OnClickListener {
            mOnItemClickListener?.onItemClick(it, position)
        }
        holder.bind<View>(R.id.cardView).setOnClickListener(onClickListener)
        holder.bind<TextView>(R.id.tv_perfect_userinfo).setOnClickListener(onClickListener)
    }

    fun mShowBigLayout(holder: ViewHolder, position: Int, data: FindDate) {
//        holder.setIsRecyclable(false)
        val newheadView = holder.bind<SimpleDraweeView>(R.id.newheadView)
        newheadView.setImageURI(data.picUrl)

        val rv_mydate_newtags = holder.bind<RecyclerView>(R.id.rv_mydate_newtags)
        var sdv_one = holder.bind<SimpleDraweeView>(R.id.sdv_one)
        var sdv_two = holder.bind<SimpleDraweeView>(R.id.sdv_two)
        var sdv_three = holder.bind<SimpleDraweeView>(R.id.sdv_three)
        var sdv_four = holder.bind<SimpleDraweeView>(R.id.sdv_four)
        var sdv_five = holder.bind<SimpleDraweeView>(R.id.sdv_five)
        if (!TextUtils.equals(data.userpics, "null")) {
            if (!TextUtils.isEmpty(data.userpics)) {
                var imglist = data.userpics.split(",")
                if (imglist.size!= 0) {
                    if(imglist.size==1){
                        sdv_one.setImageURI(imglist[0])
                        sdv_two.setImageURI("")
                        sdv_three.setImageURI("")
                        sdv_four.setImageURI("")
                        sdv_five.setImageURI("")
                    }else if(imglist.size==2){
                        sdv_one.setImageURI(imglist[0])
                        sdv_two.setImageURI(imglist[1])
                        sdv_three.setImageURI("")
                        sdv_four.setImageURI("")
                        sdv_five.setImageURI("")
                    }else if(imglist.size==3){
                        sdv_one.setImageURI(imglist[0])
                        sdv_two.setImageURI(imglist[1])
                        sdv_three.setImageURI(imglist[2])
                        sdv_four.setImageURI("")
                        sdv_five.setImageURI("")
                    }else if(imglist.size==4){
                        sdv_one.setImageURI(imglist[0])
                        sdv_two.setImageURI(imglist[1])
                        sdv_three.setImageURI(imglist[2])
                        sdv_four.setImageURI(imglist[3])
                        sdv_five.setImageURI("")
                    }else{
                        sdv_one.setImageURI(imglist[0])
                        sdv_two.setImageURI(imglist[1])
                        sdv_three.setImageURI(imglist[2])
                        sdv_four.setImageURI(imglist[3])
                        sdv_five.setImageURI(imglist[4])
                    }
                }else{
                    sdv_one.setImageURI(data.picUrl)
                    sdv_two.setImageURI("")
                    sdv_three.setImageURI("")
                    sdv_four.setImageURI("")
                    sdv_five.setImageURI("")
                }
            }else{
                sdv_one.setImageURI("")
                sdv_two.setImageURI("")
                sdv_three.setImageURI("")
                sdv_four.setImageURI("")
                sdv_five.setImageURI("")
            }
        }else{
            sdv_one.setImageURI("")
            sdv_two.setImageURI("")
            sdv_three.setImageURI("")
            sdv_four.setImageURI("")
            sdv_five.setImageURI("")
        }

        rv_mydate_newtags.setHasFixedSize(true)
        rv_mydate_newtags.layoutManager = GridLayoutManager(context, 2)
        rv_mydate_newtags.isNestedScrollingEnabled = false
        mTags.clear()
        if (!data.shengao.isNullOrEmpty()) {
            mTags.add(UserTag("身高 ${data.shengao}", R.mipmap.boy_stature_whiteicon))
        }

        if (!data.tizhong.isNullOrEmpty()) {
            mTags.add(UserTag("体重 ${data.tizhong}", R.mipmap.boy_weight_whiteicon))
        }

        if (!data.xingzuo.isNullOrEmpty()) {
            mTags.add(UserTag("星座 ${data.xingzuo}", R.mipmap.boy_constellation_whiteicon))
        }

        if (!data.city.isNullOrEmpty()) {
            mTags.add(UserTag("地区 ${data.city}", R.mipmap.boy_area_whiteicon))
        }

        if(!data.zhiye.isNullOrEmpty()){
            mTags.add(UserTag("职业 ${data.zhiye}", R.mipmap.boy_profession_whiteicon))
        }

//        var sb = StringBuffer()
//        sb.append("约会地")
//        if(!data.userlookwhere.isNullOrEmpty()){
//            sb.append(" ${data.userlookwhere}")
//        }
//
//        if(!data.userhandlookwhere.isNullOrEmpty()){
//            sb.append("、${data.userhandlookwhere}")
//        }
//
//        if(sb.length>3){
//            mTags.add(UserTag("${sb}", R.mipmap.boy_datearea_whiteicon,3))
//        }

        if(!data.city.isNullOrEmpty()){
            mTags.add(UserTag("约会地 ${data.city}", R.mipmap.boy_datearea_whiteicon,3))
        }

        rv_mydate_newtags.adapter = CardManTagAdapter(mTags)

//        var tv_newjob = holder.bind<TextView>(R.id.tv_newjob)
//        if (!data.zhiye.isNullOrEmpty()) {
//            AppUtils.setTvNewTag(context, "职业 ${data.zhiye}", 0, 2, tv_newjob)
//        } else {
//            tv_newjob.visibility = View.GONE
//        }

        var tv_newzuojia = holder.bind<TextView>(R.id.tv_newzuojia)
        if (!data.zuojia.isNullOrEmpty()) {
            AppUtils.setTvNewTag(context, "座驾 ${data.zuojia}", 0, 2, tv_newzuojia)
        } else {
            tv_newzuojia.visibility = View.GONE
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
                AppUtils.setTvNewTag(context, sb.toString(), 0, 2, tv_newaihao)
            }
        } else {
            tv_newaihao.visibility = View.GONE
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

        if (data.name.length >= 7) {
            holder.setText(R.id.tv_newname, "${data.name.substring(0, 6)}...")
        } else {
            holder.setText(R.id.tv_newname, data.name)
        }

        var tv_newvip = holder.bind<TextView>(R.id.tv_newvip)
        tv_newvip.visibility = View.VISIBLE
        var drawable = getLevelDrawable(data.userclassesid,context)
        if(drawable==null){
            tv_newvip.backgroundDrawable = null
            tv_newvip.visibility = View.GONE
        }else{
            tv_newvip.backgroundDrawable = drawable
        }

        var tv_linetime = holder.bind<TextView>(R.id.tv_linetime)
        if(data.sOnlineMsg.isNullOrEmpty()){
            tv_linetime.visibility = View.GONE
        }else{
            tv_linetime.visibility = View.VISIBLE
            if(data.sOnlineMsg.indexOf(context.getString(R.string.string_nowonline))!=-1){
                setLeftDrawable(ContextCompat.getDrawable(context,R.drawable.shape_dot_online),tv_linetime)
            }else{
                setLeftDrawable(ContextCompat.getDrawable(context,R.drawable.shape_dot_translate),tv_linetime)
            }
            tv_linetime.text = "${data.sOnlineMsg}"
        }

        var tv_loveheart_vistor = holder.bind<TextView>(R.id.tv_loveheart_vistor)

        if (data.iVistorCountAll >= 10) {
            tv_loveheart_vistor.text = "送出 [img src=redheart_small/] · ${data.iReceiveLovePoint}     访客·${data.iVistorCountAll}"
        } else {
            tv_loveheart_vistor.text = "送出 [img src=redheart_small/] · ${data.iReceiveLovePoint}"
        }
        val tv_newage = holder.bind<TextView>(R.id.tv_newage)
        if (!data.nianling.isNullOrEmpty()) {
            if (TextUtils.equals("0", data.nianling.toString())) {
                tv_newage.text = ""
            } else {
                tv_newage.text = data.nianling
            }
        }
        tv_newage.isSelected = TextUtils.equals("0", data.sex)

        var  tv_newcontent = holder.bind<TextView>(R.id.tv_newcontent)
        tv_newcontent.visibility = View.VISIBLE
        if (!data.egagementtext.isNullOrEmpty()) {
            if (!TextUtils.equals("null", data.egagementtext)) {
                tv_newcontent.text =  "${data.egagementtext}"
            } else {
                tv_newcontent.visibility = View.GONE
            }
        } else if (!(data.gexingqianming.isNullOrEmpty())) {
            if (!TextUtils.equals("null", data.gexingqianming)) {
                tv_newcontent.text =  "${data.gexingqianming}"
            } else {
                tv_newcontent.visibility = View.GONE
            }
        } else if (!data.ziwojieshao.isNullOrEmpty()) {
            if (!TextUtils.equals("null", data.ziwojieshao)) {
                tv_newcontent.text =  "${ data.ziwojieshao}"
            } else {
                tv_newcontent.visibility = View.GONE
            }
        } else {
            tv_newcontent.visibility = View.GONE
        }
    }

    /**
     * 引导女性用户资料修改
     */
    fun showUserPerfect(holder: ViewHolder, position: Int, data: FindDate) {

        val women_perfect_bg = holder.bind<SimpleDraweeView>(R.id.women_perfect_bg)
        val rv_mydate_tags = holder.bind<RecyclerView>(R.id.rv_mydate_women_perfect_tags)
        women_perfect_bg.showBlur(data.picUrl)

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

        val bigImgView = holder.bind<SimpleDraweeView>(R.id.women_perfect_imageView)
        if (!TextUtils.equals(data.userpics, "null")) {
            if (TextUtils.isEmpty(data.userpics)) {
                bigImgView.setImageURI(data.picUrl)
            } else {
                var images = data.userpics.split(",")
                if (images.size > 0) {
                    bigImgView.setImageURI(images[0])
                }
            }
        } else {
            bigImgView.setImageURI(data.picUrl)
        }

//        val headView = holder.bind<SimpleDraweeView>(R.id.women_perfect_headView)
//        headView.setImageURI(data.picUrl)

        if (data.name.length >= 8) {
            holder.setText(R.id.tv_women_perfect_name, "${data.name.substring(0, 7)}...")
        } else {
            holder.setText(R.id.tv_women_perfect_name, data.name)
        }

        val tv_age = holder.bind<TextView>(R.id.tv_women_perfect_age)

        if (!data.nianling.isNullOrEmpty()) {
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
        holder.setText(R.id.tv_women_perfect_city, a)
        val tv_women_perfect_online_time = holder.bind<TextView>(R.id.tv_women_perfect_online_time)
        val tv_vistorfollownums = holder.bind<TextView>(R.id.tv_women_perfect_vistorfollownums)
        tv_women_perfect_online_time.text = "在线时间"
        if (data.iVistorCountAll >= 10) {
            tv_vistorfollownums.text = "送出 [img src=redheart_small/] · ${data.iReceiveLovePoint}    访客·${data.iVistorCountAll} "
        } else {
            tv_vistorfollownums.text = "送出 [img src=redheart_small/] · ${data.iReceiveLovePoint}"
        }
    }
}