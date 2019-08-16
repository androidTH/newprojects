package com.d6.android.app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.OPENSTALL_CHANNEL
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



class LauncherActivity : BaseActivity() {

    private val diposable = object : DisposableSubscriber<Long>() {
        override fun onComplete() {}
        override fun onError(t: Throwable?) {}
        override fun onNext(t: Long) {
            if (t == 3L) {
                val isFirst =  SPUtils.instance().getBoolean(Const.User.IS_FIRST,true)
                if (isFirst) {
                    startActivity<SplashActivity>()
                } else {
                    val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)
                    if (isLogin) {
                        val name = SPUtils.instance().getString(Const.User.USER_NICK)
                        if (name.isEmpty()) {
                            startActivity<SetUserInfoActivity>()
                        } else {
                            startActivity<MainActivity>()
                        }
                    } else {
                        startActivity<SignInActivity>()
                    }
                }
                finish()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        immersionBar.init()
//        OpenInstall.getWakeUp(intent, wakeUpAdapter);
        OpenInstall.getInstall(mAppInstallAdapter)
        Flowable.interval(0, 1, TimeUnit.SECONDS).defaultScheduler().subscribe(diposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            diposable.dispose()
        } catch (e: Exception) {
//            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        OpenInstall.getWakeUp(intent, wakeUpAdapter)
    }

    var wakeUpAdapter: AppWakeUpAdapter = object : AppWakeUpAdapter() {
        override fun onWakeUp(appData: AppData) {
            //获取渠道数据
            val channelCode = appData.getChannel()
            //获取绑定数据
            val bindData = appData.getData()
            Log.d("OpenInstall", "getWakeUp : wakeupData = " + appData.toString())
        }
    }

    var mAppInstallAdapter = object:AppInstallAdapter(){
        override fun onInstall(p0: AppData?) {
            p0?.let {
                Log.d("OpenInstall", "${it.channel}getWakeUp : wakeupData = " + it.toString())
                SPUtils.instance().put(OPENSTALL_CHANNEL,it.channel).apply()
            }
        }
    }

}
