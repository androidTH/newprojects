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
import com.d6.android.app.models.Square
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.DateOfSquareView
import com.d6.android.app.widget.UserTrendView
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject

/**
 *动态
 */
class MySquareAdapter(mData: ArrayList<Square>,val type: Int) : HFRecyclerAdapter<Square>(mData, R.layout.item_list_user_square) {

    protected var mUserData: UserData? = null;

    override fun onBind(holder: ViewHolder, position: Int, data: Square) {
        val trendView = holder.bind<UserTrendView>(R.id.mTrendView)
        val dateofsquare_view = holder.bind<DateOfSquareView>(R.id.dateofsquare_view)
        data.sex = mUserData?.sex
        data.age = mUserData?.age

        if(data.classesid==66){
            trendView.visibility = View.GONE
            dateofsquare_view.visibility = View.VISIBLE
            dateofsquare_view.update(data)
        }else{
            trendView.visibility = View.VISIBLE
            dateofsquare_view.visibility = View.GONE
            trendView.update(data,if (type==0) 1 else 0 )
        }

        val count = data.appraiseCount ?: 0
        trendView.setPraiseClick {
            if (TextUtils.equals("1", data.isupvote)) {
                cancelPraise(data, count)
            } else {
                praise(data, count)
            }
        }

        trendView.setOnCommentClick {
            clickListener?.onItemClick(trendView, position)
        }
        trendView.setOnItemClick{v,s->
            mOnItemClickListener?.onItemClick(v,position)
        }

        trendView.setFlowerClick { square, lovePoint ->
            sendFlower(square,lovePoint)
        }

        trendView.setDeleteClick {
//            val squareActionDialog = SquareActionDialog()
//            squareActionDialog.arguments = bundleOf("id" to it.userid.toString())
//            squareActionDialog.show((context as BaseActivity).supportFragmentManager, "action")
//            squareActionDialog.setDialogListener { p, s ->
//                if (p == 1) {
//                    delete(data)
//                }
//            }

            val shareDialog = ShareFriendsDialog()
            shareDialog.arguments = bundleOf("from" to "mysquare","id" to it.userid.toString(),"sResourceId" to it.id.toString())
            var squareId = it.id.toString()
            shareDialog.show((context as BaseActivity).supportFragmentManager, "action")
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    mUserData?.let {
                        context.startActivity<ReportActivity>("id" to squareId, "tiptype" to "2")
                    }
                } else if (p == 1) {
                    delete(data)
                }
            }
        }

        trendView.onTogglePlay {
            mOnSquareAudioTogglePlay?.onSquareAudioPlayClick(position,it)
        }

        dateofsquare_view.sendDateListener {
            var appointment = it
            isBaseActivity {
                it.isAuthUser {
                    if(!TextUtils.equals(getLocalUserId(),appointment.userid)){
                        signUpDate(appointment)
                    }else{
                        it.toast("自己的约会禁止邀约")
                    }
                }
            }
        }

        dateofsquare_view.setDeleteClick {
            doReport("${it.userid}","${it.sAppointmentId}",it.iIsAnonymous!!.toInt(),data)
        }
    }

    fun setUserInfo(data: UserData){
           this.mUserData = data
    }

    private fun sendFlower(square:Square,lovePoint:Int){
        isBaseActivity {
//            var dialogSendRedFlowerDialog = SendRedFlowerDialog()
//            mData?.let {
//                dialogSendRedFlowerDialog.arguments = bundleOf("ToFromType" to 4,"userId" to square.userid.toString(),"square" to square)
//            }
//            dialogSendRedFlowerDialog.show(it.supportFragmentManager,"sendflower")
//
//            dialogSendRedFlowerDialog.setDialogListener { p, s ->
//                mData?.let {
//                    var index = it.indexOf(square)
//                    it.get(index).iFlowerCount = s.toString().toInt()+square.iFlowerCount!!.toInt()
//                    it.get(index).iIsSendFlower = 1
//                    notifyItemChanged(index+1)
//                }
//            }
            mData?.let {
                var index = it.indexOf(square)
                it.get(index).iLovePoint = lovePoint+square.iLovePoint!!.toInt()
                it.get(index).iSendLovePoint = lovePoint
                notifyItemChanged(index+1,"dddsasdf")
            }
            Request.sendLovePoint(getLoginToken(),"${square.userid}",lovePoint,1,"${square.id}").request(it,false,success={_,Data->
                var activity = it
                mData?.let {
                    Request.getUserInfo("", getLocalUserId()).request(activity,false,success = { _, data ->
                        data?.let {
                            SPUtils.instance().put(Const.User.USERLOVE_NUMS,it.iLovePoint).apply()
                        }
                    })
                }
            }){code,msg->
                mData?.let {
                    var index = it.indexOf(square)
                    it.get(index).iLovePoint = square.iLovePoint!!.toInt()-lovePoint
                    it.get(index).iSendLovePoint = -1
                    notifyItemChanged(index+1,"dddsasdf")
                }
                if (code == 2||code==3) {
                    var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                    mSendRedHeartEndDialog.show(it.supportFragmentManager, "redheartendDialog")
                }else{
                    it.toast(msg)
                }
            }
        }
    }



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

    private fun praise(square: Square, count: Int) {
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.addPraise(getLocalUserId(), square.id).request(it,false,success={ _, _ ->
                it.showToast("点赞成功")
                square.isupvote = "1"
                square.appraiseCount = count + 1
                notifyDataSetChanged()}
            ){code,msg->
                CustomToast.showToast(msg)
            }
        }
    }

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

    //举报
    private fun startActivity(id:String,tipType:String){
        context.startActivity<ReportActivity>("id" to id, "tiptype" to tipType)
    }

    private fun cancelPraise(square: Square, count: Int) {
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.cancelPraise(getLocalUserId(), square.id).request(it) { msg, _ ->
                it.showToast("取消点赞")
                square.isupvote = "0"
                square.appraiseCount = if (count - 1 < 0) 0 else count - 1
                if (type == 1) {//我点赞的。删除数据
                    mData.remove(square)
                }
                notifyDataSetChanged()
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

    private var clickListener: OnItemClickListener? = null
    fun setOnCommentClick(l: (p: Int) -> Unit) {
        clickListener = object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                l(position)
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

    private var mOnSquareAudioTogglePlay:OnSquareAudioTogglePlay?=null

    interface OnSquareAudioTogglePlay{
        fun onSquareAudioPlayClick(position:Int,square: Square)
    }
}