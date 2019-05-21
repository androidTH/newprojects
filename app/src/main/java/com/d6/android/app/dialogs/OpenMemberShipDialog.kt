package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d6.android.app.R
import com.d6.android.app.adapters.MemberShipAdapter
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
import com.d6.android.app.models.MemberBean
import com.d6.android.app.models.Square
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.RxRecyclerViewDividerTool
import com.d6.android.app.widget.badge.DisplayUtil
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import io.rong.eventbus.EventBus
import kotlinx.android.synthetic.main.dialog_membership_price_list.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.wrapContent

/**
 * 开通会员
 */
class OpenMemberShipDialog : DialogFragment() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var mMemberPriceList = ArrayList<MemberBean>()
    private var mMemberShipAdapter: MemberShipAdapter?=null
    private var mSquareId:String = ""
    private var mToFromType = 0

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
            inflater?.inflate(R.layout.dialog_membership_price_list, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMemberPriceList = arguments.getParcelableArrayList("members")
        if(mMemberPriceList==null){
            getMemberPriceList()
        }
        iv_membership_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        rv_membership_price_list.setHasFixedSize(true)
        rv_membership_price_list.layoutManager = LinearLayoutManager(context)
        mMemberShipAdapter = MemberShipAdapter(mMemberPriceList)
        rv_membership_price_list.addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                .size(dip(1))
                .color(ContextCompat.getColor(context,R.color.color_EFEFEF))
                .build())
        rv_membership_price_list.adapter = mMemberShipAdapter

        mMemberShipAdapter?.setOnItemClickListener() { adapter, view, position ->
                 mMemberShipAdapter?.let {
//                     if(it.selectedIndex == position){
//                         it.selectedIndex= -1
//                     }else{
//                         it.selectedIndex = position
//                         mFlowerCount = it.data.get(position).iFlowerCount.toString()
//                     }
                     it.notifyDataSetChanged()
                 }
        }
//        getUserInfo(id)
//        getFlowerList()
    }

    private fun getMemberPriceList() {
//        Request.getUserInfo(userId, id).request((context as BaseActivity),false,success= { msg, data ->
//            data?.let {
//                iv_sendflower_headView.setImageURI(it.picUrl)
//                tv_sendflower_name.text = it.name
//            }
//        })

        Request.findUserClasses().request((context as BaseActivity)){msg,data->
            data?.list?.let {
                mMemberShipAdapter?.setNewData(it)
            }
        }
    }

    private fun getFlowerList(){
        Request.getUserFlower().request((context as BaseActivity),false,success = {msg,data->
            mMemberShipAdapter?.let {
//                it.setNewData(data)
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
            BuyRedFlowerSuccess(receiverUserId,flowerCount)
            Request.getOrderById(orderId).request((context as BaseActivity),false,success={msg,data->
                Request.sendFlowerByOrderId(userId,receiverUserId,orderId,mSquareId).request((context as BaseActivity),true,success={msg,data->
                    if(mToFromType == 1){
                        EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt()))
                    }else if(mToFromType == 2){
//                        mSquare?.let {
//                            it.iFlowerCount = flowerCount.toInt()+it.iFlowerCount!!.toInt()
//                            EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt(),mSquare))
//                        }
                    }else if(mToFromType == 4){
                        dialogListener?.onClick(1,flowerCount)
                    }
                    dismissAllowingStateLoss()
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