package com.d6.android.app.fragments

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.d6.android.app.R
import com.d6.android.app.adapters.DateMeAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.NewDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import kotlinx.android.synthetic.main.header_date_me.view.*

class DateMeFragment :RecyclerFragment(){
    private var pageNum = 1
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private val mDates = ArrayList<NewDate>()
    private val dateMeAdapter by lazy {
        DateMeAdapter(mDates)
    }
    override fun setAdapter() = dateMeAdapter

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(context,2)
    }
    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_date_me,mSwipeRefreshLayout.mRecyclerView,false)
    }
    override fun onFirstVisibleToUser() {
        dateMeAdapter.setHeaderView(headerView)
        showDialog()
        getData()
        getDateCount()
    }

    private fun getData() {
        Request.findDatingMeList(userId,pageNum).request(this){_,data->
            if (pageNum == 1) {
                mDates.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mDates.addAll(data.list.results)
            }
            dateMeAdapter.notifyDataSetChanged()
            if (pageNum == 1) {
                mSwipeRefreshLayout.mRecyclerView.scrollToPosition(0)
            }
        }
    }

    private fun getDateCount() {
        Request.dateMeCount(userId).request(this){_,data->
            data?.let {
                headerView.tv_content.text = String.format("共收到%s人的约会邀请",it.asString)
            }
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