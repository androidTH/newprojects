package com.d6.android.app.fragments

import android.graphics.Color
import com.d6.android.app.activities.SpeedDateDetailActivity
import com.d6.android.app.adapters.DateAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.isAuthUser
import com.d6.android.app.utils.sysErr
import com.gyf.barlibrary.ImmersionBar
import org.jetbrains.anko.support.v4.startActivity

/**
 * 速约（请不要吐槽这个命名，我英语就这样了）
 */
class SpeedDateFragment : RecyclerFragment() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mSpeedDates = ArrayList<MyDate>()

    private val dateAdapter by lazy {
        DateAdapter(mSpeedDates)
    }
    private var vipIds: String? = ""
    private var typeIds: String? = ""
    private var area: String? = ""
    private var areaType: Int? = 1
    override fun setAdapter() = dateAdapter
//    private val immersionBar by lazy {
//        ImmersionBar.with(this)
//    }
    override fun onFirstVisibleToUser() {
//        immersionBar
//                .fitsSystemWindows(true)
//                .statusBarDarkFont(true)
//                .init()
        mSwipeRefreshLayout.setBackgroundColor(Color.WHITE)

        dateAdapter.setOnItemClickListener { _, position ->
            activity?.isAuthUser {
                val date = mSpeedDates[position]
                startActivity<SpeedDateDetailActivity>("data" to date)
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

//    override fun onHiddenChanged(hidden: Boolean) {
//        super.onHiddenChanged(hidden)
//        if (!hidden) {
//            immersionBar.init()
//        }
//    }

//    override fun onVisibleToUser() {
//        super.onVisibleToUser()
//        if (!mHidden) {
//            immersionBar.init()
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
//        immersionBar.destroy()
    }

}