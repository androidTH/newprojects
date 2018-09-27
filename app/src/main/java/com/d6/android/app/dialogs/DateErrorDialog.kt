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
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_date_error.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 约会发送出错
 */
class DateErrorDialog : DialogFragment(),RequestManager {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.8f).toInt()+dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(false)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_date_error, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (arguments != null) {
            val msg = arguments.getString("msg")
            tv_content.text = msg
            if (arguments.containsKey("img")) {
                val image = arguments.getString("img")
                imageView.setImageURI(image)
            }
        }

        tv_copy.setOnClickListener {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = weChat
            WXAPIFactory.createWXAPI(context,"wx43d13a711f68131c").openWXApp()
            toast("微信号已复制到剪切板")
        }
        tv_close.setOnClickListener { dismissAllowingStateLoss() }

        val sex = SPUtils.instance().getString(Const.User.USER_SEX)
        if (TextUtils.equals("1", sex)) {
            getData(0)
        } else {
            getData(1)
        }
    }

    private var weChat=""
    private fun getData(type:Int) {
        val mark = when (type) {
            0 -> "qrcode-boy"
            1 -> "qrcode-girl"
            2 -> "service_wechat_code"
            else -> "qrcode-weixin"
        }
        Request.getInfo(mark).request(this) { _, data ->
            data?.let {
                weChat = data.optString("ext1")
                tv_wx.text= "客服微信号：$weChat"
            }
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