package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
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
import com.d6.android.app.models.Square
import com.d6.android.app.models.VoiceTips
import com.d6.android.app.net.Request
import com.d6.android.app.rong.RongD6Utils
import com.d6.android.app.rong.bean.VoiceChatMsgContent
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.SENDLOVEHEART_DIALOG
import com.d6.android.app.widget.CustomToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.rong.callkit.RongCallKit
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.dialog_apply_voicechat.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent
import org.jetbrains.anko.support.v4.startActivity

/**
 * 申请连麦
 */
class ApplyVoiceChatOfDateDialog : DialogFragment(),RequestManager {

    private var appointment: MyAppointment?=null
    private var voicechatType = "1"
    private var mLocalUserLoveHeartCount:Int = -1
    private var mMinLoveHeart:Int? = -1
    private var extra:String = ""
    var mVoiceTips = VoiceTips()

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

        appointment = if (arguments != null) {
            arguments.getSerializable("data") as MyAppointment
        } else {
            MyAppointment("")
        }
        voicechatType = arguments.getString("voicechatType","1")

        if(TextUtils.equals(voicechatType,"1")){
            ll_voicechat_desc.visibility = View.GONE
            tv_voicechat_title.text = "为了营造良好的社区氛围，请在聊天中文明用语，如果被对方举报，查实将会有封号的风险"
            tv_action.text = "连麦"
        }else if(TextUtils.equals(voicechatType,"2")){
            mMinLoveHeart = appointment?.iOncePayLovePoint
            ll_voicechat_desc.visibility = View.VISIBLE
            tv_agree_points.text = "1.预付${mMinLoveHeart}个喜欢"
            tv_voicechat_title.text = "本次连麦需要打赏${mMinLoveHeart}个 [img src=redheart_small/]，打赏的喜欢将会在聊天结束后扣除"
            tv_action.text ="预付${mMinLoveHeart}个喜欢 [img src=redheart_small/]"
        }else{
            ll_voicechat_desc.visibility = View.GONE
            tv_voicechat_title.text = "本次连麦可获得${appointment?.iOncePayLovePoint}个 [img src=redheart_small/]，连麦结束后即可到账"
            tv_action.text = "连麦"
        }

        tv_action.setOnClickListener {
            if(TextUtils.equals(voicechatType,"4")){
                extra = GsonHelper.getGson().toJson(mVoiceTips)
                RongD6Utils.startSingleVoiceChat((context as BaseActivity),"${appointment?.iAppointUserid}", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO,extra)
//              sendOutgoingMessage()
                dismissAllowingStateLoss()
            }else{
                if(TextUtils.equals(voicechatType,"2")){
                    if(mLocalUserLoveHeartCount>=mMinLoveHeart?.toInt() ?: 0){
                        getData()
                    }
                }else{
                    getData()
                }
            }
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

        if(TextUtils.equals(voicechatType,"2")){
            getUserInfo()
        }
        mVoiceTips.setVoiceChatContent("${appointment?.sDesc}")
        mVoiceTips.setVoiceChatUName("${appointment?.sAppointUserName}")
        appointment?.iVoiceConnectType?.let { mVoiceTips.setVoiceChatType(it) }
    }

    private fun getUserInfo() {
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                if(it.iLovePoint < mMinLoveHeart?.toInt() ?: 0){
                    tv_action.background = ContextCompat.getDrawable(context,R.drawable.shape_radius_4r_33)
                }
                mLocalUserLoveHeartCount = it.iLovePoint
                ll_user_lovepoint.visibility = View.GONE
                tv_redheart_count.text = "剩余 [img src=redheart_small/] 不足 (剩余${mLocalUserLoveHeartCount})"
            }
        })
    }

    private fun getData() {
//        dismissAllowingStateLoss()
        isBaseActivity {
            var mActivity = it
            Request.addVoiceChat("${appointment?.sId}", getLoginToken()).request(mActivity,false,success={msg,data->
                data?.let {
                    var sAppointSignupId = it.optString("sAppointSignupId")
                    Log.i("applyvoice","${data}---${sAppointSignupId}")
                    mVoiceTips.setVoiceChatId("${sAppointSignupId}")
                    if(TextUtils.equals(voicechatType,"1")){
                        //1 无需打赏
                        extra = GsonHelper.getGson().toJson(mVoiceTips)
                        RongD6Utils.startSingleVoiceChat(mActivity,"${appointment?.iAppointUserid}", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO,extra)
//                  sendOutgoingMessage()
                        dismissAllowingStateLoss()
                    }else if(TextUtils.equals(voicechatType,"2")){
                        //申请者需要打赏
                        tv_action.text = "连麦"
                        voicechatType = "4"
                    }else {
                        //申请者可以获得
                        extra = GsonHelper.getGson().toJson(mVoiceTips)
                        RongD6Utils.startSingleVoiceChat(mActivity,"${appointment?.iAppointUserid}", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO,extra)
//                  sendOutgoingMessage()
                        dismissAllowingStateLoss()
                    }
                }
            }){code,msg->
                if(code==3||code==4){
                    CustomToast.showToast(msg)
//                    var openErrorDialog = OpenDateErrorDialog()
//                    var openErrorDialog = OpenDatePointNoEnoughDialog
//                    openErrorDialog.arguments= bundleOf("code" to code)
//                    openErrorDialog.show(it.supportFragmentManager, "d")
                }
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

    private fun sendOutgoingMessage(){
        var voicechatMsg = VoiceChatMsgContent.obtain("连麦", GsonHelper.getGson().toJson(""))
        RongIM.getInstance().insertOutgoingMessage(Conversation.ConversationType.PRIVATE, "${appointment?.iAppointUserid}", Message.SentStatus.RECEIVED,voicechatMsg, object : RongIMClient.ResultCallback<Message>() {
            override fun onSuccess(message: Message) {

            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {

            }
        })
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