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
import org.jetbrains.anko.wrapContent

/**
 * 我的页面操作弹窗
 */
class RedMoneyDesDialog : DialogFragment() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var mToFromType = 0//1 动态详情页送小红花 2 动态列表送小红花 3 聊天输入框扩展框 4 个人信息页动态送小红花 5 申请私聊送小红花

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
        mToFromType = arguments.getInt("ToFromType")

        ll_redwallet_desc.setOnClickListener {
            isBaseActivity {
                startActivity<RedMoneyDesActivity>()
            }
        }

        tv_close_redwallet.setOnClickListener {
            dismissAllowingStateLoss()
        }

    }

    private fun getUserInfo(id: String) {
        Request.getUserInfo(userId, id).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {

            }
        })
    }

    private fun getFlowerList(){
        Request.getUserFlower().request((context as BaseActivity),false,success = {msg,data->

        })
    }

    private fun BuyRedFlowerSuccess(id:String,flowerCount:String){
        val dialogSendFlowerSuccess = DialogSendFlowerSuccess()
            dialogSendFlowerSuccess.arguments = bundleOf("ToFromType" to mToFromType ,"userId" to id,"nums" to flowerCount)
            dialogSendFlowerSuccess.show((context as BaseActivity).supportFragmentManager, "sendflower")
    }

    private fun sendSysMessage(targetId:String){
         var systemmsg = CustomSystemMessage("申请置顶")
         var richContentMessage = CustomSystemMessage.obtain("申请置顶",GsonHelper.getGson().toJson(systemmsg))
         var msg = Message.obtain(targetId, Conversation.ConversationType.PRIVATE, richContentMessage);
         RongIM.getInstance().sendMessage(msg, null, null, object : IRongCallback.ISendMessageCallback{
            override fun onAttached(p0: Message?) {
            }

            override fun onError(p0: Message?, p1: RongIMClient.ErrorCode?) {

            }

            override fun onSuccess(p0: Message?) {
            }
        })
    }

    private fun buyRedFlowerPay(flowerCount:Int,receiverUserId:String,sResourceid:String,iOrderType:Int){
        val params = PayParams.Builder((context as BaseActivity))
                .wechatAppID(Const.WXPAY_APP_ID)// 仅当支付方式选择微信支付时需要此参数
                .payWay(PayWay.WechatPay)
                .UserId(userId.toInt())
                .setSUserLoginToken(getLoginToken())
                .setSendUserid(receiverUserId.toInt())
                .setSResourceid(sResourceid)
                .iFlowerCount(flowerCount)
                .goodsPrice(flowerCount)// 单位为：分 pointRule.iPrice
                .goodsName("小红花")
                .goodsIntroduction("")
                .httpType(HttpType.Post)
                .httpClientType(NetworkClientType.Retrofit)
                .requestBaseUrl(API.BASE_URL)// 此处替换为为你的app服务器host主机地址
                .setType(1)
                .build()
        EasyPay.newInstance(params).requestPayInfo(object : OnPayInfoRequestListener {
            override fun onPayInfoRequetStart() {
            }

            override fun onPayInfoRequstSuccess() {

            }

            override fun onPayInfoRequestFailure() {
            }
        }).toPay(object : OnPayResultListener {
            override fun onPaySuccess(payWay: PayWay?,orderId:String) {
                Log.i("redflowerorderId",orderId)
                if(!TextUtils.isEmpty(orderId)){
                    checkOrderStatus(receiverUserId,orderId,flowerCount.toString(),iOrderType)
                }
            }

            override fun onPayCancel(payWay: PayWay?) {
            }

            override fun onPayFailure(payWay: PayWay?, errCode: Int) {

            }
        })
    }

    /**
     * 检查订单的状态
     */
    private fun checkOrderStatus(receiverUserId:String,orderId:String,flowerCount:String,iOrderType:Int){
        if(context!=null){
            if(mToFromType!=5){
                BuyRedFlowerSuccess(receiverUserId,flowerCount)
            }
            Request.getOrderById(orderId,iOrderType).request((context as BaseActivity),false,success={msg,data->
//                Request.sendFlowerByOrderId(userId,receiverUserId,orderId,mSquareId).request((context as BaseActivity),true,success={msg,data->
//                    if(mToFromType == 1){
//                        EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt()))
//                    }else if(mToFromType == 2){
//                        mSquare?.let {
//                            it.iFlowerCount = flowerCount.toInt()+it.iFlowerCount!!.toInt()
//                            EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt(),mSquare))
//                        }
//                    }else if(mToFromType == 4){
//                        dialogListener?.onClick(1,flowerCount)
//                    }
//                    dismissAllowingStateLoss()
//                })
                if(mToFromType == 1){
                    EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt()))
                }else if(mToFromType == 2){
//                    mSquare?.let {
//                        it.iFlowerCount = flowerCount.toInt()+it.iFlowerCount!!.toInt()
//                        EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt(),mSquare))
//                    }
                }else if(mToFromType == 4){
                    dialogListener?.onClick(1,flowerCount)
                }
                dismissAllowingStateLoss()
            }){code,msg->
                CustomToast.showToast(msg)
            }
        }
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