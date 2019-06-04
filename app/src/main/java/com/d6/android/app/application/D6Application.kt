package com.d6.android.app.application

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.multidex.MultiDex
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.liaox.cachelib.CacheDBLib
import cn.liaox.cachelib.CacheDbManager
import cn.liaox.cachelib.bean.UserBean
import cn.liaox.cachelib.cache.NetworkCache
import com.bugtags.library.Bugtags
import com.d6.android.app.R
import com.d6.android.app.activities.SignInActivity
import com.d6.android.app.net.Request
import com.d6.android.app.net.ResultException
import com.d6.android.app.rong.RongPlugin
import com.d6.android.app.utils.*
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
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.UserInfo
import io.rong.push.RongPushClient
import io.rong.push.pushconfig.PushConfig
import org.jetbrains.anko.toast
import java.lang.Exception


/**
 *
 */
class D6Application : BaseApplication(), Application.ActivityLifecycleCallbacks, RongIMClient.OnReceiveMessageListener, RongIMClient.ConnectionStatusListener {
    private val activities = ArrayList<Activity>()

    override fun onActivityPaused(p0: Activity?) {

    }

    override fun onActivityResumed(p0: Activity?) {
    }

    override fun onActivityStarted(p0: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activities.remove(activity)
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
    }

    override fun onActivityStopped(p0: Activity?) {
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        activities.add(activity)
    }

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

        OpenInstall.init(this)
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
        if (message != null && message.conversationType == Conversation.ConversationType.PRIVATE) {
//            if(message.content is SquareMsgContent){
//                if(message.messageDirection == Message.MessageDirection.RECEIVE){
//                    val userInfo = RongUserInfoManager.getInstance().getUserInfo(message.senderUserId)
//                    (message.content as SquareMsgContent).content = "${userInfo.name}给你发送了一条动态"
//                    Log.i("onReceived","接受消息:${userInfo.name}给你发送了一条动态")
//                }else if(message.messageDirection == Message.MessageDirection.SEND){
//                    val userInfo = RongUserInfoManager.getInstance().getUserInfo(message.targetId)
//                    (message.content as SquareMsgContent).content = "sssss发送了一条动态"
//                    Log.i("onReceived","发送消息:你给${userInfo.name}发送了一条动态")
//                }
//            }
            sendBroadcast(Intent(Const.NEW_MESSAGE))
//            message?.let {
//                if (it.content is TipsMessage) {
//                    var mTipsMessage = (it.content as TipsMessage)
//                    sendBroadcast(Intent(Const.PRIVATECHAT_APPLY).putExtra("extra",mTipsMessage.extra))
//                }
//            }
        }
        return false
    }

    override fun onChanged(connectionStatus: RongIMClient.ConnectionStatusListener.ConnectionStatus?) {
        connectionStatus?.let {
            if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                quit()
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
        loginActivityIntent.setClass(this, SignInActivity::class.java)
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
}