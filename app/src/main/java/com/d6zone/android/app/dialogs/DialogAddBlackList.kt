package com.d6zone.android.app.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.*
import com.d6zone.android.app.R
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.interfaces.RequestManager
import com.d6zone.android.app.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_addblack.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 更新dialog
 */
class DialogAddBlackList : DialogFragment(), RequestManager, DialogInterface.OnKeyListener{

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
            inflater?.inflate(R.layout.dialog_addblack, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_blacklist_no.setOnClickListener {//不再提示
            dismissAllowingStateLoss()
        }

        tv_blacklist_ok.setOnClickListener {
            dismissAllowingStateLoss()
            dialogListener?.onClick(1,"addBlacklist")
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