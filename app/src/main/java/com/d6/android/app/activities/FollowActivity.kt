package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.adapters.FollowAdapter
import com.d6.android.app.base.RecyclerNewActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.getLocalUserId
import com.d6.android.app.utils.getLoginToken
import com.d6.android.app.widget.RxRecyclerViewDividerTool
import kotlinx.android.synthetic.main.base_recyclerview_layout.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

class FollowActivity : RecyclerNewActivity() {

    private val mHeaderView by lazy{
        layoutInflater.inflate(R.layout.header_sendliked,mSwipeRefreshLayout.mRecyclerView,false)
    }

    override fun IsShowFooter(): Boolean {
        return true
    }

    private var pageNum = 1
    private val mMessages = ArrayList<LoveHeartFans>()
    override fun layoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(this,2)
    }

    private val followAdapter by lazy {
        FollowAdapter(mMessages)
    }

    override fun adapter(): RecyclerView.Adapter<*> {
       return followAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("送出的喜欢")
        rootFl.backgroundColor = ContextCompat.getColor(this,R.color.color_F6F7FA)
        var params= mSwipeRefreshLayout.layoutParams as RelativeLayout.LayoutParams
        params.leftMargin = dip(5)
        params.rightMargin = dip(5)
        mSwipeRefreshLayout.layoutParams = params
        addItemDecoration(RxRecyclerViewDividerTool(dip(10)))

        followAdapter.setOnItemClickListener { view, position ->
            val id = mMessages[position].iSenduserid
            startActivity<UserInfoActivity>("id" to id.toString())
        }
        followAdapter.setHeaderView(mHeaderView)
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
        tv_bottom_tips.text = "送出喜欢[img src=redheart_small/] 超过${Const.iLovePointShow}将升级为超级喜欢，直接通知对方"
    }

    private fun getUserInfo(){
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request(this,false,success= { msg, data ->
            data?.let {
//                mHeaderView.tv_sendliked_nums.text = "${it.iSendLovePoint} [img src=redheart_small/]"
                setSmallTitle("累计送出${it.iSendLovePoint} [img src=redheart_small/]，相互喜欢即可解锁聊天")
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