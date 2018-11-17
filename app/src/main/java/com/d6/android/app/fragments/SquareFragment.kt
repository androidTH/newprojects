package com.d6.android.app.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import com.d6.android.app.R
import com.d6.android.app.activities.MainActivity
import com.d6.android.app.activities.ReportActivity
import com.d6.android.app.activities.SquareTrendDetailActivity
import com.d6.android.app.activities.TrendDetailActivity
import com.d6.android.app.adapters.BannerAdapter
import com.d6.android.app.adapters.SquareAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Banner
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getTrendDetail
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.header_square_list.view.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult

/**
 * Created on 2017/12/17.
 * 动态
 */
class SquareFragment : RecyclerFragment() {
    companion object {
        fun instance(id: String?): SquareFragment {
            val fragment = SquareFragment()
            val b = Bundle()
            b.putString("id", id ?: "")
            fragment.arguments = b
            return fragment
        }
    }

    private var pageNum = 1
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private val classId by lazy {
        if (arguments == null) {
            ""
        } else
            arguments.getString("id")
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private val mBanners = ArrayList<Banner>()

    private val bannerAdapter by lazy {
        BannerAdapter(mBanners)
    }

    private val mSquares = ArrayList<Square>()
    private val squareAdapter by lazy {
        SquareAdapter(mSquares)
    }
    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_square_list,mSwipeRefreshLayout.mRecyclerView,false)
    }

    private var type = 2
    override fun setAdapter() = squareAdapter

    override fun onFirstVisibleToUser() {

        headerView.mBanner.setHasFixedSize(true)
        headerView.mBanner.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        headerView.mBanner.adapter = bannerAdapter
        val helper = PagerSnapHelper()
        helper.attachToRecyclerView(headerView.mBanner)
        headerView.mBanner.isNestedScrollingEnabled = false

        bannerAdapter.setOnItemClickListener { view, position ->
            val banner = mBanners[position]
            val ids = banner.newsid ?: ""
            startActivity<SquareTrendDetailActivity>("id" to ids)
//            (activity as BaseActivity).getTrendDetail(ids){
//                startActivityForResult<TrendDetailActivity>(1, "data" to it)
//            }
        }

        mSwipeRefreshLayout.addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                .colorResId(R.color.color_ECECEC)
                .size(dip(1))
                .visibilityProvider { position, parent ->
                    position==0
                }
                .build())

        squareAdapter.setHeaderView(headerView)

        squareAdapter.setOnItemClickListener { _, position ->
            val square = mSquares[position]
            square.id?.let {
//                startActivityForResult<TrendDetailActivity>(1, "id" to square.id,"data" to square)
                startActivityForResult<SquareTrendDetailActivity>(1,"id" to it)
            }
        }

        squareAdapter.setOnCommentClick { p ->
            val square = mSquares[p]
            square.id?.let {
//                startActivityForResult<TrendDetailActivity>(1, "id" to square.id,"data" to square)
                startActivityForResult<SquareTrendDetailActivity>(1,"id" to it)
            }
        }
        showDialog()
        getData()
    }
    //筛选
    fun filter(type: Int) {
        this.type = type
        showDialog(canCancel = false)
        pullDownRefresh()
    }

    fun refresh() {
        if (type != 2) {
            if (activity is MainActivity) {
                (activity as MainActivity).setTrendTitle(2)
            }
            this.type = 2

        }
        pullDownRefresh()
    }

    private fun getBanner() {
        Request.getBanners().request(this, success = { _, data ->
            if (data?.list?.results != null) {
                mBanners.clear()
                mBanners.addAll(data.list.results)
                bannerAdapter.notifyDataSetChanged()
                showDialog()
                getSquareList()
            }
        }) { _, _ ->
            showDialog()
            getSquareList()
        }
    }

    private fun getData() {
        if (pageNum == 1) {
            getBanner()
        } else {
            getSquareList()
        }

    }

    private fun getSquareList() {
        Request.getSquareList(userId, classId, pageNum, 2,sex = type).request(this) { _, data ->
            if (pageNum == 1) {
                mSquares.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mSquares.addAll(data.list.results)
            }
            squareAdapter.notifyDataSetChanged()
            if (pageNum == 1) {
                mSwipeRefreshLayout.mRecyclerView.scrollToPosition(0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {//广场详情回来。
            pullDownRefresh()
        }
    }

    public override fun pullDownRefresh() {
        super.pullDownRefresh()
        pageNum = 1
        getData()
    }

    override fun loadMore() {
        super.loadMore()
        pageNum++
        getData()
    }
}