package com.d6.android.app.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.*
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
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.dialog_yesorno.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 更新dialog
 */
class DialogYesOrNo : DialogFragment(), RequestManager, DialogInterface.OnKeyListener{

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return mCancelable
        }
        return mCancelable
    }

    var mCancelable:Boolean = false
    private var myAppointment:MyAppointment?=null

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
            inflater?.inflate(R.layout.dialog_yesorno, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var resmsg = if(arguments.containsKey("resmsg")){
            arguments.getString("msg")
        }else{
            "你们已经解锁聊天，本次赴约申请免费，是否继续申请赴约"
        }
        var code = arguments.getString("code")

        if(TextUtils.equals("3",code)){
            myAppointment = if (arguments != null) {
                arguments.getSerializable("data") as MyAppointment
            } else {
                MyAppointment()
            }
        }

        tv_yesorno_no.setOnClickListener {//不再提示
            dismissAllowingStateLoss()
        }

        tv_yesorno_yes.setOnClickListener {
            if(TextUtils.equals("3",code)){
                getData()
            }
        }

        tv_yesorno_content.text = resmsg
    }
    private fun getData() {
        dismissAllowingStateLoss()
        isBaseActivity {
            Request.signUpdate(getLocalUserId(),myAppointment?.sId.toString(),"").request(it,success = { msg, data ->
                RongIM.getInstance().startConversation(it, Conversation.ConversationType.PRIVATE, "${myAppointment?.iAppointUserid}", "${myAppointment?.sAppointUserName}")
            }) { code, msg ->
                if(code == 3){
                    var openErrorDialog = OpenDateErrorDialog()
                    openErrorDialog.arguments= bundleOf("code" to code)
                    openErrorDialog.show(it.supportFragmentManager, "d")
                }else{
                    CustomToast.showToast(msg)
                }
            }
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