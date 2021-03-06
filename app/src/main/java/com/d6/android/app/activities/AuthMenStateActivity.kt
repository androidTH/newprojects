package com.d6.android.app.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.*
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
import com.d6.android.app.models.MemberBean
import com.d6.android.app.models.MemberComment
import com.d6.android.app.models.MemberTeQuan
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.NO_VIP_FROM_TYPE
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.GridItemDecoration
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import com.d6.android.app.widget.gallery.DSVOrientation
import com.d6.android.app.widget.gallery.transform.ScaleTransformer
import com.facebook.drawee.backends.pipeline.Fresco
import com.fm.openinstall.OpenInstall
import kotlinx.android.synthetic.main.activity_auth_state.*
import kotlinx.android.synthetic.main.include_member.*
import kotlinx.android.synthetic.main.layout_auth_top.*
import me.nereo.multi_image_selector.utils.FinishActivityManager
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.*


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
    private val AREA_REQUEST_CODE_SILIVER = 12
    private var mMemberPriceList = ArrayList<MemberBean>()

    private var mOpenMemberShipDialog:OpenMemberShipDialog?=null
    private var mSilverMemberDialog:SilverMemberDialog?=null
    private var mAppMemberDialog:AppMemberDialog?=null
    private var areaName = ""

    private var ISNOTBUYMEMBER = 0 //0 没有购买

    var mComments = ArrayList<MemberComment>()
    var mListPicsInfo = ArrayList<String>()
    private val mPicsInfoAdapter by lazy{
        VipPicsInfoQuickAdapter(mListPicsInfo)
    }

    private var mListTQ = ArrayList<MemberTeQuan>()
    private val mTeQuanQuickAdapter by lazy{
        TeQuanQuickAdapter(mListTQ)
    }

    @SuppressLint("SetTextI18n")
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
            startActivity<OpenMemberShipActivity>("list" to mMemberPriceList)
//            if(mMemberPriceList.size!=0){
//            var member = mMemberPriceList.get(rv_viptypes.currentItem)
//            if(member.ids!=22&&member.ids!=23&&member.ids!=31){
//                if(member!=null){
//                    member.iAndroidPrice?.let {
//                        var ids = member.ids
//                        ids?.let { it1 -> buyRedFlowerPay(it, areaName, it1, member.classesname.toString()) }
//                    }
//                }
//            }else if(member.ids==31){
//                 mAppMemberDialog  = AppMemberDialog()
//                 var desc = member.sTitle
//                 mAppMemberDialog?.let {
//                     it.arguments = bundleOf("bean" to member, "desc" to "${desc}")
//                     it.show(supportFragmentManager,AppMemberDialog::class.java.toString())
//                     it.setDialogListener { p, s ->
//                         if (p == 1000) {
//
//                         } else {
//                             //支付
//                             if(!TextUtils.isEmpty(s)){
//                                 var pirce =s?.let { it.toInt() }
//                                 member.ids?.let {
//                                     buyRedFlowerPay(pirce,"",it,member.classesname.toString())
//                                 }
//                             }
//                         }
//                     }
//                 }
//
//            }else {
//                mSilverMemberDialog = SilverMemberDialog()
//                mSilverMemberDialog?.let {
//                    if (member != null) {
//                        it.arguments = bundleOf("ids" to "${member.ids}", "desc" to "${member.sTitle}")
//                    }
//                    it.show(supportFragmentManager, SilverMemberDialog::class.java.toString())
//                    it.setDialogListener { p, s ->
//                        if (p == 1000) {
//                            //支付
//                            member.ids?.let {
//                                var price = s.toString().toInt()
//                                buyRedFlowerPay(price, areaName, it, member.classesname.toString())
//                            }
//                        } else if (p == 2001) {
//                            startActivityForResult<ScreeningAreaActivity>(AREA_REQUEST_CODE_SILIVER)
//                        }
//                    }
//                    areaName = ""
//                }
//            }
//            }
        }

        if(TextUtils.equals("mine",from)){
            tv_vipinfo.text = "会员有价 情缘无价"
            tv_d6vipinfo.text = getString(R.string.string_d6_desc)
        }else{
            tv_vipinfo.text = "成为会员后可使用此功能"
            tv_d6vipinfo.text = getString(R.string.string_d6_desc)
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

        rv_viptypes.addOnItemChangedListener { viewHolder, adapterPosition ->
            if(mMemberPriceList!=null&&mMemberPriceList.size>0){
                var member = mMemberPriceList.get(rv_viptypes.currentItem)
                tv_openmember.text = "开通${member.classesname}"
                if(member.ids==31){
                    tv_member_showdes.text = "(低至¥${member.iAndroidPrice}元/月)"
                }else if(member.ids==22||member.ids==23){
                    tv_member_showdes.text = ""//据说80%会员都约到了心仪的TA
                }else{
                    tv_member_showdes.text = "(¥${member.iAndroidPrice}元)"
                }
            }
        }
        mComments.add(MemberComment(getString(R.string.string_man_firstcomment),
                API.BASE_URL +"static/image/574421cfgw1ep2mr2retuj21kw2dcnnc.jpg"))
        mComments.add(MemberComment(getString(R.string.string_man_secondcomment),
                API.BASE_URL +"static/image/006lz966ly8g2vdezyk2aj30u00u0ac7.jpg"))
        mComments.add(MemberComment(getString(R.string.string_man_lastcomment),
                API.BASE_URL +"static/image/006koYhFly8g2u7m94y4oj30ro0rotai.jpg"))
        setMemeberComemnt()

        setData()

        FinishActivityManager.getManager().addActivity(this)
    }


    private fun setData(){
        rv_vip_pics.setHasFixedSize(true)
        rv_vip_pics.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_vip_pics.isNestedScrollingEnabled = false
        rv_vip_pics.adapter = mPicsInfoAdapter

        Request.getInfo(Const.PIECES_VIP_INSTRODUCE).request(this) { _, data ->
            data?.let {
//                val womanWeChat = data.optString("ext1")

                var pics = data.optString("picUrl")
                Log.i("pics", "picUrl=${pics}")
                mListPicsInfo.addAll(pics.split(",") as ArrayList<String>)
                mPicsInfoAdapter.notifyDataSetChanged()
            }
        }

        mPicsInfoAdapter.setOnItemChildClickListener { adapter, view, position ->
            if(view.id==R.id.iv_video_play){
                startActivity<SimplePlayer>("videoPath" to "http://image.d6-zone.com/1568862136529.mp4","videoType" to "1")
            }
        }
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
        Request.findYKUserClasses("7",getLoginToken()).request(this){ msg, data->
            data?.let {
                mListTQ = it.lstMembers as ArrayList<MemberTeQuan>
                tv_tqnums.text = "${mListTQ.size}项会员特权打造不一样的会员体验"
                rv_grid_tq.setHasFixedSize(true)
                rv_grid_tq.layoutManager = GridLayoutManager(this, 3) as RecyclerView.LayoutManager?
                rv_grid_tq.adapter = mTeQuanQuickAdapter
                var divider = GridItemDecoration.Builder(this)
                        .setHorizontalSpan(R.dimen.margin_1)
                        .setVerticalSpan(R.dimen.margin_1)
                        .setColorResource(R.color.color_F5F5F5)
                        .setShowLastLine(false)
                        .setShowVerticalLine(false)
                        .build()
                rv_grid_tq.addItemDecoration(divider)
                rv_grid_tq.isNestedScrollingEnabled = false
            }
        }

        Request.findUserClasses(getLoginToken()).request(this){ msg, data->
            data?.list?.let {
                mMemberPriceList = it
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
                if(mAppMemberDialog!=null){
                    mAppMemberDialog?.let {
                        it.dismissAllowingStateLoss()
                    }
                }

                if(mSilverMemberDialog!=null){
                    mSilverMemberDialog?.let {
                        it.dismissAllowingStateLoss()
                    }
                }

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
                    if (mOpenMemberShipDialog != null) {
                        mOpenMemberShipDialog?.setAddressd(areaName)
                    }
                }
            }else if(requestCode==AREA_REQUEST_CODE_SILIVER){
                areaName = data!!.getStringExtra("area")
                if (mSilverMemberDialog != null) {
                    mSilverMemberDialog?.setAddressd(areaName, rv_viptypes.currentItem)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        Fresco.getImagePipeline().clearMemoryCaches()
        if(ISNOTBUYMEMBER==0){
            pushCustomerMessage(this,getLocalUserId(),7,""){
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
        rv_vip_pics.adapter = null
        rv_grid_tq.adapter = null
//        Fresco.getImagePipeline().clearMemoryCaches()
        areaName = ""
    }
}