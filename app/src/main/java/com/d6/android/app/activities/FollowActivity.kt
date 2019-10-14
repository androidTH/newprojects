package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.FansAdapter
import com.d6.android.app.adapters.FollowAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.models.LoveHeartRule
import com.d6.android.app.models.SquareMessage
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getLocalUserId
import com.d6.android.app.utils.getLoginToken
import com.qamaster.android.ui.ScreenshotEditorActivity.startActivity
import kotlinx.android.synthetic.main.header_sendliked.view.*
import org.jetbrains.anko.startActivity

class FollowActivity : RecyclerActivity() {

    private val mHeaderView by lazy{
        layoutInflater.inflate(R.layout.header_sendliked,mSwipeRefreshLayout.mRecyclerView,false)
    }
    private var pageNum = 1
    private val mMessages = ArrayList<LoveHeartFans>()
    private val followAdapter by lazy {
        FollowAdapter(mMessages)
    }

    override fun adapter(): RecyclerView.Adapter<*> {
       return followAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleBold("送出的喜欢",true)
        followAdapter.setOnItemClickListener { view, position ->
            val id = mMessages[position].iSenduserid
            startActivity<UserInfoActivity>("id" to id.toString())
        }
        followAdapter.setHeaderView(mHeaderView)
        addItemDecoration()
        dialog()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        //getFindMyFollows
        Request.findSendLoveList(getLoginToken(),pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mMessages.clear()
                getUserInfo()
            }
            if (data?.list?.results == null || data.list?.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                data.list?.results?.let { mMessages.addAll(it) }
                if(data.list?.totalPage==1){
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                }else {
                    mSwipeRefreshLayout.setLoadMoreText("上拉加载更多")
                }
            }
            followAdapter.notifyDataSetChanged()
        }
    }

    private fun getUserInfo(){
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request(this,false,success= { msg, data ->
            data?.let {
                mHeaderView.tv_sendliked_nums.text = "累计送出 ${it.iSendLovePoint} [img src=redheart_small/]，相互喜欢即可解锁聊天"
            }
        })
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