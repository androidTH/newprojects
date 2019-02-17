package com.d6.android.app.dialogs

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.utils.*
import com.tbruyelle.rxpermissions2.RxPermissions
import com.vector.update_app.UpdateAppBean
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_updateapp.*
import kotlinx.android.synthetic.main.header_user_info_layout.view.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 更新dialog
 */
class DialogUpdateApp : DialogFragment(), RequestManager, DialogInterface.OnKeyListener{

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return mCancelable
        }
        return mCancelable
    }

    var mCancelable:Boolean = false

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

        tv_update_no.setOnClickListener {//不再提示
            SPUtils.instance().put(Const.IGNORE_VERSION,updateAppBean.newVersion).apply()
            dismissAllowingStateLoss()
        }

        tv_update_ok.setOnClickListener {
            dismissAllowingStateLoss()
            dialogListener?.onClick(1,"update")
        }

        mCancelable = updateAppBean.isConstraint
        if(updateAppBean.isConstraint){
            var lp = RelativeLayout.LayoutParams(ll_dialog.layoutParams)
            lp.topMargin = 0
            lp.leftMargin = resources.getDimensionPixelOffset(R.dimen.margin_15)
            lp.rightMargin = resources.getDimensionPixelOffset(R.dimen.margin_15)
            ll_dialog.layoutParams = lp

            tv_update_no.visibility = View.GONE
            tv_update_next.visibility = View.GONE
            iv_updateapp_sdv.visibility=View.GONE
            view_01.visibility = View.GONE
            view_02.visibility = View.GONE
            tv_update_title.visibility = View.VISIBLE
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
        }else{
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            view_01.visibility = View.VISIBLE
            view_02.visibility = View.VISIBLE
            tv_update_no.visibility = View.VISIBLE
            tv_update_next.visibility = View.VISIBLE
            iv_updateapp_sdv.visibility=View.VISIBLE
            tv_update_title.visibility = View.INVISIBLE
        }
        dialog.setOnKeyListener(this)
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