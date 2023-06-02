package com.d6zone.android.app.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.d6zone.android.app.R
import com.d6zone.android.app.activities.FindDateDetailActivity
import com.d6zone.android.app.adapters.FindDateAdapter
import com.d6zone.android.app.base.RecyclerFragment
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.MyDate
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.SPUtils
import com.d6zone.android.app.utils.isAuthUser
import com.d6zone.android.app.widget.SwipeRefreshRecyclerLayout
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity

/**
 * 觅约
 */
class HomeFindDateFragment : RecyclerFragment() {

    companion object {
        fun instance(type: Int): HomeFindDateFragment {
            val fragment = HomeFindDateFragment()
            val b = Bundle()
            b.putInt("type", type)
            fragment.arguments = b
            return fragment
        }
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private val mFindDates = ArrayList<MyDate>()
    private var pageNum = 1
    private val dateAdapter by lazy {
        FindDateAdapter(mFindDates)
    }

    override fun setAdapter() = dateAdapter
    override fun getMode() = SwipeRefreshRecyclerLayout.Mode.None
    override fun getLayoutManager() = GridLayoutManager(context, 2)

    override fun onFirstVisibleToUser() {

        mSwipeRefreshLayout.setBackgroundColor(Color.WHITE)

        mSwipeRefreshLayout.addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                .colorResId(R.color.color_ECECEC)
                .size(dip(1))
                .build())

        dateAdapter.setOnItemClickListener { _, position ->
            activity?.isAuthUser {
                val data = mFindDates[position]
                startActivity<FindDateDetailActivity>("data" to data)
            }
        }

        showDialog()
        getData()
    }

    fun refresh() {
        getData()
    }

    private fun getData() {

        Request.getFindDateList(1,pageNum,pageSize = 4).request(this){_,data->
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
        pageNum=1
        getData()
    }

    override fun loadMore() {
        super.loadMore()
        pageNum++
        getData()
    }
}