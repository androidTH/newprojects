package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.umeng.message.PushAgent
import com.umeng.message.UTrack
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.UserInfo
import io.rong.message.TextMessage
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class SettingActivity : TitleActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var mData: UserData?=null
    private val mImages = ArrayList<AddImage>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        immersionBar.init()
        title = "设置"

        val s = "微信查询  防止假冒客服与您联系！"
        tv_search_weChat.text = SpanBuilder(s)
                .color(this,4,s.length,R.color.textColor99)
                .build()

        tv_contact_us.setOnClickListener {
            startActivity<ContactUsActivity>()
        }

        rl_my_info.setOnClickListener {
            mData?.let {
                mImages.clear()
                if (!it.userpics.isNullOrEmpty()) {
                    val images = it.userpics!!.split(",")
                    images.forEach {
                        mImages.add(AddImage(it))
                    }
                }
                mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
                startActivityForResult<MyInfoActivity>(0, "data" to it,"images" to mImages)
            }
        }

        tv_online_service.setOnClickListener {
            //            val serviceId = "f67f360c9dde4b3c9eab01a0126f6684"
//            val textMsg = TextMessage.obtain("欢迎使用D6社区APP\nD6社区官网：www-d6-zone.com\n微信公众号：D6社区CM\n可关注实时了解社区动向。")
//            RongIMClient.getInstance().insertIncomingMessage(Conversation.ConversationType.PRIVATE
//                    ,"5" ,"5", Message.ReceivedStatus(0)
//                    , textMsg,object : RongIMClient.ResultCallback<Message>(){
//                override fun onSuccess(p0: Message?) {
//
//                }
//                override fun onError(p0: RongIMClient.ErrorCode?) {
//
//                }
//            })

            val serviceId = "5"
            RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, serviceId, "D6客服")
        }

        tv_feedback.setOnClickListener {
            startActivity<FeedBackActivity>()
        }

        tv_aboutUs.setOnClickListener {
            startActivity<AboutUsMainActivity>()
        }

        tv_search_weChat.setOnClickListener {
            startActivity<WeChatSearchActivity>()
        }

        btn_sign_out.setOnClickListener {
            SPUtils.instance().remove(Const.User.USER_ID)
                    .remove(Const.User.IS_LOGIN)
                    .remove(Const.User.RONG_TOKEN)
                    .remove(Const.User.USER_TOKEN)
                    .apply()
            PushAgent.getInstance(applicationContext).deleteAlias(userId, "D6", { _, _ ->

            })
            RongIM.getInstance().disconnect()
            startActivity<SignInActivity>()
            finish()
        }

        dialog()
        getUserInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getUserInfo()
        }
    }

    private fun getUserInfo() {

        Request.getUserInfo("",userId).request(this, success = { _, data ->
            this.mData = data
            mSwipeRefreshLayout.isRefreshing = false
            saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
                tv_vip.text = String.format("%s", it.classesname)
//                if (TextUtils.equals("0", it.sex)) {//女性
//                    tv_vip.visible()
//                } else {
//                    tv_vip.visible()
//                }
                tv_sex.isSelected = TextUtils.equals("0",it.sex)
                tv_sex.text = it.age
                headView.setImageURI(it.picUrl)
                tv_nick.text = it.name
                tv_signature.text = it.intro

                if(TextUtils.equals("0",mData!!.screen) || mData!!.screen.isNullOrEmpty()){
                    img_auther.visibility = View.GONE
                }else{
                    img_auther.visibility = View.VISIBLE
                }
            }
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }
}
