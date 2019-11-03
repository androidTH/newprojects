package com.d6.android.app.fragments
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.BangdanListAdapter
import com.d6.android.app.adapters.RecentlyFansAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.getLoginToken
import kotlinx.android.synthetic.main.item_loveheart.view.*

/**
 * 榜单
 */
class LoveHeartListQuickFragment : RecyclerFragment() {

    override fun setAdapter(): RecyclerView.Adapter<*> {
        return listAdapter
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
       return LinearLayoutManager(context)
    }

    private val mMessages = ArrayList<LoveHeartFans>()

    private var pageNum = 1

    private val listAdapter by lazy {
        BangdanListAdapter(mMessages)
    }

    private val mHeaderFans = ArrayList<LoveHeartFans>()
    private val mHeaderLikedAdapter by lazy {
        RecentlyFansAdapter(mHeaderFans)
    }

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.item_loveheart,null,false)
    }

    private var titleName:String?= ""
    private var type:String = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titleName = it.getString(ARG_PARAM1)
            type = it.getString(ARG_PARAM2)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addItemDecoration(1,R.color.dividing_line_color,0)
        listAdapter.setHeaderView(headerView)
    }

    override fun onFirstVisibleToUser() {
            getData()
    }

    private fun getData() {
        Request.findReceiveLoveList(getLoginToken(),pageNum).request(this) { _, data ->
            data?.let {
                if (pageNum == 1) {
                    mMessages.clear()
                    mHeaderFans.clear()
                }
                if (it.list?.results == null || it.list?.results?.isEmpty() as Boolean) {
                    if (pageNum > 1) {
                        mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                        pageNum--
                    } else {
                        mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                        headerView.rv_loveheart_top.visibility = View.GONE
                    }
                } else {
                    it.list?.results?.let {
                        mMessages.addAll(it)
                    }

                    it.unreadlist?.let { it1 ->
                        if(it1.size>0){
                            mHeaderFans.addAll(it1)
                            mHeaderLikedAdapter.notifyDataSetChanged()
                        }else{
                            headerView.rv_loveheart_top.visibility = View.GONE
                        }
                    }

                    if(it.unreadlist==null){
                        headerView.rv_loveheart_top.visibility = View.GONE
                    }

                    if(it.list?.totalPage==1){
                        mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    }else{
                        mSwipeRefreshLayout.setLoadMoreText("上拉加载更多")
                    }
                }
                listAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun pullDownRefresh() {
        super.pullDownRefresh()
        pageNum=1
        getData()
    }

    override fun loadMore() {
        super.loadMore()
        pageNum++
        getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LoveHeartListQuickFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM2, param2)
//                        putParcelable(ARG_PARAM1,param1)
                        putString(ARG_PARAM1,param1)
                    }
                }
    }
}

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"