package com.d6zone.android.app.dialogs

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.activities.WebViewActivity
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.interfaces.RequestManager
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.OnDialogListener
import com.d6zone.android.app.utils.screenWidth
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_userargeement_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 用户隐私协议
 */
class UserAgreemetDialog : DialogFragment(),RequestManager {

    private val userServiceTitle = "《用户协议》"
    private val appServiceTitle = "《隐私政策》"

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
            inflater?.inflate(R.layout.dialog_userargeement_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_agree.setOnClickListener {
            dialogListener?.let {
                it.onClick(1,"")
                dismissAllowingStateLoss()
            }
        }

        tv_common_cancel.setOnClickListener {
            dialogListener?.let {
                it.onClick(2,"")
            }
           dismissAllowingStateLoss()
        }

        val content = resources.getString(R.string.string_useragreement)
        val str = SpannableString(content)
        val start = content.indexOf("《") //第一个出现的位置

        str.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                context.startActivity<WebViewActivity>("title" to "用户协议", "url" to Const.USER_AGREEMENT_URL)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.RED
                //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
                ds.isUnderlineText = true
            }
        }, start, start + userServiceTitle.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val end = content.indexOf(appServiceTitle)
        str.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val url = Const.PRIVATE_URL
                context.startActivity<WebViewActivity>("title" to "隐私政策", "url" to url)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.RED
                //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
                ds.isUnderlineText = true
            }
        }, end, end + appServiceTitle.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv_common_tips.text = str
        tv_common_tips.setMovementMethod(LinkMovementMethod.getInstance()) //不设置 没有点击事件
        tv_common_tips.setHighlightColor(Color.TRANSPARENT)

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
    }

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}