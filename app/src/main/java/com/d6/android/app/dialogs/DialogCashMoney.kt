package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.UserData
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CashierInputFilter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_cash_widthdrawal.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 约会发送出错
 */
class DialogCashMoney : DialogFragment(),RequestManager {

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
            inflater?.inflate(R.layout.dialog_cash_widthdrawal, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mUserInfo:UserData = (arguments.get("data") as UserData)
        var cashmoney = arguments.getString("cashmoney")

        iv_cash_headView.setImageURI(mUserInfo.picUrl)
        tv_cash_money.text = String.format(getString(R.string.string_cash_money),cashmoney)

        tv_close.setOnClickListener {
            KeyboardktUtils().hideKeyboard(it)
            dismissAllowingStateLoss()
        }

        val filters = arrayOf<InputFilter>(CashierInputFilter())
        et_cash_input.filters = filters
        et_cash_input.addTextChangedListener(object:TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                if(s.isNullOrEmpty()){
                    tv_cashok.backgroundDrawable = ContextCompat.getDrawable(context,R.drawable.shape_4r_80_orange)
                }else{
                    tv_cashok.backgroundDrawable = ContextCompat.getDrawable(context,R.drawable.shape_orange)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        tv_cashok.setOnClickListener {
             var money = et_cash_input.text.toString().toFloat()
             if(money<=cashmoney.toFloat()){

             }else{
                 showToast("提现金额必须小于可提金额！")
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

    override fun onBind(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun dismissDialog() {

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}