package com.d6.android.app.dialogs

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.UnAuthUserActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.OnDialogListener
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.screenWidth
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.dialog_date_contact_auth_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 约会认证提示
 */
class DateContactAuthDialog : DialogFragment(), RequestManager {

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.9f).toInt(), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    var dateDeclarationViewView1: TextView? = null
    fun dateDeclarationView(dateDeclarationViewView: TextView) {
        dateDeclarationViewView1 = dateDeclarationViewView
    }


    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_date_contact_auth_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val phone = if (arguments != null) {
            arguments.getString("p")
        } else {
            ""
        }

        val wx = if (arguments != null) {
            arguments.getString("w")
        } else {
            ""
        }

        et_phone.setText(phone)
        et_weChat.setText(wx)

        et_weChat.setSelection(wx.length)

        if (dateDeclarationViewView1 != null) {
            et_weChat.setText(dateDeclarationViewView1!!.text.toString())
        }
        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_sure.setOnClickListener {
            submit()
        }

    }
    inline fun Activity.isAuthUser(next: () -> Unit) {
        val className = SPUtils.instance().getString(Const.User.USER_CLASS_ID)
        if (className == "7") {
            this.startActivity<UnAuthUserActivity>()
        } else {
            next()
        }
    }


    private fun submit() {
//        val phone = et_phone.text.toString().trim()
//        if (phone.isEmpty()) {
//            toast("手机号不能为空")
//            return
//        }
        val weChatAccount = et_weChat.text.toString().trim()
        if (weChatAccount.isEmpty()) {
            toast("微信账号不能为空")
            return
        }
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        if (context is BaseActivity) {
            (context as BaseActivity).dialog()
        }
        Request.updateDateInfo(userId, phone = "", egagementwx = weChatAccount).request(this) { _, data ->
            toast("联系方式已更新！")
            dialogListener?.onClick(0, et_weChat.text.toString().trim())
            dismissAllowingStateLoss()
            if (dateDeclarationViewView1 != null) {
                dateDeclarationViewView1!!.text = et_weChat.text.toString().trim()
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

    override fun dismissDialog() {
        if (context is BaseActivity) {
            (context as BaseActivity).dismissDialog()
        }
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}