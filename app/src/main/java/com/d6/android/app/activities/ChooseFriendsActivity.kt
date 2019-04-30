package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.EditorInfo
import com.d6.android.app.R
import com.d6.android.app.adapters.FriendsAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.hideSoftKeyboard
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_choose_friends.*
import org.jetbrains.anko.toast

class ChooseFriendsActivity : BaseActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var pageNum = 1
    private val mVistors = ArrayList<Fans>()
    private val mChooseFriends = ArrayList<Fans>()
    private val friendsAdapter by lazy {
        FriendsAdapter(mVistors)
    }

    fun adapter(): RecyclerView.Adapter<*> {
       return friendsAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_friends)
        immersionBar.init()
        friendsAdapter.setOnItemClickListener { view, position ->
            val mFans =  mVistors[position]
            if(mFans.iIsFollow==0){
                mFans.iIsFollow =1
                mChooseFriends.add(mFans)
            }else{
                mFans.iIsFollow =0
                mChooseFriends.remove(mFans)
            }
            if(mChooseFriends.size>0){
                tv_choose.text="确定("+mChooseFriends.size+")"
            }else{
                tv_choose.text=getString(R.string.string_choose)
            }
            friendsAdapter.notifyDataSetChanged()
        }
        initRecyclerView()
        dialog()
        getData()
        iv_back_close.setOnClickListener {
            finish()
        }

        et_searchfriends.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                 toast(et_searchfriends.text)
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

    protected fun addItemDecoration(colorId:Int = R.color.dividing_line_color, size:Int=1){
        val item = HorizontalDividerItemDecoration.Builder(this)
                .size(size)
                .color(ContextCompat.getColor(this,colorId))
                .build()
        swipeRefreshLayout.addItemDecoration(item)
    }

    private fun getData() {
        Request.getFindVistors(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mVistors.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    swipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    swipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mVistors.addAll(data.list.results)
            }
            swipeRefreshLayout.isRefreshing = false
            friendsAdapter.notifyDataSetChanged()
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