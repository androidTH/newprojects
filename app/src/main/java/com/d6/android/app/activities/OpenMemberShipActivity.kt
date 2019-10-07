package com.d6.android.app.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.AuthTipsQuickAdapter
import com.d6.android.app.adapters.MemberCommentHolder
import com.d6.android.app.adapters.MemberLevelAdapter
import com.d6.android.app.adapters.MemberShipPageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.easypay.EasyPay
import com.d6.android.app.easypay.PayParams
import com.d6.android.app.easypay.callback.OnPayInfoRequestListener
import com.d6.android.app.easypay.callback.OnPayResultListener
import com.d6.android.app.easypay.enums.HttpType
import com.d6.android.app.easypay.enums.NetworkClientType
import com.d6.android.app.easypay.enums.PayWay
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.MemberShipQuickFragment
import com.d6.android.app.models.MemberBean
import com.d6.android.app.models.MemberComment
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.NO_VIP_FROM_TYPE
import kotlinx.android.synthetic.main.activity_openmember_ship.*
import org.jetbrains.anko.bundleOf

/**
 * 开通会员
 */
class OpenMemberShipActivity : BaseActivity() {

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
    private val AREA_REQUEST_CODE_SILIVER = 12
    private var mMemberPriceList = ArrayList<MemberBean>()

    private var mOpenMemberShipDialog:OpenMemberShipDialog?=null
    private var mSilverMemberDialog:SilverMemberDialog?=null
    private var mAppMemberDialog:AppMemberDialog?=null
    private var areaName = ""

    private var ISNOTBUYMEMBER = 0 //0 没有购买

    var mComments = ArrayList<MemberComment>()
    private var mFragments = ArrayList<MemberShipQuickFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_openmember_ship)
        immersionBar.init()

        tv_membership_back.setOnClickListener {
            onBackPressed()
        }

        tv_zxkf_men.setOnClickListener {
            pushCustomerMessage(this, CustomerServiceId, 1, "", next = {
                chatService(this)
            })
        }

//        tv_wechat_kf.setOnClickListener {
//            var mWeChatKfDialog = WeChatKFDialog()
//            mWeChatKfDialog.show(supportFragmentManager,"wechatkf")
//        }

        mComments.add(MemberComment(getString(R.string.string_man_firstcomment),
                API.BASE_URL +"static/image/574421cfgw1ep2mr2retuj21kw2dcnnc.jpg"))
        mComments.add(MemberComment(getString(R.string.string_man_secondcomment),
                API.BASE_URL +"static/image/006lz966ly8g2vdezyk2aj30u00u0ac7.jpg"))
        mComments.add(MemberComment(getString(R.string.string_man_lastcomment),
                API.BASE_URL +"static/image/006koYhFly8g2u7m94y4oj30ro0rotai.jpg"))

        tab_membership.setupWithViewPager(viewpager_membership)
        viewpager_membership.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }
        })

      getMemberPriceList()
    }

    override fun onResume() {
        super.onResume()
//        member_banner.startTurning()
    }

    override fun onStop() {
        super.onStop()
//        member_banner.stopTurning()
    }

    private fun getMemberPriceList() {
        Request.findUserClasses(getLoginToken()).request(this){ msg, data->
            data?.list?.let {
                mMemberPriceList = it
                it.forEach {
                    mFragments.add(MemberShipQuickFragment.newInstance(it.classesname.toString(),it.sDesc.toString()))
                }
                viewpager_membership.adapter = MemberShipPageAdapter(supportFragmentManager,mFragments,mMemberPriceList)
                viewpager_membership.offscreenPageLimit = mFragments.size
//                mMemberLevelAdapter.setNewData(mMemberPriceList)
//                if(mMemberPriceList!=null){
//                    var mMemberBean = mMemberPriceList.get(0)
//                    tv_openmember.text = "开通"
//                }
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
//                    checkOrderStatus(orderId)
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


    private fun getUserInfo() {
        Request.getUserInfo("",userId).request(this, success = { _, data ->
            data?.let {
//                vipheaderview.setImageURI(data.picUrl)
//                tv_viplevel.text = data.classesname
                data.dUserClassEndTime?.let {
                    if(it>0){
//                        tv_vipendtime.text = "到期时间：${data.dUserClassEndTime.toTime(timeFormat)}"
                    }else{
//                        tv_vipendtime.text = ""
                    }
                }

                mMemberPriceList.forEach {
                    if(TextUtils.equals(it.ids.toString(),data.userclassesid.toString())){
                        if(TextUtils.isEmpty(it.sRemark)){
//                            view_line02.visibility = View.GONE
//                            tv_men_member_remark.visibility = View.GONE
                        }else{
//                            view_line02.visibility = View.VISIBLE
//                            tv_men_member_remark.visibility = View.VISIBLE
//                            tv_men_member_remark.text = it.sRemark
                        }
                        it.sDesc?.let {
                            var mTipsData = it.split("<br/>")
//                            rv_men_memberdesc.setHasFixedSize(true)
//                            rv_men_memberdesc.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
//                            rv_men_memberdesc.adapter = AuthTipsQuickAdapter(mTipsData)
                        }

//                        tv_mem_memberztnums.visibility = View.VISIBLE
//                        tv_data_address.visibility =View.VISIBLE
//                        view_line.visibility = View.VISIBLE
                        if(TextUtils.equals("1",it.sex.toString())){
//                            tv_data_address.text = it.sServiceArea
//                            AppUtils.setMemberNums(this,2, "直推次数: " + it.iRecommendCount!!, 0, 5, tv_mem_memberztnums)
                        }
                    }
                }
            }
        }) { _, _ ->
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == AREA_REQUEST_CODE){
                var city = data!!.getStringExtra("area")
                if(!TextUtils.equals(city,"不限地区")){
                    areaName = city
                    if (mOpenMemberShipDialog != null) {
                        mOpenMemberShipDialog?.setAddressd(areaName)
                    }
                }
            }else if(requestCode==AREA_REQUEST_CODE_SILIVER){
                areaName = data!!.getStringExtra("area")
                if (mSilverMemberDialog != null) {
//                    mSilverMemberDialog?.setAddressd(areaName, rv_viptypes.currentItem)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(ISNOTBUYMEMBER==0){
            pushCustomerMessage(this,getLocalUserId(),7,""){
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
        areaName = ""
    }
}