package com.d6.android.app.adapters

import android.graphics.Bitmap
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
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.FindDate
import com.d6.android.app.models.UserTag
import com.d6.android.app.utils.*
import com.d6.android.app.utils.BitmapUtils.clearBitmap
import com.d6.android.app.widget.convenientbanner.ConvenientBanner
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.flexbox.FlexboxLayoutManager
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.imageBitmap

class DateWomanCardAdapter(mData: ArrayList<FindDate>) : BaseRecyclerAdapter<FindDate>(mData, R.layout.item_date_womennewcard) {

    private val mTags = ArrayList<UserTag>()
    var iDateComlete: Int = 0

    private var mBannerImages = ArrayList<String>()

    private var userId = SPUtils.instance().getString(Const.User.USER_ID)//35598

    override fun onBind(holder: ViewHolder, position: Int, data: FindDate) {
        val rl_women_perfect = holder.bind<RelativeLayout>(R.id.rl_man_perfect)
        val rl_women_card = holder.bind<RelativeLayout>(R.id.rl_women_card)
        if (position == 4 && TextUtils.equals(data.accountId, userId)) {
            rl_women_perfect.visibility = View.VISIBLE
            rl_women_card.visibility = View.GONE
            showUserPerfect(holder, position, data)
        } else {
            rl_women_perfect.visibility = View.GONE
            rl_women_card.visibility = View.VISIBLE

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
                        if (!TextUtils.isEmpty(str)) {
                            mTags.add(UserTag(str, R.drawable.shape_tag_bg_6))
                        }
                    }
                }
            }

            rv_mydate_tags.adapter = CardTagAdapter(mTags)
            val tv_indexofpics = holder.bind<TextView>(R.id.tv_indexofpics)
            var bigImgView = holder.bind<SimpleDraweeView>(R.id.imageView)
            val iv_wh = holder.bind<ImageView>(R.id.iv_wh)
            mBannerImages.clear()
            if (!TextUtils.equals(data.userpics, "null")) {
                if (TextUtils.isEmpty(data.userpics)) {
                    mBannerImages.add(data.picUrl)
//                    bigImgView.setImageURI(data.picUrl)
                    tv_indexofpics.visibility = View.GONE
                } else {
                    var images = data.userpics.split(",")
                    if (images.size > 0) {
                        mBannerImages.addAll(images)
//                        bigImgView.setImageURI(images[0])
                    }
                    tv_indexofpics.visibility = View.VISIBLE
                    tv_indexofpics.setText("1/${images.size}")
                }
            } else {
                mBannerImages.add(data.picUrl)
//                bigImgView.setImageURI(data.picUrl)
                tv_indexofpics.visibility = View.GONE
            }
            clearBitmap(iv_wh)
            FrescoUtils.loadImage(context,mBannerImages[0],object: IResult<Bitmap> {
                override fun onResult(result: Bitmap?) {
                    result?.let {
                        if(it.width>=it.height){
                            bigImgView.showBlur(mBannerImages[0])
                            iv_wh.setImageBitmap(it)
                        }else{
                            bigImgView.setImageURI(mBannerImages[0])
                        }
                    }
                }
            })
            //简单使用
//        mBanners.setImages()
//                .setImageLoader(FrescoImageLoader())
//                .start()
//            var bannerImages = holder.bind<ConvenientBanner<String>>(R.id.banner_grils)
//            bannerImages.setPages(
//                    object : CBViewHolderCreator {
//                        override fun createHolder(itemView: View): WomenFindHolder {
//                            return WomenFindHolder(itemView)
//                        }
//
//                        override fun getLayoutId(): Int {
//                            return R.layout.item_findwomenbanner
//                        }
//                    },mBannerImages)
//
//            bannerImages.setOnPageChangeListener(object: OnPageChangeListener {
//                override fun onPageSelected(index: Int) {
//                    when(index){
//                        0-> {
////                            tv_numone.isEnabled = false
////                            tv_numtwo.isEnabled = true
////                            tv_numthree.isEnabled = true
//                        }
//                        1->{
////                            tv_numone.isEnabled = true
////                            tv_numtwo.isEnabled = false
////                            tv_numthree.isEnabled = true
//                        }
//                        2->{
////                            tv_numone.isEnabled = true
////                            tv_numtwo.isEnabled = true
////                            tv_numthree.isEnabled = false
//                        }
//                    }
//                }
//
//                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
//                }
//
//                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//                }
//            })

//            bannerImages.startTurning()


            val headView = holder.bind<SimpleDraweeView>(R.id.headView)
            headView.setImageURI(data.picUrl)

            if (data.name.length >= 8) {
                holder.setText(R.id.tv_name, "${data.name.substring(0, 6)}...")
            } else {
                holder.setText(R.id.tv_name, data.name)
            }

            val img_date_womenauther = holder.bind<ImageView>(R.id.img_date_womenauther)

            if(TextUtils.equals("0",data!!.screen)|| data!!.screen.isNullOrEmpty()){
                img_date_womenauther.visibility = View.GONE
            }else if(TextUtils.equals("1",data!!.screen)){
                img_date_womenauther.visibility = View.VISIBLE
                img_date_womenauther.setImageResource(R.mipmap.video_small)
            }else if(TextUtils.equals("3",data!!.screen)){
                img_date_womenauther.visibility = View.GONE
                img_date_womenauther.setImageResource(R.mipmap.renzheng_small)
            }else{
                img_date_womenauther.visibility = View.GONE
            }

            var tv_women_vip = holder.bind<TextView>(R.id.tv_vip)
            tv_women_vip.visibility = View.VISIBLE
            var drawable = getLevelDrawable(data.userclassesid,context)
            if(drawable!=null){
                tv_women_vip.backgroundDrawable = drawable
            }else{
                tv_women_vip.backgroundDrawable = null
                tv_women_vip.visibility = View.GONE
            }

            val tv_woman_age = holder.bind<TextView>(R.id.tv_womang_age)

            if (!data.nianling.isNullOrEmpty()) {
                if (TextUtils.equals("0", data.nianling)) {
                    tv_woman_age.text = ""
                } else {
                    tv_woman_age.text = data.nianling
                }
            }

            if(data.nianling.isNullOrEmpty()){
                tv_woman_age.text = ""
            }

            tv_woman_age.isSelected = TextUtils.equals("0", data.sex)

            var tv_content = holder.bind<TextView>(R.id.tv_content)

            if (!data.egagementtext.isNullOrEmpty()) {
                if(!TextUtils.equals("null",data.egagementtext)){
                    tv_content.visibility = View.GONE
                    tv_content.text =  data.egagementtext
                }else{
                    tv_content.visibility = View.GONE
                }
            } else if (!(data.gexingqianming.isNullOrEmpty())) {
                if(!TextUtils.equals("null",data.gexingqianming)){
                    tv_content.visibility = View.GONE
                    tv_content.text = data.gexingqianming
                }else{
                    tv_content.visibility = View.GONE
                }
            }else if(!data.ziwojieshao.isNullOrEmpty()){
                if(!TextUtils.equals("null",data.ziwojieshao)){
                    tv_content.visibility = View.GONE
                    tv_content.text = data.ziwojieshao
                }else{
                    tv_content.visibility = View.GONE
                }
            }else{
                tv_content.visibility = View.GONE
            }

            if (TextUtils.equals("null", data.userlookwhere)) {
                data.userlookwhere = ""
            }
            if (TextUtils.equals("null", data.userhandlookwhere)) {
                data.userhandlookwhere = ""
            }

            if(data.iPositionType==1){
                holder.setText(R.id.tv_city, data.city)
            }else{
                holder.setText(R.id.tv_city, data.sPosition)
            }

            val tv_vistorfollownums = holder.bind<TextView>(R.id.tv_vistorfollownums)
            var ll_user_vistorfollownums = holder.bind<LinearLayout>(R.id.ll_user_vistorfollownums)
            tv_vistorfollownums.visibility = View.VISIBLE
            var tv_online_time = holder.bind<TextView>(R.id.tv_online_time)
            if(data.sOnlineMsg.isNullOrEmpty()){
                tv_online_time.visibility = View.GONE
                ll_user_vistorfollownums.visibility = View.GONE
            }else{
                tv_online_time.visibility = View.VISIBLE
                ll_user_vistorfollownums.visibility = View.VISIBLE
                if(data.sOnlineMsg.indexOf(context.getString(R.string.string_nowonline))!=-1){
                    setLeftDrawable(ContextCompat.getDrawable(context,R.drawable.shape_dot_online),tv_online_time)
                }else{
                    setLeftDrawable(ContextCompat.getDrawable(context,R.drawable.shape_dot_translate),tv_online_time)
                }
                tv_online_time.text = "${data.sOnlineMsg}"
            }

            if (data.iVistorCountAll >= 50) {
                ll_user_vistorfollownums.visibility = View.VISIBLE
                tv_vistorfollownums.text = "收到 [img src=redheart_small/] · ${data.iReceiveLovePoint}     访客·${data.iVistorCountAll}"
            } else {
                if(data.iReceiveLovePoint > 0){
                    ll_user_vistorfollownums.visibility = View.VISIBLE
                    tv_vistorfollownums.text = "收到 [img src=redheart_small/] · ${data.iReceiveLovePoint}"
                }else{
                    ll_user_vistorfollownums.visibility = View.GONE
                    tv_vistorfollownums.visibility = View.GONE
                }
            }
        }

        val onClickListener = View.OnClickListener {
            mOnItemClickListener?.onItemClick(it, position)
        }

        var rl_perfect_womanuserinfo = holder.bind<RelativeLayout>(R.id.rl_perfect_manuserinfo)
        if (position == 4 && TextUtils.equals(data.accountId, userId)) {
            if (iDateComlete < 60) {
                rl_perfect_womanuserinfo.visibility = View.VISIBLE
            } else {
                rl_perfect_womanuserinfo.visibility = View.GONE
            }
        } else {
            rl_perfect_womanuserinfo.visibility = View.GONE
        }

        holder.bind<View>(R.id.cardView).setOnClickListener(onClickListener)
        holder.bind<TextView>(R.id.tv_perfect_userinfo).setOnClickListener(onClickListener)
    }

    private var mImages = ArrayList<String>()
    /**
     * 引导男性用户资料修改
     */
    fun showUserPerfect(holder: ViewHolder, position: Int, data: FindDate) {
        val rv_mydate_images = holder.bind<RecyclerView>(R.id.rv_mydate_man_pecfect_images)
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

        val rv_local_user_tags = holder.bind<RecyclerView>(R.id.rv_mydate_man_pecfect_tags)
        rv_local_user_tags.setHasFixedSize(true)
        rv_local_user_tags.layoutManager = GridLayoutManager(context, 2)
        rv_local_user_tags.isNestedScrollingEnabled = false

        mTags.clear()
        if (!data.shengao.isNullOrEmpty()) {
            mTags.add(UserTag("身高 ${data.shengao}", R.mipmap.boy_stature_icon,2))
        }

        if (!data.tizhong.isNullOrEmpty()) {
            mTags.add(UserTag("体重 ${data.tizhong}", R.mipmap.boy_weight_icon,2))
        }

        if (!data.xingzuo.isNullOrEmpty()) {
            mTags.add(UserTag("星座 ${data.xingzuo}", R.mipmap.boy_constellation_icon,2))
        }

        if (!data.city.isNullOrEmpty()) {
            mTags.add(UserTag("地区 ${data.city}", R.mipmap.boy_area_icon,2))
        }

        rv_local_user_tags.adapter = CardManTagAdapter(mTags)

        var tv_job = holder.bind<TextView>(R.id.tv_man_pecfect_job)
        if (!data.zhiye.isNullOrEmpty()) {
            AppUtils.setTvNewTag(context, "职业 ${data.zhiye}", 0, 2, tv_job)
        }

        var tv_zuojia = holder.bind<TextView>(R.id.tv_man_pecfect_zuojia)
        if (!data.zuojia.isNullOrEmpty()) {
            AppUtils.setTvNewTag(context, "座驾 ${data.zuojia}", 0, 2, tv_zuojia)
        }

        var tv_aihao = holder.bind<TextView>(R.id.tv_man_pecfect_aihao)
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
        }

        val man_perfect_headView = holder.bind<SimpleDraweeView>(R.id.man_perfect_headView)
        man_perfect_headView.setImageURI(data.picUrl)

        if (data.name.length >= 8) {
            holder.setText(R.id.tv_man_perfect_name, "${data.name.substring(0, 6)}...")
        } else {
            holder.setText(R.id.tv_man_perfect_name, data.name)
        }

        val tv_man_perferctvip = holder.bind<TextView>(R.id.tv_man_perfect_vip)

        if(TextUtils.equals(data.userclassesid,"22")){
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_ordinary))
            tv_man_perferctvip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_ordinary)
        }else if(TextUtils.equals(data.userclassesid,"23")){
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_silver))
            tv_man_perferctvip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_silver)
        }else if(TextUtils.equals(data.userclassesid,"24")){
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_gold))
            tv_man_perferctvip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_gold)
        }else if(TextUtils.equals(data.userclassesid,"25")){
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_diamonds))
            tv_man_perferctvip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_zs)
        }else if(TextUtils.equals(data.userclassesid,"26")){
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_private))
            tv_man_perferctvip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_private)
        }else if(TextUtils.equals(data.userclassesid,"7")){
            tv_man_perferctvip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.youke_icon)
        }else if(TextUtils.equals(data.userclassesid,"30")){
            tv_man_perferctvip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.ruqun_icon)
        }else if(TextUtils.equals(data.userclassesid,"31")){
            tv_man_perferctvip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.app_vip)
        }

//        if(data.classesname.isNotEmpty()){
//            tv_man_perferctvip.text = data.classesname
//            tv_man_perferctvip.visibility = View.VISIBLE
//        }else{
//            tv_man_perferctvip.visibility = View.GONE
//        }

        val tv_age = holder.bind<TextView>(R.id.tv_man_pecfect_age)

        var ll_vistor = holder.bind<LinearLayout>(R.id.ll_man_pecfert_vistor)
        var ll_like = holder.bind<LinearLayout>(R.id.ll_man_pecfert_like)
        var tv_vistor_count = holder.bind<TextView>(R.id.tv_vistor_man_pecfert_count)
        var tv_like_count = holder.bind<TextView>(R.id.tv_like_man_pecfert_count)

        if (data.iVistorCountAll > 50) {
            ll_like.visibility = View.VISIBLE
            ll_vistor.visibility = View.VISIBLE
            tv_vistor_count.text = "${data.iVistorCountAll}"
            tv_like_count.text = "${data.iFansCountAll}"
        } else {
            ll_like.visibility = View.VISIBLE
            ll_vistor.visibility = View.GONE
            tv_like_count.text = "${data.iFansCountAll}"
        }

        if (!data.nianling.isNullOrEmpty()) {
            if (TextUtils.equals("0", data.nianling)) {
                tv_age.text = ""
            } else {
                tv_age.text = if(data.nianling.equals("null")) "0" else data.nianling
            }
        }

        tv_age.isSelected = TextUtils.equals("0", data.sex)
        if (!data.egagementtext.isNullOrEmpty()) {
            holder.setText(R.id.tv_man_pecfect_content, data.egagementtext)
        } else if (!(data.gexingqianming.isNullOrEmpty())) {
            holder.setText(R.id.tv_man_pecfect_content, data.gexingqianming)
        }
    }
}