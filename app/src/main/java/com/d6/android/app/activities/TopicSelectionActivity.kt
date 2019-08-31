package com.d6.android.app.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.FriendsAdapter
import com.d6.android.app.adapters.TopicSelectionAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.FriendBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.CHOOSE_Friends
import com.d6.android.app.utils.Const.CHOOSE_TOPIC
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.hideSoftKeyboard
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_topicselection.*

/**
 * 话题选择
 */
class TopicSelectionActivity : BaseActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var pageNum = 1
    private var mTopicsSelections = ArrayList<String>()

    private val topicSelectionAdapter by lazy {
        TopicSelectionAdapter(mTopicsSelections)
    }

    fun adapter(): RecyclerView.Adapter<*> {
        return topicSelectionAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topicselection)
        immersionBar.init()

        topicSelectionAdapter.setOnItemClickListener { view, position ->
            var intent = Intent()
            intent.putExtra(CHOOSE_TOPIC, mTopicsSelections.get(position))
            setResult(RESULT_OK, intent)
            finish()
        }

        iv_back_close.setOnClickListener {
            finish()
        }

        mTopicsSelections.add("#夏日小美好")
        mTopicsSelections.add("#男生动态")
        mTopicsSelections.add("#今天晚上吃什么")
        mTopicsSelections.add("#速度与激情")
        mTopicsSelections.add("#好听的主题曲")
        mTopicsSelections.add("#减肥")

        initRecyclerView()
//        dialog()
//        pullDownRefresh()
    }

    private fun initRecyclerView() {
        swipeRefreshLayout.setLayoutManager(LinearLayoutManager(this))
        swipeRefreshLayout.setMode(SwipeRefreshRecyclerLayout.Mode.None)
        swipeRefreshLayout.isRefreshing = false
        addItemDecoration()
        swipeRefreshLayout.setAdapter(adapter())
        swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshRecyclerLayout.OnRefreshListener {
            override fun onRefresh() {
                pullDownRefresh()
            }

            override fun onLoadMore() {
                loadMore()
            }
        })
        swipeRefreshLayout.mRecyclerView.itemAnimator.changeDuration = 0
    }

    protected fun addItemDecoration(colorId: Int = R.color.color_EFEFEF, size: Int = 1) {
        val item = HorizontalDividerItemDecoration.Builder(this)
                .size(size)
                .color(ContextCompat.getColor(this, colorId))
                .build()
        swipeRefreshLayout.addItemDecoration(item)
    }

    private fun getData(uname:String) {
        Request.findUserFriends(userId,uname,pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                swipeRefreshLayout.visibility = View.VISIBLE
//                mFriends.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    swipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    if(pageNum==1){
                        swipeRefreshLayout.visibility = View.GONE
                    }
                    swipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
//                mFriends.addAll(data.list.results)
            }
//            if (mChooseFriends.size > 0) {
//                for (bean in mFriends) {
//                    if (mChooseFriends.contains(bean)) {
//                        bean.iIsChecked = 1
//                    }
//                }
//            }
//            friendsAdapter.notifyDataSetChanged()
        }
    }

    fun pullDownRefresh() {
        pageNum = 1
        getData("")
    }

    fun loadMore() {
        pageNum++
        getData("")
    }
}