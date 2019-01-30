package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.MyPointsActivity
import com.d6.android.app.activities.VistorsActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_vistor_paypoint.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 支付积分
 */
class VistorPayPointDialog : DialogFragment(),RequestManager {


    private var type:Int = 0

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
        dialog.window.setLayout((screenWidth() * 0.8f).toInt()+dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_vistor_paypoint, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var point = if (arguments != null) {
            arguments.getString("point")
        } else {
            "0"
        }

        var sAddPointDesc = arguments.getString("pointdesc")

        type = arguments.getInt("type")
        tv_vistor_pay_point.text = point
        tv_vistor_pointdesc.text = sAddPointDesc

        tv_payok.setOnClickListener {
            if(type==0){
                getData(point,"")
            }else if (type == 1) {
                dismissAllowingStateLoss()
                if (dialogListener != null) {
                    dialogListener?.onClick(type, "success")
                }
            }
        }

        tv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun getData(point:String,remainPoint:String) {
        Request.getVistorPayPoint(userId).request((context as BaseActivity),false,success={msg,data->
            if (type == 0) {
                startActivity<VistorsActivity>()
            }
            dismissAllowingStateLoss()
        }){code,msg->
            showToast(msg)
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

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}