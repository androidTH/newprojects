package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.d6.android.app.R
import com.d6.android.app.adapters.FansAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import kotlinx.android.synthetic.main.header_receiverliked.view.*
import org.jetbrains.anko.startActivity

class FansActivity : RecyclerActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val mHeaderView by lazy{
        layoutInflater.inflate(R.layout.header_receiverliked,mSwipeRefreshLayout.mRecyclerView,false)
    }

    private var pageNum = 1
    private val mMessages = ArrayList<Fans>()
    private val fansAdapter by lazy {
        FansAdapter(mMessages)
    }

    override fun adapter(): RecyclerView.Adapter<*> {
        return fansAdapter
    }

    private val mHeaderFans = ArrayList<Fans>()
    private val mHeaderLikedAdapter by lazy {
        FansAdapter(mHeaderFans)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleBold("收到的喜欢",true)
        fansAdapter .setOnItemClickListener { view, position ->
            val id = mMessages[position].iUserid
            startActivity<UserInfoActivity>("id" to id.toString())
        }
        fansAdapter.setHeaderView(mHeaderView)

        if(mHeaderFans!=null){
            mHeaderView.rv_receivedliked.setHasFixedSize(true)
            mHeaderView.rv_receivedliked.layoutManager = LinearLayoutManager(this)
            mHeaderView.rv_receivedliked.adapter = mHeaderLikedAdapter
        }

        addItemDecoration()
        dialog()
        getData()
    }

    private fun getData() {

        Request.getFindMyFans(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mMessages.clear()
            }
            if (data?.list?.results == null || data.list?.results?.isEmpty() as Boolean) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mMessages.addAll(data.list.results)
                mHeaderFans.addAll(data.list.results)
                mHeaderLikedAdapter.notifyDataSetChanged()
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