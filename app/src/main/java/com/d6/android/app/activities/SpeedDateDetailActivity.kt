package com.d6.android.app.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.d6.android.app.utils.BannerLoader
import com.d6.android.app.R
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.ShareFriendsDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.share.utils.ShareUtils
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_speed_date_detail.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

/**
 * 速约详情
 */
class SpeedDateDetailActivity : TitleActivity() {

    private val mSpeedDate by lazy {
        intent.getSerializableExtra("data") as MyDate
    }

    private val mUrls =ArrayList<String>()

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
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
        title = "速约详情"

        titleBar.addRightButton(rightId = R.mipmap.ic_more_orange, onClickListener = View.OnClickListener {
//            ShareUtils.share(this@SpeedDateDetailActivity, SHARE_MEDIA.WEIXIN, mSpeedDate.speedcontent ?: "", mSpeedDate.speednumber?:"", "http://www.d6-zone.com/JyD6/#/suyuexiangqing?ids="+mSpeedDate.id, shareListener)
            val shareDialog = ShareFriendsDialog()
            shareDialog.arguments = bundleOf("from" to "Recommend_speedDate","id" to mSpeedDate.userId.toString(),"sResourceId" to mSpeedDate.id.toString())
            shareDialog.show(supportFragmentManager, "action")
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    startActivity<ReportActivity>("id" to mSpeedDate.id.toString(), "tiptype" to "5")
                }else if(p==3){
                    ShareUtils.share(this@SpeedDateDetailActivity, SHARE_MEDIA.WEIXIN, mSpeedDate.speedcontent ?: "", mSpeedDate.speednumber?:"", "http://www.d6-zone.com/JyD6/#/suyuexiangqing?ids="+mSpeedDate.id, shareListener)
                }
            }
        })

        imageView.layoutParams.height = ((screenWidth() - 2 * dip(12)) / 1.506f).toInt()
        imageView.setImageLoader(BannerLoader(1.506f))
        imageView.requestLayout()
        imageView.isAutoPlay(false)

        imageView.setOnBannerListener {
            if (mUrls.isEmpty()) {
                return@setOnBannerListener
            }
            startActivity<ImagePagerActivity>(ImagePagerActivity.URLS to mUrls, ImagePagerActivity.CURRENT_POSITION to it)
        }

        tv_chat.setOnClickListener {
            isAuthUser {
                mSpeedDate.userId?.let {id->
                    val name = mSpeedDate.name ?: ""
//                    checkChatCount(id){
                        RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
//                    }
                }
            }
        }

        btn_contact.setOnClickListener {
            //            val dialog = ContactUsDialog()
//            dialog.show(supportFragmentManager, "us")
            isAuthUser() {
//                pushCustomerMessage(this, getLocalUserId(),4,mSpeedDate.id.toString()){
//                    chatService(this)
//                }
                ShareUtils.share(this@SpeedDateDetailActivity, SHARE_MEDIA.WEIXIN, mSpeedDate.speedcontent ?: "", mSpeedDate.speednumber?:"", "http://www.d6-zone.com/JyD6/#/suyuexiangqing?ids="+mSpeedDate.id, shareListener)
            }
        }

        getSpeedDateDetail(mSpeedDate.id.toString())
    }

    private fun getSpeedDateDetail(id: String) {
        dialog()
        Request.getSpeedDetail(id).request(this){ _, data->
            data?.let {
                refreshUI(it)
//                startActivity<SpeedDateDetailActivity>("data" to it)
            }
        }
    }

    private fun refreshUI(speedDate:MyDate) {
//        val images = arrayListOf<String>()
        mUrls.clear()
        speedDate.coverurl?.let {
            val array = it.split(",")
            if (array.isNotEmpty()) {
                mUrls.addAll(array)
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
        imageView.update(mUrls)

        headView.setImageURI(speedDate.speedpics)
//        tv_title.text = String.format("%s%s", mSpeedDate.speedwhere + mSpeedDate.handspeedwhere, mSpeedDate.speednumber)
        tv_title.text = String.format("%s%s", speedDate.speednumber,"")
        tv_title.isSelected = TextUtils.equals(speedDate.sex, "0")
        tv_age.text = speedDate.age
        if (speedDate.age.isNullOrEmpty()) {
            tv_age.gone()
        } else {
            tv_age.visible()
        }
        tv_height.text = speedDate.height
        if (speedDate.height.isNullOrEmpty()) {
            tv_height.gone()
        } else {
            tv_height.visible()
        }
        tv_weight.text = speedDate.weight
        if (speedDate.weight.isNullOrEmpty()) {
            tv_weight.gone()
        } else {
            tv_weight.visible()
        }

        val startT = speedDate.beginTime?.parserTime()
        val endT = speedDate.endTime?.parserTime()
        tv_deadline_time.text = String.format("速约时间:%s-%s", startT?.toTime("MM.dd") , endT?.toTime("MM.dd"))
        val l1 = speedDate.speedcity?.length ?: 0
        val l2 = speedDate.getSpeedStateStr().length
//        val l3 = mSpeedDate.handspeedwhere?.length ?: 0

//        tv_content.text = SpanBuilder(String.format("%s%s-%s", mSpeedDate.speedwhere + mSpeedDate.handspeedwhere, mSpeedDate.getSpeedStateStr(), mSpeedDate.speedcontent))
        tv_content.text = SpanBuilder(String.format("%s%s-%s", speedDate.speedcity, speedDate.getSpeedStateStr(), speedDate.speedcontent))
                .color(this, 0, l1 + l2 + 1, R.color.textColor)
                .build()
        tv_time.text = speedDate.createTime?.interval()
        if (TextUtils.equals("1", speedDate.screen)) {
            tv_audio_auth.visible()
        } else {
            tv_audio_auth.gone()
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
            btn_contact.isEnabled = false
            btn_contact.text = "已过期"
        } else {
//            titleBar.hideRightButton(0,false)
            btn_contact.isEnabled = true
            btn_contact.text = "联系客服"
        }
    }
}
