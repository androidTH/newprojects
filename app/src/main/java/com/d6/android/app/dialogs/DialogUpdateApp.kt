package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.utils.*
import com.vector.update_app.UpdateAppBean
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_updateapp.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 更新dialog
 */
class DialogUpdateApp : DialogFragment(), RequestManager {

    private var updateType:Int = 2

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
        dialog.window.setLayout((screenWidth() * 0.8f).toInt() + dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_updateapp, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var updateAppBean = (arguments.getSerializable("data") as UpdateAppBean)
        tv_update_content.text = updateAppBean.updateLog

        tv_update_next.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_update_no.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_update_ok.setOnClickListener {
            dialogListener?.onClick(1,"update")
        }

        if(updateType == 1){
            tv_update_no.visibility = View.GONE
            tv_update_next.visibility = View.GONE
            iv_updateapp_sdv.visibility=View.GONE
            view_01.visibility = View.GONE
            view_02.visibility = View.GONE
            tv_update_title.visibility = View.VISIBLE
        }else if(updateType == 2){
            view_01.visibility = View.VISIBLE
            view_02.visibility = View.VISIBLE
            tv_update_no.visibility = View.VISIBLE
            tv_update_next.visibility = View.VISIBLE
            iv_updateapp_sdv.visibility=View.VISIBLE
            tv_update_title.visibility = View.INVISIBLE
        }
    }


    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
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
        (context as BaseActivity).dismissDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}