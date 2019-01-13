package com.d6.android.app.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.DialogPrivateChat
import com.d6.android.app.dialogs.OpenDatePayPointDialog
import com.d6.android.app.dialogs.OpenDatePointNoEnoughDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.rong.bean.TipsMessage
import com.d6.android.app.rong.bean.TipsTxtMessage
import com.d6.android.app.rong.fragment.ConversationFragmentEx
import com.d6.android.app.utils.*
import io.rong.imkit.RongIM
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.util.*


//聊天
class ChatActivity : TitleActivity(), RongIM.OnSendMessageListener {

    private var TAG = "ChatActivity"

    private var sendCount: Int = 0
    private var PrivateChatType=0 //0 直接私聊 1 同意后开启私聊
    private var IsAgreeChat:Boolean = if(PrivateChatType==0) true else false
    private var mOtherUserInfo: UserData?=null

    private val mTargetId by lazy {
        intent.data.getQueryParameter("targetId")
    }
    private val mTitle by lazy {
        intent.data.getQueryParameter("title")
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy {
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private val mConversationType: Conversation.ConversationType by lazy {
        Conversation.ConversationType.valueOf(intent.data.lastPathSegment.toUpperCase(Locale.US))
    }

    private val broadcast by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
//               var extra = intent?.getStringExtra("extra")
//                if (!TextUtils.isEmpty(extra)) {
//                    var jsonObject = JSONObject(extra)
//                    var type = jsonObject.optString("b")
//                    Log.i("ddd","${type}===${extra}")
//                    if(TextUtils.equals("0",type)){
//                        relative_tips.visibility = View.VISIBLE
//                        linear_openchat_agree.visibility = View.VISIBLE
//                        tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
//                        tv_openchat_tips.text = getString(R.string.string_other_apply_openchat)
//                    }else if(TextUtils.equals("1",type)){
//                        relative_tips.visibility = View.VISIBLE
//                        tv_openchat_points.visibility = View.VISIBLE
//                        linear_openchat_agree.visibility = View.GONE
//                        tv_openchat_apply.visibility = View.GONE
//                        tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg), sendCount)
//                        tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
//                        IsAgreeChat = true
//                        fragment?.doIsNotSendMsg(false,"")
//                    }else if(TextUtils.equals("2",type)){
//                        relative_tips.visibility = View.VISIBLE
//                        tv_openchat_apply.visibility = View.VISIBLE
//                        tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
//                        tv_openchat_tips.text = resources.getString(R.string.string_other_agreee_openchat)
//                        IsAgreeChat = false
//                    }
//                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        immersionBar.init()
        registerReceiver(broadcast, IntentFilter(Const.PRIVATECHAT_APPLY))

        titleBar.addRightButton(rightId = R.mipmap.ic_more_orange, onClickListener = View.OnClickListener {
            startActivity<UserInfoActivity>("id" to mTargetId)
//            var mDialogPrivateChat=DialogPrivateChat()
//            mDialogPrivateChat.show(supportFragmentManager,"DialogPrivateChat")
        })

        val info = RongUserInfoManager.getInstance().getUserInfo(mTargetId)
        title = if (info == null || info.name.isNullOrEmpty()) {
            mTitle
        } else {
            info.name
        }

        var myInfo = RongUserInfoManager.getInstance().getUserInfo(userId)

        var myName = if (myInfo == null || myInfo.name.isNullOrEmpty()) {
            ""
        } else {
            myInfo.name
        }

        tv_openchat_apply.setOnClickListener {
            if(PrivateChatType == 1){
                tv_openchat_apply.isEnabled= false
                tv_openchat_apply.text = resources.getText(R.string.string_already_applay)
                sendPrivateChatMsg("${myName}向你发出了私聊申请", "0")
                fragment?.doIsNotSendMsg(true,resources.getString(R.string.string_other_agreee_openchat))
                relative_tips.visibility = View.VISIBLE
                tv_openchat_apply.visibility = View.VISIBLE
                tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                tv_openchat_tips.text = resources.getString(R.string.string_other_agreee_openchat)
            }
        }

        tv_openchat_points.setOnClickListener {
            relative_tips.visibility = View.GONE
            showDatePayPointDialog(mTitle)//支付积分
            fragment?.doIsNotSendMsg(false, "")
        }

        linear_openchat_agree.visibility = View.GONE

        tv_openchat_no.setOnClickListener {
            sendPrivateChatMsg("${myName}拒绝了你的私聊申请", "2")
            relative_tips.visibility = View.GONE
        }

        tv_openchat_agree.setOnClickListener {
            sendPrivateChatMsg("${myName}同意了你的私聊申请", "1")
            relative_tips.visibility = View.GONE
        }

        enterActivity()

        RongIM.getInstance().setSendMessageListener(this)

        getTargetUserInfo()
    }

    private fun getTargetUserInfo() {
        Request.getUserInfo(userId, mTargetId).request(this, success = { _, data ->
            data?.let {
                if(PrivateChatType == 0){
                    relative_tips.visibility = View.GONE
                }else if(PrivateChatType == 1){
                    relative_tips.visibility = View.VISIBLE
                    tv_openchat_apply.visibility = View.VISIBLE
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips.text = resources.getString(R.string.string_other_agreee_openchat)
                }
                mOtherUserInfo = it
            }
        })
    }

    private fun sendPrivateChatMsg(msg: String, type: String) {
        var mTipsTxtMessage = TipsTxtMessage(msg, type)
        var mTipsMessage = TipsMessage.obtain(msg, GsonHelper.getGson().toJson(mTipsTxtMessage))
        var message = Message.obtain(mTargetId, Conversation.ConversationType.PRIVATE, mTipsMessage)
        RongIM.getInstance().sendMessage(message, null, null, object : IRongCallback.ISendMessageCallback {
            override fun onAttached(message: Message) {
                //消息本地数据库存储成功的回调
            }

            override fun onSuccess(message: Message) {
                //消息通过网络发送成功的回调
            }

            override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) {
                //消息发送失败的回调
            }
        })
    }

    private fun showDatePayPointDialog(name: String) {
        Request.doTalkJustify(userId, mTargetId).request(this, false, success = { msg, data ->
            if (data != null) {
                var code = data!!.optInt("code")
                if (code == 1) {
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    if (point.toInt() > remainPoint.toInt()) {
                        val dateDialog = OpenDatePointNoEnoughDialog()
                        var point = data!!.optString("iTalkPoint")
                        var remainPoint = data!!.optString("iRemainPoint")
                        dateDialog.arguments = bundleOf("point" to point, "remainPoint" to remainPoint)
                        dateDialog.show(supportFragmentManager, "d")
                    } else {
                        val dateDialog = OpenDatePayPointDialog()
                        dateDialog.arguments = bundleOf("point" to point, "remainPoint" to remainPoint, "username" to name, "chatUserId" to mTargetId)
                        dateDialog.show(supportFragmentManager, "d")
                    }
                } else if (code == 0) {
                    showToast(msg.toString())
//                    RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
                } else {
                    val dateDialog = OpenDatePointNoEnoughDialog()
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    dateDialog.arguments = bundleOf("point" to point, "remainPoint" to remainPoint)
                    dateDialog.show(supportFragmentManager, "d")
                }
            } else {
                showToast(msg.toString())
            }
        }) { code, msg ->
            if (code == 0) {
                showToast(msg)
            }
        }
    }

    private fun enterActivity() {

        val token = SPUtils.instance().getString(Const.User.RONG_TOKEN)
        if (token == "default") {

        } else {
            if (RongIM.getInstance().currentConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                enterFragment(mConversationType, mTargetId)
            } else {
                reconnect(token)
            }
        }
    }

    private fun reconnect(token: String) {
        RongIM.connect(token, object : RongIMClient.ConnectCallback() {
            override fun onTokenIncorrect() {}

            override fun onSuccess(s: String) {
                enterFragment(mConversationType, mTargetId)
            }

            override fun onError(e: RongIMClient.ErrorCode) {
                enterFragment(mConversationType, mTargetId)
            }
        })
    }

    private var fragment: ConversationFragmentEx? = null
    private fun enterFragment(mConversationType: Conversation.ConversationType, mTargetId: String) {
        if (isDestroy) {
            return
        }
        fragment = ConversationFragmentEx()
        if (!TextUtils.equals(mTargetId, Const.CustomerServiceId)) {
            if(PrivateChatType == 0){
                fragment?.arguments = bundleOf("flag" to false, "hitmsg" to resources.getString(R.string.string_other_agreee_openchat))
            }else if(PrivateChatType == 1){
                fragment?.arguments = bundleOf("flag" to true, "hitmsg" to resources.getString(R.string.string_other_agreee_openchat))
            }
        }

        val uri = Uri.parse("rong://" + applicationInfo.packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId)
                .appendQueryParameter("title", mTitle)
                .build()

        fragment!!.uri = uri

        val transaction = supportFragmentManager.beginTransaction()
        //xxx 涓轰綘瑕佸姞杞界殑 id
        transaction.add(R.id.rong_content, fragment)
        transaction.commitAllowingStateLoss()

        fragment?.setOnShowAnnounceBarListener { announceMsg, annouceUrl ->
            if (!TextUtils.isEmpty(announceMsg)) {
                var jsonObject = JSONObject(announceMsg)
                var type = jsonObject.optString("b")
                Log.i("ddd","${type}===${announceMsg}")
                if(TextUtils.equals("0",type)){
                    relative_tips.visibility = View.VISIBLE
                    linear_openchat_agree.visibility = View.VISIBLE
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips.text = getString(R.string.string_other_apply_openchat)
                }else if(TextUtils.equals("1",type)){
                    relative_tips.visibility = View.VISIBLE
                    tv_openchat_points.visibility = View.VISIBLE
                    linear_openchat_agree.visibility = View.GONE
                    tv_openchat_apply.visibility = View.GONE
                    tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg), sendCount)
                    tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
                    IsAgreeChat = true
                    fragment?.doIsNotSendMsg(false,"")
                }else if(TextUtils.equals("2",type)){
                    relative_tips.visibility = View.VISIBLE
                    tv_openchat_apply.visibility = View.VISIBLE
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips.text = resources.getString(R.string.string_other_agreee_openchat)
                    IsAgreeChat = false
                }
            }
        }
    }

    override fun onSend(p0: Message?): Message? {
        return p0
    }

    override fun onSent(p0: Message?, p1: RongIM.SentMessageErrorCode?): Boolean {
        p0?.let {
            if (!TextUtils.equals(mTargetId, Const.CustomerServiceId)) {
                if(IsAgreeChat){
                    if (TextUtils.equals("1", sex)) {
                        Log.i(TAG, "用户Id${it.senderUserId}")
                        sendCount = sendCount + 1
                        tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg), sendCount)
                        tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
                        if (sendCount == 3) {
                            tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                            tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_points)
                        }
                        fragment?.doIsNotSendMsg(sendCount == 3, resources.getString(R.string.string_pay_points_openchat))
                    }
                }
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        RongIM.getInstance().setSendMessageListener(null)
        unregisterReceiver(broadcast)
    }
}
