package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.d6.android.app.R
import com.d6.android.app.adapters.FansAdapter
import com.d6.android.app.adapters.MyDateListAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import org.jetbrains.anko.startActivity

/**
 * 我的约会列表
 */
class MyDateListActivity : RecyclerActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mMyAppointments = ArrayList<MyAppointment>()
    private val fansAdapter by lazy {
        MyDateListAdapter(mMyAppointments)
    }

    override fun adapter(): RecyclerView.Adapter<*> {
        return fansAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleBold("我的约会")
        fansAdapter .setOnItemClickListener { view, position ->
//            val id = mMyAppointments[position].iAppointUserid
//            startActivity<UserInfoActivity>("id" to id.toString())
            var myAppointment = mMyAppointments[position];
            startActivity<MyDateDetailActivity>("data" to myAppointment)
        }
        addItemDecoration()
        dialog()
        getData()
    }

    private fun getData() {
        Request.findMyAppointmentList(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mMyAppointments.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mMyAppointments.addAll(data.list.results)
            }
            fansAdapter.notifyDataSetChanged()
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
