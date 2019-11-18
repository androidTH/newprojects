package com.d6.android.app.adapters

import com.d6.android.app.R
import com.d6.android.app.activities.ReportActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getLoginToken
import com.d6.android.app.utils.isAuthUser
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.VoiceChatListView
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/**
 *
 */
class VoiceChatListAdapter(mData:ArrayList<Square>): HFRecyclerAdapter<Square>(mData, R.layout.item_list_voicechat) {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: Square) {
        val view = holder.bind<VoiceChatListView>(R.id.srv_view)
        view.update(data)
        view.sendDateListener {
            var appointment = it
            isBaseActivity {
                it.isAuthUser {
                    signUpVoiceChat(appointment)
                }
            }
        }

        view.setDeleteClick {
            doReport("${it.userid}","${it.id}",it.iIsAnonymous!!.toInt())
        }
    }

    private fun signUpVoiceChat(voiceChat:Square) {
        Request.getApplyVoiceSquareLovePoint("${voiceChat.id}", getLoginToken()).request(context as BaseActivity, false,success={ msg, data->
            var mApplyVoiceChatDialog = ApplyVoiceChatDialog()
            mApplyVoiceChatDialog.arguments = bundleOf("data" to voiceChat,"voicechatType" to "${voiceChat.iVoiceConnectType}")
            mApplyVoiceChatDialog.show((context as BaseActivity).supportFragmentManager, "d")
            mApplyVoiceChatDialog.setDialogListener { p, s ->
                // mData.remove(myAppointment)
                // notifyDataSetChanged()
            }
        }){code,msg->
            if(code==0){
                //不允许申请，弹出错误信息
                var openErrorDialog = OpenDateErrorDialog()
                var jsonObject = JSONObject(msg)
                var resMsg = jsonObject.optString("resMsg")
                openErrorDialog.arguments = bundleOf("code" to 5, "msg" to resMsg)
                openErrorDialog.show((context as BaseActivity).supportFragmentManager, "d")
            }else if(code==2){
                //申请需支付爱心 iAddPoint 需要支付的爱心数量
                var mApplyVoiceChatDialog = ApplyVoiceChatDialog()
//                var jsonObject = JSONObject(msg)
//                var iAddPoint = jsonObject.optString("iAddPoint")
                mApplyVoiceChatDialog.arguments = bundleOf("data" to voiceChat,"voicechatType" to "${voiceChat.iVoiceConnectType}")
                mApplyVoiceChatDialog.show((context as BaseActivity).supportFragmentManager, "d")
                mApplyVoiceChatDialog.setDialogListener { p, s ->

                }
            }else if(code==3){
                //申请需支付爱心，爱心不足，iAddPoint 需要支付的爱心，iRemainPoint剩余的爱心
                var mOpenDatePointNoEnoughDialog = OpenDatePointNoEnoughDialog()
                var jsonObject = JSONObject(msg)
                var iAddPoint = jsonObject.getString("iAddPoint")
                var iRemainPoint = jsonObject.getString("iRemainPoint")
                mOpenDatePointNoEnoughDialog.arguments = bundleOf("point" to "${iAddPoint}", "remainPoint" to iRemainPoint,"type" to 1)
                mOpenDatePointNoEnoughDialog.show((context as BaseActivity).supportFragmentManager, "d")
            }else if(code==4){
                //允许连麦，iAddPoint 为需要打赏的爱心数量
                var mApplyVoiceChatDialog = ApplyVoiceChatDialog()
                mApplyVoiceChatDialog.arguments = bundleOf("data" to voiceChat,"voicechatType" to "${voiceChat.iVoiceConnectType}")
                mApplyVoiceChatDialog.show((context as BaseActivity).supportFragmentManager, "d")
                mApplyVoiceChatDialog.setDialogListener { p, s ->
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
//        Request.queryAppointmentPoint(getLocalUserId(),"${myAppointment.iAppointUserid}").request(context as BaseActivity, false, success = { msg, data ->
//            var mApplyVoiceChatDialog = ApplyVoiceChatDialog()
//            mApplyVoiceChatDialog.arguments = bundleOf("data" to myAppointment, "explain" to data!!)
//            mApplyVoiceChatDialog.show((context as BaseActivity).supportFragmentManager, "d")
//            mApplyVoiceChatDialog.setDialogListener { p, s ->
//                mData.remove(myAppointment)
//                notifyDataSetChanged()
//            }
//        }) { code, msg ->
//            if (code == 2) {
//                var openErrorDialog = OpenDateErrorDialog()
//                var jsonObject = JSONObject(msg)
//                var resMsg = jsonObject.optString("resMsg")
//                openErrorDialog.arguments = bundleOf("code" to code, "msg" to resMsg)
//                openErrorDialog.show((context as BaseActivity).supportFragmentManager, "d")
//            }else if(code==3){
//                var  mDialogYesOrNo = DialogYesOrNo()
//                mDialogYesOrNo.arguments = bundleOf("code" to "${code}", "msg" to msg,"data" to myAppointment)
//                mDialogYesOrNo.show((context as BaseActivity).supportFragmentManager, "dialogyesorno")
//                mDialogYesOrNo.setDialogListener { p, s ->
//                    mData.remove(myAppointment)
//                    notifyDataSetChanged()
//                }
//            }
//        }
    }

    private fun doReport(userid:String,sDateId:String,iType:Int){
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