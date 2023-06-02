package com.d6zone.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.SystemMessageNewAdapter
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.SysMessage
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.SPUtils
import com.d6zone.android.app.widget.SwipeRefreshRecyclerLayout
import kotlinx.android.synthetic.main.activity_systemmessage.*
import org.jetbrains.anko.startActivity

/**
 * 系统消息
 */
class SystemMessagesNewActivity : BaseActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private val mMessages = ArrayList<SysMessage>()
    private val adapter by lazy {
        SystemMessageNewAdapter(mMessages)
    }

    private var pageNum = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_systemmessage)
        immersionBar.init()

        swipeRefreshLayout.setLayoutManager(LinearLayoutManager(this))
        swipeRefreshLayout.setMode(SwipeRefreshRecyclerLayout.Mode.Both)
        swipeRefreshLayout.isRefreshing = false

        iv_back_close.setOnClickListener {
            finish()
        }

        swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshRecyclerLayout.OnRefreshListener{
            override fun onRefresh() {
                pullDownRefresh()
            }

            override fun onLoadMore() {
                loadMore()
            }
        })

        adapter.setOnItemClickListener { _, position ->
            val msg = mMessages[position]
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
//                    startActivity<MyDateListActivity>()
                }
                else -> startActivity<MessageDetailActivity>("data" to mMessages[position])
            }
        }

        swipeRefreshLayout.setAdapter(adapter)
        getData()
    }

    private fun getData() {
        Request.getSystemMessages(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mMessages.clear()
            }
            swipeRefreshLayout.isRefreshing = false
            if (data?.list?.results == null || data.list?.results.isEmpty()) {
                if (pageNum > 1) {
                    swipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    swipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                data.list.results?.let { mMessages.addAll(it) }
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

    fun pullDownRefresh() {
        pageNum = 1
        getData()
    }

    fun loadMore() {
        pageNum++
        getData()
    }
}
