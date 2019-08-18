package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.IntegralExplain
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_checkin_points.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * 约会发送
 */
class CheckInPointsDialog : DialogFragment(),RequestManager {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var fromType = ""

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
            inflater?.inflate(R.layout.dialog_checkin_points, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        fromType = arguments.getString("fromType","")
        tv_checkin_action.setOnClickListener {
             dismissAllowingStateLoss()
        }

        tv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        rl_first_day.setOnClickListener {
            rl_first_day.background = ContextCompat.getDrawable(context,R.mipmap.daycoin_bg_get)
            tv_first_checkin_points.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)
            tv_first_checkin_points.text = "+50积分"
            tv_checkin_action.background = ContextCompat.getDrawable(context,R.drawable.shape_80orange_4r)
        }

        rl_second_day.setOnClickListener {
            rl_second_day.background = ContextCompat.getDrawable(context,R.mipmap.daycoin_bg_get)
            tv_second_checkin_points.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)
            tv_second_checkin_points.text = "+50积分"
            tv_checkin_action.background = ContextCompat.getDrawable(context,R.drawable.shape_80orange_4r)
        }

        rl_three_day.setOnClickListener {
            rl_three_day.background = ContextCompat.getDrawable(context,R.mipmap.daycoin_bg_get)
            tv_three_checkin_points.textColor = ContextCompat.getColor(context,R.color.color_F7AB00)
            tv_three_checkin_points.text = "+150积分"
            tv_checkin_action.background = ContextCompat.getDrawable(context,R.drawable.shape_80orange_4r)
        }


        if (arguments != null) {

        }
    }

    private fun getData() {
        dismissAllowingStateLoss()
        isBaseActivity {

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