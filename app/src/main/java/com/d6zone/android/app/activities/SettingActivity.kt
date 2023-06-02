package com.d6zone.android.app.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.d6zone.android.app.R
import com.d6zone.android.app.base.TitleActivity
import com.d6zone.android.app.dialogs.DialogCancellation
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.AddImage
import com.d6zone.android.app.models.UserData
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.*
import com.d6zone.android.app.utils.Const.CustomerServiceId
import com.d6zone.android.app.utils.Const.CustomerServiceWomenId
import com.d6zone.android.app.utils.Const.INSTALL_DATA01
import com.d6zone.android.app.utils.Const.INSTALL_DATA02
import com.umeng.message.PushAgent
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import kotlin.collections.ArrayList

class SettingActivity : TitleActivity() {

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private val phone by lazy{
        SPUtils.instance().getString(Const.User.USER_PHONE)
    }

    private var mData: UserData?=null
    private val mImages = ArrayList<AddImage>()

    private val install_data01 by lazy{
        SPUtils.instance().getString(INSTALL_DATA01)
    }

    private val install_data02 by lazy{
        SPUtils.instance().getString(INSTALL_DATA02)
    }

    val token = SPUtils.instance().getString(Const.User.RONG_TOKEN)

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
//            startActivity<SimplePlayer>()
//            RongCallKitUtils.startSingleVoiceChat(this,"103162",RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO)
        }

        tv_oldfans.setOnClickListener {
            startActivity<OldFansActivity>()
        }

        tv_oldfollow.setOnClickListener {
            startActivity<OldFollowActivity>()
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
            loginOut()
//            startActivity<SignInActivity>()
            startActivity<SplashActivity>()
        }

        btn_cancellation.setOnClickListener {
            val mDialogCancellation = DialogCancellation()
            mDialogCancellation.setDialogListener { p, s ->
                if(p==1){
                    mDialogCancellation.dismissAllowingStateLoss()
                    dialog("正在注销...")
                    delAccount()
                }
            }
            mDialogCancellation.show(supportFragmentManager, "DialogCancellation")
        }

        tv_blacklist.setOnClickListener {
//           SPUtils.instance().put(DEBUG_MODE,!getDebugMode()).apply()
//            tv_blacklist.text = if(getDebugMode()){
//                "${install_data01}测试环境${install_data02}"
//            }else{
//                "${install_data01}正式环境${install_data02}"
//            }
            startActivity<BlackListActivity>()
        }

//        if(BuildConfig.DEBUG){
//            tv_blacklist.text = if(getDebugMode()){
//                "${install_data01}测试环境${install_data02}"
//            }else{
//                "${install_data01}正式环境${install_data02}"
//            }
//            tv_blacklist.visibility = View.GONE
//        }else{
//            tv_blacklist.visibility = View.GONE
//        }

//        headView.hierarchy = getHierarchy()

        tv_versionname.text = getAppVersion()

//        DataCleanManager.getTotalCacheSize(this)
        getUserInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getUserInfo()
        }
    }


    private fun delAccount(){
        Request.delAccount(getLocalUserId(),phone).request(this,success = {_,data->
            dismissDialog()
            loginOut()
            toast("你的账号已注销")
            startActivity<SplashActivity>()
        }){_,msg->
            toast(msg)
        }
    }

    private fun getUserInfo() {
        Request.getUserInfo("", getLocalUserId()).request(this, success = { _, data ->
            this.mData = data
            mSwipeRefreshLayout.isRefreshing = false
            saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
                //7 游客 25钻石 24黄金 23白银 22普通  28中级  27入门 28中级  29优质 30 入群
                tv_vip.backgroundDrawable = getLevelDrawable(it.userclassesid.toString(),this)
                tv_sex.isSelected = TextUtils.equals("0",it.sex)
                tv_age.isSelected = TextUtils.equals("0",it.sex)
//                it.age?.let {
//                    if(it.toInt()<=0){
//                        tv_sex.text =""
//                    }else{
//                        tv_sex.text = it
//                    }
//                }

                if(it.age.isNullOrEmpty()){
                    tv_age.visibility = View.GONE
                }else{
                    tv_age.visibility = View.VISIBLE
                    tv_age.text = "${it.age}岁"
                }

                headView.setImageURI(it.picUrl)

                tv_nick.text = it.name

                if (!TextUtils.isEmpty(it.intro)) {
                    tv_signature.text = it.intro
                } else {
                    tv_signature.text = getString(R.string.string_info)
                }

                if(TextUtils.equals("0",mData!!.screen)||TextUtils.equals("3",mData!!.screen) || mData!!.screen.isNullOrEmpty()){
                    img_auther.backgroundDrawable = null
                }else{
                    img_auther.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.video_small)
                }

                if(TextUtils.equals(data.accountId,CustomerServiceId)||TextUtils.equals(data.accountId,CustomerServiceWomenId)){
                    img_auther.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.official_iconnew)
                }else{
                    if(TextUtils.equals("0",it.isValid)){
                        img_auther.backgroundDrawable = ContextCompat.getDrawable(this,R.mipmap.official_forbidden_icon)
                    }
                }
            }
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loginOut(){
        SPUtils.instance().remove(Const.User.USER_ID)
                .remove(Const.User.IS_LOGIN)
                .remove(Const.User.RONG_TOKEN)
                .remove(Const.User.USER_TOKEN)
                .remove(Const.User.SLOGINTOKEN)
                .apply()
        clearLoginToken()
        SPUtils.instance().remove(Const.USERINFO)
        PushAgent.getInstance(applicationContext).deleteAlias(getLocalUserId(), "D6", { _, _ ->

        })
        RongIM.getInstance().disconnect()
        closeAll()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
