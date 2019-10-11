package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.activities.MyPointsActivity
import com.d6.android.app.adapters.BuyFlowerAdapter
import com.d6.android.app.adapters.BuyRedHeartAdapter
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
import com.d6.android.app.models.FlowerRule
import com.d6.android.app.models.LoveHeartRule
import com.d6.android.app.models.Square
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.rong.bean.CustomSystemMessage
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.SENDLOVEHEART_DIALOG
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.RxRecyclerViewDividerTool
import com.d6.android.app.widget.badge.DisplayUtil
import io.rong.eventbus.EventBus
import io.rong.imkit.RongIM
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.dialog_send_redheart.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.wrapContent

/**
 * 我的页面操作弹窗
 */
class SendLoveHeartDialog : DialogFragment() {

    private var mBuyRedHeartAdapter: BuyRedHeartAdapter?=null
    private var mToFromType = 0//1 动态详情页送小红花 2 动态列表送小红花 3 聊天输入框扩展框 4 个人信息页动态送小红花 5 申请私聊送小红花
    private var mSendLoveHeartCount:Int? = -1
    private var mSquare:Square? = null
    private var mLoveHeartList=ArrayList<LoveHeartRule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth() * 0.85f).toInt()+dip(30), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_send_redheart, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var id =  ""
        var ToFromType = 0
        try{
            id =  arguments.getString("userId")
            ToFromType = arguments.getInt("ToFromType",0)
        }catch(e:Exception){
            e.printStackTrace()
        }

        tv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_redheart_gobuy.setOnClickListener {
             isBaseActivity {
                 startActivity<MyPointsActivity>("fromType" to SENDLOVEHEART_DIALOG)
                 dismissAllowingStateLoss()
             }
        }
//        tv_wx_pay_flower.setOnClickListener {
//            var flowerCount= mFlowerCount
//            if(!TextUtils.isEmpty(flowerCount)){
//                var mSendFlowerCount = flowerCount.toInt()
//                if(mSendFlowerCount>0){
//                    buyRedFlowerPay(mSendFlowerCount,id,mSquareId)
//                }else{
//                    CustomToast.showToast("请选中或输入送花的个数")
//                }
//            }
//        }


        rv_send_redheart.setHasFixedSize(true)
        rv_send_redheart.layoutManager = GridLayoutManager(context,3) as RecyclerView.LayoutManager?
        mBuyRedHeartAdapter = BuyRedHeartAdapter(null)
        rv_send_redheart.addItemDecoration(RxRecyclerViewDividerTool(DisplayUtil.dpToPx(15)))
        rv_send_redheart.adapter = mBuyRedHeartAdapter

        mBuyRedHeartAdapter?.setOnItemClickListener() { adapter, view, position ->
            mBuyRedHeartAdapter?.let {
                mSendLoveHeartCount = it.data.get(position).iLoveCount
                if(ToFromType!=1){
                    mSendLoveHeartCount = it.data.get(position).iLoveCount
                    dialogListener?.onClick(mSendLoveHeartCount!!.toInt(), "")
                    dismissAllowingStateLoss()
                }else{
                    isBaseActivity {
                        Request.sendLovePoint(getLoginToken(), "${id}", mSendLoveHeartCount!!.toInt(), 4,"").request(it, false, success = { _, data ->
                            Log.i("GiftControl", "礼物数量${mSendLoveHeartCount}")
                            dismissAllowingStateLoss()
                        }) { code, msg ->
                            if (code == 2||code==3) {
                                var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                                mSendRedHeartEndDialog.show(it.supportFragmentManager, "redheartendDialog")
                                dismissAllowingStateLoss()
                            }
                        }
                    }
                }
            }
        }
        getUserInfo(id)
        setLoveHeartData()
//        getFlowerList()
//        sendSysMessage(id)
    }

    private fun setLoveHeartData(){
        var mLoveHeartRule1 = LoveHeartRule("1")
        mLoveHeartRule1.iLoveCount = 10

        var mLoveHeartRule2 = LoveHeartRule("1")
        mLoveHeartRule2.iLoveCount = 36

        var mLoveHeartRule3 = LoveHeartRule("1")
        mLoveHeartRule3.iLoveCount = 66

        var mLoveHeartRule4 = LoveHeartRule("1")
        mLoveHeartRule4.iLoveCount = 99

        var mLoveHeartRule5 = LoveHeartRule("1")
        mLoveHeartRule5.iLoveCount = 520

        var mLoveHeartRule6 = LoveHeartRule("1")
        mLoveHeartRule6.iLoveCount = 1314

        mLoveHeartList.add(mLoveHeartRule1)
        mLoveHeartList.add(mLoveHeartRule2)
        mLoveHeartList.add(mLoveHeartRule3)
        mLoveHeartList.add(mLoveHeartRule4)
        mLoveHeartList.add(mLoveHeartRule5)
        mLoveHeartList.add(mLoveHeartRule6)
        mBuyRedHeartAdapter?.let {
            it.setNewData(mLoveHeartList)
        }
    }

    private fun getUserInfo(id: String) {
        Request.getUserInfo(getLocalUserId(), id).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                iv_redheart_headView.setImageURI(it.picUrl)
                tv_redheart_name.text = it.name
            }
        })

        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                tv_redheart_count.text = "剩余${it.iLovePoint}"
            }
        })
    }

    private fun getFlowerList(){
        Request.getUserFlower().request((context as BaseActivity),false,success = {msg,data->
            mBuyRedHeartAdapter?.let {
//                it.setNewData(data)
            }
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

    private fun buyRedFlowerPay(flowerCount:Int,receiverUserId:String,sResourceid:String){
        val params = PayParams.Builder((context as BaseActivity))
                .wechatAppID(Const.WXPAY_APP_ID)// 仅当支付方式选择微信支付时需要此参数
                .payWay(PayWay.WechatPay)
                .UserId(getLocalUserId().toInt())
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
                    checkOrderStatus(receiverUserId,orderId,flowerCount.toString())
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
    private fun checkOrderStatus(receiverUserId:String,orderId:String,flowerCount:String){
        if(context!=null){
            if(mToFromType!=5){
                BuyRedFlowerSuccess(receiverUserId,flowerCount)
            }
            Request.getOrderById(orderId).request((context as BaseActivity),false,success={msg,data->
                if(mToFromType == 1){
                    EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt()))
                }else if(mToFromType == 2){
                    mSquare?.let {
                        it.iFlowerCount = flowerCount.toInt()+it.iFlowerCount!!.toInt()
                        EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt(),mSquare))
                    }
                }else if(mToFromType == 4){
                    dialogListener?.onClick(1,flowerCount)
                }
                dismissAllowingStateLoss()
            }){code,msg->
                CustomToast.showToast(msg)
            }
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

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}