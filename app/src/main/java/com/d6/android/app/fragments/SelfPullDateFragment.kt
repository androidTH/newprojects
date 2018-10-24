package com.d6.android.app.fragments

import android.graphics.Color
import android.os.Bundle
import com.d6.android.app.activities.SelfReleaseDetailActivity
import com.d6.android.app.adapters.SelfPullDateAdapter
import com.d6.android.app.adapters.SelfReleaseAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import org.jetbrains.anko.support.v4.startActivity

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

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var vipIds: String? = ""
    private var area: String? = ""
    private var areaType: Int? = 1
    private var dateType: String? ="1"

    private var pageNum = 1
    private val mFindDates = ArrayList<MyAppointment>()

    private val dateAdapter by lazy {
        SelfPullDateAdapter(mFindDates)
    }

    override fun getMode() = SwipeRefreshRecyclerLayout.Mode.Bottom

    override fun setAdapter() = dateAdapter

    override fun onFirstVisibleToUser() {

        mSwipeRefreshLayout.setBackgroundColor(Color.WHITE)

        addItemDecoration()

        dateAdapter.setOnItemClickListener { _, position ->
            val data = mFindDates[position]
//            startActivity<SelfReleaseDetailActivity>("data" to data)
        }

        showDialog()
        getData()
    }

    fun refreshByPublishNew() {
        this.area = ""
        this.areaType = 1
        this.vipIds = ""
        pageNum = 1
        getData()
    }

    fun refresh() {
        getData()
    }

    fun refresh(city: String?, datetype: String) {
        this.area = city
        this.dateType = datetype
        pageNum = 1
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
//        val classesId = if (vipIds.isNullOrEmpty()) {
//            null
//        } else {
//            vipIds
//        }
//        val areaStr = if (areaType == 0 && !area.isNullOrEmpty()) {
//            area
//        } else {
//            null
//        }
//        val outArea = if (areaType == 1 && !area.isNullOrEmpty()) {
//            area
//        } else {
//            null
//        }
        Request.findAppointmentList(userId,dateType,area,pageNum).request(this) { _, data ->
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