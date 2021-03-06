package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.d6.android.app.adapters.FansAdapter
import com.d6.android.app.adapters.FollowAdapter
import com.d6.android.app.adapters.VistorAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.models.SquareMessage
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import org.jetbrains.anko.startActivity

class VistorsActivity : RecyclerActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mVistors = ArrayList<Fans>()
    private val vistorAdapter by lazy {
        VistorAdapter(mVistors)
    }

    override fun adapter(): RecyclerView.Adapter<*> {
       return vistorAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "访客"
        vistorAdapter.setOnItemClickListener { view, position ->
            val id =  mVistors[position].iVistorid
            startActivity<UserInfoActivity>("id" to id.toString())
        }
        addItemDecoration()
        dialog()
        getData()
    }

    private fun getData() {

        Request.getFindVistors(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mVistors.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mVistors.addAll(data.list.results)
            }
            vistorAdapter.notifyDataSetChanged()
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