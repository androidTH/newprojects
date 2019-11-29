package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.activities.ReportActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CONVERSATION_APPLAY_DATE_TYPE
import com.d6.android.app.utils.Const.VoiceChatType
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.SelfPullDateView
import com.d6.android.app.widget.VoiceChatView
import com.umeng.socialize.utils.Log.toast
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject

/**
 *
 */
class SelfPullDateAdapter(mData:ArrayList<MyAppointment>): HFRecyclerAdapter<MyAppointment>(mData, R.layout.item_list_pull_date) {


    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: MyAppointment) {
        val view = holder.bind<SelfPullDateView>(R.id.srv_view)
        var voicechat_view = holder.bind<VoiceChatView>(R.id.voicechat_view)
        if(data.iAppointType!=VoiceChatType){
            voicechat_view.visibility = View.GONE
            view.visibility = View.VISIBLE
            view.update(data)
        }else{
            voicechat_view.visibility = View.VISIBLE
            view.visibility = View.GONE
            voicechat_view.update(data)
        }

        view.sendDateListener {
            var appointment = it
            isBaseActivity {
                it.isAuthUser {
                    signUpDate(appointment)
                }
            }
        }

        view.setDeleteClick {
            doReport("${it.iAppointUserid}","${it.sId}",it.iIsAnonymous!!.toInt(),it)
        }

        voicechat_view.sendVoiceChatListener {
            var appointment = it
            isBaseActivity {
                it.isAuthUser {
                    if(!TextUtils.equals(getLocalUserId(),"${appointment.iAppointUserid}")){
                        signUpVoiceChat(appointment)
                    }else{
                        it.toast("禁止连麦自己")
                    }
                }
            }
        }

        voicechat_view.setDeleteClick {
            doReport("${it.iAppointUserid}","${it.sId}",it.iIsAnonymous!!.toInt(),it)
        }
    }

    /**
     * 连麦
     */
    private fun signUpVoiceChat(appointment: MyAppointment) {
        Request.getApplyVoiceSquareLovePoint("${appointment.sId}", getLoginToken()).request(context as BaseActivity, false,success={ msg, data->
            data?.let {
                var iRemainPoint = it.optInt("iRemainPoint",0)
                var mApplyVoiceChatOfDateDialog = ApplyVoiceChatOfDateDialog()
                appointment.iPoint = iRemainPoint
                mApplyVoiceChatOfDateDialog.arguments = bundleOf("data" to appointment,"voicechatType" to "${appointment.iVoiceConnectType}")
                mApplyVoiceChatOfDateDialog.show((context as BaseActivity).supportFragmentManager, "d")
                mApplyVoiceChatOfDateDialog.setDialogListener { p, s ->
                }
            }
        }){code,msg->
            if(code==0){
                //不允许申请，弹出错误信息
                var openErrorDialog = OpenDateErrorDialog()
                var jsonObject = JSONObject(msg)
                var resMsg = jsonObject.optString("resMsg")
                openErrorDialog.arguments = bundleOf("code" to 5, "msg" to resMsg)
                openErrorDialog.show((context as BaseActivity).supportFragmentManager, "d")
            }else if(code==2||code==3){
                //2:申请需支付爱心 iAddPoint 需要支付的爱心数量  3:申请需支付爱心，爱心不足，iAddPoint 需要支付的爱心，iRemainPoint剩余的爱心
                var mApplyVoiceChatOfDateDialog = ApplyVoiceChatOfDateDialog()
                var jsonObject = JSONObject(msg)
                var iRemainPoint = jsonObject.optInt("iRemainPoint")
                appointment.iPoint = iRemainPoint
                mApplyVoiceChatOfDateDialog.arguments = bundleOf("data" to appointment,"voicechatType" to "${appointment.iVoiceConnectType}")
                mApplyVoiceChatOfDateDialog.show((context as BaseActivity).supportFragmentManager, "d")
                mApplyVoiceChatOfDateDialog.setDialogListener { p, s ->

                }
            }else if(code==4){
                //允许连麦，iAddPoint 为需要打赏的爱心数量
                var mApplyVoiceChatOfDateDialog = ApplyVoiceChatOfDateDialog()
                var jsonObject = JSONObject(msg)
                var iRemainPoint = jsonObject.optInt("iRemainPoint")
                appointment.iPoint = iRemainPoint
                mApplyVoiceChatOfDateDialog.arguments = bundleOf("data" to appointment,"voicechatType" to "${appointment.iVoiceConnectType}")
                mApplyVoiceChatOfDateDialog.show((context as BaseActivity).supportFragmentManager, "d")
                mApplyVoiceChatOfDateDialog.setDialogListener { p, s ->
                }
            }else if(code==5){
                //res=5，不允许连麦，对方预付的爱心已不足
                var openErrorDialog = OpenDateErrorDialog()
                var jsonObject = JSONObject(msg)
                var resMsg = jsonObject.optString("sAddPointDesc")
                openErrorDialog.arguments = bundleOf("code" to 5, "msg" to "${resMsg}")
                openErrorDialog.show((context as BaseActivity).supportFragmentManager, "d")

            }
        }
    }


    private fun signUpDate(myAppointment:MyAppointment) {
        Request.queryAppointmentPoint(userId,"${myAppointment.iAppointUserid}").request(context as BaseActivity, false, success = { msg, data ->
            val dateDialog = OpenDateDialog()
            dateDialog.arguments = bundleOf("data" to myAppointment, "explain" to data!!)
            dateDialog.show((context as BaseActivity).supportFragmentManager, "d")
//            var dateInfo = RengGongDialog()
//            var dateInfo = SelfDateDialog()
//            dateInfo.show((context as BaseActivity).supportFragmentManager, "rg")
            dateDialog.setDialogListener { p, s ->
                mData.remove(myAppointment)
                notifyDataSetChanged()
            }
        }) { code, msg ->
            if (code == 2) {
                var openErrorDialog = OpenDateErrorDialog()
                var jsonObject = JSONObject(msg)
                var resMsg = jsonObject.optString("resMsg")
                openErrorDialog.arguments = bundleOf("code" to code, "msg" to resMsg)
                openErrorDialog.show((context as BaseActivity).supportFragmentManager, "d")
            }else if(code==3){
                var  mDialogYesOrNo = DialogYesOrNo()
                mDialogYesOrNo.arguments = bundleOf("code" to "${code}", "msg" to msg,"data" to myAppointment)
                mDialogYesOrNo.show((context as BaseActivity).supportFragmentManager, "dialogyesorno")
                mDialogYesOrNo.setDialogListener { p, s ->

                }
            }
        }
    }

    private fun doReport(userid:String,sDateId:String,iType:Int,myAppointment: MyAppointment){
        val squareActionDialog = ShareFriendsDialog()
        squareActionDialog.arguments = bundleOf("from" to "selfPullDate","id" to userid,"sResourceId" to sDateId)
        squareActionDialog.show((context as BaseActivity).supportFragmentManager, "action")
        squareActionDialog.setDialogListener { p, s ->
            if (p == 0) {
                mData?.let {
                    startActivity(sDateId, "3")
                }
            }else if(p==2){
                isBaseActivity {
                    Request.addBlackList(userId, userid,iType).request(it) { _, _ ->
                        CustomToast.showToast(it.getString(R.string.string_blacklist_toast))
                    }
                }
            }else if(p==1){
                isBaseActivity {
                    //删除的方法
                    it.dialog()
                    Request.delAppointment(getLoginToken(),sDateId).request(it,false,success={_,_->
                        mData.remove(myAppointment)
                        notifyDataSetChanged()
                    }) {code,msg->
                        if(code==2){
                            toast(it,msg)
                        }
                    }
                }
            }
        }
    }

    private fun startActivity(id:String,tipType:String){
        context.startActivity<ReportActivity>("id" to id, "tiptype" to tipType)
    }


    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}