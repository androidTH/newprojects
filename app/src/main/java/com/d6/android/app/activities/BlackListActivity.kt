package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.d6.android.app.R
import com.d6.android.app.adapters.BlackListAdapter
import com.d6.android.app.adapters.FansAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.models.UserPoints
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import kotlinx.android.synthetic.main.activity_blacklist.*

/**
 * 黑名单列表
 */
class BlackListActivity : BaseActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var pageNum = 1
    private val mUserPoints = ArrayList<UserPoints>()

    private val mfans = ArrayList<Fans>()
    private val blackAdapter by lazy {
        BlackListAdapter(mfans)
    }

    private val mHeaderView by lazy {
        layoutInflater.inflate(R.layout.item_blacklist_header, blacklist_refreshrecycler.mRecyclerView, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blacklist)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.trans_parent).statusBarDarkFont(true).init()
        blacklist_refreshrecycler.setLayoutManager(LinearLayoutManager(this))
        blacklist_refreshrecycler.setAdapter(blackAdapter)
        blackAdapter.setHeaderView(mHeaderView)
        blacklist_refreshrecycler.setOnRefreshListener(this)

        tv_blacklist_back.setOnClickListener {
            finish()
        }

        getData()
    }

    private fun getData() {
        dialog("加载中...")
        Request.getFindMyFans(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mfans.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    blacklist_refreshrecycler.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    blacklist_refreshrecycler.setLoadMoreText("暂无数据")
                }
            } else {
                mfans.addAll(data.list.results)
            }
            blackAdapter.notifyDataSetChanged()
            blacklist_refreshrecycler.isRefreshing = false
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
