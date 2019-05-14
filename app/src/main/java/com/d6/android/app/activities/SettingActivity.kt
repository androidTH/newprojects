package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.DialogUpdateApp
import com.d6.android.app.dialogs.SelectChatTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.net.http.UpdateAppHttpUtil
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.umeng.message.PushAgent
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.UpdateCallback
import com.vector.update_app.utils.AppUpdateUtils
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_setting.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

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
//            startActivity<ChooseFriendsActivity>()
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

            pushCustomerMessage(this, getLocalUserId(),5,"",next = {
                chatService(this)
            })
//            chatService(this)
//            val serviceId = "5"
//            RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, serviceId, "D6客服")
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
            SPUtils.instance().remove(Const.USERINFO)
            PushAgent.getInstance(applicationContext).deleteAlias(userId, "D6", { _, _ ->

            })
            RongIM.getInstance().disconnect()
            startActivity<SignInActivity>()
            finish()
        }

        tv_blacklist.setOnClickListener {
            startActivity<BlackListActivity>()
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
                // 25钻石 24黄金 23白银 22普通  28中级
                if (TextUtils.equals("0", it.sex)) {//女性
                    //27入门 28中级  29优质
//                    tv_vip.text = String.format("%s", it.classesname)
                    if(TextUtils.equals(it.userclassesid,"27")){
//                        tv_vip.text = String.format("%s",getString(R.string.string_primary))
                        tv_vip.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.gril_cj)
                    }else if(TextUtils.equals(it.userclassesid,"28")){
//                        tv_vip.text = String.format("%s",getString(R.string.string_middle))
                        tv_vip.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.gril_zj)
                    }else if(TextUtils.equals(it.userclassesid,"29")){
//                        tv_vip.text = String.format("%s",getString(R.string.string_senior))
                        tv_vip.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.gril_gj)
                    }else{
                        tv_vip.visibility = View.GONE
                    }
                } else {
                    if(TextUtils.equals(it.userclassesid.toString(),"22")){
//                        tv_vip.text = String.format("%s",getString(R.string.string_ordinary))
                        tv_vip.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.vip_ordinary)
                    }else if(TextUtils.equals(it.userclassesid,"23")){
//                        tv_vip.text = String.format("%s",getString(R.string.string_silver))
                        tv_vip.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.vip_silver)
                    }else if(TextUtils.equals(it.userclassesid,"24")){
//                        tv_vip.text = String.format("%s",getString(R.string.string_gold))
                        tv_vip.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.vip_gold)
                    }else if(TextUtils.equals(it.userclassesid,"25")){
//                        tv_vip.text = String.format("%s",getString(R.string.string_diamonds))
                        tv_vip.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.vip_zs)
                    }else if(TextUtils.equals(it.userclassesid,"26")){
//                        tv_vip.text = String.format("%s",getString(R.string.string_private))
                        tv_vip.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.vip_private)
                    }else if(TextUtils.equals(it.userclassesid,"7")){
                        tv_vip.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.youke_icon)
                    }else{
                        tv_vip.backgroundDrawable = null
                    }
                }
                tv_sex.isSelected = TextUtils.equals("0",it.sex)
                it.age?.let {
                    if(it.toInt()<=0){
                        tv_sex.text =""
                    }else{
                        tv_sex.text = it
                    }
                }
                headView.setImageURI(it.picUrl)
                tv_nick.text = it.name
                tv_signature.text = it.intro

                if(TextUtils.equals("0",mData!!.screen)||TextUtils.equals("3",mData!!.screen) || mData!!.screen.isNullOrEmpty()){
                    img_auther.visibility = View.GONE
                }else{
                    img_auther.visibility = View.VISIBLE
                }

//                if(it.iTalkSetting==1){
//                    tv_private_chat_type.text=resources.getString(R.string.string_linechat)
//                }else if(it.iTalkSetting==2){
//                    tv_private_chat_type.text=resources.getString(R.string.string_agree_openchat)
//                }
            }
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
