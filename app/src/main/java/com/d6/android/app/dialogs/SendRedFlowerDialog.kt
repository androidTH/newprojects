package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.Spannable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.adapters.BuyFlowerAdapter
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
import com.d6.android.app.models.Comment
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.RxRecyclerViewDividerTool
import com.d6.android.app.widget.badge.DisplayUtil
import io.rong.eventbus.EventBus
import kotlinx.android.synthetic.main.dialog_send_redflower.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 我的页面操作弹窗
 */
class SendRedFlowerDialog : DialogFragment() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var mBuyFlowerAdapter: BuyFlowerAdapter?=null
    private var mSquareId:String = ""
    private var mToFromType = 0
    private var mFlowerCount:String=""

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
            inflater?.inflate(R.layout.dialog_send_redflower, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mToFromType = arguments.getInt("ToFromType")
        if(mToFromType==1){//打赏动态
            mSquareId = arguments.getString("squareId")
        }else{
            mSquareId = ""
        }
        var id =  arguments.getString("userId")

        iv_sendflower_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        tv_wx_pay_flower.setOnClickListener {
//            var flowerCount= et_sendflower_input.text.toString()
            var flowerCount= mFlowerCount
            if(!TextUtils.isEmpty(flowerCount)){
                var mSendFlowerCount = flowerCount.toInt()
                if(mSendFlowerCount>0){
                    buyRedFlowerPay(mSendFlowerCount,id)
                }else{
                    CustomToast.showToast("请选中或输入送花的个数")
                }
            }
        }

        et_sendflower_input.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int){

             }
             override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int){
                 if(s.isNotEmpty()){
                     mBuyFlowerAdapter?.let {
                         it.selectedIndex = -1
                         it.notifyDataSetChanged()
                     }
                     mFlowerCount = s.toString()
                 }
             }
             override fun afterTextChanged(s: Editable){

            }
        })

        rv_send_redflower.setHasFixedSize(true)
        rv_send_redflower.layoutManager = GridLayoutManager(context,3)
        mBuyFlowerAdapter = BuyFlowerAdapter(null)
        rv_send_redflower.addItemDecoration(RxRecyclerViewDividerTool(DisplayUtil.dpToPx(15)))
        rv_send_redflower.adapter = mBuyFlowerAdapter

        mBuyFlowerAdapter?.setOnItemClickListener() { adapter, view, position ->
                 mBuyFlowerAdapter?.let {
                     if(it.selectedIndex == position){
                         it.selectedIndex= -1
                     }else{
                         it.selectedIndex = position
                         mFlowerCount = it.data.get(position).iFlowerCount.toString()
//                         et_sendflower_input.setText(it.data.get(position).iFlowerCount.toString())
                     }
                     it.notifyDataSetChanged()
                 }
        }
        getUserInfo(id)
        getFlowerList()
    }

    private fun getUserInfo(id: String) {
        Request.getUserInfo(userId, id).request((context as BaseActivity),false,success= { msg, data ->
            data?.let {
                iv_sendflower_headView.setImageURI(it.picUrl)
                tv_sendflower_name.text = it.name
            }
        })
    }

    private fun getFlowerList(){
        Request.getUserFlower().request((context as BaseActivity),false,success = {msg,data->
            mBuyFlowerAdapter?.let {
                it.setNewData(data)
            }
        })
    }

    private fun BuyRedFlowerSuccess(id:String,flowerCount:String){
        val dialogSendFlowerSuccess = DialogSendFlowerSuccess()
            dialogSendFlowerSuccess.arguments = bundleOf("userId" to id,"nums" to flowerCount)
            dialogSendFlowerSuccess.show((context as BaseActivity).supportFragmentManager, "sendflower")
    }

    private fun buyRedFlowerPay(flowerCount:Int,receiverUserId:String){
        val params = PayParams.Builder((context as BaseActivity))
                .wechatAppID(Const.WXPAY_APP_ID)// 仅当支付方式选择微信支付时需要此参数
                .payWay(PayWay.WechatPay)
                .UserId(userId.toInt())
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
            Request.getOrderById(orderId).request((context as BaseActivity),false,success={msg,data->
                Request.sendFlowerByOrderId(userId,receiverUserId,orderId,mSquareId).request((context as BaseActivity),true,success={msg,data->
                    BuyRedFlowerSuccess(receiverUserId,flowerCount)
                    if(mToFromType == 1){
                        EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt()))
                    }
                })
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
}