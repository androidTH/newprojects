package com.d6.android.app.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.d6.android.app.R
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.rong.bean.GroupUnKnowTipsMessage
import com.d6.android.app.rong.bean.TipsMessage
import com.d6.android.app.rong.bean.TipsTxtMessage
import com.d6.android.app.rong.fragment.ConversationFragmentEx
import com.d6.android.app.utils.*
import com.d6.android.app.utils.AppUtils.Companion.setTextViewSpannable
import com.d6.android.app.utils.Const.APPLAY_CONVERTION_ISTOP
import com.d6.android.app.utils.Const.CHAT_TARGET_ID
import com.d6.android.app.utils.Const.CONVERSATION_APPLAY_DATE_TYPE
import com.d6.android.app.utils.Const.CONVERSATION_APPLAY_PRIVATE_TYPE
import com.d6.android.app.utils.Const.RECEIVER_FIRST_PRIVATE_TIPSMESSAGE
import com.d6.android.app.utils.Const.SEND_FIRST_PRIVATE_TIPSMESSAGE
import com.d6.android.app.utils.Const.SEND_GROUP_TIPSMESSAGE
import com.d6.android.app.utils.Const.WHO_ANONYMOUS
import com.d6.android.app.widget.CustomToast
import com.umeng.message.PushAgent
import io.rong.imkit.RongIM
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Group
import io.rong.imlib.model.Message
import io.rong.message.ImageMessage
import io.rong.message.TextMessage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.tv_openchat_agree_bottom
import kotlinx.android.synthetic.main.activity_chat.tv_openchat_no_bottom
import kotlinx.android.synthetic.main.layout_date_chat.*
import org.jetbrains.anko.*
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList


//聊天
class ChatActivity : BaseActivity(), RongIM.OnSendMessageListener, View.OnLayoutChangeListener {

    private var TAG = "ChatActivity"
    public var sendCount: Int = 0
    public var SendMsgTotal:Int = 3
    public var sAppointmentSignupId:String = ""

    private var IsAgreeChat:Boolean = true //true 代表需要判断聊天次数 false代表不用判断聊天次数
    private var iType:Int=1 //1、私聊 2、匿名组
    private var mGroupIdSplit:List<String> =ArrayList<String>()
    private var ISNOTYAODATE = 1// 1邀约  2赴约

    private var mRongReceiveMessage:rongReceiveMessage?=null

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

    private var isInBlackList=0
    private var isValid:String="0"

    private var mOtherUserId = ""
    private var mWhoanonymous = "1" //1我自己匿名   2对方匿名

    /**
     * 会话类型
     */
    private val mConversationType: Conversation.ConversationType by lazy {
        Conversation.ConversationType.valueOf(intent.data.lastPathSegment.toUpperCase(Locale.US))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        if(mConversationType==Conversation.ConversationType.GROUP){
            immersionBar.statusBarColor(R.color.color_8F5A5A).statusBarDarkFont(true).init()
            rl_chat_toolbar.backgroundColor = ContextCompat.getColor(this,R.color.color_8F5A5A)
            iv_nimingbg.visibility = View.VISIBLE
            tv_chattitle.textColor = ContextCompat.getColor(this,R.color.white)
            var mDrawableRight = ContextCompat.getDrawable(this,R.mipmap.titlemore_whitesmall_icon)
            tv_chattitle.setCompoundDrawablesWithIntrinsicBounds(null,null,mDrawableRight,null)

            //"anoy_" + 匿名用户的id + "_" + 报名约会用户的id   anoy_103091_100541
            mGroupIdSplit = mTargetId.split("_")
            iType = 2
            if(TextUtils.equals(mGroupIdSplit[1], getLocalUserId())){
                //我是匿名
                mOtherUserId = mGroupIdSplit[2]
                RongIM.getInstance().getHistoryMessages(mConversationType,mTargetId,-1,20,object : RongIMClient.ResultCallback<List<Message>>(){
                    override fun onSuccess(p0: List<Message>?) {
                        if (p0!!.size == 0) {
                            sendOutgoingMessage(getString(R.string.string_nm_tips),"1")
                        } else {
                            if (getOneDay(SPUtils.instance().getLong(SEND_GROUP_TIPSMESSAGE + mOtherUserId, System.currentTimeMillis()))) {
                                sendOutgoingMessage(getString(R.string.string_nm_tips),"1")
                            }
                        }
                    }

                    override fun onError(p0: RongIMClient.ErrorCode?) {

                    }
                })
                mWhoanonymous = "1"
                SPUtils.instance().put(WHO_ANONYMOUS,"1").apply()
                RongUtils.setUserInfo(mOtherUserId,tv_chattitle,chat_headView)
            }else{
                chat_headView.setImageURI("res:///"+R.mipmap.nimingtouxiang_small)
                mOtherUserId = mGroupIdSplit[1] //对方匿名
                tv_chattitle.text="匿名"
                mWhoanonymous = "2"
                SPUtils.instance().put(WHO_ANONYMOUS,"2").apply()

            }

            Request.findGroupDescByGroupId(getLocalUserId(), mTargetId).request(this, false, success = { msg, data ->
                data?.let {
                    var group = Group(it.sId, it.sGroupName, Uri.parse(it.sGroupPicUrl))
                    RongIM.getInstance().refreshGroupInfoCache(group)
                }
            })

        }else if(mConversationType.equals(Conversation.ConversationType.PRIVATE)){
            immersionBar.init()
            mOtherUserId = mTargetId
            iType = 1
//            getOtherUser()
            RongUtils.setUserInfo(mOtherUserId,tv_chattitle,chat_headView)
//            setChatTitle()

//            checkISFirstReceiverMsg()
        }

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            onBackPressed()
        }

        rl_userinfo.setOnClickListener {
            if(mConversationType.equals(Conversation.ConversationType.PRIVATE)){
                startActivity<UserInfoActivity>("id" to mOtherUserId)
            }else{
                if(TextUtils.equals("2",mWhoanonymous)){//2 对方匿名
                    var mUnknowDialog = UnKnowInfoDialog()
                    mUnknowDialog.arguments = bundleOf("otheruserId" to mOtherUserId)
                    mUnknowDialog.show(supportFragmentManager,"unknowDialog")
                }else{
                    startActivity<UserInfoActivity>("id" to mOtherUserId)
                }
            }
        }

        iv_chat_more.setOnClickListener {
            val userActionDialog = UserActionDialog()
            userActionDialog.arguments= bundleOf("isInBlackList" to isInBlackList)
            userActionDialog.setDialogListener { p, s ->
                if (p == 0) {//举报
                    startActivity<ReportActivity>("id" to mOtherUserId, "tiptype" to "1")
                } else if (p == 1) {
                    if(isInBlackList==1){
                        removeBlackList()
                    }else{
                        addBlackList()
                    }
                }
            }
            userActionDialog.show(supportFragmentManager, "user")
        }

//        if(TextUtils.equals("--",mTitle)){
//            getOtherUser()
//        }

        tv_openchat_apply_bottom.setOnClickListener {
            //            applyPrivateChat()
            Request.getUnlockTalkPoint(getLoginToken()).request(this,false,success={ msg, jsonobject->
                jsonobject?.let {
                    var iTalkPoint = it.optInt("iTalkPoint")
                    var iTalkRefusePoint = it.optInt("iTalkRefusePoint")
                    var iTalkOverDuePoint = it.optInt("iTalkOverDuePoint")

                    showDatePoint("${iTalkPoint}",iTalkRefusePoint,iTalkOverDuePoint,"","1")
                }
            }){code,msg->
                if(code == 2){
                    var jsonObject = JSONObject(msg)
                    var iTalkPoint = jsonObject.optInt("iTalkPoint")
                    var iTalkRefusePoint = jsonObject.optInt("iTalkRefusePoint")
                    var iTalkOverDuePoint = jsonObject.optInt("iTalkOverDuePoint")
                    var msg = jsonObject.optString("resMsg")
                    showDatePoint("${iTalkPoint}",iTalkRefusePoint,iTalkOverDuePoint,msg,"${code}")
                }
            }
        }

        tv_apply_sendflower.setOnClickListener {
            var dialogSendRedFlowerDialog = SendRedFlowerDialog()
            dialogSendRedFlowerDialog.arguments= bundleOf("ToFromType" to 5,"userId" to mOtherUserId)
            dialogSendRedFlowerDialog.show(supportFragmentManager,"sendflower")
        }

        tv_openchat_points.setOnClickListener {
            payPoints(mTitle)//支付积分
        }

        linear_openchat_agree_bottom.visibility = View.GONE
        linear_openchat_agree.visibility = View.GONE

        tv_openchat_no_bottom.setOnClickListener {
            //拒绝
            doUpdatePrivateChatStatus("3")
            RongUtils.setConversationTop(this,mConversationType,if(iType==2)  mTargetId else mOtherUserId,false)
            SPUtils.instance().put(APPLAY_CONVERTION_ISTOP+ getLocalUserId()+"-"+if(iType==2)  mTargetId else mOtherUserId,false).apply()
            SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE+ getLocalUserId()+"-"+if(iType==2)  mTargetId else mOtherUserId,false).apply()
        }

        tv_openchat_agree_bottom.setOnClickListener {
            //同意
            doUpdatePrivateChatStatus("2")
            RongUtils.setConversationTop(this,mConversationType,if(iType==2)  mTargetId else mOtherUserId,false)
            SPUtils.instance().put(APPLAY_CONVERTION_ISTOP+ getLocalUserId()+"-"+if(iType==2)  mTargetId else mOtherUserId,false).apply()
            SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE+ getLocalUserId()+"-"+if(iType==2)  mTargetId else mOtherUserId,false).apply()
        }

        headView_ydate.setOnClickListener {
            if(mConversationType.equals(Conversation.ConversationType.PRIVATE)){
                startActivity<UserInfoActivity>("id" to mOtherUserId)
            }else{
                if(TextUtils.equals("2",mWhoanonymous)){//2 对方匿名
                    var mUnknowDialog = UnKnowInfoDialog()
                    mUnknowDialog.arguments = bundleOf("otheruserId" to mOtherUserId)
                    mUnknowDialog.show(supportFragmentManager,"unknowDialog")
                }else{
                    startActivity<UserInfoActivity>("id" to mOtherUserId)
                }
            }
        }

        headView_fdate.setOnClickListener {
            startActivity<UserInfoActivity>("id" to getLocalUserId())
        }

        //放弃
        tv_datechat_giveup.setOnClickListener {
            updateDateStatus(sAppointmentSignupId,4)
        }

        //拒绝
        tv_datechat_no.setOnClickListener {
            updateDateStatus(sAppointmentSignupId,3)
        }

        //同意
        tv_datechat_agree.setOnClickListener {
            updateDateStatus(sAppointmentSignupId,2)
        }

        iv_chat_unfold.setOnClickListener {
            if(iv_chat_unfold.visibility == View.VISIBLE){
                extendDateChatDesc(true)
            }else{
                extendDateChatDesc(false)
            }
//            if(rl_date_chat.visibility == View.GONE){
//                rl_date_chat.visibility = View.VISIBLE
//                root_date_chat.animation = AnimationUtils.loadAnimation(this,
//                        R.anim.anim_datechat_in)
//            }else{
//                var annotation =  AnimationUtils.loadAnimation(this,
//                        R.anim.anim_datechat_out)
//                annotation.setAnimationListener(object :Animation.AnimationListener {
//                    override fun onAnimationStart(animation: Animation) {
//
//                    }
//
//                    override fun onAnimationEnd(animation: Animation) {
//                        rl_date_chat.visibility = View.GONE
//                    }
//
//                    override fun onAnimationRepeat(animation: Animation) {
//
//                    }
//                })
//                rl_date_chat.startAnimation(annotation)
//                iv_chat_unfold.animation = annotation
//            }
        }

        tv_datechat_content.setEllipsize(TextUtils.TruncateAt.END);//收起
        tv_datechat_content.maxLines = 2

        if (TextUtils.equals(mOtherUserId, Const.CustomerServiceId) || TextUtils.equals(mOtherUserId, Const.CustomerServiceWomenId)) {
            root_date_chat.visibility = View.GONE
            tv_service_time.visibility = View.VISIBLE
            checkServiceUserOnline(mOtherUserId)
        }

        enterActivity()

        RongIM.getInstance().setSendMessageListener(this)

        getOtherUser()

//        RongUtils.setConversationTop(this,mConversationType,if(iType==2)  mTargetId else mOtherUserId,true)
    }

    //检查客服是否在线
    private fun checkServiceUserOnline(iUserId:String){
        Request.checkUserOnline(getLoginToken(),iUserId).request(this,success={ _, data->
            data?.let {
                var iOnline = it.optInt("iOnline")
                if(iOnline==1){
                    var drawable = ContextCompat.getDrawable(this,R.drawable.shape_dot_offline)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//这句一定要加
                    tv_service_time.setCompoundDrawables(drawable,null,null,null)
                    tv_service_time.text = "离线:客服工作时间上午9:00-凌晨1:30, 可给客服留言"
                }else{
                    var drawable = ContextCompat.getDrawable(this,R.drawable.shape_dot_online)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//这句一定要加
                    tv_service_time.setCompoundDrawables(drawable,null,null,null)
                    tv_service_time.text = "人工在线"
                }
            }
        })
    }

    private fun showDatePoint(iTalkPoint:String,iTalkRefusePoint:Int,iTalkOverDuePoint:Int,msg:String,code:String){
        var dateDialog = OpenDatePayPointDialog()
        dateDialog.arguments = bundleOf("point" to "${iTalkPoint}","iTalkRefusePoint" to iTalkRefusePoint,"iTalkOverDuePoint" to iTalkOverDuePoint,"msg" to msg,"type" to "${code}")
        dateDialog.show(supportFragmentManager, "d")
        dateDialog.let {
            it.setDialogListener { p, s ->
                SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,true).apply()
                applyPrivateChat()
//                relative_tips.visibility = View.GONE
//                IsAgreeChat = false
//                fragment?.doIsNotSendMsg(false, "")
            }
        }
    }

    //发送匿名消息
    private fun sendOutgoingMessage(msg:String,type:String){
        var custommsg = TipsTxtMessage(msg, type)
        var richContentMessage = GroupUnKnowTipsMessage.obtain(msg, GsonHelper.getGson().toJson(custommsg))
        RongIM.getInstance().insertOutgoingMessage(mConversationType, mTargetId, Message.SentStatus.RECEIVED,richContentMessage, object : RongIMClient.ResultCallback<Message>() {
            override fun onSuccess(message: Message) {
                SPUtils.instance().put(SEND_GROUP_TIPSMESSAGE+mOtherUserId, System.currentTimeMillis()).apply()
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {

            }
        })
    }

    private fun sendOutgoingSystemMessage(msg:String,type:String){
        var custommsg = TipsTxtMessage(msg, type)
        var richContentMessage = TipsMessage.obtain(msg, GsonHelper.getGson().toJson(custommsg))
        RongIM.getInstance().insertOutgoingMessage(mConversationType, mTargetId, Message.SentStatus.RECEIVED,richContentMessage, object : RongIMClient.ResultCallback<Message>() {
            override fun onSuccess(message: Message) {
                SPUtils.instance().put(SEND_GROUP_TIPSMESSAGE+mOtherUserId, System.currentTimeMillis()).apply()
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {

            }
        })
    }

    /**
     * 获取群组成员信息
     */
    private fun setChatTitle() {
        val info = RongUserInfoManager.getInstance().getUserInfo(mOtherUserId)
        if (info == null || info.name.isNullOrEmpty()) {
            tv_chattitle.text = mTitle
        } else {
            tv_chattitle.text = info.name
        }
    }

    /**
     * 申请私聊
     */
    private fun applyPrivateChat(){
        Request.doApplyNewPrivateChat(getLoginToken(), mOtherUserId).request(this, false, success = { msg, jsonobject ->
            //                var code = jsonobject.optInt("code")
//                Log.i("chatActivity", "${jsonobject}")
//                if (code != 1){
//
//                }
            relative_tips_bottom.visibility = View.VISIBLE
            tv_apply_sendflower.visibility = View.VISIBLE
            tv_openchat_apply_bottom.visibility = View.GONE
            tv_openchat_tips_title_bottom.text = getString(R.string.string_appaying_openchat)
            tv_openchat_tips_bottom.text = getString(R.string.string_give_redflower)
        }) { code, msg ->
            showToast(msg)
        }
    }

    /**
     * 同意或拒绝
     */
    private fun doUpdatePrivateChatStatus(iStatus:String){
        Request.doUpdatePrivateChatStatus(mOtherUserId,userId,iStatus).request(this,false,success={msg,jsonObject->
            if(TextUtils.equals("2",iStatus)){
                fragment?.let {
                    relative_tips_bottom.visibility = View.GONE
                    it.hideChatInput(false)
//                    if(TextUtils.equals("1",sex)){
//                        relative_tips.visibility = View.VISIBLE
//                        tv_openchat_points.visibility = View.VISIBLE
//                        linear_openchat_agree.visibility = View.GONE
//                        IsAgreeChat = true
//                        tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg), SendMsgTotal)
//                        tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
//                    }else{
//                        relative_tips.visibility = View.GONE
//                    }
                }
            }else{
                linear_openchat_agree_bottom.visibility = View.GONE
                getApplyStatus()
            }
        }){code,msg->
            showToast(msg)
        }
    }

    private fun getOtherUser(){
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request(this, success = { _, data ->
            data?.let {
                isValid = it.isValid.toString()
//                   isInBlackList = it.iIsInBlackList!!.toInt()
            }
        }){code,msg->
            if(code==2){
                toast(msg)
                finish()
            }
        }
    }
    /**
     * 获取私聊状态
     */
    private fun getApplyStatus(){
        Request.getApplyStatus(getLocalUserId(),if(iType==2)  mTargetId else mOtherUserId,iType).request(this,false,success={ msg, jsonObjetct->
            jsonObjetct?.let {
                var code = it.optInt("code")
                Log.i("chatactivity","code=${code}----${it}")
                if(code == 1){//已申请私聊且对方已同意
                    relative_tips.visibility = View.GONE
                    if(TextUtils.equals("1",sex)){
                        sendCount = it.optInt("iTalkCount")
                        setIsNotShowPoints()
                    }else{
                        IsAgreeChat = false
                        relative_tips.visibility = View.GONE
                    }
                    // checkISFirstSendMsg()
//                    ---SPUtils.instance().put(SEND_FIRST_PRIVATE_TIPSMESSAGE+getLocalUserId(),true).apply()
                }else if(code== 2){//已申请私聊且对方还未通过
                    relative_tips_bottom.visibility = View.VISIBLE
                    tv_openchat_apply_bottom.visibility = View.GONE
                    tv_apply_sendflower.visibility = View.VISIBLE
                    tv_apply_sendflower.text = resources.getText(R.string.string_applay_sendredflower)

                    tv_openchat_tips_title_bottom.text = getString(R.string.string_appaying_openchat)
                    tv_openchat_tips_bottom.text = getString(R.string.string_give_redflower)

                    fragment?.hideChatInput( true)

                    SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,true).apply()

                }else if(code == 3){//对方发出申请私聊等待我确认
                    relative_tips_bottom.visibility = View.VISIBLE
                    linear_openchat_agree_bottom.visibility = View.VISIBLE
                    tv_openchat_tips_title_bottom.visibility = View.GONE
                    tv_openchat_tips_bottom.visibility = View.GONE
                    tv_openchat_tips_center_bottom.visibility = View.VISIBLE
                    tv_openchat_tips_center_bottom.text =String.format(getString(R.string.string_applay_tips_center),tv_chattitle.text)
                    fragment?.let {
                        it.hideChatInput( true)
                    }
                    SPUtils.instance().put(APPLAY_CONVERTION_ISTOP+ getLocalUserId()+"-"+if(iType==2)  mTargetId else mOtherUserId,true).apply()
                    SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,true).apply()

                }else if(code == 4){//双方均未发出私聊申请且双方私聊设置为同意后私聊
                    if(iType==2){
                        fragment?.let {
                            it.doIsNotSendMsg(true,"约会已结束")
                        }
                    }else{
                        relative_tips_bottom.visibility = View.VISIBLE
                        tv_openchat_apply_bottom.visibility = View.VISIBLE
                        tv_openchat_tips_title_bottom.visibility = View.VISIBLE
                        tv_openchat_tips_bottom.visibility = View.VISIBLE
                        tv_openchat_tips_center_bottom.visibility = View.GONE

                        tv_openchat_tips_title_bottom.text = resources.getString(R.string.string_openchat)
                        tv_openchat_tips_bottom.text = resources.getString(R.string.string_apply_agree_openchat_warm)
                        fragment?.let {
                            it.hideChatInput(true)
                        }
                    }
                    SPUtils.instance().put(APPLAY_CONVERTION_ISTOP+ getLocalUserId()+"-"+if(iType==2)  mTargetId else mOtherUserId,false).apply()
                    SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,false).apply()
                    SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE+ getLocalUserId()+"-"+ if(iType==2) mTargetId else mOtherUserId,false).apply()
                }else if(code == 5){//对方的私聊设置为直接私聊
                    if(TextUtils.equals("1",sex)){
                        sendCount = it.optInt("iTalkCount")
                        setIsNotShowPoints()
                        IsAgreeChat = true
                        relative_tips.visibility = View.GONE
                    }else{
                        IsAgreeChat = true
                        relative_tips.visibility = View.GONE
                    }
//                    ---checkISFirstSendMsg()
                    fragment?.let {
                        it.doIsNotSendMsg(false, resources.getString(R.string.string_other_agreee_openchat))
                    }
                }else if(code == 6){//以前聊过天的允许私聊(包括付过积分，约会过的，送过花的)
                    relative_tips_bottom.visibility = View.GONE
                    IsAgreeChat = false
                    SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE+ getLocalUserId()+"-"+ if(iType==2) mTargetId else mOtherUserId,false).apply()
//                    ---SPUtils.instance().put(SEND_FIRST_PRIVATE_TIPSMESSAGE+getLocalUserId(),false).apply()
                }else if(code ==8){
                    CustomToast.showToast(getString(R.string.string_addblacklist))
                    fragment?.let {
                        it.doIsNotSendMsg(true, getString(R.string.string_addblacklist_toast))
                    }
                }else if(code==9){ //报名约会
                    //iTalkCount:已发出的聊天消息次数  iAllTalkCount：总的聊天消息次数
                    SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,true).apply()
                    sendCount = it.optInt("iTalkCount")
                    SendMsgTotal = it.optInt("iAllTalkCount")
                    var datetime = it.optLong("dOverduetime")
                    Log.i("chatactivity","${sendCount}消息数量appointment-----${it.optJsonObj("appointment")}")
                    var appointment = GsonHelper.getGson().fromJson(it.optJsonObj("appointment"), MyAppointment::class.java)
                    appointment?.let {
                        root_date_chat.visibility = View.VISIBLE
                        setDateChatUi(appointment,sendCount,datetime)
                        CHAT_TARGET_ID = mTargetId
                    }
                }else{
                    relative_tips.visibility = View.GONE
                    tv_openchat_points.visibility = View.VISIBLE
                    linear_openchat_agree.visibility = View.GONE
                    tv_openchat_apply.visibility = View.GONE
                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchangchat)
                    tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_points)
                }
            }
        }){code,msg->
            if(code == 0){
                CustomToast.showToast(msg)
                fragment?.let {
                    it.doIsNotSendMsg(true, msg)
                }
            }
        }
    }

    private fun checkISFirstReceiverMsg(){
        if(!TextUtils.equals(mOtherUserId, Const.CustomerServiceId) || !TextUtils.equals(mOtherUserId, Const.CustomerServiceWomenId)){
            RongIM.getInstance().getHistoryMessages(mConversationType,mOtherUserId,-1,20,object : RongIMClient.ResultCallback<List<Message>>(){
                override fun onSuccess(p0: List<Message>?) {
                    var mReceiveListMessage = ArrayList<Message>()
                    p0?.let {
                        for(message:Message in it){
                            if(message.messageDirection==Message.MessageDirection.RECEIVE){
                                if(message.content is TextMessage||message is ImageMessage){
                                    mReceiveListMessage.add(message)
                                }
                            }
                        }
                    }
                    Log.i("chatactivity","消息历史记录"+mReceiveListMessage.size)
                    if (mReceiveListMessage.size==1) {
                        if(!SPUtils.instance().getBoolean(RECEIVER_FIRST_PRIVATE_TIPSMESSAGE+ getLocalUserId())){
                            sendOutgoingSystemMessage(getString(R.string.string_system_tips02),"1")
                            SPUtils.instance().put(RECEIVER_FIRST_PRIVATE_TIPSMESSAGE+ getLocalUserId(),true).apply()
                        }
                    }
                }

                override fun onError(p0: RongIMClient.ErrorCode?) {

                }
            })
        }
    }

    private val mImages = ArrayList<String>()
    private fun setDateChatUi(appointment:MyAppointment,talkCount:Int,dateTime:Long){
        headView_ydate.setImageURI(appointment.sAppointmentPicUrl)
        headView_fdate.setImageURI(appointment.sPicUrl)
        tv_yname.text = appointment.sAppointUserName
        tv_fname.text = appointment.sUserName
        tv_datechat_content.text = appointment.sDesc
        tv_datchat_address.text = "约会地点：${appointment.sPlace}"
        if(appointment.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(appointment.iAppointUserid.toString(), getLocalUserId())){
            tv_date_info.text = "对方申请赴约" //"${appointment.sUserName}申请赴约"
            tv_datechat_no.visibility = View.VISIBLE
            tv_datechat_agree.visibility = View.VISIBLE
            tv_datechat_giveup.visibility = View.GONE
            ISNOTYAODATE = 1
        }else if(appointment.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(getLocalUserId(),appointment.iUserid.toString())){
            tv_date_info.text = "等待对方确认中…"
            tv_datechat_no.visibility = View.GONE
            tv_datechat_agree.visibility = View.GONE
            tv_datechat_giveup.visibility = View.VISIBLE
            ISNOTYAODATE = 2
        }

        if(SendMsgTotal>0&&talkCount<=0){
            fragment?.let {
                it.doIsNotSendMsg(true,getString(R.string.string_applay_date_tips))
            }
        }
        rv_datechat_images.setHasFixedSize(true)
        rv_datechat_images.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        if (appointment.sAppointPic.isNullOrEmpty()) {
            rv_datechat_images.visibility = View.GONE
        }else{
            rv_datechat_images.visibility = View.GONE
            val images = appointment.sAppointPic?.split(",")
            if (images != null) {
                mImages.addAll(images.toList())
            }
            rv_datechat_images.adapter = SelfReleaselmageAdapter(mImages,1)
        }

        setTextViewSpannable(this,"倒计时：${converTime(appointment.dOverduetime)}",3,4,tv_datechat_time,R.style.tv_datechat_time,R.style.tv_datechat_numbers)
        setTextViewSpannable(this,"剩余消息：${talkCount}条",3,5,tv_datechat_nums,R.style.tv_datechat_time,R.style.tv_datechat_numbers)
        if(SendMsgTotal==-1){
            tv_datechat_nums.visibility = View.GONE
        }
        sAppointmentSignupId = appointment.sAppointmentSignupId

        root_date_chat.addOnLayoutChangeListener(this)
    }

    fun updateDateStatus(sAppointmentSignupId:String,iStatus:Int){
        Request.updateDateStatus(sAppointmentSignupId,iStatus,"").request(this, success = {msg, data->
            run {
                if (iStatus == 2) {
                    root_date_chat.visibility = View.GONE
                    setFragmentTopMargin(0)
//                    tv_date_status.text = "状态:赴约"
                } else if (iStatus == 3) {
//                    tv_date_status.text = "状态：已拒绝"
                    root_date_chat.visibility = View.GONE
                    setFragmentTopMargin(0)
                    getApplyStatus()
                }else if(iStatus == 4){
//                    tv_date_status.text="状态：主动取消"
                    root_date_chat.visibility = View.GONE
                    setFragmentTopMargin(0)
                    getApplyStatus()
                }
            }
        })
    }

    private fun checkISFirstSendMsg(){
        RongIM.getInstance().getHistoryMessages(mConversationType,mOtherUserId,-1,20,object : RongIMClient.ResultCallback<List<Message>>(){
            override fun onSuccess(p0: List<Message>?) {
                var mSendListMessage = ArrayList<Message>()
                var mReceiveListMessage = ArrayList<Message>()
                p0?.let {
                    for(message:Message in it){
                        if(message.messageDirection==Message.MessageDirection.SEND){
                            if(message.content is TextMessage||message is ImageMessage){
                                mSendListMessage.add(message)
                            }
                        }
                    }
                }
                if (mSendListMessage.size==0) {
                    SPUtils.instance().put(SEND_FIRST_PRIVATE_TIPSMESSAGE+getLocalUserId(),true).apply()
                }else{
                    SPUtils.instance().put(SEND_FIRST_PRIVATE_TIPSMESSAGE+getLocalUserId(),false).apply()
                }
            }

            override fun onError(p0: RongIMClient.ErrorCode?) {

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
            tv_openchat_tips_title.text = resources.getString(R.string.string_openchangchat)
            tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_points)
            IsAgreeChat = false
            fragment?.doIsNotSendMsg(!IsAgreeChat,resources.getString(R.string.string_pay_points_openchangchat))
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
        Request.doTalkJustifyNew(userId,if(iType==2)  mTargetId else mOtherUserId ,iType).request(this, false, success = { msg, data ->
            if (data != null) {
//                var code = data.optInt("iTalkType")
//                if (code == 2) {//已发送3次聊天消息，需要解锁无限畅聊
//                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchangchat)
//                    tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_points)
//                    fragment?.doIsNotSendMsg(true,resources.getString(R.string.string_pay_points_openchangchat))
//                } else if (code == 1) {//发送3次聊天消息以内，允许继续发送消息
//                    var iTalkCount = data.optInt("iTalkCount")
//                    sendCount = SendMsgTotal - iTalkCount
//                    tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg), sendCount)
//                    tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
//                    Log.i(TAG, "${SendMsgTotal}发送消息数量")
//                }
                var code = data.optInt("code")
                if(code==2){
                    sendCount = data.optInt("iTalkCount")
                    SendMsgTotal = data.optInt("iAllTalkCount")
                    if(sendCount<= 0){
                        if(ISNOTYAODATE ==1){
                            fragment?.doIsNotSendMsg(true,getString(R.string.string_fuyue_applay_date_tips))
                        }else{
                            fragment?.doIsNotSendMsg(true,getString(R.string.string_applay_date_tips))
                        }
                        sendCount = 0
                    }
                    setTextViewSpannable(this,"剩余消息：${sendCount}条",3,5,tv_datechat_nums,R.style.tv_datechat_time,R.style.tv_datechat_numbers)
                }
                Log.i(TAG, "${SendMsgTotal}发送消息数量${code}")
            }
        }) { code, msg ->
            if (code == 0) {
                showToast(msg)
                fragment?.let {
                    it.doIsNotSendMsg(true, getString(R.string.string_addblacklist_toast))
                }
            }
        }
    }

    /**
     * 支付积分
     */
    private fun payPoints(name: String){
        Request.doTalkJustify(userId, if(iType==2)  mTargetId else mOtherUserId).request(this,false,success = {msg,data->
            if(data!=null){
                var code = data!!.optInt("code")
                if(code == 1){
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    if(point.toInt() > remainPoint.toInt()){
//                        val dateDialog = OpenChatPointNoEnoughDialog()
                        val dateDialog = OpenDatePointNoEnoughDialog()
                        var point = data!!.optString("iTalkPoint")
                        var remainPoint = data!!.optString("iRemainPoint")
                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
                        dateDialog.show(supportFragmentManager, "d")
                    }else{
                        val dateDialog = OpenDatePayPointDialog()
                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint,"username" to name,"chatUserId" to mOtherUserId,"type" to "3")
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
                    val dateDialog = OpenChatPointNoEnoughDialog()
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
        if (TextUtils.equals("default",token)) {
            PushAgent.getInstance(applicationContext).deleteAlias(userId, "D6", { _, _ ->

            })
            startActivity<SplashActivity>()
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

        fragment?.let {
            it.uri = uri
            var mSoftKeyboardStateHelper = SoftKeyboardStateHelper(root_chat)
            mSoftKeyboardStateHelper.addSoftKeyboardStateListener(object: SoftKeyboardStateHelper.SoftKeyboardStateListener{
                override fun onSoftKeyboardClosed() {

                }

                override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
                    if(iv_chat_unfold.visibility == View.GONE){
                        extendDateChatDesc(false)
                    }
                }
            })

            it.setOnExtensionExpandedListener {it->
                if(iv_chat_unfold.visibility == View.GONE){
                    extendDateChatDesc(it)
                }
            }
            mRongReceiveMessage = rongReceiveMessage(this)
            registerReceiver(mRongReceiveMessage, IntentFilter(Const.CHAT_MESSAGE))
        }

        val transaction = supportFragmentManager.beginTransaction()
        //xxx 涓轰綘瑕佸姞杞界殑 id
        transaction.add(R.id.rong_content, fragment)
        transaction.commitAllowingStateLoss()

        fragment?.setOnShowAnnounceBarListener { announceMsg, annouceUrl ->
            if (!TextUtils.isEmpty(announceMsg)) {
                var jsonObject = JSONObject(announceMsg)
                var type = jsonObject.optString("status")
                Log.i("OnShowAnnounceBar","type${type}")
                if(TextUtils.equals("1",type)){
                    relative_tips_bottom.visibility = View.VISIBLE
                    linear_openchat_agree_bottom.visibility = View.VISIBLE
                    tv_openchat_apply_bottom.visibility = View.GONE
                    tv_openchat_tips_title_bottom.visibility = View.GONE
                    tv_openchat_tips_bottom.visibility = View.GONE
                    tv_openchat_tips_center_bottom.visibility = View.VISIBLE
                    tv_openchat_tips_center_bottom.text =String.format(getString(R.string.string_applay_tips_center),tv_chattitle.text)
                    fragment?.let {
                        it.hideChatInput( true)
                    }
                    SPUtils.instance().put(APPLAY_CONVERTION_ISTOP+ getLocalUserId()+"-"+if(iType==2)  mTargetId else mOtherUserId,true).apply()
                }else if(TextUtils.equals("2",type)){//同意
                    relative_tips_bottom.visibility = View.GONE
//                    if(TextUtils.equals("1",sex)){
//                        relative_tips.visibility = View.VISIBLE
//                        tv_openchat_points.visibility = View.VISIBLE
//                        linear_openchat_agree.visibility = View.GONE
//                        tv_openchat_apply.visibility = View.GONE
//                        tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg), SendMsgTotal)
//                        tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
//                        IsAgreeChat = true
//                    }

                    IsAgreeChat = true
//                    ---SPUtils.instance().put(SEND_FIRST_PRIVATE_TIPSMESSAGE+getLocalUserId(),true).apply()
                    fragment?.let {
                        it.hideChatInput(false)
                    }
                    SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,false).apply()

//                    checkISFirstSendMsg()
//                    if(!SPUtils.instance().getBoolean(Const.IS_FIRST_SHOW_TIPS, false)){
//                        var mDialogPrivateChat = DialogPrivateChat()
//                        mDialogPrivateChat.show(supportFragmentManager, "DialogPrivateChat")
//                    }else{
//
//                    }
                }else if(TextUtils.equals("3",type)){//拒绝
//                    relative_tips.visibility = View.VISIBLE
//                    tv_openchat_apply.visibility = View.VISIBLE
//                    tv_openchat_apply.isEnabled = true
//                    tv_openchat_apply.text = resources.getText(R.string.string_apply_openchat)
//                    tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
//                    tv_openchat_tips.text = resources.getString(R.string.string_apply_agree_openchat)

//                    IsAgreeChat = false
                    relative_tips_bottom.visibility = View.VISIBLE
                    tv_openchat_apply_bottom.visibility = View.VISIBLE
                    tv_openchat_apply_bottom.isEnabled = true
                    tv_apply_sendflower.visibility = View.GONE
                    tv_openchat_apply_bottom.text = resources.getText(R.string.string_apply_openchat)
                    tv_openchat_tips_title_bottom.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips_bottom.text = resources.getString(R.string.string_apply_agree_openchat_warm)
                    fragment?.let {
                        it.hideChatInput(true)
                    }

                    SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,false).apply()
//                    if(!SPUtils.instance().getBoolean(Const.IS_FIRST_SHOW_TIPS, false)){
//                        var mDialogPrivateChat = DialogPrivateChat()
//                        mDialogPrivateChat.show(supportFragmentManager, "DialogPrivateChat")
//                    }else{
//
//                    }
                }else if(TextUtils.equals("4",type)){
                    relative_tips_bottom.visibility = View.VISIBLE
                    tv_openchat_apply_bottom.visibility = View.VISIBLE
                    tv_openchat_tips_title_bottom.visibility = View.VISIBLE
                    tv_openchat_tips_bottom.visibility = View.VISIBLE
                    tv_openchat_apply_bottom.isEnabled = true

                    tv_apply_sendflower.visibility = View.GONE
                    tv_openchat_tips_center_bottom.visibility = View.GONE
                    linear_openchat_agree_bottom.visibility = View.GONE

                    tv_openchat_apply_bottom.text = resources.getText(R.string.string_apply_openchat)
                    tv_openchat_tips_title_bottom.text = resources.getString(R.string.string_openchat)
                    tv_openchat_tips_bottom.text = resources.getString(R.string.string_apply_agree_openchat_warm)
                    fragment?.let {
                        it.hideChatInput(true)
                    }
                    SPUtils.instance().put(APPLAY_CONVERTION_ISTOP+ getLocalUserId()+"-"+if(iType==2)  mTargetId else mOtherUserId,false).apply()
                    SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE+ getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,false).apply()
                    SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE+ getLocalUserId()+"-"+ if(iType==2) mTargetId else mOtherUserId,false).apply()
//                    linear_openchat_agree_bottom.visibility = View.GONE
//                    getApplyStatus()
                }else if(TextUtils.equals("5",type)){
                    //staus 5、6、7、8分别是接受、拒绝、取消、过期
                    root_date_chat.visibility = View.GONE
                    setFragmentTopMargin(0)
                    SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,false).apply()
                }else if(TextUtils.equals("6",type)){
                    root_date_chat.visibility = View.GONE
                    setFragmentTopMargin(0)
                    getApplyStatus()
                    SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,false).apply()
                }else if(TextUtils.equals("7",type)){
                    root_date_chat.visibility = View.GONE
                    setFragmentTopMargin(0)
                    getApplyStatus()
                    SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,false).apply()
                }else if(TextUtils.equals("8",type)){
                    root_date_chat.visibility = View.GONE
                    setFragmentTopMargin(0)
                    getApplyStatus()
                    SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,false).apply()
                }else if(TextUtils.equals("9",type)){
                    relative_tips_bottom.visibility = View.GONE
                    relative_tips.visibility = View.GONE
                    setFragmentTopMargin(0)
                    fragment?.let {
                        it.hideChatInput(false)
                    }
                    getApplyStatus()
                }
            }

        }

        if (removeKFService(mOtherUserId)) {
            getApplyStatus()
        }
    }

    fun extendDateChatDesc(isextend:Boolean){
        if(isextend){
            ll_date_dowhat.visibility = View.VISIBLE
            rv_datechat_images.visibility = View.VISIBLE
            iv_chat_unfold.visibility = View.GONE
            tv_datechat_content.setEllipsize(null)//展开
            tv_datechat_content.setSingleLine(false)//这个方法是必须设置的，否则无法展开
        }else{
            ll_date_dowhat.visibility = View.GONE
            rv_datechat_images.visibility = View.GONE
            iv_chat_unfold.visibility = View.VISIBLE
            tv_datechat_content.setEllipsize(TextUtils.TruncateAt.END);//收起
            tv_datechat_content.maxLines = 2
            Log.i("ConversationFragmentEx","${isextend}")
        }
    }

    override fun onSend(p0: Message?): Message? {
        if(TextUtils.equals(isValid,"2")){
            p0?.let {
                RongIM.getInstance().insertOutgoingMessage(mConversationType, mTargetId, Message.SentStatus.RECEIVED,it.content, object : RongIMClient.ResultCallback<Message>() {
                    override fun onSuccess(message: Message) {

                    }

                    override fun onError(errorCode: RongIMClient.ErrorCode) {

                    }
                })
            }
            return null
        }
        return p0
    }

    override fun onSent(p0: Message?, p1: RongIM.SentMessageErrorCode?): Boolean {
        p0?.let {
            if (removeKFService(mOtherUserId)) {
//                if (TextUtils.equals("1", sex)) {
                if (IsAgreeChat||(SendMsgTotal!=-1&&sAppointmentSignupId.isNotEmpty())) {
                    if (p1 == null) {
                        checkTalkJustify()
                    }
                }

//     -----      if (SPUtils.instance().getBoolean(SEND_FIRST_PRIVATE_TIPSMESSAGE + getLocalUserId())) {
//                    sendOutgoingSystemMessage(getString(R.string.string_system_tips01), "1")
//                    SPUtils.instance().put(SEND_FIRST_PRIVATE_TIPSMESSAGE + getLocalUserId(), false).apply()
//                }

                Log.i(TAG, "${p1}用户Id${it.senderUserId}")
//                    }
//                }
            }
        }
        return false
    }

    private class rongReceiveMessage(chatActivity:ChatActivity) : BroadcastReceiver() {
        private var mChatActivity: ChatActivity
        init {
            mChatActivity = chatActivity
        }
        override fun onReceive(context: Context?, intent: Intent?) {
            if(mChatActivity.SendMsgTotal!=-1&&mChatActivity.sAppointmentSignupId.isNotEmpty()){
                mChatActivity.sendCount = mChatActivity.sendCount - 1
                if(mChatActivity.sendCount<= 0){
                    mChatActivity.fragment?.doIsNotSendMsg(true,mChatActivity.getString(R.string.string_applay_date_tips))
                    mChatActivity.sendCount = 0
                }
            }
            context?.let { setTextViewSpannable(it,"剩余消息：${mChatActivity.sendCount}条",3,5,mChatActivity.tv_datechat_nums,R.style.tv_datechat_time,R.style.tv_datechat_numbers) }
        }
    }

    override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        root_date_chat.removeOnLayoutChangeListener(this)
        var height = root_date_chat.height
        setFragmentTopMargin(height)
    }

    //动态设置聊天布局marginTop
    private fun setFragmentTopMargin(height:Int){
        var rongparams = rong_content.layoutParams as ViewGroup.MarginLayoutParams
        rongparams.topMargin = height
        rong_content.layoutParams = rongparams
    }

    private fun addBlackList() {
        var mDialogAddBlackList = DialogAddBlackList()
        mDialogAddBlackList.show(supportFragmentManager, "addBlacklist")
        mDialogAddBlackList.setDialogListener { p, s ->
            dialog()

            Request.addBlackList(userId, mOtherUserId,IsNMType()).request(this) { _, _ ->
                CustomToast.showToast(getString(R.string.string_blacklist_toast))
                isInBlackList = 1
            }
        }
    }

    private fun removeBlackList(){
        Request.removeBlackList(userId,mOtherUserId,IsNMType()).request(this){msg,jsonPrimitive->
            CustomToast.showToast(msg.toString())
            isInBlackList = 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RongIM.getInstance().setSendMessageListener(null)
        if(mRongReceiveMessage!=null){
            unregisterReceiver(mRongReceiveMessage)
            mRongReceiveMessage = null
        }
        CHAT_TARGET_ID = ""
    }

    private fun IsNMType():Int{
        if (iType == 2) {
            if (TextUtils.equals(mWhoanonymous,"1")) {
                return 2
            } else {
                return 1
            }
        } else {
            return 2
        }
    }
}
