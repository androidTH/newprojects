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
import org.jetbrains.anko.toast
import org.jetbrains.anko.wrapContent

/**
 * 我的页面操作弹窗
 */
class SendLoveHeartDialog : DialogFragment() {

    private var mBuyRedHeartAdapter: BuyRedHeartAdapter?=null
    private var mToFromType = 0//1 动态详情页送小红花 2 动态列表送小红花 3 聊天输入框扩展框 4 个人信息页动态送小红花 5 申请私聊送小红花
    private var mSendLoveHeartCount:Int? = -1
    private var mLocalUserLoveHeartCount:Int? = -1
    private var mSquare:Square? = null
    private var mLoveHeartList=ArrayList<LoveHeartRule>()
    private var iIsAnonymous:String= "2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(matchParent, wrapContent)
        dialog.window.setGravity(Gravity.BOTTOM)
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
            iIsAnonymous = arguments.getString("iIsAnonymous","2")
        }catch(e:Exception){
            e.printStackTrace()
        }

        if(ToFromType == 2){
            tv_redheart_desc.text="打赏以下任意数量喜欢[img src=redheart_small/]，即可查看"
        }else{
            tv_redheart_desc.text="相互喜欢[img src=redheart_small/]即可解锁聊天，送的喜欢[img src=redheart_small/]超过1000将升级为超级喜欢，直接通知对方"
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

        rv_send_redheart.setHasFixedSize(true)
        rv_send_redheart.layoutManager = GridLayoutManager(context,3) as RecyclerView.LayoutManager?
        mBuyRedHeartAdapter = BuyRedHeartAdapter(null)
        rv_send_redheart.addItemDecoration(RxRecyclerViewDividerTool(DisplayUtil.dpToPx(15)))
        rv_send_redheart.adapter = mBuyRedHeartAdapter

        mBuyRedHeartAdapter?.setOnItemClickListener() { adapter, view, position ->
            mBuyRedHeartAdapter?.let {
                if(it.selectedIndex == position){
                    it.selectedIndex= -1
                }else{
                    it.selectedIndex = position
                    mSendLoveHeartCount = it.data.get(position).iLoveCount
                }
                var desc = it.data.get(position).sDesc
                it.notifyDataSetChanged()
                if(ToFromType!=1){
                    mSendLoveHeartCount = it.data.get(position).iLoveCount
                    mSendLoveHeartCount?.let {
                        if(it<=mLocalUserLoveHeartCount!!.toInt()){
                            dialogListener?.onClick(it, "${desc}")
                            dismissAllowingStateLoss()
                        }else{
                            tv_redheart_balance.text = "还差${mSendLoveHeartCount!!.toInt()-mLocalUserLoveHeartCount!!.toInt()}"
                            ll_user_lovepoint.visibility = View.VISIBLE
                        }
                    }
                }else{
                    isBaseActivity {
                        Request.sendLovePoint(getLoginToken(), "${id}", mSendLoveHeartCount!!.toInt(), 4,"","",desc).request(it, false, success = { _, data ->
                            Log.i("GiftControl", "礼物数量${mSendLoveHeartCount}")
                            dismissAllowingStateLoss()
                        }) { code, msg ->
                            if (code == 2||code==3) {
                                var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                                mSendRedHeartEndDialog.show(it.supportFragmentManager, "redheartendDialog")
                                dismissAllowingStateLoss()
                            }else{
                                it.toast(msg)
                            }
                        }
                    }
                }
            }
        }

        if(TextUtils.equals(iIsAnonymous,"2")){
            getUserInfo(id)
        }else{
            iv_redheart_headView.setImageURI("res:///"+R.mipmap.nimingtouxiang_big)
            tv_redheart_name.text = "匿名"
        }
        setLoveHeartData()
    }

    private fun setLoveHeartData(){
        var mLoveHeartRule1 = LoveHeartRule("1")
        mLoveHeartRule1.iLoveCount = 200
        mLoveHeartRule1.sDesc = "爱你哦"

        var mLoveHeartRule2 = LoveHeartRule("1")
        mLoveHeartRule2.iLoveCount = 520
        mLoveHeartRule2.sDesc = "我爱你"

        var mLoveHeartRule3 = LoveHeartRule("1")
        mLoveHeartRule3.iLoveCount = 1314
        mLoveHeartRule3.sDesc = "一生一世"

        var mLoveHeartRule4 = LoveHeartRule("1")
        mLoveHeartRule4.iLoveCount = 3399
        mLoveHeartRule4.sDesc = "长长久久"

        var mLoveHeartRule5 = LoveHeartRule("1")
        mLoveHeartRule5.iLoveCount = 9420
        mLoveHeartRule5.sDesc = "就是爱你"

        var mLoveHeartRule6 = LoveHeartRule("1")
        mLoveHeartRule6.iLoveCount = 20100
        mLoveHeartRule6.sDesc = "爱你一万年"

        mLoveHeartList.add(mLoveHeartRule1)
        mLoveHeartList.add(mLoveHeartRule2)
        mLoveHeartList.add(mLoveHeartRule3)
        mLoveHeartList.add(mLoveHeartRule4)
        mLoveHeartList.add(mLoveHeartRule5)
        mLoveHeartList.add(mLoveHeartRule6)
        mBuyRedHeartAdapter?.let {
            it.setNewData(mLoveHeartList)
        }

        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                if(context!= null){
                    mLocalUserLoveHeartCount = it.iLovePoint
                    tv_redheart_count.text = "${mLocalUserLoveHeartCount} [img src=redheart_small/]"
                }
            }
        })
    }

    private fun getUserInfo(id: String) {
        Request.getUserInfo(getLocalUserId(), id).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                iv_redheart_headView.setImageURI(it.picUrl)
                if(it.iMySendAllLovePoint>0){
                    tv_redheart_name.text = "已送对方${it.iMySendAllLovePoint} [img src=redheart_small/]"
                }else{
                    tv_redheart_name.text = "给对方送喜欢 [img src=redheart_small/]"
                    tv_redheart_name.visibility = View.VISIBLE
                }
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

    private fun buyRedFlowerPay(flowerCount:Int,receiverUserId:String,sResourceid:String,iOrderType:Int){
        val params = PayParams.Builder((context as BaseActivity))
                .wechatAppID(Const.WXPAY_APP_ID)// 仅当支付方式选择微信支付时需要此参数
                .payWay(PayWay.WechatPay)
                .UserId(getLocalUserId().toInt())
                .setSendUserid(receiverUserId.toInt())
                .setSUserLoginToken(getLoginToken())
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