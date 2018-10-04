package com.d6.android.app.activities

import android.os.Bundle
import android.service.carrier.CarrierMessagingService
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.ConversationsAdapter
import com.d6.android.app.adapters.PointsAdapter
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.dialogs.PointsListDialog
import com.d6.android.app.dialogs.TrendCommentsDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.SwipeItemLayout
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.d6.android.app.widget.badge.Badge
import com.d6.android.app.widget.badge.QBadgeView
import io.rong.imkit.RongIM
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.message.TextMessage
import kotlinx.android.synthetic.main.activity_mypoints.*
import kotlinx.android.synthetic.main.activity_user_info_v2.*
import kotlinx.android.synthetic.main.header_messages.*
import kotlinx.android.synthetic.main.header_messages.view.*
import kotlinx.android.synthetic.main.item_mypoints_header.*
import kotlinx.android.synthetic.main.item_mypoints_header.view.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity


/**
 * 我的积分
 */
class MyPointsActivity : BaseActivity(),SwipeRefreshRecyclerLayout.OnRefreshListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var pageNum = 1
    private val mMessages = ArrayList<Fans>()
    private val mPointsAdapter by lazy {
        PointsAdapter(mMessages)
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

        }

        mHeaderView.tv_recharge.setOnClickListener {
            val mPointsListDialog = PointsListDialog()
//            mPointsListDialog.arguments = bundleOf("payresult" to PointsListDialog.PAY_)
            mPointsListDialog.show(supportFragmentManager, "c")
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        Request.getFindMyFans(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mMessages.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                var addAll = mMessages.addAll(data.list.results)
            }
            mPointsAdapter.notifyDataSetChanged()
        }
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
