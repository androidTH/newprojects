package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import com.d6.android.app.R
import com.d6.android.app.adapters.GroupUsersAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.GroupUserBean
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.models.NewGroupBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.getLoginToken
import com.d6.android.app.utils.hideSoftKeyboard
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.rong.imkit.mention.RongMentionManager
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_groupusers.*
import org.jetbrains.anko.startActivity

/**
 * 群成员
 */
class GroupUsersActivity : BaseActivity() {

    private var pageNum = 1
    private val mFriends = ArrayList<GroupUserBean>()
    private var sUserName=""
    private lateinit var mGroupBean: NewGroupBean
    fun IsNotNullGroupBean()=::mGroupBean.isInitialized
    private var onMentionedInput:String = ""

    private val mGroupUsersAdapter by lazy {
        GroupUsersAdapter(mFriends)
    }

    fun adapter(): RecyclerView.Adapter<*> {
       return mGroupUsersAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupusers)
        immersionBar.init()

        if(intent.hasExtra("bean")){
            mGroupBean = intent.getParcelableExtra("bean")
        }

        if(intent.hasExtra("onMentionedInput")){
            onMentionedInput = intent.getStringExtra("onMentionedInput")
        }else{
            onMentionedInput = ""
        }

        mGroupUsersAdapter.setOnItemClickListener { view, position ->
            val mFriends =  mFriends[position]
            if(TextUtils.equals(onMentionedInput,"1")){
                val userInfo = UserInfo("${mFriends.iUserid}", "${mFriends.name}", Uri.parse("${mFriends.picUrl}"))
                RongMentionManager.getInstance().mentionMember(userInfo)
                finish()
            }else{
                startActivity<UserInfoActivity>("id" to "${mFriends.iUserid}")
            }
        }
        initRecyclerView()
        dialog()
        pullDownRefresh()
        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        et_searchusers.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                 sUserName = et_searchusers.text.toString().trim()
                 pullDownRefresh()
                 hideSoftKeyboard(et_searchusers)
                 true
            }
             false
        }

        et_searchusers.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s)) {
                    iv_search_clear.setVisibility(View.GONE)
                } else {
                    iv_search_clear.setVisibility(View.VISIBLE)
                }
            }
        })
        iv_search_clear.setOnClickListener {
            sUserName = ""
            et_searchusers.text.clear()
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

    private fun getData(uname:String) {
        Request.getGroupMemberListByGroupId("${mGroupBean.sId}","${uname}",pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                tv_grouptile.text = "群成员(${data?.list?.totalRecord})"
                rl_friends_empty.visibility = View.GONE
                swipeRefreshLayout.visibility = View.VISIBLE
                mFriends.clear()
            }
            swipeRefreshLayout.isRefreshing = false
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
                if(data.list?.totalPage==1){
                    swipeRefreshLayout.setLoadMoreText("没有更多了")
                }else {
                    swipeRefreshLayout.setLoadMoreText("上拉加载更多")
                }
            }
            mGroupUsersAdapter.notifyDataSetChanged()
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