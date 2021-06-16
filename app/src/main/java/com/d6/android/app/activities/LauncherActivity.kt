package com.d6.android.app.activities

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.support.annotation.NonNull
import android.text.TextUtils
import android.util.Log
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.UserAgreemetDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.rong.RongPlugin
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.INSTALL_DATA01
import com.d6.android.app.utils.Const.INSTALL_DATA02
import com.d6.android.app.utils.Const.OPENSTALL_CHANNEL
import com.d6.android.app.utils.Const.User.OAID_ANDROID
import com.fm.openinstall.OpenInstall
import com.fm.openinstall.listener.AppInstallAdapter
import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit
import com.fm.openinstall.model.AppData
import com.fm.openinstall.listener.AppWakeUpAdapter
import com.vector.update_app.utils.AppUpdateUtils
import io.rong.imkit.RongIM
import io.rong.push.RongPushClient
import io.rong.push.pushconfig.PushConfig
import org.jetbrains.anko.toast
import org.json.JSONObject


class LauncherActivity : BaseActivity() {

    private val agree:Boolean by lazy{
        SPUtils.instance().getBoolean(Const.User.ISNOTUESERAGREEMENT)
    }
    private val diposable = object : DisposableSubscriber<Long>() {
        override fun onComplete() {}
        override fun onError(t: Throwable?) {}
        override fun onNext(t: Long) {
            if (t == 1L) {
//                val isFirst =  SPUtils.instance().getBoolean(Const.User.IS_FIRST,true)
//                if (isFirst) {
//                    startActivity<SplashActivity>()
//                } else {
                    val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)
                    if (isLogin) {
                        val name = SPUtils.instance().getString(Const.User.USER_NICK)
                        if (name.isEmpty()) {
                            startActivity<SetUserInfoActivity>()
                        } else {
                            startActivity<MainActivity>()
                        }
                    } else {
//                        startActivity<SignInActivity>()
                        startActivity<SplashActivity>()
                    }
//                }
                finish()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        immersionBar.init()
        if(agree){
            OpenInstall.getInstall(mAppInstallAdapter)
            OpenInstall.getWakeUp(intent, wakeUpAdapter)
            Flowable.interval(0, 1, TimeUnit.SECONDS).defaultScheduler().subscribe(diposable)
        }else{
            showUserAgreementDialog()
        }
        getFreeChatTag()
    }

    override fun onResume() {
        super.onResume()
        if(agree){
            getOAID()
        }
    }

    private fun getFreeChatTag(){
        Request.getInfo("android_audit").request(this,success={ _, data ->
            data?.let {
                var channels = data.optString("ext1")
                var versionNum = data.optString("ext2")
//                Log.i("getFreeChatTag","渠道：$channels,版本号：$versionNum,${AppUtils.getChannelName(this)}")
                if(channels.isNullOrEmpty()){
                    SPUtils.instance().put(Const.User.ISNOTFREECHATTAG,false).apply()
                }else{
                    if(channels.contains(AppUtils.getChannelName(this))&&
                            AppUtils.compareVersion(versionNum, AppUpdateUtils.getVersionName(this))==0){
                        SPUtils.instance().put(Const.User.ISNOTFREECHATTAG,true).apply()
                    }else{
                        SPUtils.instance().put(Const.User.ISNOTFREECHATTAG,false).apply()
                    }
                }
            }
        }){code,msg->
            SPUtils.instance().put(Const.User.ISNOTFREECHATTAG,false).apply()
        }
    }

    private val appIdsUpdater = object : MiitHelper.AppIdsUpdater {

        override fun OnIdsAvalid(@NonNull ids: String) {
            Log.i("appIdsUpdater","oaid=${ids}")
            SPUtils.instance().put(OAID_ANDROID,ids).apply()
        }
    }

    private fun showUserAgreementDialog() {
        val mUserAgreemetDialog = UserAgreemetDialog()
        mUserAgreemetDialog.setDialogListener { p, s ->
            if (p == 1) {
                SPUtils.instance().put(Const.User.ISNOTUESERAGREEMENT, true).apply()
                initApp()
                OpenInstall.getInstall(mAppInstallAdapter)
                OpenInstall.getWakeUp(intent, wakeUpAdapter)
                Flowable.interval(0, 1, TimeUnit.SECONDS).defaultScheduler().subscribe(diposable)
                getOAID();
            }else{
                finish()
            }
        }
        mUserAgreemetDialog.show(supportFragmentManager, "useragreement")
    }

    private fun initApp(){
        PushHelper.init(this)
        var config = PushConfig.Builder().enableMiPush(Const.XIAOMIAPPID, Const.XIAOMIAPPKEY).build()
        RongPushClient.setPushConfig(config)
        RongIM.init(this)
        RongPlugin.init(this)

        if(PushHelper.isMainProcess(this)){
            OpenInstall.init(this)
        }
    }

    private fun getOAID(){
        try{
            if(TextUtils.isEmpty(SPUtils.instance().getString(OAID_ANDROID,""))){
                val miitHelper = MiitHelper(appIdsUpdater)
                miitHelper.getDeviceIds(applicationContext)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            immersionBar.destroy()
            diposable.dispose()
        } catch (e: Exception) {
//            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        OpenInstall.getWakeUp(intent, wakeUpAdapter)
    }

    var wakeUpAdapter: AppWakeUpAdapter = object : AppWakeUpAdapter() {
        override fun onWakeUp(appData: AppData) {
            //获取渠道数据
            val channelCode = appData.getChannel()
            //获取绑定数据
            var bindData = appData.getData()
            Log.d("OpenInstall", "${channelCode}+AppWakeUpAdapter = ${bindData}")
            SPUtils.instance().put(INSTALL_DATA02,"${channelCode}_${bindData}").apply()
        }
    }

    var mAppInstallAdapter = object: AppInstallAdapter(){
        override fun onInstall(p0: AppData?) {
            p0?.let {
                SPUtils.instance().put(OPENSTALL_CHANNEL,it.channel).apply()
                try{
                    var bindData = it.getData()
                    if(bindData.isNotEmpty()){
                        var jsonObject = JSONObject(bindData)
                        var userId = jsonObject.optInt("sInviteCode")
                        Log.d("OpenInstall", "渠道=${it.channel} wakeupData = ${userId}")
                        SPUtils.instance().put(INSTALL_DATA01,"${userId}").apply()
                    }
                }catch (e:java.lang.Exception){

                }
            }
        }
    }
}
