package com.d6.android.app.dialogs

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.RedMoneyDesActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.easypay.EasyPay
import com.d6.android.app.easypay.PayParams
import com.d6.android.app.easypay.callback.OnPayInfoRequestListener
import com.d6.android.app.easypay.callback.OnPayResultListener
import com.d6.android.app.easypay.enums.HttpType
import com.d6.android.app.easypay.enums.NetworkClientType
import com.d6.android.app.easypay.enums.PayWay
import com.d6.android.app.eventbus.FlowerMsgEvent
import com.d6.android.app.extentions.request
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.rong.bean.CustomSystemMessage
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import io.rong.eventbus.EventBus
import io.rong.imkit.RongIM
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.dialog_redmoneydesc.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 我的页面操作弹窗
 */
class RedMoneyDesDialog : DialogFragment() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private lateinit var sEnvelopeId:String

    private lateinit var sendUserId:String

    private lateinit var sEnvelopeDesc:String
    private var messageUId:String=""
    private var messageId:Int= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(matchParent, wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_redmoneydesc, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sEnvelopeId = arguments.getString("sEnvelopeId")
        sendUserId = arguments.getString("iUserId")
        sEnvelopeDesc = arguments.getString("sEnvelopeDesc")
        messageUId = arguments.getString("messageUId")
        messageId = arguments.getInt("messageId")

        ll_redwallet_desc.setOnClickListener {
            isBaseActivity {
                startActivity<RedMoneyDesActivity>("sEnvelopeId" to sEnvelopeId,"iUserId" to sendUserId,"sEnvelopeDesc" to sEnvelopeDesc)
                dismissAllowingStateLoss()
            }
        }

        tv_close_redwallet.setOnClickListener {
            dismissAllowingStateLoss()
        }

        iv_redwallet_open.setOnClickListener {
              reveiveEnvelope()
        }

        getUserInfo(sendUserId)

        if(TextUtils.isEmpty(sEnvelopeDesc.trim())){
            tv_redwallet_desc.visibility = View.GONE
        }
        tv_redwallet_desc.text ="“${sEnvelopeDesc}”"

        findEnvelopeById()
    }

    private fun getUserInfo(id: String) {
        Request.getUserInfo(userId, id).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                iv_redwallet_headView.setImageURI(it.picUrl)
                tv_redwallet_name.text = it.name
                tv_redheart_desc.text = "的小心心红包[img src=redheart_small/]"
            }
        }){code,msg->
            toast(msg)
        }
    }

    private fun findEnvelopeById(){
        //返回数据：
        //iIsGet  0 ：未领取  1：已领取
        //iStatus  1、已发出 2、已领完 3、已过期  {"sGuid":"d8c30bb2-c4c2-401b-a20c-80a6321cd0bc","iUserId":103170,"iLovePoint":6,"iLoveCount":3,"iType":2,"sResourceId":"cf0549c2-a702-4992-ae69-623662dda9f3","sSendUserName":null,"sEnvelopeDesc":"","iIsGet":1,"iGetLovePoint":2,"iRemainCount":2,"iRemainPoint":4,"dCreatetime":1614487621000,"iStatus":3}
        //iGetLovePoint 领取的爱心数量  iLoveCount   iRemainCount这两个字段
        Request.findEnvelopeById(sEnvelopeId).request((context as BaseActivity),false,success = {msg,data->
            data?.let {
                var isGet = it.iIsGet// it.optInt("iIsGet ",0)
                var iStatus = it.iStatus//it.optInt("iStatus ",0)
                if(isGet==0){
                    if(TextUtils.equals(userId,sendUserId)){
                        ll_redwallet_desc.visibility = View.VISIBLE
                        if(iStatus==2){
                            iv_redwallet_open.visibility = View.GONE
                            tv_redwallet_status.text = "手慢了，红包派完了"

                            updateMessageExtra("300")
                        }else if(iStatus==3){
                            iv_redwallet_open.visibility = View.GONE
                            tv_redwallet_status.text = "该红包已超过24小时\n" +
                                    "已过期无法领取"
                            updateMessageExtra("400")
                        }else{
                            iv_redwallet_open.visibility = View.VISIBLE
                        }
                    }else{
                        if(iStatus==2){
                            iv_redwallet_open.visibility = View.GONE
                            ll_redwallet_desc.visibility = View.VISIBLE
                            tv_redwallet_status.text = "手慢了，红包派完了"

                            updateMessageExtra("300")
                        }else if(iStatus==3){
                            iv_redwallet_open.visibility = View.GONE
                            ll_redwallet_desc.visibility = View.VISIBLE
                            tv_redwallet_status.text = "该红包已超过24小时\n" +
                                    "已过期无法领取"

                            updateMessageExtra("400")
                        }else{
                            ll_redwallet_desc.visibility = View.GONE
                            iv_redwallet_open.visibility = View.VISIBLE
                        }
                    }

                }else{
                    tv_redwallet_status.text = "${it.iGetLovePoint} [img src=redheart_small/]"
                    ll_redwallet_desc.visibility = View.VISIBLE
                    iv_redwallet_open.visibility = View.GONE
                }


//                RongIMClient.getInstance().updateMessageExpansion(null,"$messageUId",object : RongIMClient.OperationCallback(){
//                    override fun onError(p0: RongIMClient.ErrorCode?) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onSuccess() {
//                        TODO("Not yet implemented")
//                    }
//                })
            }
        }){code,msg->
            toast(msg)
        }
    }

    private fun reveiveEnvelope(){
        Request.reveiveEnvelope(sEnvelopeId).request((context as BaseActivity),false,success = {msg,data->
             data?.let {
                 Log.i("RedMoneyDesDialog","json=${it}")
                 iv_redwallet_open.visibility = View.GONE
                 var resCode = it.optInt("resCode")
                 if(resCode==100){
                     tv_redwallet_status.text = "已领取红包，不能重复领取"
                 }else if(resCode==200){
                     tv_redwallet_status.text = "领取成功"
                 }else if(resCode==300){
                     tv_redwallet_status.text = "手慢了，红包派完了"
                 }else if(resCode==400){
                     tv_redwallet_status.text = "该红包已超过24小时\n" +
                             "已过期无法领取"
                 }

                 updateMessageExtra("$resCode")

                 startActivity<RedMoneyDesActivity>("sEnvelopeId" to sEnvelopeId,"iUserId" to sendUserId,"sEnvelopeDesc" to sEnvelopeDesc)
                 dismissAllowingStateLoss()

             }
        }){code,msg->

        }
    }

    private fun updateMessageExtra(code:String){
        RongIMClient.getInstance().setMessageExtra(messageId,"$code",object:RongIMClient.ResultCallback<Boolean>(){
            override fun onError(p0: RongIMClient.ErrorCode?) {

            }

            override fun onSuccess(p0: Boolean?) {
                if(dialogListener!=null){
                    dialogListener?.onClick(1,code)
                }
            }
        })
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
}