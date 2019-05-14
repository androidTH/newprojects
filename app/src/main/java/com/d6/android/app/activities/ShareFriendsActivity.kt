package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.d6.android.app.R
import com.d6.android.app.adapters.FriendsAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.FriendBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getShareUserId
import com.d6.android.app.utils.hideSoftKeyboard
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_choose_friends.*
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

class ShareFriendsActivity : BaseActivity() {
    private val mLocalUserId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var pageNum = 1
    private var sUserName=""
    private var mFriends = ArrayList<FriendBean>()
    private var mChooseFriends = ArrayList<FriendBean>()
    private var mSelectionState = "1" //0 代表多选 1代表单选

    private val iType by lazy{
        intent.getIntExtra("iType",0)
    }

    private val sResourceId by lazy{
        intent.getStringExtra("sResourceId")
    }

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
            if(TextUtils.equals(mSelectionState,"1")){
                val mFriend = mFriends[position]
                mChooseFriends.add(mFriend)
                shareFriends()
            }else{
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
                    tv_choose.textColor = ContextCompat.getColor(this,R.color.color_F7AB00)
                }else {
                    tv_choose.text = "确定"
                    tv_choose.textColor = ContextCompat.getColor(this,R.color.color_CDCDCD)
                }
                friendsAdapter.notifyItemChanged(position)
            }
        }

        tv_choose.setOnClickListener {
            if(TextUtils.equals(mSelectionState,"1")){
                tv_choose.text = "确定"
                tv_choose.textColor = ContextCompat.getColor(this,R.color.color_CDCDCD)
                mSelectionState = "0"
            }else{
                if (mChooseFriends != null) {
                    shareFriends()
                }
            }
        }

        if(TextUtils.equals(mSelectionState,"1")){
            tv_choose.text = "多选"
        }else{
            tv_choose.text = "确定"
        }

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        et_searchfriends.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                sUserName = et_searchfriends.text.toString().trim()
                pullDownRefresh()
                hideSoftKeyboard(et_searchfriends)
                true
            }
            false
        }
        initRecyclerView()
        dialog()
        getData("")
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
        swipeRefreshLayout.mRecyclerView.itemAnimator.changeDuration = 0
    }

    protected fun addItemDecoration(colorId: Int = R.color.color_D8D8D8, size: Int = 1) {
        val item = HorizontalDividerItemDecoration.Builder(this)
                .size(size)
                .color(ContextCompat.getColor(this, colorId))
                .build()
        swipeRefreshLayout.addItemDecoration(item)
    }

    private fun getData(username:String) {
        Request.findUserFriends(mLocalUserId,username,pageNum).request(this) { _, data ->
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
        getData(sUserName)
    }

    fun loadMore() {
        pageNum++
        getData(sUserName)
    }

    private fun shareFriends(){
        var userIds = getShareUserId(mChooseFriends)
        Request.shareMessage(mLocalUserId,iType,sResourceId,userIds).request(this,true,success = {msg,data->
            toast(msg.toString())
            mChooseFriends.clear()
            finish()
        })
    }
}