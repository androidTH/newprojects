package com.d6.android.app.activities

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.widget.Toast
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.alertDialog
import com.d6.android.app.utils.defaultScheduler
import com.gyf.barlibrary.ImmersionBar
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit

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
}
