package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.MemberCommentHolder
import com.d6.android.app.adapters.MemberLevelAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.OpenMemberShipDialog
import com.d6.android.app.dialogs.PayResultDialog
import com.d6.android.app.easypay.EasyPay
import com.d6.android.app.easypay.PayParams
import com.d6.android.app.easypay.callback.OnPayInfoRequestListener
import com.d6.android.app.easypay.callback.OnPayResultListener
import com.d6.android.app.easypay.enums.HttpType
import com.d6.android.app.easypay.enums.NetworkClientType
import com.d6.android.app.easypay.enums.PayWay
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.MemberBean
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.NO_VIP_FROM_TYPE
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import com.d6.android.app.widget.gallery.DSVOrientation
import com.d6.android.app.widget.gallery.transform.ScaleTransformer
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_auth_state.*
import kotlinx.android.synthetic.main.layout_auth_top.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast


/**
 * 约会认证情况
 */
class AuthMenStateActivity : BaseActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val from by lazy{
        intent.getStringExtra(NO_VIP_FROM_TYPE)
    }

    private val mMemberLevelAdapter by lazy{
        MemberLevelAdapter(mMemberPriceList)
    }

    private val AREA_REQUEST_CODE = 11
    private var mMemberPriceList = ArrayList<MemberBean>()

    private lateinit var mOpenMemberShipDialog:OpenMemberShipDialog
    private var areaName=""

    var mComments = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_state)

        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .titleBar(tv_back)
                .init()

        tv_back.setOnClickListener {
            finish()
        }

        tv_zxkf_men.setOnClickListener {
            pushCustomerMessage(this, getLocalUserId(), 5, "", next = {
                chatService(this)
            })
        }

        ll_openmemeber.setOnClickListener {
            mOpenMemberShipDialog = OpenMemberShipDialog()
            mOpenMemberShipDialog.arguments = bundleOf("members" to mMemberPriceList)
            mOpenMemberShipDialog.show(supportFragmentManager,OpenMemberShipDialog::class.java.toString())
            mOpenMemberShipDialog.setDialogListener { p, s ->
                if(p==2001){
                    startActivityForResult<AreaChooseActivity>(AREA_REQUEST_CODE)
                }else{
                    var memberBean = mMemberPriceList.get(p)
                    var classId = memberBean.ids.toString()
                    if(TextUtils.equals("22",classId)||TextUtils.equals("23",classId)){
                        if(TextUtils.isEmpty(areaName)){
                            toast("请选择约会地区，不同地区价格稍有差异")
                        }else{
                            memberBean.iAndroidPrice?.let {
                                var price = it
                                memberBean.ids?.let {
                                    buyRedFlowerPay(price,areaName,it,memberBean.classesname.toString())
                                }
                            }
                        }
                    }else if(classId.isNullOrEmpty()){
                        memberBean.iAndroidPrice?.let {
                            var price = it
                            memberBean.ids?.let {
                                buyRedFlowerPay(price,"",it,memberBean.classesname.toString())
                            }
                        }
                    }
                }
            }
        }

        if(TextUtils.equals("mine",from)){
            tv_d6vipinfo.text = "会员有价 情缘无价"
        }else{
            tv_d6vipinfo.text = "D6是一个高端私密交友社区，部分服务仅对会员开放"
        }
        rv_viptypes.setHasFixedSize(true)
        rv_viptypes.setOrientation(DSVOrientation.HORIZONTAL)
        rv_viptypes.setSlideOnFling(false)
        rv_viptypes.adapter = mMemberLevelAdapter
        rv_viptypes.setItemTransitionTimeMillis(150)
        rv_viptypes.setItemTransformer(ScaleTransformer.Builder()
                .setMinScale(1.0f)
                .build())
        mComments.add("“感觉还不错吧 里面人还蛮多的 也交到一些朋友啦 中级会员的我已经呆了一年多了 也算比较熟悉了 群的种类多 有些蛮热闹 也有线下聚会”")
        mComments.add("“特别好玩的一个app，里面可以看到动态还能发起约会，最近更新的版本修复了很多bug，灰常棒~”")
        mComments.add("“我进Ｄ6以来、我炮友基本都是d6的、而且客服比别的平台负责多了、APP其他都挺好的、就是不能发布视频”")
        setMemeberComemnt()
    }

    private fun setMemeberComemnt(){
        member_banner.setPages(
                object : CBViewHolderCreator {
                    override fun createHolder(itemView: View): MemberCommentHolder {
                        return MemberCommentHolder(itemView)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.item_vipcomment
                    }
                },mComments)

        member_banner.setOnPageChangeListener(object:OnPageChangeListener{
            override fun onPageSelected(index: Int) {
                when(index){
                    0-> {
                        tv_numone.isEnabled = false
                        tv_numtwo.isEnabled = true
                        tv_numthree.isEnabled = true
                    }
                    1->{
                        tv_numone.isEnabled = true
                        tv_numtwo.isEnabled = false
                        tv_numthree.isEnabled = true
                    }
                    2->{
                        tv_numone.isEnabled = true
                        tv_numtwo.isEnabled = true
                        tv_numthree.isEnabled = false
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            }
        })

        getMemberPriceList()
    }

    override fun onResume() {
        super.onResume()
        member_banner.startTurning()
    }

    override fun onStop() {
        super.onStop()
        member_banner.stopTurning()
    }

    private fun getMemberPriceList() {
        Request.findUserClasses().request(this){msg,data->
            data?.list?.let {
                mMemberPriceList = it
                mMemberLevelAdapter.setNewData(mMemberPriceList)
            }
        }
    }

    private fun buyRedFlowerPay(price:Int?,sAreaName:String,userclassId:Int,userclassname:String){
        val params = PayParams.Builder(this)
                .wechatAppID(Const.WXPAY_APP_ID)// 仅当支付方式选择微信支付时需要此参数
                .payWay(PayWay.WechatPay)
                .UserId(userId.toInt())
                .goodsPrice(price)// 单位为：分 pointRule.iPrice
                .goodsName(userclassname)
                .goodsIntroduction("会员等级充值")
                .setSAeaName(sAreaName)
                .setUserClassId(userclassId)
                .httpType(HttpType.Post)
                .httpClientType(NetworkClientType.Retrofit)
                .requestBaseUrl(API.BASE_URL)// 此处替换为为你的app服务器host主机地址
                .setType(2)
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
                if(!TextUtils.isEmpty(orderId)){
                    checkOrderStatus(orderId)
                }
            }

            override fun onPayCancel(payWay: PayWay?) {
            }

            override fun onPayFailure(payWay: PayWay?, errCode: Int) {
                var payResultDialog = PayResultDialog()
                payResultDialog.arguments = bundleOf("payresult" to "wx_pay_fail")
                payResultDialog.show(supportFragmentManager, "fd")
            }
        })
    }

    /**
     * 检查订单的状态
     */
    private fun checkOrderStatus(orderId:String){
            Request.getOrderById(orderId).request(this,false,success={msg,data->
                mOpenMemberShipDialog.dismissAllowingStateLoss()
                var payResultDialog = PayResultDialog()
                payResultDialog.arguments = bundleOf("buyType" to "memeber", "payresult" to "wx_pay_success")
                payResultDialog.show(supportFragmentManager, "fd")
            }){code,msg->
                CustomToast.showToast(msg)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == AREA_REQUEST_CODE){
                var areaName = data!!.getStringExtra("area")
                mOpenMemberShipDialog.setAddressd(areaName)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
        areaName = ""
    }
}