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
import com.d6.android.app.widget.CustomToast
import com.umeng.message.PushAgent
import io.rong.imkit.RongIM
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_user_info_v2.*
import kotlinx.coroutines.experimental.channels.Send
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.util.*


//聊天
class ChatActivity : TitleActivity(), RongIM.OnSendMessageListener {

    private var TAG = "ChatActivity"
    private var sendCount: Int = 0
    private var SendMsgTotal:Int = 3

    private var IsAgreeChat:Boolean = false

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

    /**
     * 会话类型
     */
    private val mConversationType: Conversation.ConversationType by lazy {
        Conversation.ConversationType.valueOf(intent.data.lastPathSegment.toUpperCase(Locale.US))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        immersionBar.init()

        titleBar.addRightButton(rightId = R.mipmap.ic_more_orange, onClickListener = View.OnClickListener {
            startActivity<UserInfoActivity>("id" to mTargetId)
        })

        val info = RongUserInfoManager.getInstance().getUserInfo(mTargetId)
        title = if (info == null || info.name.isNullOrEmpty()) {
            mTitle
        } else {
            info.name
        }

        if(TextUtils.equals("--",mTitle)){
            getOtherUser()
        }

        var myInfo = RongUserInfoManager.getInstance().getUserInfo(userId)

        var myName = if (myInfo == null || myInfo.name.isNullOrEmpty()) {
            ""
        } else {
            myInfo.name
        }

        tv_openchat_apply.setOnClickListener {
            tv_openchat_apply.isEnabled = false
            tv_openchat_apply.text = resources.getText(R.string.string_already_applay)
            applyPrivateChat()
            relative_tips.visibility = View.VISIBLE
            tv_openchat_apply.visibility = View.VISIBLE
            tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
            tv_openchat_tips.text = resources.getString(R.string.string_other_agreee_openchat)
        }

        tv_openchat_points.setOnClickListener {
            payPoints(mTitle)//支付积分
        }

        linear_openchat_agree.visibility = View.GONE

        tv_openchat_no.setOnClickListener {
            //拒绝
            doUpdatePrivateChatStatus("3")
        }

        tv_openchat_agree.setOnClickListener {
            //同意
            doUpdatePrivateChatStatus("2")
        }

        enterActivity()

        RongIM.getInstance().setSendMessageListener(this)
    }

    /**
     * 申请私聊
     */
    private fun applyPrivateChat(){
        Request.doApplyPrivateChat(userId,mTargetId).request(this,false,success={msg,jsonobject->

        }){code,msg->
            showToast(msg)
        }
    }

    /**
     * 同意或拒绝
     */
    private fun doUpdatePrivateChatStatus(iStatus:String){
        Request.doUpdatePrivateChatStatus(mTargetId,userId,iStatus).request(this,false,success={msg,jsonObject->
            relative_tips.visibility = View.GONE
            if(TextUtils.equals("2",iStatus)){
                fragment?.let {
                    it.doIsNotSendMsg(false,"")
                    if(TextUtils.equals("1",sex)){
                        relative_tips.visibility = View.VISIBLE
                        tv_openchat_points.visibility = View.VISIBLE
                        linear_openchat_agree.visibility = View.GONE
                        IsAgreeChat = true
                        tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg), SendMsgTotal)
                        tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
                    }else{
                        relative_tips.visibility = View.GONE
                    }
                }
            }
        }){code,msg->
            showToast(msg)
        }
    }

    private fun getOtherUser(){
        Request.getUserInfo(userId,mTargetId).request(this, success = { _, data ->
               data?.let {
                   title = it.name
               }
        })
    }
    /**
     * 获取私聊状态
     */
    private fun getApplyStatus(){
        Request.getApplyStatus(userId,mTargetId).request(this,false,success={msg,jsonObjetct->
            jsonObjetct?.let {
                var code = it.optInt("code")
                if(code == 1){//已申请私聊且对方已同意
                    if(TextUtils.equals("1",sex)){
                        sendCount = it.optInt("iTalkCount")
                        setIsNotShowPoints()
                    }else{
                        relative_tips.visibility = View.GONE
                    }

                    if(!SPUtils.instance().getBoolean(Const.IS_FIRST_SHOW_TIPS, false)){
                        var mDialogPrivateChat = DialogPrivateChat()
                        mDialogPrivateChat.show(supportFragmentManager, "DialogPrivateChat")
                    }else{

                    }
                }else if(code== 2){//已申请私聊且对方还未通过
                    tv_openchat_apply.isEnabled= false
                    tv_openchat_apply.text = resources.getText(R.string.string_already_applay)
                    relative_tips.visibility = View.VISIBLE
                    tv_openchat_apply.visibility = View.VISIBLE
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips.text = resources.getString(R.string.string_other_agreee_openchat)
                    fragment?.doIsNotSendMsg( true, resources.getString(R.string.string_other_agreee_openchat))
                }else if(code == 3){//对方发出申请私聊等待我确认
                    relative_tips.visibility = View.VISIBLE
                    linear_openchat_agree.visibility = View.VISIBLE
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips.text = getString(R.string.string_other_apply_openchat)
                    fragment?.let {
                        it.doIsNotSendMsg( true, resources.getString(R.string.string_other_agreee_openchat))
                    }
                }else if(code == 4){//双方均未发出私聊申请
                    relative_tips.visibility = View.VISIBLE
                    tv_openchat_apply.visibility = View.VISIBLE
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips.text = resources.getString(R.string.string_other_agreee_openchat)
                    fragment?.let {
                        it.doIsNotSendMsg(true, resources.getString(R.string.string_other_agreee_openchat))
                    }
                }else if(code == 5){//对方的私聊设置为直接私聊
                     if(TextUtils.equals("1",sex)){
                         sendCount = it.optInt("iTalkCount")
                         setIsNotShowPoints()
                     }else{
                         IsAgreeChat = false
                         relative_tips.visibility = View.GONE
                     }
                }else if(code == 6){//以前聊过天的允许私聊(包括付过积分，约会过的，送过花的)
                    relative_tips.visibility = View.GONE
                    IsAgreeChat = false
                }else if(code ==8){
                    CustomToast.showToast(getString(R.string.string_addblacklist))
                    fragment?.let {
                        it.doIsNotSendMsg(true, getString(R.string.string_addblacklist_toast))
                    }
                }else{
                    relative_tips.visibility = View.GONE
                    tv_openchat_points.visibility = View.VISIBLE
                    linear_openchat_agree.visibility = View.GONE
                    tv_openchat_apply.visibility = View.GONE
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_points)
                }
            }
        })
    }

    /**
     * 男用户超过三次支付积分的判断
     */
    private fun setIsNotShowPoints(){
        relative_tips.visibility = View.VISIBLE
        tv_openchat_points.visibility = View.VISIBLE
        if(sendCount>=3){
            tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
            tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_points)
            IsAgreeChat = false
            fragment?.doIsNotSendMsg(!IsAgreeChat,resources.getString(R.string.string_other_agreee_openchat))
        }else{
            IsAgreeChat = true
            sendCount = SendMsgTotal - sendCount
            tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg), sendCount)
            tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
            fragment?.doIsNotSendMsg(!IsAgreeChat,"")
        }
    }

    /**
     * 检查聊天次数
     */
    private fun checkTalkJustify() {
        Request.doTalkJustifyNew(userId, mTargetId).request(this, false, success = { msg, data ->
            if (data != null) {
                var code = data.optInt("iTalkType")
                if (code == 2) {//已发送3次聊天消息，需要解锁无限畅聊
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_points)
                    fragment?.doIsNotSendMsg(true,resources.getString(R.string.string_pay_points_openchat))
                } else if (code == 1) {//发送3次聊天消息以内，允许继续发送消息
                    var iTalkCount = data.optInt("iTalkCount")
                    sendCount = SendMsgTotal - iTalkCount
                    tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg), sendCount)
                    tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
                    Log.i(TAG, "${SendMsgTotal}发送消息数量")
                }
                Log.i(TAG, "${SendMsgTotal}发送消息数量${code}")
            }
        }) { code, msg ->
            if (code == 0) {
                showToast(msg)
            }
        }
    }

    /**
     * 支付积分
     */
    private fun payPoints(name: String){
        Request.doTalkJustify(userId, mTargetId).request(this,false,success = {msg,data->
            if(data!=null){
                var code = data!!.optInt("code")
                if(code == 1){
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    if(point.toInt() > remainPoint.toInt()){
                        val dateDialog = OpenDatePointNoEnoughDialog()
                        var point = data!!.optString("iTalkPoint")
                        var remainPoint = data!!.optString("iRemainPoint")
                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
                        dateDialog.show(supportFragmentManager, "d")
                    }else{
                        val dateDialog = OpenDatePayPointDialog()
                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint,"username" to name,"chatUserId" to mTargetId,"type" to "1")
                        dateDialog.show(supportFragmentManager, "d")
                        dateDialog.let {
                            it.setDialogListener { p, s ->
                                relative_tips.visibility = View.GONE
                                IsAgreeChat = false
                                fragment?.doIsNotSendMsg(false,"")
                            }
                        }
                    }
                } else if(code == 0){
                    showToast(msg.toString())
                } else {
                    val dateDialog = OpenDatePointNoEnoughDialog()
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
                    dateDialog.show(supportFragmentManager, "d")
                }
            }else{
                showToast(msg.toString())
            }
        }) { code, msg ->
             if(code == 0){
                 showToast(msg)
             }
        }
    }

    /**
     * 进入聊天页面
     */
    private fun enterActivity() {

        val token = SPUtils.instance().getString(Const.User.RONG_TOKEN)
        if (token == "default") {
            PushAgent.getInstance(applicationContext).deleteAlias(userId, "D6", { _, _ ->

            })
            startActivity<SignInActivity>()
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
                var type = jsonObject.optString("status")
                if(TextUtils.equals("1",type)){
                    relative_tips.visibility = View.VISIBLE
                    linear_openchat_agree.visibility = View.VISIBLE
                    tv_openchat_apply.visibility = View.GONE
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips.text = getString(R.string.string_other_apply_openchat)
                }else if(TextUtils.equals("2",type)){//同意
                    if(TextUtils.equals("1",sex)){
                        relative_tips.visibility = View.VISIBLE
                        tv_openchat_points.visibility = View.VISIBLE
                        linear_openchat_agree.visibility = View.GONE
                        tv_openchat_apply.visibility = View.GONE
                        tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg), SendMsgTotal)
                        tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
                        IsAgreeChat = true
                    }else{
                        relative_tips.visibility = View.GONE
                    }
                    fragment?.doIsNotSendMsg(false,"")
                    if(!SPUtils.instance().getBoolean(Const.IS_FIRST_SHOW_TIPS, false)){
                        var mDialogPrivateChat = DialogPrivateChat()
                        mDialogPrivateChat.show(supportFragmentManager, "DialogPrivateChat")
                    }else{

                    }
                }else if(TextUtils.equals("3",type)){//拒绝
                    relative_tips.visibility = View.VISIBLE
                    tv_openchat_apply.visibility = View.VISIBLE
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips.text = resources.getString(R.string.string_other_agreee_openchat)
                    IsAgreeChat = false
                    if(!SPUtils.instance().getBoolean(Const.IS_FIRST_SHOW_TIPS, false)){
                        var mDialogPrivateChat = DialogPrivateChat()
                        mDialogPrivateChat.show(supportFragmentManager, "DialogPrivateChat")
                    }else{

                    }
                }
            }
        }

        if (!TextUtils.equals(mTargetId, Const.CustomerServiceId)) {
            getApplyStatus()
        }

    }

    override fun onSend(p0: Message?): Message? {
        return p0
    }

    override fun onSent(p0: Message?, p1: RongIM.SentMessageErrorCode?): Boolean {
        p0?.let {
            if (!TextUtils.equals(mTargetId, Const.CustomerServiceId)) {
                if (TextUtils.equals("1", sex)) {
                    if (IsAgreeChat) {
                        if(p1==null){
                            checkTalkJustify()
                        }
                        Log.i(TAG, "${p1}用户Id${it.senderUserId}")
                    }
                }
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        RongIM.getInstance().setSendMessageListener(null)
    }
}
