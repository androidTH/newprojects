package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.adapters.SquareMessageAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.SquareMessage
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import org.jetbrains.anko.startActivity

class SquareMessagesActivity : RecyclerActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mMessages = ArrayList<SquareMessage>()
    private val adapter by lazy {
        SquareMessageAdapter(mMessages)
    }
    override fun adapter() = adapter

    override fun IsShowFooter(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "动态消息"
        adapter.setOnItemClickListener { view, position ->
            val squareMessage = mMessages[position]
            startActivity<SquareTrendDetailActivity>("id" to "${squareMessage.url}","position" to position)
        }
        addItemDecoration()
        getData()
    }

    private fun getData() {

        Request.getNewSquareMessages(userId, pageNum).request(this) { _, data ->
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
            adapter.notifyDataSetChanged()
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