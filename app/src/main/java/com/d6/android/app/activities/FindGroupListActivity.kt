package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.FindGroupListAdapter
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
 * 群列表
 */
class FindGroupListActivity : BaseActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener {

    private val mGroupList = ArrayList<NewGroupBean>()
    private val mGroupListAdapter by lazy {
        FindGroupListAdapter(mGroupList)
    }

    override fun onRefresh() {
        pageNum = 1
        getGroupData()
    }

    override fun onLoadMore() {
        pageNum++
        getGroupData()
    }

    private var pageNum = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findgrouplist)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.trans_parent).statusBarDarkFont(true).init()
//        addItemDecoration()
        userlist_refreshrecycler.setLayoutManager(LinearLayoutManager(this))
        userlist_refreshrecycler.setMode(SwipeRefreshRecyclerLayout.Mode.Both)
        userlist_refreshrecycler.setAdapter(mGroupListAdapter)
        userlist_refreshrecycler.addItemDecoration(getItemDecoration())

        mGroupListAdapter.setOnItemClickListener { _, position ->
            val groupBean = mGroupList[position]
            startActivity<GroupJoinActivity>("bean" to groupBean)
        }

        userlist_refreshrecycler.setOnRefreshListener(this)

        tv_userlist_back.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        getGroupData()
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

    private fun getGroupData() {
        Request.getMyGroupList(pageNum).request(this,success = { _, data ->
            data?.let {
                if (pageNum == 1) {
                    mGroupList.clear()
                }
                userlist_refreshrecycler.isRefreshing = false
                if (data?.list?.results == null || data.list.results.isEmpty()) {
                    if (pageNum > 1) {
                        userlist_refreshrecycler.setLoadMoreText("--- 更多优质群陆续开放中 ---")
                    } else {
                        userlist_refreshrecycler.setLoadMoreText("--- 更多优质群陆续开放中 ---")
                    }
                } else {
                    data.list.results?.let { mGroupList.addAll(it) }
                }
                if(data?.list?.totalPage==0||data?.list?.totalPage==1){
                    userlist_refreshrecycler.setLoadMoreText("--- 更多优质群陆续开放中 ---")
                }else{
                    userlist_refreshrecycler.setLoadMoreText("上拉加载更多")
                }
                mGroupListAdapter.notifyDataSetChanged()
                if((data?.list?.results == null || data.list.results.isEmpty())&&data?.list?.totalPage==0){
                    userlist_refreshrecycler.setLoadMoreText("--- 更多优质群陆续开放中 ---")
                }else {

                }
            }
        }) { code, msg ->
            toast(msg)
        }
    }
}
