package com.d6.android.app.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.d6.android.app.R
import com.d6.android.app.activities.MyPointsActivity
import com.d6.android.app.adapters.EmotionGridViewAdapter
import com.d6.android.app.adapters.EmotionPagerAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.easypay.EasyPay
import com.d6.android.app.easypay.PayParams
import com.d6.android.app.easypay.callback.OnPayInfoRequestListener
import com.d6.android.app.easypay.callback.OnPayResultListener
import com.d6.android.app.easypay.enums.HttpType
import com.d6.android.app.easypay.enums.NetworkClientType
import com.d6.android.app.easypay.enums.PayWay
import com.d6.android.app.extentions.request
import com.d6.android.app.models.GiftBeans
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.SENDLOVEHEART_DIALOG
import com.d6.android.app.widget.badge.DisplayUtil
import kotlinx.android.synthetic.main.dialog_select_giftlist.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent
import java.util.*

/**
 * 选择礼物
 */
class SelectGiftListDialog : DialogFragment() {

    private var mLocalUserLoveHeartCount:Int = -1
    private var titleStype = "date"

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
            inflater?.inflate(R.layout.dialog_select_giftlist, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments!=null){
            if(arguments.containsKey("giftlist")){
                mAllGiftList = arguments.getParcelableArrayList<GiftBeans>("giftlist")
            }
            titleStype = arguments.getString("titleStype","date")
            if(TextUtils.equals("other",titleStype)){
                tv_yaoyuetitle.text="赠送礼物"
                tv_redheart_desc.text="赠送礼物表表心意,更容易收到对方的回复哦"
            }else{
                tv_yaoyuetitle.text="邀约礼物"
                tv_redheart_desc.text="添加礼物将会收到更多人申请哦"
            }
        }

        tv_redheart_gobuy.setOnClickListener {
            isBaseActivity {
                startActivity<MyPointsActivity>("fromType" to SENDLOVEHEART_DIALOG)
                dismissAllowingStateLoss()
            }
        }

        tv_my_redheart.setOnClickListener {
            isBaseActivity {
                startActivity<MyPointsActivity>("fromType" to SENDLOVEHEART_DIALOG)
                dismissAllowingStateLoss()
            }
        }

        vp_complate_gift_layout.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var oldPagerPos = 0
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                ll_point_group.playByStartPointToNext(oldPagerPos, position)
                oldPagerPos = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        if(mAllGiftList!=null&&mAllGiftList.size==0){
            getGiftList()
        }else{
            showGift(mAllGiftList)
        }
        ll_user_lovepoint.visibility = View.GONE
        setLoveHeartData()
    }

    private var mAllGiftList: ArrayList<GiftBeans> = ArrayList()
    private var emotionNames: ArrayList<GiftBeans> = ArrayList()
    private var emotionViews: ArrayList<GridView> = ArrayList()
    private var emotionPagerGvAdapter: EmotionPagerAdapter? = null

    private fun getGiftList(){
        Request.getGiftList().request((context as BaseActivity), false, success = { msg, data ->
            data?.let {
                showGift(it)
            }
        })
    }

    private fun showGift(data: ArrayList<GiftBeans>){
        if(context!=null){
            var screenWidth = AppScreenUtils.getScreenRealWidth((context as BaseActivity))
            // item的间距
            val spacing: Int = DisplayUtil.dp2px(activity, 15f)
            // 动态计算item的宽度和高度
            val itemWidth = (screenWidth - spacing * 5) / 4
            //动态计算gridview的总高度
            val gvHeight = itemWidth * 2 + spacing * 1
            for (j in data) {
                emotionNames.add(j)
                if (emotionNames.size == 8) {
                    val gv: GridView = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight)
                    emotionViews.add(gv)
                    // 添加完一组表情,重新创建一个表情名字集合
                    emotionNames = ArrayList()
                }
            }

            // 判断最后是否有不足20个表情的剩余情况
            if (emotionNames.size > 0) {
                val gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight)
                emotionViews.add(gv)
            }
            //初始化指示器
            ll_point_group.initIndicator(emotionViews.size)
            // 将多个GridView添加显示到ViewPager中
            emotionPagerGvAdapter = EmotionPagerAdapter(emotionViews)
            vp_complate_gift_layout.setAdapter(emotionPagerGvAdapter)
        }

    }

    /**
     * 创建显示表情的GridView
     */
    private fun createEmotionGridView(emotionNames: List<GiftBeans>, gvWidth: Int, padding: Int, itemWidth: Int, gvHeight: Int): GridView {
        // 创建GridView
        val gv = GridView(activity)
        //设置点击背景透明
        gv.setSelector(android.R.color.transparent)
        //设置4列
        gv.numColumns = 4
        gv.setPadding(padding, padding, padding, padding)
        gv.horizontalSpacing = padding
        gv.verticalSpacing = padding
        //设置GridView的宽高
        val params = ViewGroup.LayoutParams(gvWidth, gvHeight)
        gv.layoutParams = params
        // 给GridView设置表情图片
        val adapter = EmotionGridViewAdapter(activity, emotionNames, itemWidth)
        gv.adapter = adapter
        //设置全局点击事件
        gv.setOnItemClickListener { parent, view, position, id ->
            var loveNum = emotionNames.get(position).loveNum?.toInt() ?: 0;
            if(mLocalUserLoveHeartCount >= loveNum){
                if(TextUtils.equals("other",titleStype)){
                    toast("接口")
                }else{
                    dialogListener?.onClick(position,emotionNames.get(position).name)
                    dismissAllowingStateLoss()
                }
            }else{
                ll_user_lovepoint.visibility = View.VISIBLE
                tv_redheart_balance.text = "还差${loveNum - mLocalUserLoveHeartCount}"
            }
        }
        return gv
    }


    private fun setLoveHeartData(){

        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request((context as BaseActivity), false, success = { msg, data ->
            data?.let {
                if (context != null) {
                    mLocalUserLoveHeartCount = it.iLovePoint
                    tv_redheart_count.text = "${mLocalUserLoveHeartCount} [img src=redheart_small/]"

                    tv_my_redheart.text = "${mLocalUserLoveHeartCount} [img src=redheart_small/]"
                }
            }
        })
    }

    private fun getUserInfo(id: String) {
        Request.getUserInfo(getLocalUserId(), id).request((context as BaseActivity), false, success = { msg, data ->
            data?.let {

//                if(it.iMySendAllLovePoint>0){
//                    tv_redheart_name.text = "已送对方${it.iMySendAllLovePoint} [img src=redheart_small/]"
//                }else{
//                    tv_redheart_name.text = "给对方送喜欢 [img src=redheart_small/]"
//                    tv_redheart_name.visibility = View.VISIBLE
//                }
            }
        })
    }

    private fun buyRedFlowerPay(flowerCount: Int, receiverUserId: String, sResourceid: String, iOrderType: Int){
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
            override fun onPaySuccess(payWay: PayWay?, orderId: String) {
                Log.i("redflowerorderId", orderId)
                if (!TextUtils.isEmpty(orderId)) {
                    checkOrderStatus(receiverUserId, orderId, flowerCount.toString(), iOrderType)
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
    private fun checkOrderStatus(receiverUserId: String, orderId: String, flowerCount: String, iOrderType: Int){
//        if(context!=null){
//            if(mToFromType!=5){
//                BuyRedFlowerSuccess(receiverUserId,flowerCount)
//            }
//            Request.getOrderById(orderId,iOrderType).request((context as BaseActivity),false,success={msg,data->
//                if(mToFromType == 1){
//                    EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt()))
//                }else if(mToFromType == 2){
//                    mSquare?.let {
//                        it.iFlowerCount = flowerCount.toInt()+it.iFlowerCount!!.toInt()
//                        EventBus.getDefault().post(FlowerMsgEvent(flowerCount.toInt(),mSquare))
//                    }
//                }else if(mToFromType == 4){
//                    dialogListener?.onClick(1,flowerCount)
//                }
//                dismissAllowingStateLoss()
//            }){code,msg->
//                CustomToast.showToast(msg)
//            }
//        }
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