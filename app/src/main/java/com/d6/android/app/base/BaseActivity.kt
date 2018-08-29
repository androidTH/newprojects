package com.d6.android.app.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Window
import com.bugtags.library.Bugtags
import com.d6.android.app.R
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.widget.ProgressDialog
import com.umeng.analytics.MobclickAgent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import java.lang.Exception
import android.view.MotionEvent



/**
 * 基础activity，包含设置默认强制竖屏显示，广播方式实现关闭全部继承自该activity，并注册了关闭广播的子类
 *
 */
abstract class BaseActivity : AppCompatActivity(), AnkoLogger, RequestManager {

    lateinit var ACTION_CLOSE_ALL: String
    val compositeDisposable = CompositeDisposable()
    //改用lazy初始，第一次使用时才会初始化
    private val dialog: ProgressDialog by lazy {
        ProgressDialog(this, R.style.Theme_ProgressDialog)
    }

    var isDestroy = false

    private val closeAllReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && TextUtils.equals(intent.action, ACTION_CLOSE_ALL)) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ACTION_CLOSE_ALL = "cn.base.%s.all.close".format(packageName)
        System.err.println(ACTION_CLOSE_ALL)
        if (isRegisterCloseBroadReceiver()) {
            registerReceiver(closeAllReceiver, IntentFilter(ACTION_CLOSE_ALL))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.isNotEmpty()) {
            for (permission in permissions) {
                SPUtils.instance().put(permission, false).apply()
            }
        }
    }

    fun closeAll() {
        sendBroadcast(Intent(ACTION_CLOSE_ALL))
    }

    /**
     * 是否注册关闭全部的广播
     */
    protected fun isRegisterCloseBroadReceiver(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
        Bugtags.onResume(this);
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
        Bugtags.onPause(this);
    }

    override fun onDestroy() {
        isDestroy = true
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }

        try {
            if (isRegisterCloseBroadReceiver()) {
                unregisterReceiver(closeAllReceiver)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        dismissDialog()
    }

    fun dialog(msg: String = "加载中...", canCancel: Boolean = true) {
        if (!canCancel) {
            dialog.setOnCancelListener {
                if (this.finishWhenCancelDialog()) {
                    finish()
                }
            }
        } else {
            //这里设置如果是可以取消的监听器置null了。可以自己在页面上重新设置想要的操作。这里不知道具体需求。
            dialog.setOnCancelListener(null)
        }
        dialog.setCanceledOnTouchOutside(canCancel)
        dialog.setMessage(msg)
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    override fun dismissDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, event)
        return super.dispatchTouchEvent(event)
    }

    /**
     * 是否在取消progressDialog的时候关闭页面
     */
    protected open fun finishWhenCancelDialog() = true

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun showToast(msg:String) {
        toast(msg)
    }
}