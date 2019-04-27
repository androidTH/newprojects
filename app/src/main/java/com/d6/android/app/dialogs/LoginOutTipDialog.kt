package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_date_loginout_tip_layout.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 登录过期dialog
 */
class LoginOutTipDialog : DialogFragment(),RequestManager {

    private val wxApi by lazy {
        WXAPIFactory.createWXAPI(context, "wx43d13a711f68131c")
    }

    private val shareApi by lazy {
        UMShareAPI.get(context)
    }

    private val devicetoken by lazy {
        SPUtils.instance().getString(Const.User.DEVICETOKEN)
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }
    private val compositeDisposable by lazy {
        CompositeDisposable()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.9f).toInt(), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(false)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_date_loginout_tip_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_action.setOnClickListener {
            if (wxApi.isWXAppInstalled) {
                weChatLogin()
            } else {
                toast("请先安装微信")
            }
        }
    }

    private fun weChatLogin() {
        isBaseActivity {
            shareApi.getPlatformInfo(activity, SHARE_MEDIA.WEIXIN, object : UMAuthListener {
                override fun onComplete(p0: SHARE_MEDIA?, p1: Int, data: MutableMap<String, String>?) {
                    if (data != null) {
                        val openId = if (data.containsKey("openid")) data["openid"] else ""
                        val unionId = if (data.containsKey("unionid")) data["unionid"] else ""
                        val name = if (data.containsKey("name")) data["name"] else ""
                        val gender = if (data.containsKey("gender")) data["gender"] else "" //"access_token" -> "15_DqQo8GAloYTRPrkvE9Mn1TLJx06t2t8jcTnlVjTtWtCtB10KlEQJ-pksniTDmRlN1qO8OMgEH-6WaTEPbeCYXLegAsvy6iolB3FHfefn4Js"
                        val iconUrl = if (data.containsKey("iconurl")) data["iconurl"] else "" //"refreshToken" -> "15_MGQzdG8xEsuOJP-LvI80gZsR0OLgpcKlTbWjiQXJfAQJEUufz4OxdqmTh6iZnnNZSgOgHskEv-N8FexuWMsqenRdRtSycKVNGKkgfiVNJGs"
                        updateUnionId(openId.toString(),unionId.toString())
                    } else {
                        toast("拉取微信信息异常！")
                    }
                }

                override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
                    toast("取消登录")
                    it.dismissDialog()
                }

                override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
                    p2?.printStackTrace()
                    toast("微信登录异常！")
                    it.dismissDialog()
                }

                override fun onStart(p0: SHARE_MEDIA?) {
                }

            })
        }
    }

    private fun updateUnionId(sOpenId:String,unionId:String){
        Request.updateUnionId(userId,sOpenId,unionId).request(this,false,success={code,data->
            dismissAllowingStateLoss()
        }){code,msg->
            if(code == 0){
                showToast(msg)
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }


    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun dismissDialog() {

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}