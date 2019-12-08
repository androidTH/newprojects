package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.activities.ReportActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.*
import com.d6.android.app.eventbus.FlowerMsgEvent
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.DateOfSquareView
import com.d6.android.app.widget.TrendView
import com.d6.android.app.widget.VoiceChatView
import com.d6.android.app.widget.gift.CustormAnim
import com.d6.android.app.widget.gift.GiftControl
import com.umeng.socialize.utils.Log.toast
import kotlinx.android.synthetic.main.item_audio.view.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject

/**
 *
 */
class SquareAdapter(mData: ArrayList<Square>) : HFRecyclerAdapter<Square>(mData, R.layout.item_list_square) {

    override fun onBind(holder: ViewHolder, position: Int, data: Square) {
        val trendView = holder.bind<TrendView>(R.id.mTrendView)
        trendView.visibility = View.VISIBLE
        trendView.update(data)

        trendView.setPraiseClick {
            if (TextUtils.equals("1", data.isupvote)) {
                cancelPraise(data)
            } else {
                praise(data)
            }
        }

        trendView.setOnCommentClick {
            clickListener?.onItemClick(trendView,position)
        }

        trendView.setOnItemClick {v,s->
            mOnItemClickListener?.onItemClick(v,position)
        }

        trendView.setOnSquareDetailsClick {
            mOnSquareDetailsClick?.onSquareDetails(position,it)
        }

        trendView.setDeleteClick {
            val shareDialog = ShareFriendsDialog()
            shareDialog.arguments = bundleOf("from" to "square","id" to it.userid.toString(),"sResourceId" to it.id.toString())
            shareDialog.show((context as BaseActivity).supportFragmentManager, "action")
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    mData?.let {
                        startActivity(data.id!!, "2")
                    }
                } else if (p == 1) {
                    delete(data)
                }
            }
        }

        trendView.sendFlowerClick { square, lovePoint ->
            //            trendView.addGiftNums(1,false,false)
//            trendView.doLoveHeartAnimation()
//            var dialogSendRedFlowerDialog = SendRedFlowerDialog()
//            dialogSendRedFlowerDialog.arguments= bundleOf("ToFromType" to 2,"userId" to it.userid.toString(),"square" to it)
//            dialogSendRedFlowerDialog.show((context as BaseActivity).supportFragmentManager,"sendflower")
            sendLoveHeart(square,lovePoint)
        }

        trendView.onTogglePlay {
            mOnSquareAudioTogglePlay?.onSquareAudioPlayClick(position,it)
        }
//        voicechat_view.sendVoiceChatListener {
//            var square = it
//            isBaseActivity {
//                it.isAuthUser {
//                    if(!TextUtils.equals(getLocalUserId(),square.userid)){
//                        signUpVoiceChat(square)
//                    }else{
//                        it.toast("禁止连麦自己")
//                    }
//                }
//            }
//        }
//
//        voicechat_view.setDeleteClick {
//            doReport("${it.userid}","${it.sAppointmentId}",it.iIsAnonymous!!.toInt(),data)
//        }
    }

    //举报
    private fun startActivity(id:String,tipType:String){
        context.startActivity<ReportActivity>("id" to id, "tiptype" to tipType)
    }

    private fun sendLoveHeart(square:Square,lovePoint:Int){
        isBaseActivity {
            square.iLovePoint = lovePoint+square.iLovePoint!!.toInt()
            square.iSendLovePoint = lovePoint
            notifyDataSetChanged()
            Request.sendLovePoint(getLoginToken(),"${square.userid}",lovePoint,1,"${square.id}").request(it,true,success={_,Data->
//                square.iLovePoint = lovePoint+square.iLovePoint!!.toInt() 2.11
//                notifyDataSetChanged()   2.11
//                EventBus.getDefault().post(FlowerMsgEvent(lovePoint,square))
                Request.getUserInfo("", getLocalUserId()).request(it,false,success = { _, data ->
                    data?.let {
                        SPUtils.instance().put(Const.User.USERLOVE_NUMS,it.iLovePoint).apply()
                    }
                })
            }){code,msg->
                if (code == 2||code==3) {
                    var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                    mSendRedHeartEndDialog.show(it.supportFragmentManager, "redheartendDialog")
                }else{
                    it.toast(msg)
                }
                square.iLovePoint = square.iLovePoint!!.toInt()-lovePoint
                square.iSendLovePoint = -1
                notifyDataSetChanged()
            }
        }
    }

    /**
     * 约会
     */
    private fun signUpDate(myAppointment:Square) {
        Request.queryAppointmentPoint(getLocalUserId(),"${myAppointment.userid}").request(context as BaseActivity, false, success = { msg, data ->
            val dateDialog = OpenDateDialog()
            var appoinment = MyAppointment(myAppointment.id)
            appoinment.iAppointUserid = myAppointment.userid?.toInt()
            appoinment.sAppointUserName = myAppointment.name
            dateDialog.arguments = bundleOf("data" to appoinment, "explain" to data!!)
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
                var appoinment = MyAppointment(myAppointment.id)
                appoinment.iAppointUserid = myAppointment.userid?.toInt()
                appoinment.sAppointUserName = myAppointment.name
                mDialogYesOrNo.arguments = bundleOf("code" to "${code}", "msg" to msg,"data" to appoinment)
                mDialogYesOrNo.show((context as BaseActivity).supportFragmentManager, "dialogyesorno")
                mDialogYesOrNo.setDialogListener { p, s ->
                    mData.remove(myAppointment)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun doReport(userid:String,sDateId:String,iType:Int,data:Square){
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
                    Request.addBlackList(getLocalUserId(), userid,iType).request(it) { _, _ ->
                        CustomToast.showToast(it.getString(R.string.string_blacklist_toast))
                    }
                }
            }else if(p==1){
                isBaseActivity {
                    //删除的方法
                    delete(data)
                }
            }
        }
    }

    private fun praise(square: Square) {
        isBaseActivity {
            it.dialog()
            Request.addPraise(getLocalUserId(), square.id).request(it,true) { msg, data ->
                showTips(data,"","")
                square.isupvote = "1"
                square.appraiseCount = (square.appraiseCount?:0) + 1
                notifyDataSetChanged()
            }
        }
    }

    //删除动态
    private fun delete(square: Square){
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.deleteSquare(getLocalUserId(), square.id).request(it) { _, _ ->
                it.showToast("删除成功")
                mData.remove(square)
                notifyDataSetChanged()
            }
        }
    }

    private fun cancelPraise(square: Square) {
        isBaseActivity {
            it.dialog()
            Request.cancelPraise(getLocalUserId(), square.id).request(it) { msg, _ ->
                square.isupvote = "0"
                square.appraiseCount = if (((square.appraiseCount?:0) - 1) < 0) 0 else (square.appraiseCount?:0) - 1
                notifyDataSetChanged()
            }
        }
    }

    fun setOnSquareDetailsClick(action:(position:Int,square:Square)->Unit) {
        this.mOnSquareDetailsClick = object : OnSquareDetailsClick {
            override fun onSquareDetails(position:Int,square: Square) {
                action(position,square)
            }
        }
    }

    fun setOnSquareAudioToggleClick(action:(position:Int,square:Square)->Unit) {
        this.mOnSquareAudioTogglePlay = object : OnSquareAudioTogglePlay {
            override fun onSquareAudioPlayClick(position: Int, square: Square) {
                action(position, square)
            }
        }
    }

    private var mOnSquareDetailsClick: OnSquareDetailsClick?=null
    private var mOnSquareAudioTogglePlay:OnSquareAudioTogglePlay?=null

    interface OnSquareDetailsClick{
        fun onSquareDetails(position:Int,square: Square)
    }

    interface OnSquareAudioTogglePlay{
        fun onSquareAudioPlayClick(position:Int,square: Square)
    }

    private var clickListener: OnItemClickListener? = null

    fun setOnCommentClick(l: (p: Int) -> Unit) {
        clickListener = object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                l(position)
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}