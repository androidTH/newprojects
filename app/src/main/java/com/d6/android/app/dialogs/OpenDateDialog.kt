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
import kotlinx.android.synthetic.main.dialog_date_send.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 约会发送
 */
class OpenDateDialog : DialogFragment(),RequestManager {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var myAppointment:MyAppointment?=null
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
            inflater?.inflate(R.layout.dialog_date_send, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myAppointment = if (arguments != null) {
            arguments.getSerializable("data") as MyAppointment
        } else {
            MyAppointment()
        }
        fromType = arguments.getString("fromType","")
        tv_action.setOnClickListener {
            if(TextUtils.isEmpty(fromType)){
                getData()
            }else{
                dialogListener?.onClick(1,myAppointment?.sId.toString())
                dismissAllowingStateLoss()
            }
        }

        tv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

       if(arguments !=null){
           var it = arguments.getParcelable("explain") as IntegralExplain
           tv_preparepoints.text = "本次申请将预付${it.iAppointPoint}积分，申请之后双方将获得3天的限时免费聊天机会。先聊一聊彼此了解之后再决定是否邀约哦～"
           tv_agree_points.text = "达成邀约，扣除预付积分"
           tv_noagree_points.text = "对方拒绝，返还一半积分"//${it.iAppointPointRefuse}
           tv_timeout_points.text = "3天后未回复，返还全部积分"
           myAppointment?.let {
               if(it.hasGift){
                   tv_gift_tips.visibility = View.VISIBLE
                   tv_self_gift.visibility = View.VISIBLE
                   tv_self_gift.text = "邀约礼物·${it.giftName}(${it.giftLoveNum}颗 [img src=redheart_small/])" //x${it.giftNum}
               }else{
                   tv_gift_tips.visibility = View.GONE
                   tv_self_gift.visibility = View.GONE
               }
           }
        }
    }

    private fun getData() {
        dismissAllowingStateLoss()
        isBaseActivity {
            //194ecdb4-4809-4b2d-bf32-42a3342964df
            Request.signUpdate(userId,myAppointment?.sId.toString(),"").request(it,success = { msg, data ->
//                var openSuccessDialog = OpenDateSuccessDialog()
//                var sId = data?.optString("sId")
//                var explain = arguments.getParcelable("explain") as IntegralExplain
//                openSuccessDialog.arguments = bundleOf("point" to explain.iAppointPoint.toString(),"sId" to sId.toString())
//                openSuccessDialog.show(it.supportFragmentManager, "d")
                if(myAppointment?.iIsAnonymous==1){
                    RongIM.getInstance().startConversation(it, Conversation.ConversationType.GROUP, "anoy_${myAppointment?.iAppointUserid}_${getLocalUserId()}", "匿名")
                }else{
                    RongIM.getInstance().startConversation(it, Conversation.ConversationType.PRIVATE, "${myAppointment?.iAppointUserid}", "${myAppointment?.sAppointUserName}")
                }
                dialogListener?.onClick(2,myAppointment?.sId.toString())
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

    private fun queryPoints(){
//        isBaseActivity {
//            Request.queryAppointmentPoint(userId).request(it, success = {msg,data->
//                data?.let {
//                    tv_preparepoints.text = "本次约会将预付${it.iAppointPoint}积分"
//                    tv_agree_points.text = "对方同意,预付${it.iAppointPoint}积分"
//                    tv_noagree_points.text = "对方拒绝,返还${it.iAppointPointRefuse}积分"
//                    tv_timeout_points.text = "超时未回复,返还${it.iAppointPointCancel}积分"
//                }
//            }){code,msg->
//                if(code == 2){
//                    toast(msg)
//                    dismissAllowingStateLoss()
//                    var openErrorDialog = OpenDateErrorDialog()
//                    openErrorDialog.arguments= bundleOf("code" to code)
//                    openErrorDialog.show(it.supportFragmentManager, "d")
//                }
//            }
//        }
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