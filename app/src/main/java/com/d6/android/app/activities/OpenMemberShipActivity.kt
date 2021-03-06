package com.d6.android.app.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import com.d6.android.app.widget.CustomToast
import com.fm.openinstall.OpenInstall
import kotlinx.android.synthetic.main.activity_openmember_ship.*
import me.nereo.multi_image_selector.utils.FinishActivityManager
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColor

/**
 * 开通会员
 */
class OpenMemberShipActivity : BaseActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val AREA_REQUEST_CODE = 11
    private val AREA_REQUEST_CODE_SILIVER = 12
    private var mMemberPriceList = ArrayList<MemberBean>()

    private var mOpenMemberShipDialog: OpenMemberShipDialog? = null
    private var mSilverMemberDialog: SilverMemberDialog? = null
    private var mAppMemberDialog: AppMemberDialog? = null
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

        mComments.add(MemberComment(getString(R.string.string_man_firstcomment),
                API.BASE_URL + "static/image/574421cfgw1ep2mr2retuj21kw2dcnnc.jpg"))
        mComments.add(MemberComment(getString(R.string.string_man_secondcomment),
                API.BASE_URL + "static/image/006lz966ly8g2vdezyk2aj30u00u0ac7.jpg"))
        mComments.add(MemberComment(getString(R.string.string_man_lastcomment),
                API.BASE_URL + "static/image/006koYhFly8g2u7m94y4oj30ro0rotai.jpg"))

//        tab_membership.setupWithViewPager(viewpager_membership)
        tab_membership.addOnTabSelectedListener(object : com.d6.android.app.widget.tablayout.TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: com.d6.android.app.widget.tablayout.TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: com.d6.android.app.widget.tablayout.TabLayout.Tab?) {
                tab?.let {
                    setTabSelected(true, it)
                    viewpager_membership.setCurrentItem(it.getPosition())
                }
            }

            override fun onTabUnselected(tab: com.d6.android.app.widget.tablayout.TabLayout.Tab?) {
                tab?.let {
                    setTabSelected(false, it)
                }
            }
        })
        viewpager_membership.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (mMemberPriceList != null && mMemberPriceList.size > 0) {
                    tab_membership.getTabAt(position)?.let {
                        tab_membership.selectTab(it, true)
                    }
                    setButtonContent(position)
                }
            }
        })

        ll_openmemeber_ship.setOnClickListener {
            if (mMemberPriceList != null && mMemberPriceList.size > 0) {
                var member = mMemberPriceList.get(viewpager_membership.currentItem)
                if (member.ids != 22 && member.ids != 23 && member.ids != 31) {
                    if (member != null) {
                        member.iAndroidPrice?.let {
                            var ids = member.ids
                            ids?.let { it1 -> buyRedFlowerPay(it, areaName, it1, member.classesname.toString()) }
                        }
                    }
                } else if (member.ids == 31) {
                    mAppMemberDialog = AppMemberDialog()
                    var desc = member.sTitle
                    mAppMemberDialog?.let {
                        it.arguments = bundleOf("bean" to member, "desc" to "${desc}")
                        it.show(supportFragmentManager, AppMemberDialog::class.java.toString())
                        it.setDialogListener { p, s ->
                            if (p == 1000) {

                            } else {
                                //支付
                                if (!TextUtils.isEmpty(s)) {
                                    var pirce = s?.let { it.toInt() }
                                    member.ids?.let {
                                        buyRedFlowerPay(pirce, "", it, member.classesname.toString())
                                    }
                                }
                            }
                        }
                    }

                } else {
                    mSilverMemberDialog = SilverMemberDialog()
                    mSilverMemberDialog?.let {
                        if (member != null) {
                            it.arguments = bundleOf("ids" to "${member.ids}", "desc" to "${member.sTitle}")
                        }
                        it.show(supportFragmentManager, SilverMemberDialog::class.java.toString())
                        it.setDialogListener { p, s ->
                            if (p == 1000) {
                                //支付
                                member.ids?.let {
                                    var price = s.toString().toInt()
                                    buyRedFlowerPay(price, areaName, it, member.classesname.toString())
//                                    FinishActivityManager.getManager().finishActivity(AuthMenStateActivity::class.java)
//                                    var payResultDialog = PayResultDialog()
//                                    payResultDialog.arguments = bundleOf("buyType" to "memeber", "payresult" to "wx_pay_success")
//                                    payResultDialog.show(supportFragmentManager, "fd")
//                                    payResultDialog.setDialogListener { p, s ->
//                                        startActivity<MemberActivity>()
//                                        onBackPressed()
//                                    }

                                }
                            } else if (p == 2001) {
                                startActivityForResult<ScreeningAreaActivity>(AREA_REQUEST_CODE_SILIVER)
                            }
                        }
                        areaName = ""
                    }
                }
            }
        }
        mMemberPriceList = intent.getParcelableArrayListExtra<MemberBean>("list")
        getMemberPriceList()
    }

    private fun setTabSelected(flag: Boolean, tab: com.d6.android.app.widget.tablayout.TabLayout.Tab) {
        var tv = tab.getCustomView()!!.findViewById<TextView>(R.id.tv_tab)
        var paint = tv.getPaint()
        paint.setFakeBoldText(flag)
        if (flag) {
            tv.textColor = ContextCompat.getColor(this, R.color.color_F7AB00)
        } else {
            tv.textColor = ContextCompat.getColor(this, R.color.color_666666)
        }
    }

    private fun getMemberPriceList() {
        try{
            if (mMemberPriceList != null && mMemberPriceList.size > 0) {
                tv_openmember.postDelayed(object:Runnable{
                    override fun run() {
                        InitUIData()
                    }
                }, 100)
            } else {
                dialog()
                Request.findUserClasses(getLoginToken()).request(this) { msg, data ->
                    data?.list?.let {
                        mMemberPriceList = it
                        InitUIData()
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
            dialog()
            Request.findUserClasses(getLoginToken()).request(this) { msg, data ->
                data?.list?.let {
                    mMemberPriceList = it
                    InitUIData()
                }
            }
        }
    }

    private fun InitUIData() {
        if (mMemberPriceList != null && mMemberPriceList.size > 0) {
            mMemberPriceList.forEach {
                mFragments.add(MemberShipQuickFragment.newInstance(it, it.sDesc.toString()))
                var tab = tab_membership.newTab()
                var inflater = View.inflate(this, R.layout.tab_item, null)
                var title = inflater.findViewById<TextView>(R.id.tv_tab)
                var iv_tuijian = inflater.findViewById<ImageView>(R.id.iv_tag)
                if (it.classesname!!.startsWith("APP")) {
                    iv_tuijian.visibility = View.VISIBLE
                } else {
                    iv_tuijian.visibility = View.GONE
                }
                title.text = it.classesname
                tab.setCustomView(inflater)
                tab_membership.addTab(tab)
            }
            viewpager_membership.adapter = MemberShipPageAdapter(supportFragmentManager, mFragments, mMemberPriceList)
            viewpager_membership.offscreenPageLimit = mFragments.size
            setButtonContent(0)
        }
    }

    private fun setButtonContent(position: Int) {
        var mMemberBean = mMemberPriceList.get(position)
        var openmemberdesc = "开通${mMemberBean.classesname}"
        if (mMemberBean.ids == 31) {
            openmemberdesc = "${openmemberdesc} · 低至¥${mMemberBean.iAndroidPrice}元/月"
        } else if (mMemberBean.ids == 22 || mMemberBean.ids == 23) {
            openmemberdesc = "${openmemberdesc}" //据说80%会员都约到了心仪的TA
        } else {
            openmemberdesc = "${openmemberdesc} · ¥${mMemberBean.iAndroidPrice}元"
        }
        tv_openmember.text = openmemberdesc
    }

    private fun buyRedFlowerPay(price: Int?, sAreaName: String, userclassId: Int, userclassname: String) {
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
            override fun onPaySuccess(payWay: PayWay?, orderId: String) {
                if (!TextUtils.isEmpty(orderId)) {
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
    private fun checkOrderStatus(orderId: String) {
        Request.getOrderById(orderId).request(this, false, success = { msg, data ->
            if (mAppMemberDialog != null) {
                mAppMemberDialog?.let {
                    it.dismissAllowingStateLoss()
                }
            }

            if (mSilverMemberDialog != null) {
                mSilverMemberDialog?.let {
                    it.dismissAllowingStateLoss()
                }
            }
            ISNOTBUYMEMBER = 1
//            ns_auth_mem.visibility = View.GONE
//            ll_bottom.visibility = View.GONE
//            member.visibility = View.VISIBLE
            OpenInstall.reportEffectPoint("open_vip", 1)//会员转化
            FinishActivityManager.getManager().finishActivity(AuthMenStateActivity::class.java)
            var payResultDialog = PayResultDialog()
            payResultDialog.arguments = bundleOf("buyType" to "memeber", "payresult" to "wx_pay_success")
            payResultDialog.show(supportFragmentManager, "fd")
            payResultDialog.setDialogListener { p, s ->
                startActivity<MemberActivity>()
                onBackPressed()
            }
        }) { code, msg ->
            CustomToast.showToast(msg)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AREA_REQUEST_CODE) {
                var city = data!!.getStringExtra("area")
                if (!TextUtils.equals(city, "不限地区")) {
                    areaName = city
                    if (mOpenMemberShipDialog != null) {
                        mOpenMemberShipDialog?.setAddressd(areaName)
                    }
                }
            } else if (requestCode == AREA_REQUEST_CODE_SILIVER) {
                areaName = data!!.getStringExtra("area")
                if (mSilverMemberDialog != null) {
                    mSilverMemberDialog?.setAddressd(areaName, viewpager_membership.currentItem)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (ISNOTBUYMEMBER == 0) {
            pushCustomerMessage(this, getLocalUserId(), 7, "") {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
        areaName = ""
        mMemberPriceList.clear()
        mOpenMemberShipDialog = null
        mSilverMemberDialog= null
        mAppMemberDialog= null
    }
}