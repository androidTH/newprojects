package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.d6.android.app.utils.BannerLoader
import com.d6.android.app.R
import com.d6.android.app.adapters.CardChatManTagAdapter
import com.d6.android.app.adapters.FindDateImagesAdapter
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.DateTypeDialog
import com.d6.android.app.dialogs.ShareFriendsDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.models.UserTag
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.facebook.drawee.backends.pipeline.Fresco
import com.share.utils.ShareUtils
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_speed_date_detail.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

/**
 * 速约详情
 */
class SpeedDateDetailActivity : TitleActivity() {

//    private val mSpeedDate by lazy {
//        var serializable = intent.getSerializableExtra("data")
//        if(serializable!=null){
//            serializable as MyDate
//        }else{
//            MyDate("")
//        }
//    }

    private lateinit var mSpeedDate:MyDate
    fun IsNotNullDate()=::mSpeedDate.isInitialized

    private val mUrls =ArrayList<String>()

    private val imgAdapter by lazy {
        FindDateImagesAdapter(mUrls)
    }

    private val mTags =ArrayList<UserTag>()

    private val mUserTagAdapter by lazy{
        CardChatManTagAdapter(mTags)
    }

    private val shareListener by lazy {
        object : UMShareListener {
            override fun onResult(p0: SHARE_MEDIA?) {
            }

            override fun onCancel(p0: SHARE_MEDIA?) {
            }

            override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
            }

            override fun onStart(p0: SHARE_MEDIA?) {
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_date_detail)
        immersionBar.init()
        title = "速约"

        titleBar.addRightButton(rightId = R.mipmap.ic_more_orange, onClickListener = View.OnClickListener {
//            ShareUtils.share(this@SpeedDateDetailActivity, SHARE_MEDIA.WEIXIN, mSpeedDate.speedcontent ?: "", mSpeedDate.speednumber?:"", "http://www.d6-zone.com/JyD6/#/suyuexiangqing?ids="+mSpeedDate.id, shareListener)
            val shareDialog = ShareFriendsDialog()
            shareDialog.arguments = bundleOf("from" to "Recommend_speedDate","id" to "${mSpeedDate.userId}","sResourceId" to "${mSpeedDate.id}")
            shareDialog.show(supportFragmentManager, "action")
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    startActivity<ReportActivity>("id" to "${mSpeedDate.id}", "tiptype" to "5")
                }else if(p==3){
                    ShareUtils.share(this@SpeedDateDetailActivity, SHARE_MEDIA.WEIXIN, mSpeedDate.speedcontent ?: "", mSpeedDate.speednumber?:"", "http://www.d6-zone.com/JyD6/#/suyuexiangqing?ids="+mSpeedDate.id, shareListener)
                }
            }
        })


//        tv_chat.setOnClickListener {
//            isAuthUser {
//                mSpeedDate.userId?.let {id->
//                    val name = mSpeedDate.name ?: ""
////                    checkChatCount(id){
//                        RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
////                    }
//                }
//            }
//        }

        rv_speeddateimages.setHasFixedSize(true)
        rv_speeddateimages.isNestedScrollingEnabled=false
        rv_speeddateimages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_speeddateimages.adapter = imgAdapter

        tv_speeddate.setOnClickListener {
//            isAuthUser() {
//                ShareUtils.share(this@SpeedDateDetailActivity, SHARE_MEDIA.WEIXIN, mSpeedDate.speedcontent ?: "", mSpeedDate.speednumber?:"", "http://www.d6-zone.com/JyD6/#/suyuexiangqing?ids="+mSpeedDate.id, shareListener)
//            }
            startActivity<RGServiceInfoActivity>("data" to mSpeedDate)
//            var mDateTypeDialog = DateTypeDialog()
//            mDateTypeDialog.arguments = bundleOf("pics" to mUrls[0])
//            mDateTypeDialog.setDialogListener { p, s ->
//                if(p==1){//分享约会
//
//                }else if(p==2){//复制微信
//
//                }
//            }
//            mDateTypeDialog.show(supportFragmentManager,"dateType")
        }

        if(intent.hasExtra("id")){
            getSpeedDateDetail(intent.getStringExtra("id"))
        }else{
            var serializable = intent.getSerializableExtra("data")
            if(serializable!=null){
                mSpeedDate = serializable as MyDate
                if(IsNotNullDate()){
                    getSpeedDateDetail("${mSpeedDate.id}")
                }
            }
        }
    }

    private fun getSpeedDateDetail(id: String) {
//        dialog()
        Request.getSpeedDetail(id).request(this){ _, data->
            data?.let {
                refreshUI(it)
                mSpeedDate = it
//                startActivity<SpeedDateDetailActivity>("data" to it)
            }
        }
    }

    private fun refreshUI(speedDate:MyDate) {
        speeddate_type.text = String.format("%s：%s",speedDate.getSpeedStateStr(),speedDate.speednumber)
        tv_speeddate_time.text = "${speedDate.getSpeedStateStr()}时间"
        tv_speeddate_info.text = "${speedDate.getSpeedStateStr()}说明"

        tv_speeddate_age.text = "${speedDate.age}岁"
        tv_speeddate_age.isSelected = TextUtils.equals(speedDate.sex, "0")
        tv_speeddate_sex.isSelected = TextUtils.equals(speedDate.sex, "0")
//        val images = arrayListOf<String>()


        mTags.clear()
        if (!TextUtils.isEmpty(speedDate.height)) {
            mTags.add(UserTag("身高 " + speedDate.height!!, R.mipmap.boy_stature_icon))
        }

        if (!TextUtils.isEmpty(speedDate.weight)) {
            mTags.add(UserTag("体重 " + speedDate.weight!!, R.mipmap.boy_weight_grayicon))
        }

        if (!TextUtils.isEmpty(speedDate.xingzuo)) {
            mTags.add(UserTag("星座 " + speedDate.xingzuo,R.mipmap.boy_constellation_icon ))
        }

        if (!TextUtils.isEmpty(speedDate.speedcity)) {
            mTags.add(UserTag("地区 " + speedDate.speedcity,R.mipmap.date_area_icon))
        }

        if(!TextUtils.isEmpty(speedDate.sLookUserClass)){
            mTags.add(UserTag("要求 " + speedDate.sLookUserClass, R.mipmap.icon_need))
        }

        if (!TextUtils.isEmpty(speedDate.job)) {
//            AppUtils.setTvTag(this, "职业 ${speedDate.job}", 0, 2, tv_job)
            mTags.add(UserTag("职业 ${speedDate.job}", R.mipmap.boy_profession_icon));
        } else {
            tv_job.visibility = View.GONE
        }

        if (!speedDate.zuojia.isNullOrEmpty()) {
//            AppUtils.setTvTag(this, "座驾 ${speedDate.zuojia}", 0, 2, tv_zuojia)
            mTags.add(UserTag("座驾 ${speedDate.zuojia}", R.mipmap.boy_car_icon))
        } else {
            tv_zuojia.visibility = View.GONE
        }

        if (!speedDate.hobbit.isNullOrEmpty()) {
            var mHobbies = speedDate.hobbit?.replace("#", ",")?.split(",")
            var sb = StringBuffer()
            sb.append("爱好 ")
            if (mHobbies != null) {
                for (str in mHobbies) {
                    sb.append("${str} ")
                }
//                AppUtils.setTvTag(this, sb.toString(), 0, 2, tv_aihao)
                mTags.add(UserTag("${sb}", R.mipmap.boy_hobby_icon))
            }
        } else {
            tv_aihao.visibility = View.GONE
        }

        rv_speeddate_tags.setHasFixedSize(true)
        rv_speeddate_tags.layoutManager = GridLayoutManager(this, 2)//FlexboxLayoutManager(this)
        rv_speeddate_tags.isNestedScrollingEnabled = false
        rv_speeddate_tags.adapter = mUserTagAdapter

        mUrls.clear()
        speedDate.coverurl?.let {
            if(it.isNotEmpty()){
                val array = it.split(",")
                if (array.isNotEmpty()) {
                    mUrls.addAll(array)
                }
            }
        }

        //如果内容图片为空
        if (mUrls.isEmpty()) {
            speedDate.speedpics?.let {
                val array = it.split(",")
                if (array.isNotEmpty()) {
                    mUrls.addAll(array)
                }
            }
        }
        imgAdapter.notifyDataSetChanged()

//        val startT = speedDate.beginTime?.parserTime()
//        val endT = speedDate.endTime?.parserTime()
//        speedDate.createTime?.interval()
        tv_speeddate_showtime.text = String.format("%s-%s", speedDate.beginTime.parserTime().toTime("yyyy.MM.dd") , speedDate.endTime?.parserTime().toTime("yyyy.MM.dd")) //speedDate.createTime?.interval()
        if(!speedDate.speedcontent.isNullOrEmpty()){
            ll6.visibility = View.VISIBLE
            tv_content.text =speedDate.speedcontent
        }else{
            ll6.visibility = View.GONE
        }

//        val l1 = speedDate.speedcity?.length ?: 0
//        val l2 = speedDate.getSpeedStateStr().length
//        val l3 = mSpeedDate.handspeedwhere?.length ?: 0

//        tv_content.text = SpanBuilder(String.format("%s%s-%s", mSpeedDate.speedwhere + mSpeedDate.handspeedwhere, mSpeedDate.getSpeedStateStr(), mSpeedDate.speedcontent))

                // SpanBuilder(String.format("%s%s-%s", speedDate.speedcity, speedDate.getSpeedStateStr(), speedDate.speedcontent))
//                .color(this, 0, l1 + l2 + 1, R.color.textColor)
//                .build()

        if (TextUtils.equals(speedDate.sex, "1")) {
            tv_speeddate_vip.visibility = View.VISIBLE
            img_auther.visibility = View.GONE
            tv_speeddate_vip.backgroundDrawable = getLevelDrawableOfClassName(speedDate.classesname.toString(),this)
        } else {
            tv_speeddate_vip.visibility = View.GONE
            img_auther.visibility = View.VISIBLE
            if (TextUtils.equals("1", speedDate.screen)) {
                img_auther.backgroundDrawable= ContextCompat.getDrawable(this,R.mipmap.video_big)
            } else if(TextUtils.equals("0", speedDate.screen)){
                img_auther.visibility = View.GONE
            }else if(TextUtils.equals("3",speedDate.screen)){
                img_auther.visibility = View.GONE
                img_auther.backgroundDrawable=ContextCompat.getDrawable(this,R.mipmap.renzheng_big)
            }
        }

        val end = speedDate.endTime.parserTime().toTime("yyyy-MM-dd")
        val cTime = if (D6Application.systemTime <= 0) {
            System.currentTimeMillis()
        } else {
            D6Application.systemTime
        }
        val current = cTime.toTime("yyyy-MM-dd")
        if (current > end) {//已过期
//            titleBar.hideAllRightButton()
//            btn_contact.isEnabled = false
//            btn_contact.text = "已过期"
            iv_speed_timeout.visibility = View.VISIBLE
//            btn_contact.visibility = View.VISIBLE
            rl_bottom.visibility = View.GONE
            tv_speeddate.visibility = View.GONE
        } else {
            iv_speed_timeout.visibility = View.GONE
//            btn_contact.visibility = View.VISIBLE
            tv_speeddate.visibility = View.VISIBLE
//            titleBar.hideRightButton(0,false)
//            btn_contact.isEnabled = true
//            btn_contact.text = "联系客服"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Fresco.getImagePipeline().clearMemoryCaches()
        imgAdapter.mData.clear()
        System.gc()
        finish()
    }
}
