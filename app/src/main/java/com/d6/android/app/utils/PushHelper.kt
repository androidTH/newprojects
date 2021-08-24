package com.d6.android.app.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.d6.android.app.utils.Const.UMENG_APPKEY
import com.d6.android.app.utils.Const.UMENG_MESSAGE_SECRET
import com.taobao.accs.ACCSClient
import com.taobao.accs.AccsClientConfig
import com.taobao.agoo.TaobaoRegister
import com.uc.crashsdk.export.CrashApi
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.utils.UMUtils
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.MsgConstant
import com.umeng.message.PushAgent
import com.umeng.socialize.PlatformConfig
import me.jessyan.autosize.utils.LogUtils
import org.android.agoo.xiaomi.MiPushRegistar

/**
 * PushSDK集成帮助类
 */
object PushHelper {
    private val TAG = PushHelper::class.java.simpleName

    fun preInit(context: Context?) {
        try {
            //解决推送消息显示乱码的问题
            val builder = AccsClientConfig.Builder()
            builder.setAppKey("umeng:" + UMENG_APPKEY)
            builder.setAppSecret(UMENG_MESSAGE_SECRET)
            builder.setTag(AccsClientConfig.DEFAULT_CONFIGTAG)
            ACCSClient.init(context, builder.build())
            TaobaoRegister.setAccsConfigTag(context, AccsClientConfig.DEFAULT_CONFIGTAG)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        UMConfigure.preInit(context, UMENG_APPKEY, getAppMetaData(context, "UMENG_CHANNEL"))
        if (!isMainProcess(context)) {
            init(context)
        }
    }

    /**
     * 初始化。
     * 场景：用户已同意隐私政策协议授权时
     * @param context 应用上下文
     */
    fun init(context: Context?) {
        // 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
        //日志开关 设置友盟调试模式
        UMConfigure.init(context,UMConfigure.DEVICE_TYPE_PHONE, Const.UMENG_MESSAGE_SECRET)
        PlatformConfig.setWeixin(Const.WEIXINID, Const.WEIXINSECERT)
        PlatformConfig.setWXFileProvider("com.d6.android.app.FileProvider");
        val mPushAgent = PushAgent.getInstance(context)
        mPushAgent.notificationPlaySound = MsgConstant.NOTIFICATION_PLAY_SERVER
        mPushAgent.setMessageHandler(CustomNotification())
        mPushAgent.setNotificationClickHandler(CustomNotificationHandler())
        val random = (Math.random() * 2).toInt()
//        if(random==0){
//            MobclickAgent.setCatchUncaughtExceptions(false)
//        }else{
//            MobclickAgent.setCatchUncaughtExceptions(true)
//        }

        MobclickAgent.setCatchUncaughtExceptions(true)

//        var customInfo = Bundle()
//        customInfo.putBoolean("mCallNativeDefaultHandler",true);
//        CrashApi.getInstance().updateCustomInfo(customInfo);

        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(object : IUmengRegisterCallback {

            override fun onSuccess(deviceToken: String) {
                Log.i("mPushAgent","devicetoken${deviceToken}")
                SPUtils.instance().put(Const.User.DEVICETOKEN, deviceToken).apply()
                //注册成功会返回device token ArblO5X82GPZtR8dvWGOMXlPXpdJsOcOdTAoti6gm_ew
            }

            override fun onFailure(s: String, s1: String) {
                Log.i("mPushAgent","$s,devicetoken${s1}")
            }
        })

        registerOtherDeviceChannel(context)
    }

    /**
     * 注册设备推送通道（小米、华为等设备的推送）
     *
     * @param context 应用上下文
     */
    private fun registerOtherDeviceChannel(context: Context?) {
        //小米通道，填写您在小米后台APP对应的xiaomi id和key
        MiPushRegistar.register(context, Const.XIAOMIAPPID,Const.XIAOMIAPPKEY);
        //华为，注意华为通道的初始化参数在minifest中配置
//        HuaWeiRegister.register((Application) context.getApplicationContext());
        //魅族，填写您在魅族后台APP对应的app id和key
//        MeizuRegister.register(context, PushConstants.MEI_ZU_ID, PushConstants.MEI_ZU_KEY);
//        //OPPO，填写您在OPPO后台APP对应的app key和secret
//        OppoRegister.register(context, PushConstants.OPPO_KEY, PushConstants.OPPO_SECRET);
//        //vivo，注意vivo通道的初始化参数在minifest中配置
//        VivoRegister.register(context);
    }

    /**
     * 是否运行在主进程
     *
     * @param context 应用上下文
     * @return true: 主进程；false: 子进程
     */
    fun isMainProcess(context: Context?): Boolean {
        return UMUtils.isMainProgress(context)
    }

    fun getAppMetaData(ctx: Context?, key: String?): String? {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null
        }
        var resultData = "Umeng"
        try {
            val packageManager = ctx.packageManager
            if (packageManager != null) {
                //注意此处为ApplicationInfo，因为友盟设置的meta-data是在application标签中
                val applicationInfo = packageManager.getApplicationInfo(ctx.packageName, PackageManager.GET_META_DATA)
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        //key要与manifest中的配置文件标识一致
                        resultData = applicationInfo.metaData.getString(key)
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return resultData
    }
}