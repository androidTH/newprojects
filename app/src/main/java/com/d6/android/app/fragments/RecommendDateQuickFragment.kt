package com.d6.android.app.fragments

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.d6.android.app.activities.FindDateDetailActivity
import com.d6.android.app.activities.SpeedDateDetailActivity
import com.d6.android.app.adapters.RecommentAllQuickDateAdapter
import com.d6.android.app.base.ReRecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * 人工推荐
 */
class RecommendDateQuickFragment : ReRecyclerFragment() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mSpeedDates = ArrayList<MyDate>()

    private val dateAdapter by lazy {
        RecommentAllQuickDateAdapter(mSpeedDates);
    }

    private var iLookType: String = ""
    private var sPlace: String = ""

    override fun setAdapter(): BaseQuickAdapter<*, *> {
        return dateAdapter
    }

    override fun getLayoutManager(): GridLayoutManager {
        return GridLayoutManager(context, 2)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateAdapter.setOnItemClickListener { adapter, view, position ->
            activity?.doAuthUser {
                val date = dateAdapter.data[position]
                if (date.iType == 1) {
                    startActivity<FindDateDetailActivity>("data" to date)
                } else if (date.iType == 2) {
                    startActivity<SpeedDateDetailActivity>("data" to date)
                }
            }
        }
    }

    override fun onFirstVisibleToUser() {
        showDialog()
        pullDownRefresh()
    }

    fun getData(ilookType: String, city: String) {
        this.iLookType = ilookType
        this.sPlace = city
        Request.findLookAllAboutList(userId, iLookType, sPlace, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                dateAdapter.data.clear()
                setRefreshing(false)
                dateAdapter.loadMoreEnd(true)
                dateAdapter.setEnableLoadMore(true)
            }
            if (data?.list?.results == null || data.list?.results.isEmpty()) {
                if (pageNum > 1) {
                    pageNum--
                }
                dateAdapter.loadMoreEnd(false)
            } else {
                dateAdapter.addData(data.list.results)
                dateAdapter.loadMoreComplete()
            }
            dateAdapter.notifyDataSetChanged()
        }
    }

    fun pullRefresh(ilookType: String="", city: String="") {
        pageNum = 1
        dateAdapter.setEnableLoadMore(false)//这里的作用是防止下拉刷新的时候还可以上拉加载
        getData(ilookType, city)
    }

    override fun pullDownRefresh() {
        super.pullDownRefresh()
        pullRefresh()
    }

    override fun loadMore() {
        super.loadMore()
        pageNum++
        getData(iLookType, sPlace)
    }
}