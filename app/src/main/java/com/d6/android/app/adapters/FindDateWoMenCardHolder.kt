package com.d6.android.app.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.d6.android.app.R
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.FindDate
import com.d6.android.app.models.UserTag
import com.d6.android.app.utils.*
import com.d6.android.app.widget.convenientbanner.holder.Holder
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.flexbox.FlexboxLayoutManager
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.support.v4.startActivity
import java.util.*

/**
 * author : jinjiarui
 * time   : 2019/01/03
 * desc   :
 * version:
 */
class FindDateWoMenCardHolder(itemView: View?) : Holder<FindDate>(itemView) {

    private lateinit var rl_women_card: RelativeLayout
    private lateinit var bigImgView: SimpleDraweeView
    private lateinit var iv_wh: SimpleDraweeView
    private lateinit var rv_mydate_tags: RecyclerView
    private lateinit var tv_name: TextView
    private lateinit var img_date_womenauther: ImageView
    private lateinit var tv_women_vip: TextView
    private lateinit var tv_woman_sex: TextView
    private lateinit var tv_content: TextView
    private lateinit var tv_city: TextView
    private lateinit var tv_woman_age: TextView
    private lateinit var tv_vistorfollownums: TextView
    private lateinit var tv_online_time: TextView
    private lateinit var ll_user_vistorfollownums: LinearLayout
    private val mTags = ArrayList<UserTag>()
    private var mBannerImages = ArrayList<String>()
    private lateinit var mContext: Context

    constructor(itemView: View?, context: Context) : this(itemView) {
        suspend { itemView }
        mContext = context
    }

    override fun initView(itemView: View) {
        rl_women_card = itemView.findViewById(R.id.rl_women_card)
        bigImgView = itemView.findViewById(R.id.imageView)
        iv_wh = itemView.findViewById(R.id.iv_wh)
        rv_mydate_tags = itemView.findViewById(R.id.rv_mydate_tags)
        tv_name = itemView.findViewById(R.id.tv_name)
        img_date_womenauther = itemView.findViewById(R.id.img_date_womenauther)
        tv_women_vip = itemView.findViewById(R.id.tv_vip)
        tv_woman_age = itemView.findViewById(R.id.tv_womang_age)
        tv_woman_sex = itemView.findViewById(R.id.tv_womang_sex)
        tv_content = itemView.findViewById(R.id.tv_content)
        tv_city = itemView.findViewById(R.id.tv_city)
        tv_vistorfollownums = itemView.findViewById(R.id.tv_vistorfollownums)
        tv_online_time = itemView.findViewById(R.id.tv_online_time)
        ll_user_vistorfollownums = itemView.findViewById(R.id.ll_user_vistorfollownums)
    }


    override fun updateUI(data: FindDate, position: Int, total: Int) {
        rv_mydate_tags.setHasFixedSize(true)
        rv_mydate_tags.layoutManager = FlexboxLayoutManager(mContext)
        rv_mydate_tags.isNestedScrollingEnabled = false

        mTags.clear()
        var sblens = StringBuffer()
        if (!data.shengao.isNullOrEmpty()) {
            sblens.append("身高 ${data.shengao}")
            mTags.add(UserTag("身高 ${data.shengao}", R.drawable.shape_tag_bg_1))
        }

        if (!data.tizhong.isNullOrEmpty()) {
            sblens.append("体重 ${data.tizhong}")
            mTags.add(UserTag("体重 ${data.tizhong}", R.drawable.shape_tag_bg_2))
        }

        if (!data.zhiye.isNullOrEmpty()) {
            sblens.append("${data.zhiye}")
            mTags.add(UserTag(data.zhiye, R.drawable.shape_tag_bg_3))
        }

        if (!data.xingzuo.isNullOrEmpty()) {
            sblens.append("${data.xingzuo}")
            mTags.add(UserTag(data.xingzuo ?: "", R.drawable.shape_tag_bg_5))
        }

        if(sblens.length<31){
            if (!data.xingquaihao.isNullOrEmpty()) {
                var mHobbies = data.xingquaihao?.replace("#", ",")?.split(",")
                if (mHobbies != null) {
                    for (str in mHobbies) {
                        if (!TextUtils.isEmpty(str)) {
                            sblens.append("${str}")
                            if(sblens.length<31){
                                mTags.add(UserTag(str, R.drawable.shape_tag_bg_6))
                            }else{
                                break
                            }
                        }
                    }
                }
            }
        }
        rv_mydate_tags.adapter = CardTagAdapter(mTags)

        mBannerImages.clear()
        if (!TextUtils.equals(data.userpics, "null")) {
            if (TextUtils.isEmpty(data.userpics)) {
                mBannerImages.add(data.picUrl)
            } else {
                var images = data.userpics.split(",")
                if (images.size > 0) {
                    mBannerImages.addAll(images)
                }
            }
        } else {
            mBannerImages.add(data.picUrl)
        }
        FrescoUtils.loadImage(mContext, mBannerImages[0], object : IResult<Bitmap> {
            override fun onResult(result: Bitmap?) {
                result?.let {
                    if (it != null) {
                        if (it.width >= it.height) {
                            iv_wh.visibility = View.VISIBLE
                            var index = mBannerImages[0].indexOf("?")
                            var url = if(index!=-1){
                                mBannerImages[0].subSequence(0, index)
                            }else{
                                mBannerImages[0]
                            }
                            if(url.contains(Const.D6_WWW_TAG)){
                                bigImgView.showBlur(mBannerImages[0])
                            }else{
                                bigImgView.setImageURI("${url}${Const.BLUR_50}")
                            }
                            iv_wh.setImageURI(mBannerImages[0])
                        } else {
                            iv_wh.visibility = View.GONE
                            bigImgView.setImageURI(mBannerImages[0])
                        }
                    } else {
                        iv_wh.visibility = View.GONE
                        bigImgView.setImageURI(mBannerImages[0])
                    }
                }
            }
        })

//            val headView = holder.bind<SimpleDraweeView>(R.id.headView)
//            headView.setImageURI(data.picUrl)

        if (data.name.length >= 8) {
           tv_name.text = "${data.name.substring(0, 6)}..."
        } else {
            tv_name.text =  data.name
        }

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

        tv_women_vip.visibility = View.VISIBLE
        var drawable = getLevelDrawable(data.userclassesid,mContext)
        if(drawable!=null){
            tv_women_vip.backgroundDrawable = drawable
        }else{
            tv_women_vip.backgroundDrawable = null
            tv_women_vip.visibility = View.GONE
        }

        if (!data.nianling.isNullOrEmpty()) {
            if (TextUtils.equals("0", data.nianling)) {
                tv_woman_age.visibility = View.GONE
            } else {
                tv_woman_age.visibility = View.VISIBLE
                tv_woman_age.text = "${data.nianling}岁"
            }
        }else{
            tv_woman_age.visibility = View.GONE
        }

        tv_woman_age.isSelected = TextUtils.equals("0", data.sex)

        tv_woman_sex.isSelected = TextUtils.equals("0", data.sex)


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
            tv_city.text = "${data.city}"
        }else{
            tv_city.text = "${data.sPosition}"
        }

        Log.i("address","${data.city},--${data.sPosition}")

        tv_vistorfollownums.visibility = View.VISIBLE
        if(data.iOnline==1){
            tv_online_time.visibility = View.GONE
            ll_user_vistorfollownums.visibility = View.GONE
        }else{
            tv_online_time.visibility = View.VISIBLE
            ll_user_vistorfollownums.visibility = View.VISIBLE
            if(data.iOnline==2){
                setLeftDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_dot_online),tv_online_time)
                tv_online_time.text = "当前状态·${data.sOnlineMsg}"
            }else{
                setLeftDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_dot_translate),tv_online_time)
                tv_online_time.text = "在线时间·${data.sOnlineMsg}"
            }
        }

        var sblove = StringBuffer()
        if(data.iReceiveLovePoint>0){
            sblove.append("收到 [img src=redheart_small/] · ${data.iReceiveLovePoint}     ")
        }

        if(data.iVistorCountAll>=10){
            sblove.append("访客·${data.iVistorCountAll}")
        }

        if(sblove.toString().length>0){
            ll_user_vistorfollownums.visibility = View.VISIBLE
            tv_vistorfollownums.visibility = View.VISIBLE
            tv_vistorfollownums.text="${sblove}"
        }else{
            ll_user_vistorfollownums.visibility = View.GONE
            tv_vistorfollownums.visibility = View.GONE
        }
    }
}