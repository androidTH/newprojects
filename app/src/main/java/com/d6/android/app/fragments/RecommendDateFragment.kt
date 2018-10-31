package com.d6.android.app.fragments

import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.d6.android.app.activities.FindDateDetailActivity
import com.d6.android.app.activities.SpeedDateDetailActivity
import com.d6.android.app.adapters.DateAdapter
import com.d6.android.app.adapters.RecommendAllDateAdapter
import com.d6.android.app.adapters.RecommendDateAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.isAuthUser
import com.d6.android.app.utils.sysErr
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.fragment_date.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * 人工推荐
 */
class RecommendDateFragment : RecyclerFragment() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mSpeedDates = ArrayList<MyDate>()

    private val dateAdapter by lazy {
        RecommendAllDateAdapter(mSpeedDates)
    }

    private var vipIds: String? = ""
    private var typeIds: String? = ""
    private var area: String? = ""
    private var areaType: Int? = 1

    override fun setAdapter() = dateAdapter

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(context, 2)
    }

    override fun onFirstVisibleToUser() {
        mSwipeRefreshLayout.setBackgroundColor(Color.WHITE)

        mSwipeRefreshLayout.setLayoutManager(getLayoutManager())

        dateAdapter.setOnItemClickListener { _, position ->
            activity?.isAuthUser {
                val date = mSpeedDates[position]
                if(date.iType == 1){
                    startActivity<FindDateDetailActivity>("data" to date)
                }else if(date.iType == 2){
                    startActivity<SpeedDateDetailActivity>("data" to date)
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
        Request.findLookAllAboutList(userId, pageNum).request(this) { _, data ->
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