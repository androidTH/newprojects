package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import com.d6.android.app.R
import com.d6.android.app.adapters.CardTagAdapter
import com.d6.android.app.adapters.MyImageAdapter
import com.d6.android.app.adapters.UserTagAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.UserData
import com.d6.android.app.models.UserTag
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.CustomerServiceWomenId
import com.google.android.flexbox.FlexboxLayoutManager
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_mycard.*
import org.jetbrains.anko.*
import java.lang.StringBuilder

/**
 * 客服查找
 */
class MyCardActivity : BaseActivity() {

    private val mTags = ArrayList<UserTag>()

    private val userTagAdapter by lazy {
        UserTagAdapter(mTags)
    }

    private val mLocalUserSex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private var mData: UserData? = null
    private val mImages = ArrayList<AddImage>()

    private val myImageAdapter by lazy {
        MyImageAdapter(mImages)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mycard)
        immersionBar.init()

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        sw_card_off.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                updateIsFind(1)
            }else{
                updateIsFind(2)
            }
        }

        tv_adduserinfo.setOnClickListener {
            mData?.let {
                startActivityForResult<MyInfoActivity>(0, "data" to it, "images" to mImages)
            }
        }


        if(TextUtils.equals(mLocalUserSex,"0")){
            rl_userinfo_card.visibility = View.GONE
            rl_mycard_women.visibility = View.VISIBLE
        }else{
            rl_userinfo_card.visibility = View.VISIBLE
            rl_mycard_women.visibility = View.GONE
        }

        rv_my_images.setHasFixedSize(true)
        rv_my_images.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_my_images.isNestedScrollingEnabled = false
        rv_my_images.adapter = myImageAdapter
        rv_my_images.addItemDecoration(VerticalDividerItemDecoration.Builder(this)
                .colorResId(android.R.color.transparent)
                .size(dip(5))
                .build())

        rv_tags.setHasFixedSize(true)
        rv_tags.layoutManager = GridLayoutManager(this, 2)//FlexboxLayoutManager(this)
        rv_tags.isNestedScrollingEnabled = false
        rv_tags.adapter = userTagAdapter

    }

    override fun onResume() {
        super.onResume()
        tv_adduserinfo.postDelayed(object:Runnable{
            override fun run() {
                getUserInfo()
            }
        }, 100)
    }

    private fun getUserInfo() {
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request(this, success = { _, data ->
            data?.let {
                mData = it
                var dataCompletion:Double =(it.iDatacompletion/120.0)
                var percent = Math.round((dataCompletion*100)).toInt()
                if(percent<=30){
                    tv_tips_center.visibility = View.GONE
                    sw_card_off.visibility = View.GONE
                    tv_adduserinfo.visibility = View.VISIBLE
                    tv_tips_top.visibility = View.VISIBLE
                    tv_tips_bottom.visibility = View.VISIBLE
                }else{
                    tv_tips_center.visibility = View.VISIBLE
                    sw_card_off.visibility = View.VISIBLE
                    tv_adduserinfo.visibility = View.GONE
                    tv_tips_top.visibility = View.GONE
                    tv_tips_bottom.visibility = View.GONE
                    if(it.iIsFind==1){
                        sw_card_off.isChecked = true
                    }else{
                        sw_card_off.isChecked = false
                    }
                }
                var sb = StringBuilder()
                if(it.iAllExposureCount>0){
                    sb.append("累计获得了${it.iAllExposureCount}次曝光，")
                }
                sb.append("收到${it.iReceiveLovePoint}个喜欢")
                tv_allexposure.text = sb.toString()

                var lastdaysb =StringBuffer()
                if(it.iLastDayExposureCount>0){
                    lastdaysb.append("昨日获得${it.iAllExposureCount}次曝光，")
                }
                lastdaysb.append("收到${it.iLastDayReceiveLovePoint}个喜欢")
                tv_lastdayexposure.text = lastdaysb.toString()
                if(TextUtils.equals(mLocalUserSex,"0")){
                   setGrilsInfo(it)
                }else{
                   setMenInfo(it)
                }
            }
        }) { code, msg ->
            if(code==2){
                toast(msg)
            }
        }
    }

    private fun setGrilsInfo(it:UserData){
        rv_grils_tags.setHasFixedSize(true)
        rv_grils_tags.layoutManager = FlexboxLayoutManager(this)
        rv_grils_tags.isNestedScrollingEnabled = false
        mTags.clear()
        if (!it.height.isNullOrEmpty()) {
            mTags.add(UserTag("身高:${it.height}", R.drawable.shape_tag_bg_1))
        }

        if (!it.weight.isNullOrEmpty()) {
            mTags.add(UserTag("体重:${it.weight}", R.drawable.shape_tag_bg_2))
        }

        if (!it.job.isNullOrEmpty()) {
            mTags.add(UserTag("${it.job}", R.drawable.shape_tag_bg_3))
        }

        if (!it.constellation.isNullOrEmpty()) {
            mTags.add(UserTag(it.constellation ?: "", R.drawable.shape_tag_bg_5))
        }

        if (!it.hobbit.isNullOrEmpty()) {
            var mHobbies = it.hobbit?.replace("#", ",")?.split(",")
            if (mHobbies != null) {
                for (str in mHobbies) {
                    if (!TextUtils.isEmpty(str)) {
                        mTags.add(UserTag(str, R.drawable.shape_tag_bg_6))
                    }
                }
            }
        }
        rv_grils_tags.adapter = CardTagAdapter(mTags)

        if (!TextUtils.equals(it.userpics, "null")) {
            if (TextUtils.isEmpty(it.userpics)) {
                imageView.setImageURI(it.picUrl)
                tv_indexofpics.visibility = View.GONE
            } else {
                var images = it.userpics?.split(",")
                if (images != null) {
                    if (images.size > 0) {
                        imageView.setImageURI(images[0])
                    }
                }
                tv_indexofpics.visibility = View.VISIBLE
                if (images != null) {
                    tv_indexofpics.setText("1/${images.size}")
                }
            }
        } else {
            imageView.setImageURI(it.picUrl)
            tv_indexofpics.visibility = View.GONE
        }

        if (it.name?.length ?:0 >= 8) {
            tv_grilname.text =  "${it.name?.substring(0, 6)}..."
        } else {
            tv_grilname.text = it.name
        }


//        val img_date_womenauther = holder.bind<ImageView>(R.id.img_date_womenauther)
//
//        if(TextUtils.equals("0",data!!.screen)|| data!!.screen.isNullOrEmpty()){
//            img_date_womenauther.visibility = View.GONE
//        }else if(TextUtils.equals("1",data!!.screen)){
//            img_date_womenauther.visibility = View.VISIBLE
//            img_date_womenauther.setImageResource(R.mipmap.video_small)
//        }else if(TextUtils.equals("3",data!!.screen)){
//            img_date_womenauther.visibility = View.GONE
//            img_date_womenauther.setImageResource(R.mipmap.renzheng_small)
//        }else{
//            img_date_womenauther.visibility = View.GONE
//        }

        tv_grilvip.visibility = View.VISIBLE
        var drawable = getLevelDrawable("${it.userclassesid}",this)
        if(drawable!=null){
            tv_grilvip.backgroundDrawable = drawable
        }else{
            tv_grilvip.backgroundDrawable = null
            tv_grilvip.visibility = View.GONE
        }

        if (!it.age.isNullOrEmpty()) {
            if (TextUtils.equals("0", it.age)) {
                tv_grilage.text = ""
            } else {
                tv_grilage.text = it.age
            }
        }

        if(it.age.isNullOrEmpty()){
            tv_grilage.text = ""
        }

        tv_grilage.isSelected = TextUtils.equals("0", it.sex)

        if (!it.egagementtext.isNullOrEmpty()) {
            if(!TextUtils.equals("null",it.egagementtext)){
                tv_content.visibility = View.GONE
                tv_content.text =  it.egagementtext
            }else{
                tv_content.visibility = View.GONE
            }
        } else if (!(it.signature.isNullOrEmpty())) {
            if(!TextUtils.equals("null",it.signature)){
                tv_content.visibility = View.VISIBLE
                tv_content.text = it.signature
            }else{
                tv_content.visibility = View.GONE
            }
        }else if(!it.intro.isNullOrEmpty()){
            if(!TextUtils.equals("null",it.intro)){
                tv_content.visibility = View.VISIBLE
                tv_content.text = it.intro
            }else{
                tv_content.visibility = View.GONE
            }
        }else{
            tv_content.visibility = View.GONE
        }

        if (TextUtils.equals("null", it.userlookwhere)) {
            it.userlookwhere = ""
        }
        if (TextUtils.equals("null", it.userhandlookwhere)) {
            it.userhandlookwhere = ""
        }

        tv_city.text = it.city
        if (it.iVistorCountAll >= 50) {
            tv_vistorfollownums.text = "收到 [img src=redheart_small/] · ${it.iFansCountAll}     访客·${it.iVistorCountAll}"
        } else {
            tv_vistorfollownums.text = "收到 [img src=redheart_small/] · ${it.iFansCountAll}"
        }
    }

    private fun setMenInfo(it:UserData){
        tv_nick.text = it.name
        iv_bg.showBlur(it.picUrl)
//                headerView.headView.hierarchy = getHierarchy(it.sex.toString())
        headView.setImageURI(it.picUrl)
        tv_nick.text = it.name
        if (!TextUtils.isEmpty(it.intro)) {
            tv_other_auther_sign.text = it.intro
        } else {
            tv_other_auther_sign.text = "本宝宝还没想到好的自我介绍~"
        }

        tv_likehe.text = "喜欢TA·${it.iLovePoint}  访客·${it.iVistorCountAll}"
//                it.isValid?.let {
//                    if (TextUtils.equals("1", it)) {
//                        rl_prompt.visibility = View.GONE
//                        var lp = RelativeLayout.LayoutParams(rl_headView.layoutParams)
//                        lp?.let {
//                            lp.topMargin = resources.getDimensionPixelOffset(R.dimen.height_75)
//                            lp.leftMargin = resources.getDimensionPixelOffset(R.dimen.margin16dp)
//                            lp.rightMargin = resources.getDimensionPixelOffset(R.dimen.margin_6)
//                        }
//                        headerView.rl_headView.layoutParams = lp
//                    }
//                }

        tv_sex.isSelected = TextUtils.equals("0", it.sex)
        it.age?.let {
            if (it.toInt() <= 0) {
                tv_sex.text = ""
            } else {
                tv_sex.text = it
            }
        }

//                if (TextUtils.equals("0", it.sex)) {
        if(TextUtils.equals(getLocalUserId(),CustomerServiceId)||TextUtils.equals(getLocalUserId(),CustomerServiceWomenId)){
            img_other_auther.visibility = View.GONE
//                    img_date_auther.visibility = View.GONE
            img_official.visibility = View.VISIBLE
//                    img_date_auther.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.official_iconnew)
        }else{
            if (TextUtils.equals("0", it.screen) || TextUtils.equals("3", it.screen) || it.screen.isNullOrEmpty()) {
                img_other_auther.visibility = View.GONE
//                        img_date_auther.visibility = View.GONE
                img_other_auther.backgroundDrawable=ContextCompat.getDrawable(this,R.mipmap.renzheng_big)
//                        img_date_auther.backgroundDrawable=ContextCompat.getDrawable(this,R.mipmap.renzheng_small)
                if (TextUtils.equals("3", it.screen)) {
                    tv_other_auther_sign.visibility = View.GONE
                } else {
                    tv_other_auther_sign.visibility = View.GONE
                }
            } else if (TextUtils.equals("1", it.screen)) {
                img_other_auther.visibility = View.VISIBLE
//                        img_date_auther.visibility = View.VISIBLE
                tv_other_auther_sign.visibility = View.GONE

                img_other_auther.backgroundDrawable=ContextCompat.getDrawable(this,R.mipmap.video_big)
//                        img_date_auther.backgroundDrawable=ContextCompat.getDrawable(this,R.mipmap.video_small)
            }

            if(TextUtils.equals("0",it.isValid)||TextUtils.equals("2",it.isValid)){
                img_other_auther.visibility = View.GONE
//                        img_date_auther.visibility = View.VISIBLE
                img_official.visibility = View.VISIBLE
                img_official.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.official_forbidden_icon)
//                        img_date_auther.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.official_forbidden_icon)
            }
        }

        img_other_auther.setOnClickListener {
            toast(getString(R.string.string_auth))
        }

        var drawable = getLevelDrawable(it.userclassesid.toString(),this)
        tv_vip.backgroundDrawable = drawable
//                tv_date_vip.backgroundDrawable = drawable

        mTags.clear()
        if (!it.height.isNullOrEmpty()) {
            mTags.add(UserTag("身高 ${it.height}", R.mipmap.boy_stature_whiteicon))
        }
        if (!it.weight.isNullOrEmpty()) {
            mTags.add(UserTag("体重 ${it.weight}", R.mipmap.boy_weight_whiteicon))
        }

        if (!it.constellation.isNullOrEmpty()) {
            mTags.add(UserTag("星座 ${it.constellation}", R.mipmap.boy_constellation_whiteicon))
        }

        if (!it.city.isNullOrEmpty()) {
            mTags.add(UserTag("地区 ${it.city}", R.mipmap.boy_area_whiteicon))
        }

        if(mTags.size==0){
            rv_tags.visibility = View.GONE
        }else{
            rv_tags.visibility = View.VISIBLE
        }

        userTagAdapter.notifyDataSetChanged()

        if (!it.job.isNullOrEmpty()) {
            AppUtils.setUserInfoTvTag(this, "职业 ${it.job}", 0, 2, tv_job)
        } else {
            tv_job.visibility = View.GONE
        }

        if (!it.zuojia.isNullOrEmpty()) {
            AppUtils.setUserInfoTvTag(this, "座驾 ${it.zuojia}", 0, 2, tv_zuojia)
        } else {
            tv_zuojia.visibility = View.GONE
        }

        if (!it.hobbit.isNullOrEmpty()) {
            var mHobbies = it.hobbit?.replace("#", ",")?.split(",")
            var sb = StringBuffer()
            sb.append("爱好 ")
            if (mHobbies != null) {
                //
                for (str in mHobbies) {
                    if (!TextUtils.isEmpty(str)) {
                        sb.append("${str} ")
                    }
                }
                if (sb.toString().trim().length <= 2) {
                    tv_aihao.visibility = View.GONE
                }
                AppUtils.setUserInfoTvTag(this, sb.toString(), 0, 2, tv_aihao)
            }
        } else {
            tv_aihao.visibility = View.GONE
        }

        if (!TextUtils.equals("null", it.userpics)) {
            refreshImages(it)
        }else{
            rv_my_images.visibility = View.GONE
        }

        rl_userinfo_card.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                var addd = iv_bg.layoutParams
                addd.height = rl_userinfo_card.measuredHeight
                iv_bg.layoutParams = addd
            }
        })
    }

    private fun refreshImages(userData: UserData) {
        mImages.clear()
        if (!userData.userpics.isNullOrEmpty()) {
            userData.userpics?.let {
                val images = it.split(",")
                images.forEach {
                    mImages.add(AddImage(it))
                }
            }
        }else{
            rv_my_images.visibility = View.GONE
        }
        myImageAdapter.notifyDataSetChanged()
    }

    private fun updateIsFind(iIsFind:Int){
        Request.updateCardIsFind(getLoginToken(),iIsFind).request(this,false,success={_,data->

        })
    }

}