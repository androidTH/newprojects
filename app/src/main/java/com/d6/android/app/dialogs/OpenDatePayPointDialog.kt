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
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.dialog_date_paypoint.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 支付积分
 */
class OpenDatePayPointDialog : DialogFragment(),RequestManager {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private lateinit var chatUserId:String
    private lateinit var username:String;

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
            inflater?.inflate(R.layout.dialog_date_paypoint, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var point = if (arguments != null) {
            arguments.getString("point")
        } else {
            "0"
        }

        var remainPoint = arguments.getString("remainPoint")

        tv_payok.setOnClickListener {
            getData(point,remainPoint)
        }

        tv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

       if(arguments !=null){
           username = arguments.getString("username")
           chatUserId = arguments.getString("chatUserId")
           tv_mypointnums.text = "${point}积分"
           tv_payinfo.text = "支付后即可与${username}私聊"
        }
    }

    private fun getData(point:String,remainPoint:String) {
        dismissAllowingStateLoss()
        isBaseActivity {
            //194ecdb4-4809-4b2d-bf32-42a3342964df
            Request.doUnlockTalk(userId, chatUserId).request(it,success = {msg,data->
                RongIM.getInstance().startConversation(it, Conversation.ConversationType.PRIVATE, chatUserId, username)
            }){code,msg->
                var openErrorDialog = OpenDatePointNoEnoughDialog()
                openErrorDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
                openErrorDialog.show((context as BaseActivity).supportFragmentManager, "d")
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

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}