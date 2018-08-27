package com.d6.android.app.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.d6.android.app.activities.FindDateDetailActivity
import com.d6.android.app.adapters.FindDateAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.isAuthUser
import com.d6.android.app.utils.sysErr
import org.jetbrains.anko.support.v4.startActivity

/**
 * 觅约
 */
class FindDateFragment : RecyclerFragment() {

    companion object {
        fun instance(type: Int): FindDateFragment {
            val fragment = FindDateFragment()
            val b = Bundle()
            b.putInt("type", type)
            fragment.arguments = b
            return fragment
        }
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var vipIds: String? = ""
    private var area: String? = ""
    private var areaType: Int? = 1

    private val mFindDates = ArrayList<MyDate>()
    private var pageNum = 1
    private val dateAdapter by lazy {
        FindDateAdapter(mFindDates)
    }

    override fun setAdapter() = dateAdapter

    override fun getLayoutManager() = GridLayoutManager(context, 2)

    override fun onFirstVisibleToUser() {

        mSwipeRefreshLayout.setBackgroundColor(Color.WHITE)

        dateAdapter.setOnItemClickListener { _, position ->
            activity?.isAuthUser {
                val data = mFindDates[position]
                startActivity<FindDateDetailActivity>("data" to data)
            }
        }

        showDialog()
        getData()
    }

    fun refresh(area: String?, areaType: Int, levelIds: String?) {
        this.area = area
        this.areaType = areaType
        this.vipIds = levelIds
        pageNum = 1
        getData()
    }

    private fun getData() {
        val classesId = if (vipIds.isNullOrEmpty()) {
            null
        } else {
            vipIds
        }
        val areaStr = if (areaType == 1 && !area.isNullOrEmpty()) {
            area
        } else {
            null
        }
        val outArea = if (areaType == 0 && !area.isNullOrEmpty()) {
            area
        } else {
            null
        }
        Request.getFindDateList(1, pageNum, classesId = classesId, area = areaStr, outArea = outArea).request(this) { _, data ->
            if (pageNum == 1) {
                mFindDates.clear()
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