package com.d6.android.app.dialogs

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.MyDateActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_self_date.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 人工匹配约会
 */
class SelfDateDialog : DialogFragment(),RequestManager {

    private val compositeDisposable by lazy {
        CompositeDisposable()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.8f).toInt() + dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(false)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_self_date, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val dateBean = if (arguments != null) {
//            arguments.getSerializable("data") as DateBean
//        } else {
//            DateBean()
//        }

        var spannableString = SpannableString(resources.getString(R.string.string_ask_service))
        spannableString.setSpan( ForegroundColorSpan(ContextCompat.getColor(context,R.color.color_F7AB00)), 7,spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_lianxi.setText(spannableString)

        tv_lianxi.setOnClickListener {
               isBaseActivity {
                   it.pushCustomerMessage((context as BaseActivity), getLocalUserId(),8,"",next = {
                       chatService(it)
                       dismissAllowingStateLoss()
                   })
               }
        }

        tv_know.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun getAuthState() {
        if (context is BaseActivity) {
            (context as BaseActivity).dialog(canCancel = false)
        }
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getAuthState(userId).request(this){ _, data->
//            if (data != null) {
//                val wanshanziliao = data.optDouble("wanshanziliao")
//                if (wanshanziliao < 8) {//资料完善程度大于=80%
//                    startActivity<DateAuthStateActivity>()
//                    return@request
//                }
//                val lianxifangshi = data.optInt("lianxifangshi")
//                if (lianxifangshi == 0) {
//                    startActivity<DateAuthStateActivity>()
//                    return@request
//                }
//
//                val qurenzheng = data.optInt("qurenzheng")
//                if (qurenzheng == 0) {
//                    startActivity<DateAuthStateActivity>()
//                    return@request
//                }
//
//                startActivity<MyDateActivity>()
//
//            } else {
//                startActivity<DateAuthStateActivity>()
//            }

            if (data != null) {
                val wanshanziliao = data.optDouble("wanshanziliao")
                if (wanshanziliao < 8) {//资料完善程度大于=80%
                    startActivity<MyDateActivity>("whetherOrNotToBeCertified" to "0")
                    return@request
                }
                val lianxifangshi = data.optInt("lianxifangshi")
                if (lianxifangshi == 0) {
                    startActivity<MyDateActivity>("whetherOrNotToBeCertified" to "0")
                    return@request
                }

                val qurenzheng = data.optInt("qurenzheng")
                if (qurenzheng == 0) {
                    startActivity<MyDateActivity>("whetherOrNotToBeCertified" to "0")
                    return@request
                }

                startActivity<MyDateActivity>("whetherOrNotToBeCertified" to "1")

            } else {//startActivity<DateAuthStateActivity>
                startActivity<MyDateActivity>("whetherOrNotToBeCertified" to "0")
            }


        }
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

    private var dialogListener: OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
}