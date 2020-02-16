package com.d6.android.app.activities

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.support.annotation.NonNull
import android.text.TextUtils
import android.util.Log
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.INSTALL_DATA01
import com.d6.android.app.utils.Const.INSTALL_DATA02
import com.d6.android.app.utils.Const.OPENSTALL_CHANNEL
import com.d6.android.app.utils.Const.User.OAID_ANDROID
import com.d6.android.app.utils.MiitHelper
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.defaultScheduler
import com.fm.openinstall.OpenInstall
import com.fm.openinstall.listener.AppInstallAdapter
import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit
import com.fm.openinstall.model.AppData
import com.fm.openinstall.listener.AppWakeUpAdapter
import org.json.JSONObject


class LauncherActivity : BaseActivity() {

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
        OpenInstall.getInstall(mAppInstallAdapter)
        OpenInstall.getWakeUp(intent, wakeUpAdapter)
        Flowable.interval(0, 1, TimeUnit.SECONDS).defaultScheduler().subscribe(diposable)
    }

    override fun onResume() {
        super.onResume()
        if(TextUtils.isEmpty(SPUtils.instance().getString(OAID_ANDROID,""))){
            val miitHelper = MiitHelper(appIdsUpdater)
            miitHelper.getDeviceIds(applicationContext)
        }
    }

    private val appIdsUpdater = object : MiitHelper.AppIdsUpdater {

        override fun OnIdsAvalid(@NonNull ids: String) {
            Log.i("appIdsUpdater","oaid=${ids}")
            SPUtils.instance().put(OAID_ANDROID,ids).apply()
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
