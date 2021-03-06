package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.FindDate
import com.d6.android.app.models.UserTag
import com.d6.android.app.utils.AppUtils
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.flexbox.FlexboxLayoutManager
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivity

class DateCardAdapter(mData: ArrayList<FindDate>) : BaseRecyclerAdapter<FindDate>(mData, R.layout.item_date_newcard) {

    private val mImages = ArrayList<String>()
    private val mTags = ArrayList<UserTag>()
    private var userId = SPUtils.instance().getString(Const.User.USER_ID)//35598

    var iDateComlete: Int = 0

    override fun onBind(holder: ViewHolder, position: Int, data: FindDate) {
        val rl_man_card = holder.bind<RelativeLayout>(R.id.rl_man_card)
        val rl_women_perfect = holder.bind<RelativeLayout>(R.id.rl_women_perfect)
        if (position == 4 && TextUtils.equals(data.accountId, userId)) {
            rl_man_card.visibility = View.GONE
            rl_women_perfect.visibility = View.VISIBLE
            showUserPerfect(holder, position, data)
        } else {
            rl_man_card.visibility = View.VISIBLE
            rl_women_perfect.visibility = View.GONE
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

                        rv_mydate_images.adapter = DatelmageAdapter(mImages,1)
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
                mTags.add(UserTag("地区 ${data.city}", R.mipmap.boy_area_icon))
            }

            rv_mydate_tags.adapter = CardManTagAdapter(mTags)

            var tv_job = holder.bind<TextView>(R.id.tv_job)
            if (!data.zhiye.isNullOrEmpty()) {
                AppUtils.setTvTag(context, "职业 ${data.zhiye}", 0, 2, tv_job)
            }else{
                tv_job.visibility = View.GONE
            }

            var tv_zuojia = holder.bind<TextView>(R.id.tv_zuojia)
            if (!data.zuojia.isNullOrEmpty()) {
                AppUtils.setTvTag(context, "座驾 ${data.zuojia}", 0, 2, tv_zuojia)
            }else{
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
                    AppUtils.setTvTag(context, sb.toString(), 0, 2, tv_aihao)
                }
            }else{
                tv_aihao.visibility = View.GONE
            }

            val headView = holder.bind<SimpleDraweeView>(R.id.headView)
            headView.setImageURI(data.picUrl)

            val img_date_menauther = holder.bind<ImageView>(R.id.img_date_menauther)

            if(TextUtils.equals("0",data!!.screen)|| data!!.screen.isNullOrEmpty()){
                img_date_menauther.visibility = View.GONE
            }else if(TextUtils.equals("1",data!!.screen)){
                img_date_menauther.setImageResource(R.mipmap.video_small)
            }else if(TextUtils.equals("3",data!!.screen)){
                img_date_menauther.visibility = View.GONE
                img_date_menauther.setImageResource(R.mipmap.renzheng_small)
            }else{
                img_date_menauther.visibility = View.GONE
            }


            if (data.name.length >= 5) {
                holder.setText(R.id.tv_name, "${data.name.substring(0, 4)}...")
            } else {
                holder.setText(R.id.tv_name, data.name)
            }

            var tv_vip = holder.bind<TextView>(R.id.tv_vip)
            tv_vip.visibility = View.VISIBLE
            if(TextUtils.equals(data.userclassesid.toString(),"22")){
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_ordinary))
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_ordinary)
            }else if(TextUtils.equals(data.userclassesid,"23")){
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_silver))
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_silver)
            }else if(TextUtils.equals(data.userclassesid,"24")){
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_gold))
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_gold)
            }else if(TextUtils.equals(data.userclassesid,"25")){
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_diamonds))
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_zs)
            }else if(TextUtils.equals(data.userclassesid,"26")){
//                        headerView.tv_vip.text = String.format("%s",getString(R.string.string_private))
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_private)
            }else if(TextUtils.equals(data.userclassesid,"7")){
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.youke_icon)
            }else if(TextUtils.equals(data.userclassesid,"30")){
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.ruqun_icon)
            }else if(TextUtils.equals(data.userclassesid,"31")){
                tv_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.app_vip)
            }else{
                tv_vip.backgroundDrawable = null
                tv_vip.visibility = View.GONE
            }

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
            if (!data.egagementtext.isNullOrEmpty()) {
                if(!TextUtils.equals("null",data.egagementtext)){
                    holder.setText(R.id.tv_content, data.egagementtext)
                }else{
                    holder.setText(R.id.tv_content, "")
                }
            } else if (!(data.gexingqianming.isNullOrEmpty())) {
                if(!TextUtils.equals("null",data.gexingqianming)){
                    holder.setText(R.id.tv_content, data.gexingqianming)
                }else{
                    holder.setText(R.id.tv_content, "")
                }
            }else if(!data.ziwojieshao.isNullOrEmpty()){
                if(!TextUtils.equals("null",data.ziwojieshao)){
                    holder.setText(R.id.tv_content, data.ziwojieshao)
                }else{
                    holder.setText(R.id.tv_content, "")
                }
            }else{
                holder.setText(R.id.tv_content,"")
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

        val onClickListener = View.OnClickListener {
            mOnItemClickListener?.onItemClick(it, position)
        }
        holder.bind<View>(R.id.cardView).setOnClickListener(onClickListener)
        holder.bind<TextView>(R.id.tv_perfect_userinfo).setOnClickListener(onClickListener)
    }

    /**
     * 引导女性用户资料修改
     */
    fun showUserPerfect(holder: ViewHolder, position: Int, data: FindDate) {
        val rv_mydate_tags = holder.bind<RecyclerView>(R.id.rv_mydate_women_perfect_tags)
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

        val headView = holder.bind<SimpleDraweeView>(R.id.women_perfect_headView)
        headView.setImageURI(data.picUrl)

        if (data.name.length >= 8) {
            holder.setText(R.id.tv_women_perfect_name, "${data.name.substring(0, 6)}...")
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

        val tv_vistorfollownums = holder.bind<TextView>(R.id.tv_women_perfect_vistorfollownums)
        if (data.iVistorCountAll >= 50) {
            tv_vistorfollownums.text = "访客·${data.iVistorCountAll} 喜欢·${data.iFansCountAll}人"
        } else {
            tv_vistorfollownums.text = "喜欢·${data.iFansCountAll}人"
        }
    }
}