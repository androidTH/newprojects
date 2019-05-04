package com.d6.android.app.activities

import android.content.Intent
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
import com.d6.android.app.models.FriendBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.CHOOSE_Friends
import com.d6.android.app.utils.NetworkUtils
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.hideSoftKeyboard
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_choose_friends.*
import org.jetbrains.anko.toast
import java.lang.Exception

class ChooseFriendsActivity : BaseActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var pageNum = 1
    private var mFriends = ArrayList<FriendBean>()
    private var mChooseFriends = ArrayList<FriendBean>()

    private val friendsAdapter by lazy {
        FriendsAdapter(mFriends)
    }

    fun adapter(): RecyclerView.Adapter<*> {
        return friendsAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_friends)
        immersionBar.init()
        friendsAdapter.setOnItemClickListener { view, position ->
            val mFriend = mFriends[position]
            if (mChooseFriends.contains(mFriend)) {
                mFriend.iIsChecked = 0
                mChooseFriends.remove(mFriend)
            } else {
                if (mChooseFriends.size > 4) {
                    toast("最多选择5个用户")
                } else {
                    mFriend.iIsChecked = 1
                    mChooseFriends.add(mFriend)
                }
            }
            if (mChooseFriends.size > 0) {
                tv_choose.text = "确定(" + mChooseFriends.size + ")"
            } else {
                tv_choose.text = getString(R.string.string_choose)
            }
            friendsAdapter.notifyDataSetChanged()
        }

        tv_choose.setOnClickListener {
            var intent = Intent()
            intent.putParcelableArrayListExtra(CHOOSE_Friends, mChooseFriends)
            setResult(RESULT_OK, intent)
            finish()
        }

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        et_searchfriends.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                toast(et_searchfriends.text)
                hideSoftKeyboard(et_searchfriends)
                true
            }
            false
        }
        try {
            mChooseFriends = intent.getParcelableArrayListExtra<FriendBean>(CHOOSE_Friends)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initRecyclerView()
        dialog()
        getData()
    }

    private fun initRecyclerView() {
        swipeRefreshLayout.setLayoutManager(LinearLayoutManager(this))
        swipeRefreshLayout.setMode(SwipeRefreshRecyclerLayout.Mode.Both)
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
    }

    protected fun addItemDecoration(colorId: Int = R.color.dividing_line_color, size: Int = 1) {
        val item = HorizontalDividerItemDecoration.Builder(this)
                .size(size)
                .color(ContextCompat.getColor(this, colorId))
                .build()
        swipeRefreshLayout.addItemDecoration(item)
    }

    private fun getData() {
        Request.findUserFriends(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mFriends.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    swipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    swipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mFriends.addAll(data.list.results)
            }
            if (mChooseFriends.size > 0) {
                for (bean in mFriends) {
                    if (mChooseFriends.contains(bean)) {
                        bean.iIsChecked = 1
                    }
                }
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