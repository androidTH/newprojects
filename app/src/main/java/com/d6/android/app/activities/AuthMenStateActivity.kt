package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.AuthTipsQuickAdapter
import com.d6.android.app.adapters.MemberCommentHolder
import com.d6.android.app.adapters.MemberLevelAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.OpenMemberShipDialog
import com.d6.android.app.dialogs.PayResultDialog
import com.d6.android.app.dialogs.WeChatKFDialog
import com.d6.android.app.easypay.EasyPay
import com.d6.android.app.easypay.PayParams
import com.d6.android.app.easypay.callback.OnPayInfoRequestListener
import com.d6.android.app.easypay.callback.OnPayResultListener
import com.d6.android.app.easypay.enums.HttpType
import com.d6.android.app.easypay.enums.NetworkClientType
import com.d6.android.app.easypay.enums.PayWay
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MemberBean
import com.d6.android.app.models.MemberComment
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.NO_VIP_FROM_TYPE
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import com.d6.android.app.widget.gallery.DSVOrientation
import com.d6.android.app.widget.gallery.transform.ScaleTransformer
import com.fm.openinstall.OpenInstall
import kotlinx.android.synthetic.main.activity_auth_state.*
import kotlinx.android.synthetic.main.include_member.*
import kotlinx.android.synthetic.main.layout_auth_top.*
import org.jetbrains.anko.bundleOf
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
    private var areaName = ""

    private var ISNOTBUYMEMBER = 0 //0 没有购买

    var mComments = ArrayList<MemberComment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_state)

        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .titleBar(tv_back)
                .init()

        tv_back.setOnClickListener {
            onBackPressed()
        }

        tv_zxkf_men.setOnClickListener {
            pushCustomerMessage(this, CustomerServiceId, 1, "", next = {
                chatService(this)
            })
        }

        tv_wechat_kf.setOnClickListener {
            var mWeChatKfDialog = WeChatKFDialog()
            mWeChatKfDialog.show(supportFragmentManager,"wechatkf")
        }

        ll_openmemeber.setOnClickListener {
            mOpenMemberShipDialog = OpenMemberShipDialog()
            mOpenMemberShipDialog.arguments = bundleOf("members" to mMemberPriceList)
            mOpenMemberShipDialog.show(supportFragmentManager,OpenMemberShipDialog::class.java.toString())
            mOpenMemberShipDialog.setDialogListener { p, s ->
                if(p==2001){
                    startActivityForResult<ScreeningAreaActivity>(AREA_REQUEST_CODE)
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
                    }else if(classId.isNotEmpty()){
                        memberBean.iAndroidPrice?.let {
                            var price = it
                            memberBean.ids?.let {
                                buyRedFlowerPay(price,areaName,it,memberBean.classesname.toString())
                            }
                        }
                    }
                }
            }
            areaName = ""
        }

        if(TextUtils.equals("mine",from)){
            tv_vipinfo.text = "会员有价 情缘无价"
            tv_d6vipinfo.text = "D6定位高端私密私人定制交友社区，以高品质、高素质的会员为基础，全球拥有60000+的优质华人会员。平台将实行会员制，成为会员后即可享受私人定制服务"
        }else{
            tv_vipinfo.text = "成为会员后可使用此功能"
            tv_d6vipinfo.text = "D6定位高端私密私人定制交友社区，以高品质、高素质的会员为基础，全球拥有60000+的优质华人会员。平台将实行会员制，成为会员后即可享受私人定制服务"
        }
        rv_viptypes.setHasFixedSize(true)
        rv_viptypes.setOrientation(DSVOrientation.HORIZONTAL)
        rv_viptypes.setSlideOnFling(false)
        rv_viptypes.isNestedScrollingEnabled = false
        rv_viptypes.adapter = mMemberLevelAdapter
        rv_viptypes.setItemTransitionTimeMillis(150)
        rv_viptypes.setItemTransformer(ScaleTransformer.Builder()
                .setMinScale(1.0f)
                .build())
        mComments.add(MemberComment(getString(R.string.string_man_firstcomment),
                API.BASE_URL +"static/image/574421cfgw1ep2mr2retuj21kw2dcnnc.jpg"))
        mComments.add(MemberComment(getString(R.string.string_man_secondcomment),
                API.BASE_URL +"static/image/006lz966ly8g2vdezyk2aj30u00u0ac7.jpg"))
        mComments.add(MemberComment(getString(R.string.string_man_lastcomment),
                API.BASE_URL +"static/image/006koYhFly8g2u7m94y4oj30ro0rotai.jpg"))
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
        Request.findUserClasses(getLoginToken()).request(this){ msg, data->
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
                ISNOTBUYMEMBER = 1
                ns_auth_mem.visibility = View.GONE
                ll_bottom.visibility = View.GONE
                member.visibility = View.VISIBLE
                var payResultDialog = PayResultDialog()
                payResultDialog.arguments = bundleOf("buyType" to "memeber", "payresult" to "wx_pay_success")
                payResultDialog.show(supportFragmentManager, "fd")
                getUserInfo()
                OpenInstall.reportEffectPoint("open_vip",1)//会员转化
            }){code,msg->
                CustomToast.showToast(msg)
            }
    }

    private fun getUserInfo() {
        Request.getUserInfo("",userId).request(this, success = { _, data ->
            data?.let {
                vipheaderview.setImageURI(data.picUrl)
                tv_viplevel.text = data.classesname
                data.dUserClassEndTime?.let {
                    if(it>0){
                        tv_vipendtime.text = "到期时间：${data.dUserClassEndTime.toTime(timeFormat)}"
                    }else{
                        tv_vipendtime.text = ""
                    }
                }

                mMemberPriceList.forEach {
                    if(TextUtils.equals(it.ids.toString(),data.userclassesid.toString())){
                        if(TextUtils.isEmpty(it.sRemark)){
                            view_line02.visibility = View.GONE
                            tv_men_member_remark.visibility = View.GONE
                        }else{
                            view_line02.visibility = View.VISIBLE
                            tv_men_member_remark.visibility = View.VISIBLE
                            tv_men_member_remark.text = it.sRemark
                        }
                        it.sDesc?.let {
                            var mTipsData = it.split("<br/>")
                            rv_men_memberdesc.setHasFixedSize(true)
                            rv_men_memberdesc.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                            rv_men_memberdesc.adapter = AuthTipsQuickAdapter(mTipsData)
                        }

                        tv_mem_memberztnums.visibility = View.VISIBLE
                        tv_data_address.visibility =View.VISIBLE
                        view_line.visibility = View.VISIBLE
                        if(TextUtils.equals("1",it.sex.toString())){
                            tv_data_address.text = it.sServiceArea
                            AppUtils.setMemberNums(this,2, "直推次数: " + it.iRecommendCount!!, 0, 5, tv_mem_memberztnums)
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
                    mOpenMemberShipDialog.setAddressd(areaName)
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