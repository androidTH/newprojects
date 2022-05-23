package com.d6.android.app.activities

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.FilterTrendDialog
import com.d6.android.app.dialogs.LoginOutTipDialog
import com.d6.android.app.dialogs.SendRedHeartEndDialog
import com.d6.android.app.eventbus.LoveHeartMsgEvent
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.*
import com.d6.android.app.models.FollowFansVistor
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CHECK_OPEN_UNKNOW
import com.d6.android.app.utils.Const.CHECK_OPEN_UNKNOW_MSG
import com.d6.android.app.widget.popup.EasyPopup
import com.d6.android.app.widget.popup.XGravity
import com.d6.android.app.widget.popup.YGravity
import com.tbruyelle.rxpermissions2.RxPermissions
import com.umeng.message.PushAgent
import io.rong.imkit.RongIM
import io.rong.imkit.manager.IUnReadMessageObserver
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.MD5
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Group
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_privacy.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachWithIndex
import org.json.JSONObject
import java.util.*

/**
 * 主页
 */
class MainActivity : BaseActivity(), IUnReadMessageObserver,RongIM.GroupInfoProvider{

    override fun getGroupInfo(p0: String?): Group? {
        var group: Group
        try{
            group = RongUserInfoManager.getInstance().getGroupInfo(p0.toString())
            if(group==null){
                Request.findGroupDescByGroupId(getLocalUserId(), p0.toString()).request(this, false, success = { msg, data ->
                    data?.let {
                        group = Group(it.sId, it.sGroupName, Uri.parse(it.sGroupPicUrl))
                        RongIM.getInstance().refreshGroupInfoCache(group)
                    }
                })
            }
        }catch(e:java.lang.Exception){
            group = Group(p0.toString(),"匿名",Uri.parse("res:///"+R.mipmap.nimingtouxiang_small))
            RongIM.getInstance().refreshGroupInfoCache(group)
        }
        return group
    }

    private var isUpdown:Boolean = false
    private val tabTexts = arrayOf("邀约","发现","动态","聊天", "我的")//"遇见",

    private val tabImages = arrayOf(R.drawable.home_main_selector,R.drawable.home_speed_date_selector,R.drawable.home_square_selector
            ,R.drawable.home_msg_selector, R.drawable.home_mine_selector)//R.drawable.home_speed_date_selector,
    private val fragmentArray = arrayOf<Class<*>>(HomeFragment::class.java,DateFragment::class.java, SquareMainFragment::class.java,
            MessageFragment::class.java,MineFragment::class.java)//DateFragment::class.java   HomeFindFragment::class.java
    private var unReadDateMsg:Int=-1
    private var unReadMsgNum:Int=0
    private var unReadServiceMsgNum:Int=0//男游客浮动移除

    private var filterTrendDialog:FilterTrendDialog?=null
    private var token = SPUtils.instance().getString(Const.User.RONG_TOKEN)

    private var mCirclePop:EasyPopup?=null

    private val broadcast by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                    unReadMsgNum=0
//                    getSysLastOne()
                    getSquareMsg()
                    intent?.let {
                        var userId = intent.getStringExtra("senderUserId")
                        var userInfo = RongUserInfoManager.getInstance().getUserInfo(userId)
                        if(userInfo==null) {
                            Request.getUserInfoDetail("${userId}").request(context as BaseActivity, false, success = { msg, data ->
                                data?.let {
                                    val info = UserInfo("${it.accountId}", "${data.name}", Uri.parse("${data.picUrl}"))
                                    RongIM.getInstance().refreshUserInfoCache(info)
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    private val broadcasmine by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                    tabhost.currentTab = 4
                }
            }
        }
    }

    private val rongBroadcast by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                    getUnReadCount()
                }
            }
        }
    }

    private val manService by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                    if(showFloatManService()){
                        rl_service.visibility = View.GONE
                    }else{
                        rl_service.visibility = View.GONE
                    }
                }
            }
        }
    }

    private val mineBroadcast by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                    val view = tabhost.tabWidget.getChildTabViewAt(4).findViewById<View>(R.id.tv_msg_red) as TextView
                    intent?.let {
                        var showwarm = intent.getBooleanExtra("showwarm",false)
                        if(showwarm){
                            view.visibility = View.VISIBLE
                        }else{
                            view.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private val mHomeDateStateBar by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                    intent?.let {
//                        isUpdown = it.getBooleanExtra(ISUPDOWN,false)
//                        if(isUpdown){
//                            titleBar.backgroundColor = ContextCompat.getColor(context,R.color.white)
//                            tv_title.textColor = ContextCompat.getColor(context,R.color.color_black)
//                            tv_date_mydate.textColor = ContextCompat.getColor(context,R.color.color_black)
//                        }else{
//                            titleBar.backgroundColor = ContextCompat.getColor(context,R.color.color_black)
//                            tv_title.textColor = ContextCompat.getColor(context,R.color.white)
//                            tv_date_mydate.textColor = ContextCompat.getColor(context,R.color.white)
//                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        immersionBar.navigationBarColor("#FFFFFF").init()
        registerReceiver(broadcast, IntentFilter(Const.YOUMENG_MSG_NOTIFION))
        registerReceiver(broadcasmine, IntentFilter(Const.YOUMENG_MSG_NOTIFION_MINE))
        registerReceiver(rongBroadcast, IntentFilter(Const.NEW_MESSAGE))
        registerReceiver(mineBroadcast, IntentFilter(Const.MINE_MESSAGE))
//        registerReceiver(manService, IntentFilter(Const.MINE_MANSERVICE_YOUKE)) 2.5移除
//        registerReceiver(mHomeDateStateBar, IntentFilter(Const.HOMEDATE_STATEBAR))

        tabhost.setup(this, supportFragmentManager, R.id.container)
        tabhost.tabWidget.dividerDrawable = null
        tabTexts.forEachWithIndex { i, it ->
            val spec = tabhost.newTabSpec(it).setIndicator(addTab(i))
            tabhost.addTab(spec, fragmentArray[i], null)
        }

        //默认第一个标签
        tabhost.setCurrentTabByTag(tabTexts[0])
        tabhost.setOnTabChangedListener {
            titleBar.visible()
            line.visible()
            iv_right.text = ""
            tv_date_tab.visibility = View.GONE
            tv_find_tab.visibility = View.INVISIBLE
            tv_square_tab.visibility = View.INVISIBLE
            tabhost.backgroundColor = ContextCompat.getColor(this,R.color.white)
            view_line_h.visibility = View.VISIBLE
            when {
                TextUtils.equals(it, tabTexts[0]) -> {
                    tv_title.textColor = ContextCompat.getColor(this,R.color.color_333333)
                    tv_title.visible()
                    tv_create_date.visibility = View.GONE
                    tv_date_mydate.gone()
                    date_headView.gone()
                    setNoticeIsNoShow()
                    iv_right.gone()
                    tv_title1.gone()
                    tv_title.text = "邀约"
                    tv_find_tab.visibility = View.VISIBLE

                    val fragment = supportFragmentManager.findFragmentByTag(tabTexts[1])
                    if (fragment != null && fragment is DateFragment) {
                        fragment.setResetTopInfo()
                    }
                }
                TextUtils.equals(it, tabTexts[1]) -> {
//                    immersionBar.init()
//                    titleBar.backgroundColor = ContextCompat.getColor(this,R.color.white)
//                    titleBar.gone()
//                    iv_right.gone()
//                    tv_title1.gone()
//                    tv_title.text = "D6社区"
//                    tv_square_tab.visibility = View.VISIBLE
                    titleBar.gone()
                    line.gone()
                    getUserInfoUnMsg()

//                    tv_title.textColor = ContextCompat.getColor(this,R.color.color_333333)
//                    tv_title.visible()
//                    tv_create_date.visibility = View.GONE
//                    tv_date_mydate.gone()
//                    date_headView.gone()
                    setNoticeIsNoShow()
                    iv_right.gone()
                    tv_title1.gone()
//                    tv_title.text = "发现"
                    tv_square_tab.visibility = View.VISIBLE

                    val fragment = supportFragmentManager.findFragmentByTag(tabTexts[1])
                    if (fragment != null && fragment is DateFragment) {
                        fragment.getPeoples()
                    }
                }
                TextUtils.equals(it, tabTexts[2]) -> {
                    immersionBar.init()
                    titleBar.backgroundColor = ContextCompat.getColor(this,R.color.white)
                    tv_create_date.gone()
                    tv_date_mydate.gone()
                    date_headView.gone()
                    tv_title.gone()
                    iv_mydate_newnotice.gone()
                    iv_right.visible()
                    tv_title1.visible()
                    iv_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    tv_title.text = ""
                    iv_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_add_orange, 0)
                    tv_title1.text = "动态"
                    tv_date_tab.visibility = View.VISIBLE
                    getUserInfoUnMsg()
//                    tabhost.backgroundColor = ContextCompat.getColor(this,R.color.color_282828)
//                    view_line_h.visibility = View.INVISIBLE
//                    titleBar.visibility = View.GONE
//                    tv_title.textColor = ContextCompat.getColor(this,R.color.color_333333)
//                    tv_title.visible()
//                    tv_create_date.visible()
//                    tv_date_mydate.visible()
//                    date_headView.visible()
////                    setNoticeIsNoShow()
//                    iv_right.gone()
//                    tv_title1.gone()
//                    tv_title.text = "人工服务"
//                    tv_date_tab.visibility = View.VISIBLE
                    val fragment = supportFragmentManager.findFragmentByTag(tabTexts[1])
                    if (fragment != null && fragment is DateFragment) {
                        fragment.setResetTopInfo()
                    }
                }

                TextUtils.equals(it, tabTexts[3]) -> {
                    immersionBar.init()
                    titleBar.backgroundColor = ContextCompat.getColor(this,R.color.white)
                    titleBar.gone()
                    line.gone()
//                    iv_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
//                    iv_right.imageResource = R.mipmap.ic_msg_setting
                    tv_title.text = "聊天"
//                    iv_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_msg_setting, 0)
//                    iv_right.text = "发布"
                    tv_title1.text = ""
                    getUserInfoUnMsg()
                    val fragment = supportFragmentManager.findFragmentByTag(tabTexts[1])
                    if (fragment != null && fragment is DateFragment) {
                        fragment.setResetTopInfo()
                    }
                    if(unReadMsgNum==0){
                        getSquareMsg()
                    }
                }
                TextUtils.equals(it, tabTexts[4]) -> {
                    immersionBar.init()
                    titleBar.backgroundColor = ContextCompat.getColor(this,R.color.white)
                    titleBar.gone()
                    iv_right.gone()
                    tv_title1.gone()
                    tv_title.text = "我的"
                    line.gone()
                    val fragment = supportFragmentManager.findFragmentByTag(tabTexts[1])
                    if (fragment != null && fragment is DateFragment) {
                        fragment.setResetTopInfo()
                    }
                }
            }
        }

        tv_title1.setOnClickListener {
//            filterTrendDialog = FilterTrendDialog()
//            filterTrendDialog?.setDialogListener { p, s ->
//                setTrendTitle(p)
//                val fragment = supportFragmentManager.findFragmentByTag(tabTexts[2])
//                if (fragment != null && fragment is SquareMainFragment) {
//                    fragment.filter(p)
//                }
//            }
//            filterTrendDialog?.show(supportFragmentManager, "ftd")
        }

        tv_create_date.visibility = View.GONE

        tv_create_date.setOnClickListener {
            when (tabhost.currentTab) {
                0 -> {
//                    var view = it
//                    mCirclePop?.let {
//                        it.showAtAnchorView(view, YGravity.BELOW, XGravity.ALIGN_RIGHT, -23,-15)
//                    }

                    isCheckOnLineAuthUser(this, getLocalUserId()){
                        startActivityForResult<PublishFindDateActivity>(10)
                    }
                }
            }
        }

//        date_headView.hierarchy = getHierarchy()

        date_headView.setOnClickListener {
            getAuthState()
        }

        tv_date_mydate.setOnClickListener {
            getAuthState()
        }

        tv_find_tab.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentByTag(tabTexts[0])
            if (fragment != null && fragment is HomeFragment) {
                fragment.refresh(getUserSex(),-1)
            }
        }

        tv_square_tab.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentByTag(tabTexts[1])
            if (fragment != null && fragment is HomeFindFragment) {
                fragment.refresh()
            }
        }

        tv_date_tab.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentByTag(tabTexts[2])
            if (fragment != null && fragment is SquareMainFragment) {
                fragment.refresh()
            }
        }

        iv_right.setOnClickListener {
            when (tabhost.currentTab) {
                0 -> {
                    var view = it;
                    mCirclePop?.let {
                        it.showAtAnchorView(view, YGravity.BELOW, XGravity.ALIGN_RIGHT, -23,-15)
                    }
                }
                2 -> {
                    if (!isFastClick()) {
                        isCheckOnLineAuthUser(this, getLocalUserId()) {
                            startActivityForResult<ReleaseNewTrendsActivity>(11)
                        }
                    }
                }
            }
        }

        rl_service.setOnClickListener {
//           pushCustomerMessage(this, getLocalUserId(),5,"",next = {
//                chatService(this)
//            })
            chatService(this)
        }

        //默认标题
        tv_title.text = "邀约"
        tv_title.textColor = ContextCompat.getColor(this,R.color.color_333333)
        titleBar.visibility = View.VISIBLE

        if (token.isNotEmpty()) {
            judgeDataB()
            val uid = SPUtils.instance().getString(Const.User.USER_ID)
            val nick = SPUtils.instance().getString(Const.User.USER_NICK)
            val head = SPUtils.instance().getString(Const.User.USER_HEAD)
            RongIM.getInstance().setCurrentUserInfo(UserInfo(uid, nick, Uri.parse(head)))

            RongIM.connect(token, object : RongIMClient.ConnectCallback() {
                override fun onSuccess(p0: String?) {
                }

                override fun onDatabaseOpened(p0: RongIMClient.DatabaseOpenStatus?) {
                }

                override fun onError(p0: RongIMClient.ConnectionErrorCode?) {

                }
            })
        }
        PushAgent.getInstance(this.applicationContext).addAlias(getLocalUserId(), "D6") { _, _ -> }
        Request.updateDeviceType(getLocalUserId()).request(this, false) { _, _ -> }

        //3.1注释
//        val head = SPUtils.instance().getString(Const.User.USER_HEAD)
//        date_headView.setImageURI(head)
//

//        UnReadMessageCountChangedObserver()

        diyUpdate(this,"")

        getUserQueryAnonymous()

        getPermission()

        RongIM.setGroupInfoProvider(this,true)

        AppUtils.setRealHWRatio(this)

        initPopup()

        EventBus.getDefault().register(this)

        date_headView.postDelayed(object:Runnable{
            override fun run() {
                getUserInfo()
            }
        },300)

        RongIM.setUserInfoProvider(object:RongIM.UserInfoProvider{
            override fun getUserInfo(p0: String?): UserInfo {
                return UserInfo(getLocalUserId(), getLocalUserName(), Uri.parse(getLocalUserHeadPic()))
            }
        },true)

//        Log.i("MainActivityfffffff","${getLoginToken()}")

//        NetStateUtil.connectingAddress(this)
    }

    private fun initPopup(){
        mCirclePop = EasyPopup.create()
                .setContentView(this, R.layout.popup_release_layout)
                .setAnimationStyle(R.style.RightTop2PopAnim)
                .setOnViewListener { view, popup ->
                    view.findViewById<TextView>(R.id.tv_createvoicechat).setOnClickListener {
                        isCheckOnLineAuthUser(this, getLocalUserId()) {
                            startActivityForResult<VoiceChatCreateActivity>(1)
                        }
                        mCirclePop!!.dismiss()
                    }

                    view.findViewById<TextView>(R.id.tv_create_date).setOnClickListener {
                        isCheckOnLineAuthUser(this, getLocalUserId()){
                            startActivityForResult<PublishFindDateActivity>(10)
                        }
                        mCirclePop!!.dismiss()
                    }
                }
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .apply()
    }

    fun judgeDataB() {
        Request.findMyDatingList(SPUtils.instance().getString(Const.User.USER_ID), 1).request(this) { _, data ->
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                SPUtils.instance().put(Const.User.IASKSOMEONEELSE,false).apply()
            }else{
                SPUtils.instance().put(Const.User.IASKSOMEONEELSE,true).apply()
            }
        }

        Request.findDatingMeList(SPUtils.instance().getString(Const.User.USER_ID),1).request(this){_,data->
            if (data?.list?.results == null || data?.list?.results?.isEmpty()) {
                SPUtils.instance().put(Const.User.SOMEONE_ELSE_MAKES_AN_APPOINTMENT_WITH_ME,false).apply()
            }else{
                SPUtils.instance().put(Const.User.SOMEONE_ELSE_MAKES_AN_APPOINTMENT_WITH_ME,true).apply()
            }
        }
    }

    private fun getAuthState() {
//        startActivity<MyDateListActivity>()
    }

    override fun onResume() {
        super.onResume()
        if(tabhost.currentTab==0){
//            myDateUnMsg()
        }
        unReadMsgNum = 0  // 注释
        getUserInfoUnMsg()
        reconnect()
//        Log.i("MainActivity","IMEI=${MD5.encrypt(getSIMEI(this),true)},androidId=${getAndroidID(this)},MD5=${MD5.encrypt(getSIMEI(this).toLowerCase())}")
//        Log.i("MainActivity","UUID=${UUID.randomUUID()},MD5=${MD5.encrypt(UUID.randomUUID().toString().toLowerCase())}")
    }

    private fun reconnect() {
        if(RongIM.getInstance().currentConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED){
            getUnReadCount()
        }else{
            RongIM.connect(token, object : RongIMClient.ConnectCallback() {
                override fun onDatabaseOpened(p0: RongIMClient.DatabaseOpenStatus?) {
                }

                override fun onSuccess(s: String) {
                    getUnReadCount()
                }

                override fun onError(p0: RongIMClient.ConnectionErrorCode?) {
                }
            })
        }
    }

    fun setBottomBarNormal(tabIndex:Int){
        var  view = tabhost.tabWidget.getChildTabViewAt(tabIndex)
        val textView = view.find<TextView>(R.id.img)
        if(tabIndex == 2){
            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabImages[2], 0, 0)
        }else if(tabIndex == 1){
            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabImages[1], 0, 0)
        }else if(tabIndex == 0){
            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabImages[0], 0, 0)
        }
    }

    /**
     * 保存用户信息
     */
    private fun getUserInfo() {
        Request.getUserInfo("", getLocalUserId()).request(this, false, success = { _, data ->
            data?.let {
                SPUtils.instance().put(Const.USERINFO, GsonHelper.getGson().toJson(it)).apply()
                SPUtils.instance().put(Const.User.USER_DATACOMPLETION, it.iDatacompletion).apply()
                saveUserInfo(it)
                if (!it.wxid.isNullOrEmpty()) {
                    if (it.sUnionid.isNullOrEmpty()) {
                        val mLoginOutTipDialog = LoginOutTipDialog()
                        mLoginOutTipDialog.show(supportFragmentManager, "action")
                    }
                }

                if (it.isShowGift == 0) {
                    SPUtils.instance().put("IsShowGift" + getLocalUserId(), false).apply()
                } else {
                    SPUtils.instance().put("IsShowGift" + getLocalUserId(), true).apply()
                }

                if (it.iIsFind == 1) {
                    SPUtils.instance().put("IsFind" + getLocalUserId(), false).apply()
//                    sw_card_off.isChecked = true
                } else {
                    SPUtils.instance().put("IsFind" + getLocalUserId(), true).apply()
//                    sw_card_off.isChecked = false
                }
                if (it.iListSetting == 1) {
                    SPUtils.instance().put("ListSetting" + getLocalUserId(), false).apply()
                } else {
                    SPUtils.instance().put("ListSetting" + getLocalUserId(), true).apply()
                }

                if (it.iSendPointShow == 1) {
                    SPUtils.instance().put("iSendPointShow" + getLocalUserId(), false).apply()
                } else {
                    SPUtils.instance().put("iSendPointShow" + getLocalUserId(), true).apply()
                }

                if (it.iPhonePrivacy == 1) {
                    SPUtils.instance().put("iPhonePrivacy" + getLocalUserId(), true).apply()
                } else {
                    SPUtils.instance().put("iPhonePrivacy" + getLocalUserId(), false).apply()
                }
                /*2.5移除
                if(showFloatManService()){
                    rl_service.visibility = View.GONE
                }else{
                    rl_service.visibility = View.GONE
                }*/
            }
        })
    }

//    private fun updateDateNotice(){
//        var fragment = supportFragmentManager.findFragmentByTag(tabTexts[1])
//        if (fragment != null && fragment is DateFragment) {
//            fragment.showNotice(false)
//        }
//    }

    private fun getUserQueryAnonymous(){
        Request.getUserQueryAnonymous(getLoginToken()).request(this,false,success={msg,jsonobject->
           SPUtils.instance().put(CHECK_OPEN_UNKNOW,"open").apply()
        }){code,msg->
            if(code == 2){
                val jsonObject = JSONObject(msg)
                SPUtils.instance().put(CHECK_OPEN_UNKNOW_MSG,jsonObject.optString("sAnonymousDesc")).apply()
            }
            SPUtils.instance().put(CHECK_OPEN_UNKNOW,"close").apply()
        }
    }

    private fun UnReadMessageCountChangedObserver(){
//        var ConversationTypes = {Conversation.ConversationType.PRIVATE;Conversation.ConversationType.SYSTEM;Conversation.ConversationType.PUBLIC_SERVICE}
        RongIM.getInstance().addUnReadMessageCountChangedObserver(this,Conversation.ConversationType.PRIVATE)
    }

    private fun getUnReadCount() {
        RongIM.getInstance().getUnreadCount(arrayOf(Conversation.ConversationType.PRIVATE,Conversation.ConversationType.GROUP),false,object : RongIMClient.ResultCallback<Int>() {
            override fun onSuccess(p0: Int?) {
                p0?.let {
                    val fragment = supportFragmentManager.findFragmentByTag(tabTexts[3])
                    if (fragment != null && fragment is MessageFragment) {
                        fragment.getChatMsg()
                    }
                    val view1 = tabhost.tabWidget.getChildTabViewAt(3)
                    if (view1 != null) {
                        unReadMsgNum = 0

                        unReadMsgNum = unReadMsgNum + it

                        Log.i("messagesssssss","${unReadMsgNum}显示getUnreadCount")

                          //2.5移除
//                        if(showFloatManService()){
//                            getServiceUnReadMsg()
//                        }else{
//                            unReadServiceMsgNum = 0
//                            getSysLastOne()
//                        }

//                        getSysLastOne()
                        getSquareMsg()
                    }
                }
            }

            override fun onError(p0: RongIMClient.ErrorCode?) {

            }

        })
    }

    private fun myDateUnMsg(){
        Request.getUnreadAppointmentCount(SPUtils.instance().getString(Const.User.USER_ID)).request(this, success = { msg, data ->
            if (data != null) {
                data.unreadCount?.let {
                    unReadDateMsg = it
                    setNoticeIsNoShow()
                }
            }
        })
    }

    private fun getUserInfoUnMsg(){
        Request.getUserFollowAndFansandVistor(getLocalUserId()).request(this,success = { s:String?, data: FollowFansVistor?->
            data?.let {
                val view = tabhost.tabWidget.getChildTabViewAt(4).findViewById<View>(R.id.tv_msg_red) as TextView
                var mFollowFansVistor = it
                Request.getUserInfo(getLocalUserId(), getLocalUserId()).request(this,false,success= { msg, data ->
                    data?.let {
                        if (it.iReceiveNewLovePoint!!>0||mFollowFansVistor.iVistorCount!!>0) {
//                    view.visibility = View.VISIBLE
                            val fragment = supportFragmentManager.findFragmentByTag(tabTexts[4])
                            if (fragment != null && fragment is MineFragment) {
                                fragment.showLikeWarm(true,it.iReceiveNewLovePoint, mFollowFansVistor.iPointNew!!.toInt(), mFollowFansVistor.iVistorCount!!.toInt())
                            }
                        } else {
                            view.visibility = View.GONE
                        }
                    }
                })
            }
        })
    }

    private fun setNoticeIsNoShow(){
        val view = tabhost.tabWidget.getChildTabViewAt(0).findViewById<View>(R.id.tv_msg_red) as TextView
        if(unReadDateMsg > 0){
            iv_mydate_newnotice.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
        }else{
            iv_mydate_newnotice.visibility = View.GONE
            view.visibility = View.GONE
        }
    }

    fun setTrendTitle(p: Int) {
         when (p) {
            0 -> isCheckOnLineAuthUser(this, getLocalUserId()) {
                startActivityForResult<ReleaseNewTrendsActivity>(11)
            }
            1 -> isCheckOnLineAuthUser(this, getLocalUserId()){
                startActivityForResult<PublishFindDateActivity>(10)
            }
            else -> "全部动态"
        }
    }

    private fun getSysLastOne() {
        Request.getSystemMessages(getLocalUserId(), 1, pageSize = 1).request(this, false, success = { _, data ->
            val view = (tabhost.tabWidget.getChildTabViewAt(3).findViewById<View>(R.id.tv_msg_red) as TextView)
            if (data?.list?.results == null || data.list?.results?.isEmpty()) {
                //无数据
                getSquareMsg()
            } else {
                val fragment = supportFragmentManager.findFragmentByTag(tabTexts[3])
                if (fragment != null && fragment is MessageFragment) {
                    fragment.setSysMsg(data)
                }
                unReadMsgNum  =unReadMsgNum + data.count!!.toInt()
                Log.i("messagesssssss","${unReadMsgNum}显示SystemMessages${data.count}")
                getSquareMsg()
//                if ((data.count ?: 0) > 0) {
//                    view?.visible()
//                } else {
//                    getSquareMsg()
//                }
            }

        }) { _, _ ->
            getSquareMsg()
        }
    }

    private fun getSquareMsg() {
        Request.getNewSquareMessages(getLocalUserId(), 1, pageSize = 1).request(this, false, success = { _, data ->
            val view = tabhost.tabWidget.getChildTabViewAt(3).findViewById<View>(R.id.tv_msg_count) as TextView
            Log.i("messagesssssss","${unReadMsgNum}显示")
            if (data?.list?.results == null || data?.list?.results?.isEmpty()!!) {
                //无数据
               // unReadMsgNum = unReadMsgNum - unReadServiceMsgNum//2.5移除
                if(unReadMsgNum > 0){
                  if(unReadMsgNum>=99){
                        view.text = "99+"
                    }else{
                        view.text = "${unReadMsgNum}"
                    }
//                    unReadMsgNum = 0 // 注释
                    view?.visible()
                }else{
                    view?.gone()
                }
            } else {
                val fragment = supportFragmentManager.findFragmentByTag(tabTexts[3])
                if (fragment != null && fragment is MessageFragment) {
                    fragment.setSquareMsg(data)
                }
                Log.i("messagesssssss","SquareMsg${data.count}显示")
//                unReadMsgNum = unReadMsgNum + data.count!!.toInt() - unReadServiceMsgNum//2.5移除
                unReadMsgNum = unReadMsgNum + data.count
                if(unReadMsgNum>0){
                    if(unReadMsgNum>=99){
                        view.text = "99+"
                    }else{
                        view.text = "${unReadMsgNum}"
                    }
                    view?.visible()
                }else{
                    view?.gone()
                }
            }
        }) { _, _ ->

        }
    }

    /**
     * 设置tab
     */
    private fun addTab(i: Int): View {
        //取得布局实例
        val view = View.inflate(this@MainActivity, R.layout.tab_home_bottom_content, null)
        //取得布局对象
        val textView = view.find<TextView>(R.id.img)
        //设置图标
        textView.setCompoundDrawablesWithIntrinsicBounds(0, tabImages[i], 0, 0)
        //设置标题
        textView.text = tabTexts[i]
        return view
    }

    fun changeTab(index: Int) {
        tabhost.currentTab = index
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(loveHeartMsgEvent: LoveHeartMsgEvent){
        Request.sendLovePoint(getLoginToken(), "${loveHeartMsgEvent.getmTargetId()}",loveHeartMsgEvent.count, 4,"").request(this, false, success = { _, data ->
            Log.i("GiftControl", "礼物数量${loveHeartMsgEvent.count}")
        }) { code, msg ->
            if (code == 2||code==3) {
                var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                mSendRedHeartEndDialog.show(supportFragmentManager, "redheartendDialog")
            }else{
                toast(msg)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {//筛选
//                val area = data.getStringExtra("area")
//                val areaType = data.getIntExtra("areaType", -1)
//                val typeIds = data.getStringExtra("typeIds")
//                val vipIds = data.getStringExtra("vipIds")
//                val fragment = supportFragmentManager.findFragmentByTag("速约")
//                if (fragment != null && fragment is SpeedDateFragment) {
//                    fragment.refresh(area, areaType, typeIds, vipIds)
//                }
            } else if (requestCode == 1||requestCode==10) {//||requestCode==10
                val fragment = supportFragmentManager.findFragmentByTag(tabTexts[0])
                if (fragment != null && fragment is HomeFragment) {
                    fragment.refresh("-1",0)
                }
            }else if(requestCode == 11){
                val fragment = supportFragmentManager.findFragmentByTag(tabTexts[2])
                if (fragment != null && fragment is SquareMainFragment) {
                    fragment.refresh()
                }
            }
        }
    }

    override fun onCountChanged(p0: Int) {
//        val view1 = tabhost.tabWidget.getChildTabViewAt(3)
//        val fragment = supportFragmentManager.findFragmentByTag(tabTexts[3])
//        if (fragment != null && fragment is MessageFragment) {
//            fragment.getChatMsg()
//        }
//        unReadMsgNum = p0
//        if(p0>0){
//            if (view1 != null) {
//                val view = view1.find<View>(R.id.tv_msg_count)
//                view?.visible()
//            }
//        }else{
//            if (view1 != null) {
//                val view = view1.find<View>(R.id.tv_msg_count)
//                view?.gone()
//            }
//        }
        Log.i("messagesssssss","onCountChanged")
    }

    private fun getServiceUnReadMsg(){
        RongIM.getInstance().getUnreadCount(Conversation.ConversationType.PRIVATE,Const.CustomerServiceId,object:RongIMClient.ResultCallback<Int>(){
            override fun onSuccess(p0: Int?) {
                p0?.let {
                    if(it >0 ){
                        tv_service_count.visibility = View.VISIBLE
                        tv_service_count.text = "${it}"
                    }else{
                        tv_service_count.visibility = View.GONE
                    }
                    unReadServiceMsgNum = it
                    getSysLastOne()
                }
            }

            override fun onError(p0: RongIMClient.ErrorCode?) {

            }
        })
    }

    private var mExitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                toast("再按一次返回桌面")
                mExitTime = System.currentTimeMillis()
            } else {
//                moveTaskToBack(true)
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(broadcast)
            unregisterReceiver(rongBroadcast)
            unregisterReceiver(mineBroadcast)
        } catch (e: Exception) {

        }
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 权限检查
     */
    fun getPermission() {
        RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 12) {
            if (TextUtils.equals(permissions[0],Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                alertDialog( "请注意", "本应用需要使用访问本地存储权限，否则无法正常使用！", false, "确定", "取消", DialogInterface.OnClickListener { _, _ -> finish() }, DialogInterface.OnClickListener { _, _ -> finish() })
                return
            }
        }
    }
}
