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
import com.d6.android.app.activities.MyPointsActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.rong.RongCallKitUtils
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.SENDLOVEHEART_DIALOG
import com.d6.android.app.widget.CustomToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.rong.callkit.RongCallKit
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.dialog_apply_voicechat.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent
import org.jetbrains.anko.support.v4.startActivity

/**
 * 申请连麦
 */
class ApplyVoiceChatDialog : DialogFragment(),RequestManager {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var myAppointment:MyAppointment?=null
    private var voicechatType = "1"
    private var mLocalUserLoveHeartCount:Int = -1
    private var mMinLoveHeart=99

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
            inflater?.inflate(R.layout.dialog_apply_voicechat, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myAppointment = if (arguments != null) {
            arguments.getSerializable("data") as MyAppointment
        } else {
            MyAppointment()
        }
        voicechatType = arguments.getString("voicechatType","1")
        if(TextUtils.equals(voicechatType,"1")){
            ll_voicechat_desc.visibility = View.GONE
            tv_voicechat_title.text = "为了营造良好的社区氛围，请在聊天中文明用语，如果被对方举报，查实将会有封号的风险"
            tv_action.text = "连麦"
        }else if(TextUtils.equals(voicechatType,"2")){
            ll_voicechat_desc.visibility = View.VISIBLE
            tv_voicechat_title.text = "本次连麦需要打赏xx个 [img src=redheart_small/]，打赏的喜欢将会在聊天结束后扣除"
        }else{
            ll_voicechat_desc.visibility = View.GONE
            tv_voicechat_title.text = "本次连麦可获得xx个 [img src=redheart_small/]，连麦结束后即可到账"
            tv_action.text = "连麦"
        }

        tv_action.setOnClickListener {
            getData()
        }

        tv_redheart_gobuy.setOnClickListener {
            isBaseActivity {
                startActivity<MyPointsActivity>("fromType" to SENDLOVEHEART_DIALOG)
                dismissAllowingStateLoss()
            }
        }

        tv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        getUserInfo()
    }

    private fun getUserInfo() {
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                mLocalUserLoveHeartCount = it.iLovePoint
                if(mLocalUserLoveHeartCount<mMinLoveHeart){
                    tv_action.background = ContextCompat.getDrawable(context,R.drawable.shape_radius_4r_33)
                    ll_user_lovepoint.visibility = View.GONE
                    tv_redheart_count.text = "剩余 [img src=redheart_small/] 不足 (剩余${mLocalUserLoveHeartCount})"
                }else{
                    ll_user_lovepoint.visibility = View.GONE
                }
            }
        })
    }

    private fun getData() {
//        dismissAllowingStateLoss()
        isBaseActivity {
            if(TextUtils.equals(voicechatType,"1")){//1 无需打赏
                RongCallKitUtils.startSingleVoiceChat(it,"${myAppointment!!.iAppointUserid}", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO)
                dismissAllowingStateLoss()
            }else if(TextUtils.equals(voicechatType,"2")){//申请者需要打赏
                if(mLocalUserLoveHeartCount>mMinLoveHeart){
                    tv_action.text = "连麦"
                    voicechatType = "3"
                }
            }else {//申请者可以获得
                RongCallKitUtils.startSingleVoiceChat(it,"${myAppointment!!.iAppointUserid}", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO)
                dismissAllowingStateLoss()
            }

            //194ecdb4-4809-4b2d-bf32-42a3342964df
//            Request.signUpdate(userId,myAppointment?.sId.toString(),"").request(it,success = { msg, data ->
////                var openSuccessDialog = OpenDateSuccessDialog()
////                var sId = data?.optString("sId")
////                var explain = arguments.getParcelable("explain") as IntegralExplain
////                openSuccessDialog.arguments = bundleOf("point" to explain.iAppointPoint.toString(),"sId" to sId.toString())
////                openSuccessDialog.show(it.supportFragmentManager, "d")
//                if(myAppointment?.iIsAnonymous==1){
//                    RongIM.getInstance().startConversation(it, Conversation.ConversationType.GROUP, "anoy_${myAppointment?.iAppointUserid}_${getLocalUserId()}", "匿名")
//                }else{
//                    RongIM.getInstance().startConversation(it, Conversation.ConversationType.PRIVATE, "${myAppointment?.iAppointUserid}", "${myAppointment?.sAppointUserName}")
//                }
//                dialogListener?.onClick(2,myAppointment?.sId.toString())
//            }) { code, msg ->
//                if(code == 3){
//                    var openErrorDialog = OpenDateErrorDialog()
//                    openErrorDialog.arguments= bundleOf("code" to code)
//                    openErrorDialog.show(it.supportFragmentManager, "d")
//                }else{
//                    CustomToast.showToast(msg)
//                }
//            }
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