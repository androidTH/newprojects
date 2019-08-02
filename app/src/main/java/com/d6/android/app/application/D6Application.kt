package com.d6.android.app.application

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.multidex.MultiDex
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import cn.liaox.cachelib.CacheDBLib
import cn.liaox.cachelib.CacheDbManager
import cn.liaox.cachelib.bean.UserBean
import cn.liaox.cachelib.cache.NetworkCache
import com.bugtags.library.Bugtags
import com.d6.android.app.R
import com.d6.android.app.activities.SplashActivity
import com.d6.android.app.net.Request
import com.d6.android.app.net.ResultException
import com.d6.android.app.rong.RongPlugin
import com.d6.android.app.rong.bean.*
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.APPLAY_CONVERTION_ISTOP
import com.d6.android.app.utils.Const.CONVERSATION_APPLAY_DATE_TYPE
import com.d6.android.app.utils.Const.CONVERSATION_APPLAY_PRIVATE_TYPE
import com.d6.android.app.utils.RongUtils.getConnectCallback
import com.facebook.drawee.view.SimpleDraweeView
import com.fm.openinstall.OpenInstall
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.MsgConstant
import com.umeng.message.PushAgent
import com.umeng.socialize.PlatformConfig
import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber
import io.rong.imkit.RongIM
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imkit.utils.SystemUtils
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.*
import io.rong.message.TextMessage
import io.rong.push.RongPushClient
import io.rong.push.pushconfig.PushConfig
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 *
 */
class D6Application : BaseApplication(), RongIMClient.OnReceiveMessageListener, RongIMClient.ConnectionStatusListener{

    companion object {
        var isChooseLoginPage = false
        var systemTime = 0L
    }

    override fun getSPName() = "com.d6.android.data"

    override fun onCreate() {
        super.onCreate()
        disableAPIDialog()
//        UMConfigure.setLogEnabled(true)
//        Bugout.init(this, "ed3b07b4f9f09c390b7dd863e153a276", "d6")
        UMConfigure.init(this, Const.UMENG_APPKEY, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, Const.UMENG_MESSAGE_SECRET)
        PlatformConfig.setWeixin("wx43d13a711f68131c", "00537b54033cd022ceda1894bae5ebf5")
//        Config.DEBUG = true
        val mPushAgent = PushAgent.getInstance(this)
        mPushAgent.notificationPlaySound = MsgConstant.NOTIFICATION_PLAY_SERVER
        mPushAgent.setMessageHandler(CustomNotification())
        mPushAgent.setNotificationClickHandler(CustomNotificationHandler())
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(object : IUmengRegisterCallback {

            override fun onSuccess(deviceToken: String) {
                SPUtils.instance().put(Const.User.DEVICETOKEN, deviceToken)
                //注册成功会返回device token ArblO5X82GPZtR8dvWGOMXlPXpdJsOcOdTAoti6gm_ew
            }

            override fun onFailure(s: String, s1: String) {
            }
        })

        if (applicationInfo.packageName == getCurProcessName(applicationContext)) {
//            RongPushClient.registerHWPush(this);
            var config = PushConfig.Builder().enableMiPush(Const.XIAOMIAPPID, Const.XIAOMIAPPKEY).build()
            RongPushClient.setPushConfig(config)

            RongIM.init(this)
            RongPlugin.init(this)
            RongIM.getInstance().setMessageAttachedUserInfo(true)
//            RongIMClient.getInstance().setPushContentShowStatus(false, object : RongIMClient.OperationCallback() {
//                override fun onSuccess() {
//
//                }
//
//                override fun onError(errorCode: RongIMClient.ErrorCode) {
//
//                }
//            })
            initCacheLib()
            setInputProvider()
        }

        //在这里初始化
        Bugtags.start(Const.BUGTAGS_KEY, this, Bugtags.BTGInvocationEventBubble)

        if(isMainProcess()){
            OpenInstall.init(this)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun getCurProcessName(context: Context): String? {
        val pid = android.os.Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.runningAppProcesses
                .firstOrNull { it.pid == pid }
                ?.processName
    }

    private fun setInputProvider() {
        RongIM.setOnReceiveMessageListener(this)
        RongIM.setConnectionStatusListener(this)
        RongIM.setConversationClickListener(RongIMConversationClickListener())
    }

    private fun initCacheLib() {
        CacheDBLib.init(this)

        RongUtils.setUserProvider(object : RongUtils.UserProvider {
            private val views = ArrayList<View?>()
            private val map = HashMap<String, Boolean>()
            override fun getUser(userId: String, textView: TextView?, imageView: ImageView?) {
                views.add(textView)
                views.add(imageView)
                val isLoad = map[userId] ?: false
                if (map.containsKey(userId) && !isLoad) {
                    return
                }
                if (!map.containsKey(userId)) {
                    map.put(userId, false)
                }

                CacheDbManager.getInstance().load(userId, UserBean::class.java, object : NetworkCache<UserBean>() {
                    override fun get(key: String, cls: Class<UserBean>): Flowable<UserBean> {
                        return Request.getUserInfoDetail(key).ioScheduler().flatMap {
                            val data = it.data
                            if (it.res == 1) {
                                val userBean = UserBean()
                                userBean.userId = data?.accountId
                                val nick = data?.name ?: ""
                                userBean.nickName = nick
                                val url = data?.picUrl ?: ""
                                userBean.headUrl = url
                                userBean.status = 1//必须设置有效性>0的值
                                val info = UserInfo(userId, nick, Uri.parse(url))
                                RongIM.getInstance().refreshUserInfoCache(info)
                                Flowable.just(userBean)
                            } else {
                                Flowable.error(ResultException(it.resMsg))
                            }
                        }
                    }
                }).defaultScheduler().subscribe(object : DisposableSubscriber<UserBean>() {
                    override fun onError(t: Throwable?) {
                        t?.printStackTrace()
                        map.put(userId, true)
                    }

                    override fun onNext(t: UserBean?) {
                        if (map.containsKey(userId)) {
                            map.remove(userId)
                        }
                        val removes = ArrayList<View?>()
                        for (view in views) {
                            if (checkView(view, userId)) {
                                if (view is TextView) {
                                    view.text = t?.nickName
                                }
                                if (view is SimpleDraweeView) {
                                    view.setImageURI(t?.headUrl)
                                }
                                removes.add(view)
                            }
                        }
                        views.removeAll(removes)
                    }

                    override fun onComplete() {

                    }
                })
            }
        })

    }


    private fun checkView(view: View?, key: String): Boolean {
        return view?.getTag(R.id.view_tag) != null && view.getTag(R.id.view_tag) is String && TextUtils.equals(view.getTag(R.id.view_tag).toString(), key)
    }

    override fun onReceived(message: Message?, p1: Int): Boolean {
        if (message != null &&(message.conversationType == Conversation.ConversationType.PRIVATE||message.conversationType==Conversation.ConversationType.GROUP)) {
            sendBroadcast(Intent(Const.NEW_MESSAGE))
        }

        //“加微信”检测（检测到文本中有连续6位及以上是数字或字母的消息）
        if(message!=null&&(message.conversationType == Conversation.ConversationType.PRIVATE||message.conversationType==Conversation.ConversationType.GROUP)){
            if(message.content is CustomMessage){
                var flag =  SPUtils.instance().getBoolean(APPLAY_CONVERTION_ISTOP+ getLocalUserId()+"-"+message.targetId,false)
                if(flag){
                    if(message.messageDirection== Message.MessageDirection.RECEIVE){
                        RongUtils.setConversationTop(this,message.conversationType,message.targetId,true)
                    }
                }
                //“加微信”检测（检测到文本中有连续6位及以上是数字或字母的消息）
            }else if(message.content is TipsMessage){
                if(removeKFService(message.targetId)){
                    var tipsMessage = message.content as TipsMessage
                    var jsonObject = JSONObject(tipsMessage.extra)
                    var type = jsonObject.optString("status")
                    if(TextUtils.equals("1",type)){
                        SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+message.targetId,true).apply()
                    }else if(TextUtils.equals("3",type)){
                        SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+message.targetId,false).apply()
                    }else if(TextUtils.equals("4",type)){
                        RongUtils.setConversationTop(this,message.conversationType,message.targetId,false)
                    }else if(TextUtils.equals("5",type)){//约会 同意
                        SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+message.targetId,true).apply()
                        SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+message.targetId,false).apply()
                    }else if(TextUtils.equals("6",type)){//约会 拒绝
                        SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+ message.targetId,false).apply()
                    }else if(TextUtils.equals("7",type)){//约会 取消
                        SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+ message.targetId,false).apply()
                    }else if(TextUtils.equals("8",type)){//约会 过期
                        SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+ message.targetId,false).apply()
                    }else if(TextUtils.equals("9",type)){//约会 申请
                        SPUtils.instance().put(CONVERSATION_APPLAY_DATE_TYPE + getLocalUserId()+"-"+ message.targetId,true).apply()
                        SPUtils.instance().put(CONVERSATION_APPLAY_PRIVATE_TYPE + getLocalUserId()+"-"+message.targetId,false).apply()
                    }
                    Log.i("ffffffffff","${tipsMessage.content}type${type}")
                }
            }
        }

        if(message!=null&&(message.conversationType == Conversation.ConversationType.PRIVATE||message.conversationType==Conversation.ConversationType.GROUP)){
            if (message.content is TextMessage) {
                var txtMessage = message.content as TextMessage
                if (checkJoinWx(txtMessage.content)) {
                    sendOutgoingSystemMessage(getString(R.string.string_system_tips03), "1", message)
                }
            }
        }

        if(SystemUtils.isInBackground(this)){
            if(message==null){
                return false
            }
            var notification:Notification
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var builder:Notification.Builder?
            if (Build.VERSION.SDK_INT >= 26) {
                val channel = NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH)
                manager?.createNotificationChannel(channel)
                builder= Notification.Builder(this, channel.id)
            }else{
                builder= Notification.Builder(this)
            }
            val myNotificationView = RemoteViews(getPackageName(), R.layout.notification_view)
            var title = ""
            if(message.conversationType == Conversation.ConversationType.PRIVATE){
                val userInfo = RongUserInfoManager.getInstance().getUserInfo(message.senderUserId)
                if(userInfo!=null){
                    title = userInfo.name
                    myNotificationView.setTextViewText(R.id.notification_title, userInfo.name)
                }else{
                    myNotificationView.setTextViewText(R.id.notification_title, "D6社区")
                }
            }else if(message.conversationType==Conversation.ConversationType.GROUP){
                myNotificationView.setTextViewText(R.id.notification_title,"匿名")
            }

            myNotificationView.setTextViewText(R.id.notification_text,getMessageContent(message.content))

            val uri = Uri.parse("rong://" + getApplicationInfo().processName).buildUpon().appendPath("conversation").appendPath(message.conversationType.getName().toLowerCase(Locale.US)).
                    appendQueryParameter("targetId", message.targetId).appendQueryParameter("title", title).build()
            val intent = Intent("android.intent.action.VIEW", uri)
            var pendingIntent = PendingIntent.getActivity(
                                           this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
            myNotificationView.setTextViewText(R.id.push_current_time, getTimeHaveHour(message!!.receivedTime))
            myNotificationView.setImageViewBitmap(R.id.notification_large_icon, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            builder.setContent(myNotificationView)
            builder.setAutoCancel(true)
            builder.setContentIntent(pendingIntent)
            notification = builder.build()
            manager.notify(1,notification)
        }
        return true
    }

    override fun onChanged(connectionStatus: RongIMClient.ConnectionStatusListener.ConnectionStatus?) {
        connectionStatus?.let {
            if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                quit()
            }else if(connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.TOKEN_INCORRECT){
                val token = SPUtils.instance().getString(Const.User.RONG_TOKEN)
                if(!TextUtils.isEmpty(token)){
                    RongIM.connect(token,getConnectCallback())
                }
            }
        }
    }

    private fun quit() {
        Handler(Looper.getMainLooper()).post({
            toast("您的帐号在别的设备上登录，请妥善保管好自己的密码！")
        })

        D6Application.isChooseLoginPage = true
        SPUtils.instance().remove(Const.User.USER_ID)
                .remove(Const.User.IS_LOGIN)
                .remove(Const.User.RONG_TOKEN)
                .remove(Const.User.USER_TOKEN)
                .remove(Const.User.SLOGINTOKEN)
                .apply()
        val loginActivityIntent = Intent()
        loginActivityIntent.setClass(this, SplashActivity::class.java)
        loginActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(loginActivityIntent)
    }

    /**
     * 去除安卓9.0系统上弹窗
     */
    private fun disableAPIDialog(){
        if (Build.VERSION.SDK_INT < 28)return
        try {
            var clazz = Class.forName("android.app.ActivityThread")
            var currentActivityThread = clazz.getDeclaredMethod("currentActivityThread")
            currentActivityThread.setAccessible(true)
            var activityThread = currentActivityThread.invoke(null)
            var mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown")
            mHiddenApiWarningShown.setAccessible(true)
            mHiddenApiWarningShown.setBoolean(activityThread, true)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    private fun getMessageContent(content:MessageContent):String{
        if(content is TextMessage){
            var contactNotificationMessage = content
            return contactNotificationMessage.content
        }else if(content is SquareMsgContent){
            var contactNotificationMessage = content
            return contactNotificationMessage.content
        }else if(content is AppointmentMsgContent){
            var contactNotificationMessage = content
            return contactNotificationMessage.content
        }else if(content is BusinessCardMMsgContent){
            var contactNotificationMessage = content
            return contactNotificationMessage.content
        }else if(content is BusinessCardFMsgContent){
            var contactNotificationMessage = content
            return contactNotificationMessage.content
        }else if(content is TipsMessage){
            var contactNotificationMessage = content
            return  contactNotificationMessage.content
        }else if(content is CommentMsgContent){
            var contactNotificationMessage = content
            return contactNotificationMessage.content
        }else if(content is LookDateMsgContent){
            var contactNotificationMessage = content
            return contactNotificationMessage.content
        }else if(content is SpeedDateMsgContent) {
            var contactNotificationMessage = content
            return contactNotificationMessage.content
        }
        return "您收到了一条消息"
    }

    fun isMainProcess(): Boolean {
        val pid = android.os.Process.myPid()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in activityManager.runningAppProcesses) {
            if (appProcess.pid === pid) {
                return applicationInfo.packageName == appProcess.processName
            }
        }
        return false
    }
}