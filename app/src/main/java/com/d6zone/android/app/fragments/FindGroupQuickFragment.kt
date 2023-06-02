package com.d6zone.android.app.fragments
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import com.chad.library.adapter.base.BaseQuickAdapter
import com.d6zone.android.app.R
import com.d6zone.android.app.activities.GroupJoinActivity
import com.d6zone.android.app.adapters.FindGroupQuickAdapter
import com.d6zone.android.app.base.ReRecyclerFragment
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.FindGroupBean
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.widget.loadmore.FindGroupLoadMoreView
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import org.jetbrains.anko.support.v4.startActivity

/**
 * 群列表
 */
class FindGroupQuickFragment : ReRecyclerFragment() {

    override fun setAdapter(): BaseQuickAdapter<*, *> {
        return FindGroupAdapter
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
       return LinearLayoutManager(context)
    }

    private val mGroupList = ArrayList<FindGroupBean>()
    private var pageNum = 1

    private val FindGroupAdapter by lazy {
        FindGroupQuickAdapter(mGroupList)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addItemDecoration()
        FindGroupAdapter.setLoadMoreView(FindGroupLoadMoreView())
        FindGroupAdapter.setOnItemClickListener { adapter, view, position ->
            val groupBean = mGroupList[position]
            if(groupBean.iJoinGroup>0){
                RongIM.getInstance().startConversation(context, Conversation.ConversationType.GROUP,"${groupBean.sId}", "")
            }else{
                startActivity<GroupJoinActivity>("bean" to groupBean)
            }
        }
        FindGroupAdapter.setHeaderAndEmpty(true)
    }

    override fun onFirstVisibleToUser() {
        getData()
    }

    private fun getData() {
        Request.findGroups("",pageNum).request(this) { _, data ->
            data?.let {
                if (pageNum == 1) {
                    FindGroupAdapter.data.clear()
                    setRefreshing(false)
                    FindGroupAdapter.loadMoreEnd(true)
                    FindGroupAdapter.setEnableLoadMore(true)
                }
                if (data?.list?.results == null || data.list?.results.isEmpty()) {
                    FindGroupAdapter.loadMoreEnd(false)
                } else {
                    data.list.results?.let { FindGroupAdapter.addData(it) }
                    FindGroupAdapter.loadMoreComplete()
                }
                FindGroupAdapter.notifyDataSetChanged()

                if(FindGroupAdapter.data.size==0&&activity!=null){
                    FindGroupAdapter.emptyView = LayoutInflater.from(activity).inflate(R.layout.empty_group_layout,null)
                }
            }
        }
    }

    override fun pullDownRefresh() {
        super.pullDownRefresh()
        pageNum=1
        FindGroupAdapter.setEnableLoadMore(false)
        getData()
    }

    override fun loadMore() {
        super.loadMore()
        pageNum++
        getData()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                FindGroupQuickFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1,param1)
                        putInt(ARG_PARAM2, param2)
                    }
                }
    }
}

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"