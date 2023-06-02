package com.d6zone.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6zone.android.app.R
import com.d6zone.android.app.activities.MyPointsActivity
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.interfaces.RequestManager
import com.d6zone.android.app.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_sendredheartend_layout.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 约会认证提示
 */
class SendRedHeartEndDialog : DialogFragment(),RequestManager {

    private val mUserSex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX,"")
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
            inflater?.inflate(R.layout.dialog_sendredheartend_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rl_buy.setOnClickListener {
            isBaseActivity {
//                chatService(it)
                startActivity<MyPointsActivity>()
            }
            dismissAllowingStateLoss()
        }

        tv_redheart_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

//        var resMsg = arguments.getString("resMsg")

        if(TextUtils.equals(mUserSex,"0")){
            tv_redheart_tips.text = "可前往【钱包】中购买\n" +
                    "或完善资料后等待别人的赠送"
        }else{
            tv_redheart_tips.text = "可前往【钱包】中购买"
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