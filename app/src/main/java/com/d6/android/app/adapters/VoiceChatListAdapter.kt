package com.d6.android.app.adapters

import android.util.Log
import com.d6.android.app.R
import com.d6.android.app.activities.ReportActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.CONVERSATION_APPLAY_DATE_TYPE
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getLocalUserId
import com.d6.android.app.utils.isAuthUser
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.SelfPullDateView
import com.d6.android.app.widget.VoiceChatView
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/**
 *
 */
class VoiceChatListAdapter(mData:ArrayList<MyAppointment>): HFRecyclerAdapter<MyAppointment>(mData, R.layout.item_list_voicechat) {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: MyAppointment) {
        val view = holder.bind<VoiceChatView>(R.id.srv_view)
        view.update(data)
        view.sendDateListener {
            var appointment = it
            isBaseActivity {
                it.isAuthUser {
                    signUpDate(appointment)
                }
            }
        }

        view.setDeleteClick {
            doReport(it.iAppointUserid.toString(),it.sId.toString(),it.iIsAnonymous!!.toInt())
        }
    }

    private fun signUpDate(myAppointment:MyAppointment) {
        Request.queryAppointmentPoint(getLocalUserId(),"${myAppointment.iAppointUserid}").request(context as BaseActivity, false, success = { msg, data ->
//            var mApplyVoiceChatDialog = ApplyVoiceChatDialog()
//            mApplyVoiceChatDialog.arguments = bundleOf("data" to myAppointment, "explain" to data!!)
//            mApplyVoiceChatDialog.show((context as BaseActivity).supportFragmentManager, "d")
//            mApplyVoiceChatDialog.setDialogListener { p, s ->
//                mData.remove(myAppointment)
//                notifyDataSetChanged()
//            }
//            var mApplyVoiceChatPointsDialog = ApplyVoiceChatPointsDialog()
//            mApplyVoiceChatPointsDialog.show((context as BaseActivity).supportFragmentManager, "d")
//            mApplyVoiceChatPointsDialog.setDialogListener { p, s ->
//                mData.remove(myAppointment)
//                notifyDataSetChanged()
//            }

            var mRewardVoiceChatPointsDialog = RewardVoiceChatPointsDialog()
            mRewardVoiceChatPointsDialog.show((context as BaseActivity).supportFragmentManager, "d")
            mRewardVoiceChatPointsDialog.setDialogListener { p, s ->
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
                    mData.remove(myAppointment)
                    notifyDataSetChanged()
                }
            }
        }
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