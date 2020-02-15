package com.d6.android.app.activities

import android.os.Bundle
import com.d6.android.app.adapters.SystemMessageAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.SysMessage
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getTrendDetail
import com.d6.android.app.widget.SwipeItemLayout
import org.jetbrains.anko.startActivity


/**
 * 系统消息
 */
class SystemMessagesActivity : RecyclerActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private val mMessages = ArrayList<SysMessage>()
    private val adapter by lazy {
        SystemMessageAdapter(mMessages)
    }
    override fun adapter() = adapter
    private var pageNum = 1
    override fun IsShowFooter(): Boolean {
        return false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "系统消息"
        addItemDecoration()

        adapter.setOnItemClickListener { _, position ->
            val msg = mMessages[position]
//            sysErr("------------->${msg.urltype}")
            when {
                msg.urltype == "0" -> //链接
                    startActivity<WebViewActivity>("url" to (msg.url?:""),"title" to "")
                msg.urltype == "1" -> //广场
//                    getTrendDetail((msg.url?:"")){
//                        startActivity<TrendDetailActivity>("data" to it)
//                    }
                    startActivity<SquareTrendDetailActivity>("id" to (msg.url?:""),"position" to position)
                msg.urltype == "2" -> //会员
                    startActivity<UserInfoActivity>("id" to (msg.url?:""))
                msg.urltype == "4" -> {//速约
                    getSpeedDateDetail((msg.url ?: ""))
                }
                msg.urltype == "5"->{
                    startActivity<MyDateListActivity>()
                }
                else -> startActivity<MessageDetailActivity>("data" to mMessages[position])
            }
        }
        mSwipeRefreshLayout.mRecyclerView.addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(this))
        getData()
    }

    private fun getData() {

        Request.getSystemMessages(userId, pageNum).request(this) { _, data ->
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

    private fun getSpeedDateDetail(id: String) {
        dialog()
        Request.getSpeedDetail(id).request(this){_,data->
            data?.let {
                startActivity<SpeedDateDetailActivity>("data" to it)
            }
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
