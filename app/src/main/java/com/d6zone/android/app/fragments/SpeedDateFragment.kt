package com.d6zone.android.app.fragments

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.d6zone.android.app.activities.FindDateDetailActivity
import com.d6zone.android.app.activities.SpeedDateDetailActivity
import com.d6zone.android.app.adapters.DateAdapter
import com.d6zone.android.app.base.RecyclerFragment
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.MyDate
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.isAuthUser
import com.d6zone.android.app.utils.sysErr
import org.jetbrains.anko.support.v4.startActivity

/**
 * 速约
 */
class SpeedDateFragment : RecyclerFragment() {

    private var pageNum = 1
    private val mSpeedDates = ArrayList<MyDate>()

    private val dateAdapter by lazy {
        DateAdapter(mSpeedDates)
    }

    private var vipIds: String? = ""
    private var typeIds: String? = ""
    private var area: String? = ""
    private var areaType: Int? = 1

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun setAdapter() = dateAdapter

    override fun onFirstVisibleToUser() {
        mSwipeRefreshLayout.setBackgroundColor(Color.WHITE)

        dateAdapter.setOnItemClickListener { _, position ->
            activity?.isAuthUser {
                val date = mSpeedDates[position]
                if(date.iType == 1){
                    startActivity<SpeedDateDetailActivity>("data" to date)
                }else{
                    startActivity<FindDateDetailActivity>("data" to date)
                }
            }
        }
        showDialog()
        getData()
    }

    fun refresh(area: String?,areaType:Int,  typeIds: String?, levelIds: String?) {
        sysErr("---------------refresh-----------")
        this.area = area
        this.areaType = areaType
        this.typeIds = typeIds
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
        val arraySpeedState = if (typeIds.isNullOrEmpty()) {
            null
        } else {
            typeIds
        }
        val areaStr = if (areaType == 0 && !area.isNullOrEmpty()) {
            area
        } else {
            null
        }
        val outArea = if (areaType == 1 && !area.isNullOrEmpty()) {
            area
        } else {
            null
        }

        Request.getSpeedDateList(1, pageNum, classesId = classesId, arraySpeedState = arraySpeedState, area = areaStr,outArea = outArea).request(this) { _, data ->
            if (pageNum == 1) {
                mSpeedDates.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mSpeedDates.addAll(data.list.results)
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

    override fun onDestroy() {
        super.onDestroy()
    }

}