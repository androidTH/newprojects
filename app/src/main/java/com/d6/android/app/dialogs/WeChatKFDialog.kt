package com.d6.android.app.dialogs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.text.ClipboardManager
import android.text.TextUtils
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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_wechatkf_layout.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 约会认证提示
 */
class WeChatKFDialog : DialogFragment(),RequestManager {

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
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_wechatkf_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_copy.setOnClickListener {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = weChat
            WXAPIFactory.createWXAPI(context,"wx43d13a711f68131c").openWXApp()
            toast("微信号已复制到剪切板")
        }
        tv_action.setOnClickListener {
//            var localUserId =getLocalUserId()
//            isBaseActivity {
//                it.pushCustomerMessage(this,localUserId,2,localUserId,next = {
//                     chatService(it)
//                })
//            }
//            WXAPIFactory.createWXAPI(context,"wx43d13a711f68131c").openWXApp()

            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = weChat
            WXAPIFactory.createWXAPI(context,"wx43d13a711f68131c").openWXApp()
            toast("微信号已复制到剪切板")
        }

        tv_close.setOnClickListener {
             dismissAllowingStateLoss()
        }

        getData()
    }

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
    private var weChat=""
    private fun getData() {
        Request.getInfo(Const.SERVICE_WECHAT_CODE).request(this) { _, data ->
            data?.let {
                val sex = SPUtils.instance().getString(Const.User.USER_SEX)
                if(TextUtils.equals(sex, "0")){
                    weChat  = data.optString("ext5")
                }else{
                    weChat = data.optString("ext6")
                }
                tv_wx.text= "客服微信号：$weChat"

                tv_action.text = "复制客服微信：${weChat}"
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

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}