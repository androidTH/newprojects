package com.d6.android.app.fragments

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.d6.android.app.R
import com.d6.android.app.activities.MainActivity
import com.d6.android.app.adapters.SelfPullDateAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout

/**
 * 自主发布约会
 */
class SelfPullDateFragment : RecyclerFragment() {

    companion object {
        fun instance(type: Int): SelfPullDateFragment {
            val fragment = SelfPullDateFragment()
            val b = Bundle()
            b.putInt("type", type)
            fragment.arguments = b
            return fragment
        }
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var vipIds: String? = ""
    private var area: String? = ""
    private var areaType: Int? = 1
    private var dateType: String? =""

    private var pageNum = 1
    private val mFindDates = ArrayList<MyAppointment>()

    private val dateAdapter by lazy {
        SelfPullDateAdapter(mFindDates)
    }

    override fun getMode() = SwipeRefreshRecyclerLayout.Mode.Bottom

    override fun setAdapter() = dateAdapter

    override fun onFirstVisibleToUser() {

        mSwipeRefreshLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.color_F5F5F5))

//        addItemDecoration()

        dateAdapter.setOnItemClickListener { _, position ->
            val data = mFindDates[position]
//            startActivity<SelfReleaseDetailActivity>("data" to data)
        }

        getData()
    }

    fun refresh() {
        pageNum = 1
        getData()
    }

    fun refresh(city: String?, datetype: String) {
        this.area = city
        this.dateType = datetype
        pageNum = 1
        getData()
    }

    private fun getData() {
        Request.findAppointmentList(userId,dateType,area,pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mFindDates.clear()
                mSwipeRefreshLayout.mRecyclerView.scrollToPosition(0)
            }
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