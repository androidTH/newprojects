package com.d6zone.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.EditorInfo
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.SearchFriendsAdapter
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.UserData
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.SPUtils
import com.d6zone.android.app.utils.hideSoftKeyboard
import com.d6zone.android.app.widget.SwipeRefreshRecyclerLayout
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_search_friends.*
import org.jetbrains.anko.startActivity


/**
 * 客服查找
 */
class SearchFriendsActivity : BaseActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var pageNum = 1
    private val mFriends = ArrayList<UserData>()
    private var sUserName="";
    private val friendsAdapter by lazy {
        SearchFriendsAdapter(mFriends)
    }

    fun adapter(): RecyclerView.Adapter<*> {
       return friendsAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_friends)
        immersionBar.init()
        friendsAdapter.setOnItemClickListener { view, position ->
            val mFriends =  mFriends[position]
            startActivity<UserInfoActivity>("id" to mFriends.accountId.toString())
        }
        initRecyclerView()
        dialog()
        pullDownRefresh()
        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        et_searchfriends.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                 sUserName = et_searchfriends.text.toString().trim()
                 pullDownRefresh()
                 hideSoftKeyboard(et_searchfriends)
                 true
            }
             false
        }
    }

    private fun initRecyclerView(){
        swipeRefreshLayout.setLayoutManager(LinearLayoutManager(this))
        swipeRefreshLayout.setMode(SwipeRefreshRecyclerLayout.Mode.Both)
        swipeRefreshLayout.isRefreshing = false
        addItemDecoration()
        swipeRefreshLayout.setAdapter(adapter())
        swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshRecyclerLayout.OnRefreshListener{
            override fun onRefresh() {
                pullDownRefresh()
            }

            override fun onLoadMore() {
                loadMore()
            }
        })
    }

    protected fun addItemDecoration(colorId:Int = R.color.color_D8D8D8, size:Int=1){
        val item = HorizontalDividerItemDecoration.Builder(this)
                .size(size)
                .color(ContextCompat.getColor(this,colorId))
                .build()
        swipeRefreshLayout.addItemDecoration(item)
    }

//    private fun searchFriends(sUserName:String){
//        pageNum =1
//        Request.findAllUserFriends(userId,sUserName,pageNum).request(this){_,data->
//            if (pageNum == 1) {
//                mFriends.clear()
//            }
//            if (data?.list?.results == null || data.list.results.isEmpty()) {
//                if (pageNum > 1) {
//                    swipeRefreshLayout.setLoadMoreText("没有更多了")
//                    pageNum--
//                } else {
//                    swipeRefreshLayout.setLoadMoreText("暂无数据")
//                }
//            } else {
//                mFriends.addAll(data.list.results)
//            }
//            swipeRefreshLayout.isRefreshing = false
//            friendsAdapter.notifyDataSetChanged()
//        }
//    }

    private fun getData(uname:String) {
        Request.findAllUserFriends(userId,uname,pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                rl_friends_empty.visibility = View.GONE
                swipeRefreshLayout.visibility = View.VISIBLE
                mFriends.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    swipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    if(pageNum==1){
                        rl_friends_empty.visibility = View.VISIBLE
                        swipeRefreshLayout.visibility = View.GONE
                    }
                    swipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mFriends.addAll(data.list.results)
            }
            swipeRefreshLayout.isRefreshing = false
            friendsAdapter.notifyDataSetChanged()
        }
    }

     fun pullDownRefresh() {
        pageNum = 1
        getData(sUserName)
    }

     fun loadMore() {
        pageNum++
        getData(sUserName)
    }
}