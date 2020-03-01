package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.HeaderGroupListAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.adapters.UsersFrendListAdapter
import com.d6.android.app.models.*
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_userslist.*
import kotlinx.android.synthetic.main.header_grouplist.view.*
import org.jetbrains.anko.toast
import org.jetbrains.anko.startActivity

/**
 * 分享群和好友
 */
class GoodFriendsListActivity : BaseActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener {
    override fun onRefresh() {
        pageNum = 1
        getData()
    }

    override fun onLoadMore() {
        pageNum++
        getData()
    }

    private val mFriends = ArrayList<FriendBean>()
    private val mGroupUsersAdapter by lazy {
        UsersFrendListAdapter(mFriends)
    }

    private var pageNum = 1
    private val mHeaderView by lazy {
        layoutInflater.inflate(R.layout.header_grouplist, userlist_refreshrecycler.mRecyclerView, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userslist)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.trans_parent).statusBarDarkFont(true).init()
//        addItemDecoration()
        userlist_refreshrecycler.setLayoutManager(LinearLayoutManager(this))
        userlist_refreshrecycler.setAdapter(mGroupUsersAdapter)
        userlist_refreshrecycler.addItemDecoration(getItemDecoration())
        mGroupUsersAdapter.setHeaderView(mHeaderView)

        if(mHeaderView!=null){
            mHeaderView.rv_grouplist.setHasFixedSize(true)
            mHeaderView.rv_grouplist.layoutManager = LinearLayoutManager(this)
            mHeaderView.rv_grouplist.addItemDecoration(getItemDecoration())
            mHeaderView.rv_grouplist.adapter = mGroupListAdapter
            mGroupListAdapter.setOnItemClickListener { _, position ->
                val groupBean = mGroupList[position]
                RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP,"${groupBean.sId}","${groupBean.sGroupName}")
            }
        }

        userlist_refreshrecycler.setOnRefreshListener(this)

        tv_userlist_back.setOnClickListener {
            finish()
        }

        mGroupUsersAdapter.setOnItemClickListener { view, position ->
            var userBean = mFriends[position]
            startActivity<UserInfoActivity>("id" to "${userBean.iUserid}")
//            if(RongIM.getInstance()!=null){
//                RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, "${userBean.iUserid}", "${userBean.sUserName}")
//            }
        }
        getGroupData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    protected fun getItemDecoration(colorId:Int = R.color.color_EAEAEA, size:Int=1):HorizontalDividerItemDecoration{
        var item = HorizontalDividerItemDecoration.Builder(this)
                .size(size)
                .color(ContextCompat.getColor(this,colorId))
                .build()
        return item
    }

    protected fun addItemDecoration(colorId:Int = R.color.color_EFEFEF, size:Int=1){
        val item = HorizontalDividerItemDecoration.Builder(this)
                .size(size)
                .color(ContextCompat.getColor(this,colorId))
                .build()
        userlist_refreshrecycler.addItemDecoration(item)
    }

    private val mGroupList = ArrayList<NewGroupBean>()
    private val mGroupListAdapter by lazy {
        HeaderGroupListAdapter(mGroupList)
    }

    private fun getGroupData() {
        Request.getMyGroupList(pageNum).request(this,success = { _, data ->
            data?.let {
                mGroupList.clear()
                if (it.list?.results == null || it.list.results.isEmpty()) {
                    mHeaderView.tv_receiveliked_title.visibility = View.GONE
                    mHeaderView.rv_grouplist.visibility = View.GONE
                }else{
                    mGroupList.addAll(it.list?.results)
                    mGroupListAdapter.notifyDataSetChanged()
                }
            }
        }) { code, msg ->
            toast(msg)
        }
    }

    private fun getData() {
        Request.findUserFriends_New("",pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mFriends.clear()
            }
            userlist_refreshrecycler.isRefreshing = false
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    userlist_refreshrecycler.setLoadMoreText("没有更多了")
                } else {
                    userlist_refreshrecycler.setLoadMoreText("暂无数据")
                }
            } else {
                data.list.results?.let { mFriends.addAll(it) }
            }
            if(data?.list?.totalPage==0||data?.list?.totalPage==1){
                userlist_refreshrecycler.setLoadMoreText("没有更多了")
            }else{
                userlist_refreshrecycler.setLoadMoreText("上拉加载更多")
            }
            mGroupUsersAdapter.notifyDataSetChanged()
            if((data?.list?.results == null || data.list.results.isEmpty())&&data?.list?.totalPage==0){
                userlist_refreshrecycler.setLoadMoreText("")
            }else {

            }
        }
    }
}
