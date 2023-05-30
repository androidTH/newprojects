package com.d6.android.app.dialogs

import android.net.Uri
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
import com.d6.android.app.models.Square
import com.d6.android.app.models.UserData
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
import io.rong.callkit.util.CallKitUtils
import io.rong.imkit.RongContext
import io.rong.imkit.RongIM
import io.rong.imkit.utilities.PermissionCheckUtil
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.UserInfo
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

    private var voiceChat:Square?=null
    private var voicechatType = "1"
    private var mLocalUserLoveHeartCount:Int = -1
    private var mMinLoveHeart:Int = 0
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
        dialog.window.setLayout((screenWidth() * 0.83f).toInt()+dip(30), wrapContent)
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
        voiceChat = if (arguments != null) {
            arguments.getSerializable("data") as Square
        } else {
            Square()
        }
        voicechatType = arguments.getString("voicechatType","1")
        if(TextUtils.equals(voicechatType,"1")){
            ll_voicechat_desc.visibility = View.GONE
            tv_voicechat_title.text = "为了营造良好的社区氛围，请在聊天中文明用语，如果被对方举报，查实将会有封号的风险"
            tv_action.text = "连麦"
        }else if(TextUtils.equals(voicechatType,"2")){
            mMinLoveHeart = voiceChat?.iOncePayLovePoint!!
            tv_agree_points.text = "1.预付${mMinLoveHeart}个喜欢"
            ll_voicechat_desc.visibility = View.VISIBLE
            tv_voicechat_title.text = "本次连麦需要打赏${mMinLoveHeart}个 [img src=redheart_small/]，打赏的喜欢将会在聊天结束后扣除"
            tv_action.text ="预付${mMinLoveHeart}个喜欢 [img src=redheart_small/]"
        }else{
            ll_voicechat_desc.visibility = View.GONE
            tv_voicechat_title.text = "本次连麦可获得${voiceChat?.iOncePayLovePoint}个 [img src=redheart_small/]，连麦结束后即可到账"
            tv_action.text = "连麦"
        }

        tv_action.setOnClickListener {
            if(TextUtils.equals(voicechatType,"0")){
                PermissionsUtils.getInstance().checkPermissions((context as BaseActivity), CallKitUtils.getCallpermissions(), object : PermissionsUtils.IPermissionsResult {
                    override fun forbidPermissions() {

                    }

                    override fun passPermissions() {
                        getData()
                    }
                })
            }else{
                if(TextUtils.equals(voicechatType,"2")){
                    if(mLocalUserLoveHeartCount>=mMinLoveHeart){
                        //申请者需要打赏
                        tv_action.text = "连麦"
                        voicechatType = "0"
                    }else{
                        ll_user_lovepoint.visibility = View.VISIBLE
                        tv_action.background = ContextCompat.getDrawable(context,R.drawable.shape_radius_4r_33)
                    }
                }else{
                    PermissionsUtils.getInstance().checkPermissions((context as BaseActivity), CallKitUtils.getCallpermissions(), object : PermissionsUtils.IPermissionsResult {
                        override fun forbidPermissions() {

                        }

                        override fun passPermissions() {
                            getData()
                        }
                    })
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
//            getUserInfo()
            mLocalUserLoveHeartCount = voiceChat!!.iRemainPoint!!
            tv_redheart_count.text = "剩余 [img src=redheart_small/] 不足 (剩余${mLocalUserLoveHeartCount})"
        }
        mVoiceTips.setVoiceChatContent("${voiceChat?.content}")
        mVoiceTips.setVoiceChatUName("${voiceChat?.name}")
        mVoiceTips.setVoiceChatType(voiceChat?.iVoiceConnectType!!)

//        var info = UserInfo("${voiceChat?.userid}","${voiceChat?.name}", Uri.parse("${voiceChat?.picUrl}"))
//        RongContext.getInstance().currentUserInfo = info
    }

    private fun getUserInfo() {
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                //                if(it.iLovePoint < mMinLoveHeart?.toInt() ?: 0){
//                    tv_action.background = ContextCompat.getDrawable(context,R.drawable.shape_radius_4r_33)
//                }
                mLocalUserLoveHeartCount = it.iLovePoint
                tv_redheart_count.text = "剩余 [img src=redheart_small/] 不足 (剩余${mLocalUserLoveHeartCount})"
            }
        })
    }

    private fun getData() {
        isBaseActivity {
            var mActivity = it
            Request.addVoiceChat("${voiceChat?.sAppointmentId}", getLoginToken()).request(mActivity,false,success={msg,data->
                data?.let {
                    var sAppointSignupId = it.optString("sAppointSignupId")
                    mVoiceTips.setVoiceChatId("${sAppointSignupId}")
                    extra = GsonHelper.getGson().toJson(mVoiceTips)
                    RongIM.getInstance().startConversation(mActivity, Conversation.ConversationType.PRIVATE, "${voiceChat?.userid}", "${voiceChat?.name}")
                    RongD6Utils.startSingleVoiceChat(mActivity,"${voiceChat?.userid}", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO,extra)
                    dismissAllowingStateLoss()
                }
            }){code,msg->
                if(code==3||code==4){
                    CustomToast.showToast(msg)
                }
            }
        }
    }

    private fun sendOutgoingMessage(){
        var voicechatMsg = VoiceChatMsgContent.obtain("连麦", GsonHelper.getGson().toJson(""))
        RongIM.getInstance().insertOutgoingMessage(Conversation.ConversationType.PRIVATE, "${voiceChat?.userid}", Message.SentStatus.RECEIVED,voicechatMsg, object : RongIMClient.ResultCallback<Message>() {
            override fun onSuccess(message: Message) {

            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {

            }
        })
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