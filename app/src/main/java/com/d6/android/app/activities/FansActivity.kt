package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.d6.android.app.adapters.FansAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.SquareMessage
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import org.jetbrains.anko.startActivity

class FansActivity : RecyclerActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mMessages = ArrayList<SquareMessage>()
    private val fansAdapter by lazy {
        FansAdapter(mMessages)
    }

    override fun adapter(): RecyclerView.Adapter<*> {
       return fansAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "粉丝"
        fansAdapter .setOnItemClickListener { view, position ->
            val squareMessage = mMessages[position]
            startActivity<SquareTrendDetailActivity>("id" to squareMessage.newsId)
        }
        addItemDecoration()
        dialog()
        getData()
    }

    private fun getData() {

        Request.getSquareMessages(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mMessages.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mMessages.addAll(data.list.results)
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