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
import com.d6.android.app.R
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.models.NewGroupBean
import com.d6.android.app.net.Request
import com.d6.android.app.rong.RongD6Utils
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
import com.d6.android.app.utils.Const.Max_Angle
import com.d6.android.app.utils.Const.RECEIVER_FIRST_PRIVATE_TIPSMESSAGE
import com.d6.android.app.utils.Const.SEND_FIRST_PRIVATE_TIPSMESSAGE
import com.d6.android.app.utils.Const.SEND_GROUP_TIPSMESSAGE
import com.d6.android.app.utils.Const.WHO_ANONYMOUS
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.gift.CustormAnim
import com.d6.android.app.widget.gift.GiftControl
import com.d6.android.app.widget.gift.GiftModel
import com.umeng.message.PushAgent
import io.rong.callkit.RongCallKit
import io.rong.callkit.util.CallKitUtils
import io.rong.imkit.RongIM
import io.rong.imkit.mention.IMentionedInputListener
import io.rong.imkit.mention.RongMentionManager
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Group
import io.rong.imlib.model.Message
import io.rong.imlib.model.UserInfo
import io.rong.message.ImageMessage
import io.rong.message.TextMessage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.tv_openchat_agree_bottom
import kotlinx.android.synthetic.main.activity_chat.tv_openchat_no_bottom
import kotlinx.android.synthetic.main.layout_date_chat.*
import me.nereo.multi_image_selector.utils.FinishActivityManager
import org.jetbrains.anko.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


//聊天
class ChatActivity : BaseActivity(), RongIM.OnSendMessageListener, View.OnLayoutChangeListener {

    private var TAG = "ChatActivity"
    var sendCount: Int = 0
    var SendMsgTotal:Int = 3
    var sAppointmentSignupId:String = ""
    var sAppointType:Int? = 5
    private var directionDate:Int = 1 //1 连麦约会创建者 2 代表发起者
    private var receiveMsgCount:Int = 0
    private var SendMsgCount:Int = 0
    private var progressAngle:Float = 0.0f
    private var IsAgreeChat:Boolean = true //true 代表需要判断聊天次数 false代表不用判断聊天次数
    private var iType:Int=1 //1、私聊 2、匿名组 3 群聊
    private var mGroupIdSplit:List<String> =ArrayList<String>()
    private var ISNOTYAODATE = 1// 1邀约  2赴约
    private var iCanTalk:Int = 2 //1 已解锁  2未解锁
    private var hasReceiveMsg:Boolean = false
    private var iIsFriend:Int = -1 //1、好友 2、非好友

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

    private lateinit var mGroupBean: NewGroupBean
    fun IsNotNullGroupBean()=::mGroupBean.isInitialized
    //礼物
    private var giftControl: GiftControl? = null

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
            //"anoy_" + 匿名用户的id + "_" + 报名约会用户的id   anoy_103091_100541
            mGroupIdSplit = mTargetId.split("_")
            iv_privatechat_sendredheart.visibility = View.GONE
            if(mGroupIdSplit!=null&&mGroupIdSplit.size>1){
                iType = 2
                immersionBar.statusBarColor(R.color.color_8F5A5A).statusBarDarkFont(true).init()
                rl_chat_toolbar.backgroundColor = ContextCompat.getColor(this,R.color.color_8F5A5A)
                iv_nimingbg.visibility = View.VISIBLE
                tv_chattitle.textColor = ContextCompat.getColor(this,R.color.white)
                var mDrawableRight = ContextCompat.getDrawable(this,R.mipmap.titlemore_whitesmall_icon)
                tv_chattitle.setCompoundDrawablesWithIntrinsicBounds(null,null,mDrawableRight,null)

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

                Request.findGroupDescByGroupId(getLoginToken(), mTargetId).request(this, false, success = { msg, data ->
                    data?.let {
                        var group = Group(it.sId, it.sGroupName, Uri.parse(it.sGroupPicUrl))
                        RongIM.getInstance().refreshGroupInfoCache(group)
                    }
                })
            }else{
                immersionBar.init()
                SPUtils.instance().put(WHO_ANONYMOUS,"3").apply()
                iType = 3
                mOtherUserId = mTargetId
                tv_chattitle.setCompoundDrawables(null, null, null, null)
                CheckUserInGroup()
//                RongIM.getInstance().setGroupMembersProvider(object:RongIM.IGroupMembersProvider{
//                    override fun getGroupMembers(p0: String?, callback: RongIM.IGroupMemberCallback?) {
//                        Request.getGroupAllMemberListByGroupId("${p0}",1).request(this@ChatActivity,false,success={msg,data->
//                            val userInfos = ArrayList<UserInfo>()
//                            if (data?.list?.results != null) {
//                                for (groupMember in data?.list?.results) {
//                                    if (groupMember != null) {
//                                        val userInfo = UserInfo("${groupMember.iUserid}", "${groupMember.name}", Uri.parse("${groupMember.picUrl}"))
//                                        userInfos.add(userInfo)
//                                    }
//                                }
//                            }
//                            callback?.let {
//                                callback.onGetGroupMembersResult(userInfos) // 调用 callback 的 onGetGroupMembersResult 回传群组信息
//                            }
//                        })
//                    }
//                })
//                var group = RongUserInfoManager.getInstance().getGroupInfo(mOtherUserId)
            }
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
                    if(iType==3){
//                        if(IsNotNullGroupBean()){
//                         startActivity<GroupSettingActivity>("bean" to mGroupBean)
//                        }
                    }else{
                        startActivity<UserInfoActivity>("id" to mOtherUserId)
                    }
                }
            }
        }

        iv_chat_more.setOnClickListener {
            if(iType==1||iType==2){
                val userActionDialog = UserActionDialog()
                if(!TextUtils.equals(mOtherUserId, Const.CustomerServiceId)&&!TextUtils.equals(mOtherUserId, Const.CustomerServiceWomenId)){
                    userActionDialog.arguments= bundleOf("isInBlackList" to isInBlackList,"iType" to "${iIsFriend}")
                }else{
                    userActionDialog.arguments= bundleOf("isInBlackList" to isInBlackList,"iType" to "-1")
                }
                userActionDialog.setDialogListener { p, s ->
                    if (p == 0) {//举报
                        startActivity<ReportActivity>("id" to mOtherUserId, "tiptype" to "1")
                    } else if (p == 1) {
                        if(isInBlackList==1){
                            removeBlackList()
                        }else{
                            addBlackList()
                        }
                    }else if(p==2){
                        val mRemoveUserInfo = RemoveUserDialog()
                        mRemoveUserInfo.setDialogListener { p, s ->
                            if(p==1){
                                Request.deleteFriend("${mOtherUserId}").request(this, false, success = { msg, data ->
                                    RongD6Utils.deleConverstion(mConversationType, mOtherUserId, object : RongIMClient.ResultCallback<Boolean>() {
                                        override fun onSuccess(p0: Boolean?) {
//                                            RongIM.getInstance().clearMessages(mConversationType,
//                                                    mOtherUserId, null)
//                                            RongIMClient.getInstance().cleanRemoteHistoryMessages(mConversationType, mOtherUserId, System.currentTimeMillis(),
//                                                    null)
                                            Const.UPDATE_GROUPS_STATUS = -1
                                            iv_chat_more.postDelayed(object:Runnable{
                                                override fun run() {
                                                    onBackPressed()
                                                }
                                            },100)
                                        }

                                        override fun onError(p0: RongIMClient.ErrorCode?) {
                                            toast("删除失败！")
                                        }
                                    })
                                }) { code, msg ->
                                    toast("${msg}")
                                }
                            }
                        }
                        mRemoveUserInfo.show(supportFragmentManager, "user")
                    }
                }
                userActionDialog.show(supportFragmentManager, "user")
            }else if(iType==3){
                if(IsNotNullGroupBean()){
                    startActivity<GroupSettingActivity>("bean" to mGroupBean)
                }
            }
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

        tv_help_service.setOnClickListener {
            showServiceDialog()
        }

        tv_help_service_chat.setOnClickListener {
            showServiceDialog()
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

//        headView_ydate.setOnClickListener {
//            if(mConversationType.equals(Conversation.ConversationType.PRIVATE)){
//                startActivity<UserInfoActivity>("id" to mOtherUserId)
//            }else{
//                if(TextUtils.equals("2",mWhoanonymous)){//2 对方匿名
//                    var mUnknowDialog = UnKnowInfoDialog()
//                    mUnknowDialog.arguments = bundleOf("otheruserId" to mOtherUserId)
//                    mUnknowDialog.show(supportFragmentManager,"unknowDialog")
//                }else{
//                    startActivity<UserInfoActivity>("id" to mOtherUserId)
//                }
//            }
//        }

//        headView_fdate.setOnClickListener {
//            startActivity<UserInfoActivity>("id" to getLocalUserId())
//        }

        //放弃
        tv_datechat_giveup.setOnClickListener {
            updateDateStatus(sAppointmentSignupId,4)
        }

        //拒绝
        tv_datechat_no.setOnClickListener {
            if(sAppointType==9){
                if(directionDate==1){
                    updateSquareSignUp("${Const.mVoiceTips.voiceChatId}","3")
                }else{
                    //连麦放弃
                    updateSquareSignUp("${Const.mVoiceTips.voiceChatId}","4")
                }
            }else{
                updateDateStatus(sAppointmentSignupId,3)
            }
        }

        //同意
        tv_datechat_agree.setOnClickListener {
            if(sAppointType==9){
                PermissionsUtils.getInstance().checkPermissions(this, CallKitUtils.getCallpermissions(), object : PermissionsUtils.IPermissionsResult {
                    override fun forbidPermissions() {

                    }

                    override fun passPermissions() {
                        updateSquareSignUp("${Const.mVoiceTips.voiceChatId}","2")
                    }
                })
            }else{
                updateDateStatus(sAppointmentSignupId,2)
            }
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

        iv_privatechat_sendredheart.setOnClickListener {
            isAuthUser {
                addGiftNums(1,false,false)
                VibrateHelp.Vibrate(this,VibrateHelp.time50)
            }
        }

        iv_privatechat_sendredheart.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                var mSendLoveHeartDialog = SendLoveHeartDialog()
                mSendLoveHeartDialog.arguments = bundleOf("userId" to "${mOtherUserId}")
                mSendLoveHeartDialog.setDialogListener { p, s ->
                    addGiftNums(p, false,true)
                }
                mSendLoveHeartDialog.show(supportFragmentManager, "sendloveheartDialog")
                return true
            }
        })

        tv_datechat_content.setEllipsize(TextUtils.TruncateAt.END);//收起
        tv_datechat_content.maxLines = 2

        if (TextUtils.equals(mOtherUserId, Const.CustomerServiceId) || TextUtils.equals(mOtherUserId, Const.CustomerServiceWomenId)) {
            root_date_chat.visibility = View.GONE
            tv_service_time.visibility = View.VISIBLE
        }

        checkServiceUserOnline(mOtherUserId)

        enterActivity()

        RongIM.getInstance().setSendMessageListener(this)

        getOtherUser()

        initGift()

        FinishActivityManager.getManager().addActivity(this)
//        RongUtils.setConversationTop(this,mConversationType,if(iType==2)  mTargetId else mOtherUserId,true)
    }

    /**
     * 显示求助客服dialog
     */
    private fun showServiceDialog(){
        val customerServiceDialog = CustomerServiceDialog()
        customerServiceDialog.arguments = bundleOf("resMsg" to "对方可能暂时没看到你的申请，你可以求助你的专属微信客服联系对方",
                "dialog_title" to "求助客服联系对方","service_type" to "1")
        customerServiceDialog.show(supportFragmentManager, "resMsg")
    }

    override fun onResume() {
        super.onResume()
        if(iType==3){
            Request.getGroupByGroupId(mOtherUserId).request(this,false,success={msg,data->
                data?.let {
                    mGroupBean = data
                    if(IsNotNullGroupBean()){
                        tv_chattitle.text = "${data.sGroupName}(${mGroupBean.iMemberCount})"
                        chat_headView.setImageURI(mGroupBean.sGroupPic)
                    }
                }
            })

            RongMentionManager.getInstance().setMentionedInputListener(object: IMentionedInputListener {
                override fun onMentionedInput(p0: Conversation.ConversationType?, p1: String?): Boolean {
                    if(IsNotNullGroupBean()){
                        startActivity<GroupUsersActivity>("bean" to mGroupBean, "onMentionedInput" to "1")
                    }
                    return true
                }
            })
        }
    }


    private fun initGift() {
        giftControl = GiftControl(this)
        giftControl?.let {
            it.setGiftLayout(ll_privatechat_gift_parent, 1)
                    .setHideMode(false)
                    .setCustormAnim(CustormAnim())
            it.setmGiftAnimationEndListener {
                Request.sendLovePoint(getLoginToken(), "${mOtherUserId}",it, 4,"").request(this, false, success = { _, data ->
                    Log.i("GiftControl", "礼物数量${it}")
                }) { code, msg ->
                    if (code == 2||code==3) {
                        var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                        mSendRedHeartEndDialog.show(supportFragmentManager, "redheartendDialog")
                    }else{
                        toast(msg)
                    }
                }
                }
            }
    }

    //连击礼物数量
    private fun addGiftNums(giftnum: Int, currentStart: Boolean = false,JumpCombo:Boolean = false) {
        if (giftnum == 0) {
            return
        } else {
            giftControl?.let {
                //这里最好不要直接new对象
                var giftModel = GiftModel()
                giftModel.setGiftId("礼物Id").setGiftName("礼物名字").setGiftCount(giftnum).setGiftPic("")
                        .setSendUserId("1234").setSendUserName("吕靓茜").setSendUserPic("").setSendGiftTime(System.currentTimeMillis())
                        .setCurrentStart(currentStart)
                if (currentStart) {
                    giftModel.setHitCombo(giftnum)
                }
                if(JumpCombo){
                    giftModel.setJumpCombo(giftnum)
                }
                it.loadGift(giftModel)
                Log.d("TAG", "onClick: " + it.getShowingGiftLayoutCount())
            }

            loveheart_privatechat.showAnimationRedHeart(null)
        }

        VibrateHelp.Vibrate(this,VibrateHelp.time50)
    }

    private fun showLongClickDialog(){
        var mSendLoveHeartDialog = SendLoveHeartDialog()
        mSendLoveHeartDialog.arguments = bundleOf("userId" to "${mOtherUserId}")
        mSendLoveHeartDialog.setDialogListener { p, s ->
            addGiftNums(p, false,true)
        }
        mSendLoveHeartDialog.show(supportFragmentManager, "sendloveheartDialog")
    }

    //检查客服是否在线
    private fun checkServiceUserOnline(iUserId:String){
        Request.checkUserOnline(getLoginToken(),iUserId).request(this,success={ _, data->
            data?.let {
                var iOnline = it.optInt("iOnline")
                if(iOnline==1){
                    if(TextUtils.equals(mOtherUserId, Const.CustomerServiceId) || TextUtils.equals(mOtherUserId, Const.CustomerServiceWomenId)){
                        var drawable = ContextCompat.getDrawable(this,R.drawable.shape_dot_offline)
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//这句一定要加
                        tv_service_time.setCompoundDrawables(drawable,null,null,null)
                        tv_service_time.text = "离线:客服工作时间上午9:00-凌晨1:30, 可给客服留言"
                    }else{
                        tv_service_time.visibility = View.GONE
                    }
                }else{
                    var sOnlineMsg = it.optString("sOnlineMsg")
                    if(TextUtils.equals(mOtherUserId, Const.CustomerServiceId) || TextUtils.equals(mOtherUserId, Const.CustomerServiceWomenId)){
                        var drawable = ContextCompat.getDrawable(this,R.drawable.shape_dot_online)
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//这句一定要加
                        tv_service_time.setCompoundDrawables(drawable,null,null,null)
                        tv_service_time.text = "人工在线"
                    }else{
                      if(iOnline==2){
                          var drawable = ContextCompat.getDrawable(this, R.drawable.shape_dot_online)
                          setLeftDrawable(drawable,tv_service_time)
                          tv_service_time.text = sOnlineMsg
                      }else if(iOnline==3){
                          tv_service_time.text = sOnlineMsg
                      }
                    }
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
            tv_apply_sendflower.visibility = View.GONE//2.11.0
            tv_openchat_apply_bottom.visibility = View.GONE
            tv_openchat_tips_title_bottom.text = getString(R.string.string_appaying_openchat)
            tv_openchat_tips_bottom.text = getString(R.string.string_give_redflower)
            tv_help_service.visibility = View.VISIBLE
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
                fragment?.hideChatInput(false)
                if(it.has("iIsFriend")){
                    iIsFriend = it.optInt("iIsFriend")
                }

//                if(iIsFriend==1){
//                    Const.UPDATE_GROUPS_STATUS = -1
//                }

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
                    tv_apply_sendflower.visibility = View.GONE //2.11.0
                    tv_apply_sendflower.text = resources.getText(R.string.string_applay_sendredflower)

                    tv_openchat_tips_title_bottom.text = getString(R.string.string_appaying_openchat)
                    tv_openchat_tips_bottom.text = getString(R.string.string_give_redflower)
                    tv_help_service.visibility = View.VISIBLE

                    fragment?.hideChatInput( true)

                    SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,true).apply()

                }else if(code == 3){//对方发出申请私聊等待我确认
                    relative_tips_bottom.visibility = View.VISIBLE
                    linear_openchat_agree_bottom.visibility = View.VISIBLE
                    tv_openchat_tips_title_bottom.visibility = View.GONE
                    tv_openchat_tips_bottom.visibility = View.GONE
                    tv_help_service.visibility = View.GONE
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
                        linear_openchat_agree_bottom.visibility = View.GONE
                        tv_help_service.visibility = View.GONE

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
                    try{
                        SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+ if(iType==2)  mTargetId else mOtherUserId,true).apply()
                        sendCount = it.optInt("iTalkCount")
                        SendMsgTotal = it.optInt("iAllTalkCount")
                        var datetime = it.optLong("dOverduetime")
                        iCanTalk = it.optInt("iCanTalk",2)
                        Log.i("chatactivity","${sendCount}消息数量appointment-----${it.optJsonObj("appointment")}")
                        var appointment = GsonHelper.getGson().fromJson(it.optJsonObj("appointment"), MyAppointment::class.java)
                        appointment?.let {
                            root_date_chat.visibility = View.VISIBLE
                            setDateChatUi(appointment,sendCount,datetime)
                            CHAT_TARGET_ID = mTargetId
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
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
        sAppointType = appointment.iAppointType
        linear_datechat_agree_bottom.visibility = View.VISIBLE
        if(sAppointType==6){
            tv_help_service_chat.visibility = View.VISIBLE
            rl_circlebar.visibility = View.VISIBLE
            tv_progress.visibility = View.VISIBLE
            ll_date_dowhat.visibility = View.GONE
            linear_datechat_agree_bottom.visibility = View.GONE
            circlebarview.setProgressNum(0.0f,0)
            if(iCanTalk==1){
                rl_date_bottom.visibility = View.GONE
                line_date_dowhat.visibility = View.GONE
            }
        }else if(sAppointType==9){
            if(appointment.iVoiceConnectType==2){
                tv_datchat_address.text = "申请者需打赏喜欢，${appointment.iOncePayLovePoint}喜欢/分钟"
            }else if(appointment.iVoiceConnectType==3){
                tv_datchat_address.text = "申请者将获得喜欢，${appointment.iOncePayLovePoint}喜欢/分钟"
            }else{
                tv_datchat_address.text = "无需打赏"
            }
            ll_date_dowhat.visibility = View.VISIBLE
            var drawable = ContextCompat.getDrawable(this, R.mipmap.liwu_feed)
            setLeftDrawable(drawable,tv_datchat_address)

            Const.mVoiceTips.setVoiceChatContent("${appointment?.sDesc}")
            Const.mVoiceTips.setVoiceChatUName("${appointment?.sAppointUserName}")
            appointment?.iVoiceConnectType?.let { Const.mVoiceTips.setVoiceChatType(it) }
            Const.mVoiceTips.setVoiceChatId("${appointment?.sAppointmentSignupId}")
        }else{
            rl_circlebar.visibility = View.GONE
            tv_progress.visibility = View.GONE
            ll_date_dowhat.visibility = View.VISIBLE
            if(appointment.iFeeType==1){
                tv_datchat_address.text = "约会费用：全包"
            }else{
                tv_datchat_address.text = "约会费用：AA"
            }

            var drawable = ContextCompat.getDrawable(this, R.mipmap.list_feiyong_icon)
            setLeftDrawable(drawable,tv_datchat_address)
        }

        var index = 1
        if(!TextUtils.equals("null","${sAppointType}")){
            index = sAppointType!!.toInt()-1
            tv_datetype_name.text = Const.dateTypes[index]
        }else{
            tv_datetype_name.text = Const.dateTypes[4]
            index = 4
        }

        if(index!= Const.dateTypesBig.size){
            var drawable = ContextCompat.getDrawable(this,Const.dateTypesBig[index])
            setLeftDrawable(drawable,tv_datetype_name)
        }

        tv_datechat_content.text = appointment.sDesc

        if(appointment.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(appointment.iAppointUserid.toString(), getLocalUserId())){
            directionDate = 1
            Const.mVoiceTips.voiceChatdirection = directionDate
            if(sAppointType==6){
                tv_date_info.text = "聊天可填充 [img src=heart_gray/]，填满后即可无限聊天"
                circlebarview.setMaxNum(Max_Angle)
                appointment.iProgress?.let { it ->
                    progressAngle = it *1.0f
                }
                var angle_nums = (Max_Angle/100)*progressAngle
                circlebarview.setProgressNum(angle_nums,0)
                tv_progress.text = "${progressAngle.toInt()}%"
            }else if(sAppointType==9){
                tv_date_info.text = "${appointment.sUserName} 申请连麦"
                tv_datechat_agree.text = "连麦"
            }else{
                tv_date_info.text = "${appointment.sUserName} 申请赴约"
            }
            //"${appointment.sUserName}申请赴约"

            tv_datechat_no.visibility = View.VISIBLE
            tv_datechat_agree.visibility = View.VISIBLE

            tv_datechat_giveup.visibility = View.GONE
            ISNOTYAODATE = 1
        }else if(appointment.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(getLocalUserId(),appointment.iUserid.toString())){
            directionDate = 2
            Const.mVoiceTips.voiceChatdirection = directionDate
            tv_datechat_agree.visibility = View.GONE
            if(sAppointType==6){
                tv_date_info.text = "聊天可填充 [img src=heart_gray/]，填满后即可无限聊天"
                circlebarview.setMaxNum(Max_Angle)
                appointment.iProgress?.let { it ->
                    progressAngle = it *1.0f
                }
                var angle_nums = (Max_Angle/100)*progressAngle
                circlebarview.setProgressNum(angle_nums,0)
                tv_progress.text = "${progressAngle.toInt()}%"
            }else if(sAppointType==9){
                tv_date_info.text = "你申请了连麦"
                tv_datechat_no.visibility = View.VISIBLE
                tv_datechat_agree.visibility = View.VISIBLE
                tv_datechat_giveup.visibility = View.GONE
                tv_datechat_no.text = "放弃"
                tv_datechat_agree.text = "连麦"
            }else{
                tv_date_info.text = "等待对方确认中…"
                tv_datechat_no.visibility = View.GONE
                tv_datechat_giveup.visibility = View.VISIBLE
                tv_help_service_chat.visibility = View.VISIBLE
            }
            ISNOTYAODATE = 2
        }

//        if(SendMsgTotal>0&&talkCount<=0){
//            fragment?.let {
//                it.doIsNotSendMsg(true,getString(R.string.string_applay_date_tips))
//            }
//        }

        rv_datechat_images.setHasFixedSize(true)
        rv_datechat_images.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        if (appointment.sAppointPic.isNullOrEmpty()) {
            rv_datechat_images.visibility = View.GONE
            iv_chat_unfold.visibility = View.GONE
        }else{
            rv_datechat_images.visibility = View.GONE
            val images = appointment.sAppointPic?.split(",")
            if (images != null) {
                mImages.addAll(images.toList())
            }
            rv_datechat_images.adapter = SelfReleaselmageAdapter(mImages,1)
        }

        setTextViewSpannable(this,"倒计时：${converTime(appointment.dOverduetime)}",3,4,tv_datechat_time,R.style.tv_datechat_time,R.style.tv_datechat_numbers)
//        setTextViewSpannable(this,"剩余消息：${talkCount}条",3,5,tv_datechat_nums,R.style.tv_datechat_time,R.style.tv_datechat_numbers)
//        if(SendMsgTotal==-1){
//            tv_datechat_nums.visibility = View.GONE
//        }
        sAppointmentSignupId = appointment.sAppointmentSignupId

        root_date_chat.addOnLayoutChangeListener(this)
    }

    fun updateSquareSignUp(sSquareSignupId:String,iStatus:String) {
        Request.updateSquareSignUp(sSquareSignupId,iStatus,0, getLoginToken()).request(this,false,success={code,data->
            if(TextUtils.equals(iStatus,"2")){
                var extra = GsonHelper.getGson().toJson(Const.mVoiceTips)
                RongD6Utils.startSingleVoiceChat(this, "${mOtherUserId}", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO, extra)
            }else{
                root_date_chat.visibility = View.GONE
                setFragmentTopMargin(0)
                getApplyStatus()
            }
        })
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

    fun updateProgress(iProgress:Int){
        Request.updateProgress(sAppointmentSignupId,iProgress).request(this,false,success={msg,data->
            data?.let {

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
        Log.i("chatActivity","token=${token}")
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
        if (checkKFService(mOtherUserId)) {
            if(iType!=3){
                fragment?.arguments = bundleOf("hideinput" to true)
            }else{
                fragment?.arguments = bundleOf("hideinput" to false)
            }
        }else{
            fragment?.arguments = bundleOf("hideinput" to false)
        }

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

            it.setOnExtensionExpandedListener(object:ConversationFragmentEx.OnExtensionExpandedListener{
                override fun onExpandedListener(flag: Boolean?) {
                    if(iv_chat_unfold.visibility == View.GONE){
                        flag?.let {
                            extendDateChatDesc(flag)
                        }
                    }
                }

                override fun onSendLoveHeart(){
                    isAuthUser {
                        addGiftNums(1,false,false)
                    }
                }

                override fun onSendLongLoveHeart() {
                    showLongClickDialog()
                }
            })
            mRongReceiveMessage = rongReceiveMessage(this)
            registerReceiver(mRongReceiveMessage, IntentFilter(Const.CHAT_MESSAGE))
        }

        val transaction = supportFragmentManager.beginTransaction()
        //xxx
        transaction.add(R.id.rong_content, fragment)
        transaction.commitAllowingStateLoss()

        fragment?.setOnShowAnnounceBarListener { announceMsg, annouceUrl ->
            if (!TextUtils.isEmpty(announceMsg)) {
                var jsonObject = JSONObject(announceMsg)
                var type = jsonObject.optString("status")
                Log.i("OnShowAnnounceBar","${announceMsg}type${type}")
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
                }else {
                    try{
                        if(jsonObject.has("iVoiceStatus")){
                            var iVoiceStatus = jsonObject.optString("iVoiceStatus")
                            if(TextUtils.equals("4",iVoiceStatus)){
                                root_date_chat.visibility = View.GONE
                                setFragmentTopMargin(0)
                                getApplyStatus()
                            }
                        }else if(jsonObject.has("iGroupStatus")){
                            //2、加入 3、踢出 4、主动退出 5、群组解散
                            var iGroupStatus = jsonObject.optString("iGroupStatus")
                            if(!TextUtils.equals("2",iGroupStatus)){
                                CheckUserInGroup()
                            }
                        }else{
                            getApplyStatus()
                        }
                    }catch (e:java.lang.Exception){
                        e.printStackTrace()
                    }
                }
            }
        }

        if (checkKFService(mOtherUserId)) {
            if(iType!=3){
                getApplyStatus()
            }
        }
    }

    fun extendDateChatDesc(isextend:Boolean){
        if(isextend){
            if(sAppointType!=6){
                ll_date_dowhat.visibility = View.VISIBLE
            }else{
                ll_date_dowhat.visibility = View.GONE
            }
            if(mImages!=null&&mImages.size>0){
                rv_datechat_images.visibility = View.VISIBLE
            }
            iv_chat_unfold.visibility = View.GONE
            tv_datechat_content.setEllipsize(null)//展开
            tv_datechat_content.setSingleLine(false)//这个方法是必须设置的，否则无法展开
        }else{
            if(sAppointType!=6){
                ll_date_dowhat.visibility = View.VISIBLE
            }
            rv_datechat_images.visibility = View.GONE
            if(mImages!=null&&mImages.size>0){
                iv_chat_unfold.visibility = View.VISIBLE
            }else{
                iv_chat_unfold.visibility = View.GONE
            }

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


    private fun setChatAngle(){
        if(SendMsgCount>3){
            Log.i("onSentchat","收到：${receiveMsgCount},发出：${SendMsgCount}")
            SendMsgCount = 1
            receiveMsgCount = 0
        }else if(receiveMsgCount >3){
            Log.i("onSentchat","收到：${receiveMsgCount},发出：${SendMsgCount}")
            SendMsgCount = 0
            receiveMsgCount = 1
        }else if(SendMsgCount==3&&receiveMsgCount == 1){
            SendMsgCount = 1
            receiveMsgCount = 0
        }else if(receiveMsgCount==3&&SendMsgCount==1){
            SendMsgCount = 0
            receiveMsgCount = 1
        }else if (SendMsgCount == 1 && receiveMsgCount == 1) {
            progressAngle = progressAngle + 1.0f
            updateProgress(progressAngle.toInt())
            Log.i("onSentchat","收到：${receiveMsgCount},发出：${SendMsgCount}")
        }else if (SendMsgCount == 1 && receiveMsgCount==2) {
            progressAngle = progressAngle + 1.0f
            updateProgress(progressAngle.toInt())
            Log.i("onSentchat","收到：${receiveMsgCount},发出：${SendMsgCount}")
            SendMsgCount = 1
//            receiveMsgCount=1
        } else if (SendMsgCount==2 && receiveMsgCount == 1) {
            progressAngle = progressAngle + 1.0f
            updateProgress(progressAngle.toInt())
            Log.i("onSentchat","收到：${receiveMsgCount},发出：${SendMsgCount}")
//            SendMsgCount = 0
            receiveMsgCount=1
        }
        Log.i("onSentchat","收到：${receiveMsgCount},发出：${SendMsgCount}")
        var Angle_nums = (Max_Angle/100)*progressAngle
        circlebarview.setProgressNum(Angle_nums,0)
        tv_progress.text = "${progressAngle.toInt()}%"
        Log.i("onSentchat", "发送了消息成功${progressAngle}")

        if(progressAngle>=100){
            updateDateStatus(sAppointmentSignupId,2)
        }
    }

    private fun getLastMessage(direction:Message.MessageDirection){
        var maxCount = 0
        maxCount=3
        var hasMessage:Boolean =false
        try{
            RongIM.getInstance().getHistoryMessages(mConversationType,mOtherUserId,-1,maxCount,object : RongIMClient.ResultCallback<List<Message>>(){
                override fun onSuccess(p0: List<Message>?) {
                    if(p0!=null&&p0.size>0){
                        for(index in 0.. (p0.size-1)){
                            if(p0[index].objectName.contains("RC:")){
                                if(p0[index].messageDirection==direction){
                                    hasMessage=true
                                    break
                                }
                            }
                        }
                    }
                    if(hasMessage){
                        progressAngle = progressAngle + 1.0f
                        updateProgress(progressAngle.toInt())
                        var Angle_nums = (Max_Angle/100)*progressAngle
                        circlebarview.setProgressNum(Angle_nums,0)
                        tv_progress.text = "${progressAngle.toInt()}%"
                        Log.i("onSentchat", "发送了消息成功${progressAngle}")

                        if(progressAngle>=100){
                            updateDateStatus(sAppointmentSignupId,2)
                        }
                    }
                }

                override fun onError(p0: RongIMClient.ErrorCode?) {

                }
            })
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }

    }

    override fun onSent(p0: Message?, p1: RongIM.SentMessageErrorCode?): Boolean {
        p0?.let {
            if (checkKFService(mOtherUserId)) {
//                if (TextUtils.equals("1", sex)) {
                if (sAppointType==6&&iCanTalk==2) {
                    if (p1 == null) {
//                        SendMsgCount = SendMsgCount +1
//                        setChatAngle()
                        getLastMessage(Message.MessageDirection.RECEIVE)
//                        checkTalkJustify()
                    }
                }
                Log.i(TAG, "${p1}用户Id${it.senderUserId}")
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
            context?.let {
                if(mChatActivity.sAppointType==6&&mChatActivity.iCanTalk==2){
//                    mChatActivity.receiveMsgCount = mChatActivity.receiveMsgCount + 1
//                    mChatActivity.setChatAngle()
                    mChatActivity.getLastMessage(Message.MessageDirection.SEND)
                }
            }
//            context?.let { setTextViewSpannable(it,"剩余消息：${mChatActivity.sendCount}条",3,5,mChatActivity.tv_datechat_nums,R.style.tv_datechat_time,R.style.tv_datechat_numbers) }
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

    /**
     * 检查用户是否在群里
     */
    private fun CheckUserInGroup(){
        Request.getGroupByGroupId("${mOtherUserId}").request(this,false,success={msg,data->
            if(data!=null){
                // var iInGroup = it.optInt("iInGroup")//1、不在  2、在
                if(data.iGroupStatus==1){
                    if(data.iInGroup==1){
                        toast("你不在此群聊中")
                        onBackPressed()
                    }
                }else{
                    if(data.iGroupStatus==2){
                        toast("群已停用")
                    }else{
                        toast("群已解散")
                    }
                    onBackPressed()
                }
            }else{
                onBackPressed()
            }
        }){code,msg->
            onBackPressed()
        }
    }
}
