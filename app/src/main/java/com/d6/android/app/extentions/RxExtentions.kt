package com.d6.android.app.extentions

import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.text.TextUtils
import com.d6.android.app.activities.SignChooseActivity
import com.d6.android.app.activities.SignInActivity
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.ConfirmDialog
import com.d6.android.app.dialogs.SingleActionDialog
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.Response
import com.d6.android.app.net.Error
import com.d6.android.app.net.ResultException
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.defaultScheduler
import com.d6.android.app.utils.sysErr
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Created on 2017/12/27.
 */

inline fun <reified O, I : Response<O>> Flowable<I>.request(requestManager: RequestManager, showToast: Boolean = false, crossinline success: (msg:String?,t: O?) -> Unit, crossinline error : (code: Int, msg: String) -> Unit) {
    this.defaultScheduler().subscribe(object : DisposableSubscriber<I>() {
        override fun onStart() {
            super.onStart()
            requestManager.onBind(this)
        }
        override fun onError(t: Throwable?) {
            t?.printStackTrace()
            requestManager.dismissDialog()
            var code = -1
            var msg = ""
            when (t) {
                is JsonSyntaxException -> {
                    msg = Error.PARSER_ERROR
                }
                is ConnectException -> {
                    msg = Error.NET_ERROR
                }
//                is SocketTimeoutException -> msg = Error.NET_ERROR
                is HttpException -> {
                    msg = Error.SERVER_ERROR
                    val tCode = t.code()
                    if (tCode == 401) {//未通过服务端认证。需前往登录
                        msg = "登录信息已过期,请重新登录"
                        if (!D6Application.isChooseLoginPage) {
                            D6Application.isChooseLoginPage = true
                            SPUtils.instance().remove(Const.User.USER_ID)
                                    .remove(Const.User.IS_LOGIN)
                                    .remove(Const.User.RONG_TOKEN)
                                    .remove(Const.User.USER_TOKEN)
                                    .apply()
                            if (requestManager is BaseActivity) {
                                requestManager.closeAll()
                                requestManager.startActivity<SignInActivity>()
                            }else if (requestManager is Fragment) {
                                if (requestManager.activity != null && requestManager.activity is BaseActivity) {
                                    (requestManager.activity as BaseActivity).closeAll()
                                    requestManager.startActivity<SignInActivity>()
                                }
                            }
                        }
                    }else if(tCode == 404){
                        msg = Error.SERVER_404ERROR
                    } else if (tCode == -3) {//账号已禁用
                        if (requestManager is BaseActivity) {
                            val confirmDialog = ConfirmDialog()
                            confirmDialog.arguments = bundleOf("msg" to "对不起，您的账号已被禁用，如有疑问，请联系D6客服。")
                            confirmDialog.setDialogListener { p, s ->
                                requestManager.closeAll()
                                requestManager.startActivity<SignInActivity>()
                            }
                            confirmDialog.show(requestManager.supportFragmentManager,"con")
                        }
                    }
                }
                is ResultException -> {
                    code = t.code
                    msg = t.message!!
                }
            }
            if(!TextUtils.isEmpty(msg)){
                error(code, msg)
                if (code == 200 || code == -3) {
                    if (requestManager is Fragment) {
                        if (requestManager.activity != null && requestManager.activity is BaseActivity) {
                            val mSingleActionDialog = SingleActionDialog()
                            mSingleActionDialog.arguments = bundleOf("message" to msg)
                            mSingleActionDialog.show((requestManager.activity as BaseActivity).supportFragmentManager, "action")
                        }
                    }
                }else{
                    if (showToast) {
                        requestManager.showToast(msg)
                    }
                }
            }
        }

        override fun onComplete() {

        }

        override fun onNext(t: I) {
            requestManager.dismissDialog()
            if (t.res == 1) {//成功
                success(t.resMsg,t.data)
            }else {
                if(t.data!=null&&t.data!="null"){
                    error(t.res, t.data.toString())
                    if (showToast) {
                        requestManager.showToast(t.resMsg.toString())
                    }
                }else{
                    onError(ResultException(t.res, t.resMsg))
                }
            }
        }
    })
}

inline fun <reified O, I : Response<O>> Flowable<I>.request(requestManager: RequestManager, showToast: Boolean = true, crossinline success: (msg:String?,t: O?) -> Unit) {
    request(requestManager,showToast,success){_,_->}
}