package com.d6.android.app.dialogs

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_payresult_layout.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textResource

/**
 * 积分充值结果dialog
 */
class PayResultDialog : DialogFragment(),RequestManager {

    public val PAY_SUCCESS:String?="wx_pay_success"
    public val PAY_FAIL:String?="wx_pay_fail"

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun dismissDialog() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).dismissDialog()
        }
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private val compositeDisposable = CompositeDisposable()

    private val payResult by lazy {
        if (arguments!=null) {
            arguments.getString("payresult", PAY_FAIL)
        }else{
            PAY_SUCCESS
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(matchParent, resources.getDimensionPixelSize(R.dimen.height_300))
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_payresult_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_payresult_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        if(TextUtils.equals(payResult.toString(), PAY_SUCCESS)){
            iv_payresult_icon.imageResource= R.mipmap.intergral_successful_icon
            tv_paysuccess_title.textResource = R.string.str_points_pay_success
            tv_payresult_success.visibility = View.VISIBLE
            tv_payresult_fail.visibility = View.GONE
            ll_wx_copy.visibility = View.GONE
        }else{
            iv_payresult_icon.imageResource = R.mipmap.intergral_failed_icon
            tv_paysuccess_title.textResource = R.string.str_points_pay_fail
            tv_payresult_fail.visibility = View.VISIBLE
            ll_wx_copy.visibility = View.VISIBLE
            tv_payresult_success.visibility = View.GONE
        }

        tv_payresult_success.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_payresult_fail_copywx.setOnClickListener {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = weChat
            toast("微信号已复制到剪切板")
        }

        getData()
    }
    private var weChat=""
    private fun getData() {
        Request.getInfo(Const.SERVICE_WECHAT_CODE).request(this) { _, data ->
            data?.let {
                val sex = SPUtils.instance().getString(Const.User.USER_SEX)
                if(TextUtils.equals(sex, "0")){
                    weChat  = data.optString("ext1")
                }else{
                    weChat = data.optString("ext2")
                }
                tv_payreuslt_wx.text= "客服微信号：$weChat"
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

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        dismissDialog()
    }
    override fun onDetach() {
        super.onDetach()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}