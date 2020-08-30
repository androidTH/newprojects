package com.d6.android.app.dialogs

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.net.Request.pushCustomerMessage
import com.d6.android.app.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_costomerservice_layout.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 约会认证提示
 */
class CustomerServiceDialog : DialogFragment(),RequestManager {

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
            inflater?.inflate(R.layout.dialog_costomerservice_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var service_type = arguments.getString("service_type")
        if(TextUtils.equals("1",service_type)){
            tv_customerservice_contact.text ="联系专属微信客服"
            tv_customerservice_cancel.text = "放弃"
        }
        tv_customerservice_contact.setOnClickListener {
            isBaseActivity {
//                it.pushCustomerMessage(it, getLocalUserId(),5,"",next = {
//                    chatService(it)
//                })
//                var lan = it.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
//                var intent = Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setComponent(lan.getComponent());
//                startActivity(intent)
                if(TextUtils.equals("1",service_type)){
                    try {
                        val intent = Intent(Intent.ACTION_MAIN)
                        val cmp = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
                        intent.addCategory(Intent.CATEGORY_LAUNCHER)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.component = cmp
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        // TODO: handle exception
                        toast("检查到您手机没有安装微信，请安装后使用该功能")
                    }
                }
            }
            dismissAllowingStateLoss()
        }

        tv_customerservice_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        var resMsg = arguments.getString("resMsg")
        var title = arguments.getString("dialog_title")
        tv_customerservice_tips.text = resMsg
        tv_customerservice_title.text = title

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