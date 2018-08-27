package com.d6.android.app.fragments

import android.graphics.Color
import android.os.Bundle
import com.d6.android.app.activities.SelfReleaseDetailActivity
import com.d6.android.app.adapters.SelfReleaseAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import org.jetbrains.anko.support.v4.startActivity

/**
 * 自主发布
 */
class HomeSelfReleaseFragment : RecyclerFragment() {

    companion object {
        fun instance(type: Int): HomeSelfReleaseFragment {
            val fragment = HomeSelfReleaseFragment()
            val b = Bundle()
            b.putInt("type", type)
            fragment.arguments = b
            return fragment
        }
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mFindDates = ArrayList<MyDate>()

    private val dateAdapter by lazy {
        SelfReleaseAdapter(mFindDates)
    }

    override fun getMode() = SwipeRefreshRecyclerLayout.Mode.None
    override fun setAdapter() = dateAdapter

    override fun onFirstVisibleToUser() {

        mSwipeRefreshLayout.setBackgroundColor(Color.WHITE)

        addItemDecoration()
        dateAdapter.setOnItemClickListener { _, position ->
            val data = mFindDates[position]
            startActivity<SelfReleaseDetailActivity>("data" to data)
        }

        showDialog()
        getData()
    }

    fun refresh() {
        getData()
    }

    private fun getData() {

        Request.getSelfReleaseList(userId, pageNum,pageSize = 4).request(this) { _, data ->
            if (pageNum == 1) {
                mFindDates.clear()
            }
            mSwipeRefreshLayout.setLoadMoreText("更多会员信息请点击更多")
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mFindDates.addAll(data.list.results)
            }
            dateAdapter.notifyDataSetChanged()
        }
    }

    override fun pullDownRefresh() {
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