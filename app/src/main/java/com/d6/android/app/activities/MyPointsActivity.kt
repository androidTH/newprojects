package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.PointsAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.DialogCashMoney
import com.d6.android.app.dialogs.PayResultDialog
import com.d6.android.app.dialogs.PointsListDialog
import com.d6.android.app.easypay.EasyPay
import com.d6.android.app.easypay.PayParams
import com.d6.android.app.easypay.callback.OnPayInfoRequestListener
import com.d6.android.app.easypay.callback.OnPayResultListener
import com.d6.android.app.easypay.enums.HttpType
import com.d6.android.app.easypay.enums.NetworkClientType
import com.d6.android.app.easypay.enums.PayWay
import com.d6.android.app.extentions.request
import com.d6.android.app.models.PointRule
import com.d6.android.app.models.UserData
import com.d6.android.app.models.UserPoints
import com.d6.android.app.net.API
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import kotlinx.android.synthetic.main.activity_mypoints.*
import kotlinx.android.synthetic.main.item_mypoints_header.view.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity


/**
 * 我的积分
 */
class MyPointsActivity : BaseActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val myPointNums by lazy {
        intent.getStringExtra("points")
    }

    private var mUserInfo: UserData? =null

    private var pageNum = 1
    private val mUserPoints = ArrayList<UserPoints>()
    lateinit var mPointsListDialog: PointsListDialog

    private val mPointsAdapter by lazy {
        PointsAdapter(mUserPoints)
    }

    private val mHeaderView by lazy {
        layoutInflater.inflate(R.layout.item_mypoints_header, mypoints_refreshrecycler.mRecyclerView, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypoints)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.trans_parent).statusBarDarkFont(true).init()
        mypoints_refreshrecycler.setLayoutManager(LinearLayoutManager(this))
        mypoints_refreshrecycler.setAdapter(mPointsAdapter)
        mPointsAdapter.setHeaderView(mHeaderView)
        mypoints_refreshrecycler.setOnRefreshListener(this)

        tv_mypoints_back.setOnClickListener {
            finish()
        }

        tv_points_info.setOnClickListener {
            startActivity<PointExplainActivity>()
        }

        mHeaderView.tv_recharge.setOnClickListener {
            mPointsListDialog = PointsListDialog()
//            mPointsListDialog.arguments = bundleOf("payresult" to PointsListDialog.PAY_)
            mPointsListDialog.show(supportFragmentManager, "c")
            mPointsListDialog.setOnPayListener { p, data ->
                payMoney(data)
            }
        }

        mHeaderView.tv_privilege.setOnClickListener {
            startActivity<PrivilegeDescActivity>()
        }

        mHeaderView.tv_cash_money.setOnClickListener {
            var dialogCashMoney = DialogCashMoney()
            mUserInfo?.let {
                dialogCashMoney.arguments = bundleOf("data" to it,"cashmoney" to "50")
            }
            dialogCashMoney.show(supportFragmentManager,"cashmoney")
        }

        getUserInfo()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        Request.getUserPoints(userId, pageNum).request(this, success = { _, data ->
            if (pageNum == 1) {
                mUserPoints.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mypoints_refreshrecycler.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mypoints_refreshrecycler.setLoadMoreText("暂无数据")
                }
            } else {
                var addAll = mUserPoints.addAll(data.list.results)
            }
            mPointsAdapter.notifyDataSetChanged()
            mypoints_refreshrecycler.isRefreshing = false
        })
    }

    private fun payMoney(pointRule: PointRule) {
        val params = PayParams.Builder(this)
                .wechatAppID(Const.WXPAY_APP_ID)// 仅当支付方式选择微信支付时需要此参数
                .payWay(PayWay.WechatPay)
                .UserId(userId.toInt())
                .iPoint(pointRule.iPoint)
                .goodsPrice(pointRule.iPrice)// 单位为：分 pointRule.iPrice
                .goodsName("")
                .goodsIntroduction("")
                .httpType(HttpType.Post)
                .httpClientType(NetworkClientType.Retrofit)
                .requestBaseUrl(API.BASE_URL)// 此处替换为为你的app服务器host主机地址
                .build()
        EasyPay.newInstance(params).requestPayInfo(object : OnPayInfoRequestListener {
            override fun onPayInfoRequetStart() {
            }

            override fun onPayInfoRequstSuccess() {

            }

            override fun onPayInfoRequestFailure() {
            }
        }).toPay(object : OnPayResultListener {
            override fun onPaySuccess(payWay: PayWay?) {
                mPointsListDialog.dismissAllowingStateLoss()
                getUserInfo()
                var payResultDialog = PayResultDialog()
                payResultDialog.arguments = bundleOf("payresult" to "wx_pay_success")
                payResultDialog.show(supportFragmentManager, "fd")
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
        Request.getUserInfo("", userId).request(this, success = { _, data ->
            data?.let {
                mUserInfo = it
                mHeaderView.tv_mypointnums.text = it.iPoint.toString()
                SPUtils.instance().put(Const.User.USERPOINTS_NUMS, it.iPoint.toString()).apply()
                mHeaderView.iv_wallet_headView.setImageURI(it.picUrl)
                mHeaderView.tv_wallet_username.text = it.name
                if (TextUtils.equals(it.sex, "0")) {
                    mHeaderView.ll_huiyuan_info.visibility = View.GONE
                } else {
                    mHeaderView.ll_huiyuan_info.visibility = View.VISIBLE
                }
            }
            mUserInfo = data
        })
    }

    override fun onRefresh() {
        pageNum = 1
        getData()
    }

    override fun onLoadMore() {
        pageNum++
        getData()
    }
}
