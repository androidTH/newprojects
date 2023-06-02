package com.d6zone.android.app.dialogs

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
import com.d6zone.android.app.R
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.interfaces.RequestManager
import com.d6zone.android.app.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.dialog_date_contact_layout.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 约会联系方式
 */
class DateContactDialog : DialogFragment(),RequestManager {

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

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_date_contact_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phone = if (arguments != null) {
            arguments.getString("phone")
        } else {
            ""
        }

        val wx = if (arguments != null) {
            arguments.getString("wx")
        } else {
            ""
        }

        tv_phone.text = phone

        et_weChat.text = wx

        tv_sure.setOnClickListener {
            dismissAllowingStateLoss()
        }

        if(TextUtils.isEmpty(tv_phone.text.toString())){
            tv_copy_phone!!.visibility = View.GONE
        }else{
            tv_copy_phone!!.visibility = View.VISIBLE
        }

        if(TextUtils.isEmpty(et_weChat.text.toString())){
            tv_copy_wx!!.visibility = View.GONE
        }else{
            tv_copy_wx!!.visibility = View.VISIBLE
        }

        tv_copy_phone.setOnClickListener {
//            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            // 将文本内容放到系统剪贴板里。
//            cm.text = tv_phone.text.toString()
//            toast("手机号已复制到剪切板")
            RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE, arguments.getString("ids"), arguments.getString("name"))
//            .isAuthUser {
//                RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE, id, name)
//            }
        }

        tv_copy_wx.setOnClickListener {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = et_weChat.text.toString()
            toast("微信号已复制到剪切板")
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