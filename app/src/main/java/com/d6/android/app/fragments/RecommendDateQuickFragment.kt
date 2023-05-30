package com.d6.android.app.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.d6.android.app.R
import com.d6.android.app.activities.FindDateDetailActivity
import com.d6.android.app.activities.SpeedDateDetailActivity
import com.d6.android.app.adapters.RecommentAllQuickDateAdapter
import com.d6.android.app.base.ReRecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.loadmore.CustomLoadMoreView
import org.jetbrains.anko.support.v4.startActivity

/**
 * 人工推荐
 */
class RecommendDateQuickFragment : ReRecyclerFragment() {

    private var pageNum = 1
    private val mSpeedDates = ArrayList<MyDate>()

    private val dateAdapter by lazy {
        RecommentAllQuickDateAdapter(mSpeedDates);
    }

    private var iLookType: String = ""
    private var sPlace: String = ""

    override fun setAdapter(): BaseQuickAdapter<*, *> {
        return dateAdapter
    }

    private val mRecommendHeaderView by lazy{
//        layoutInflater.inflate(R.layout.layout_header_recommend, null,false)
        LayoutInflater.from(getActivity()).inflate(R.layout.layout_header_recommend, null,false)
    }

//    override fun getLayoutManager(): GridLayoutManager {
//        return GridLayoutManager(context, 2)
//    }

    override fun getLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            iLookType = it.getString(ARG_PARAM1)
            sPlace = it.getString(ARG_PARAM2)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        immersionBar.statusBarColor(R.color.color_black).statusBarDarkFont(false).init()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSwipeRefreshLayout.setBackgroundColor(Color.BLACK)
        dateAdapter.setLoadMoreView(CustomLoadMoreView())
        dateAdapter.setOnItemClickListener { adapter, view, position ->
                val date = dateAdapter.data[position]
                if (date.iType == 1) {
                    startActivity<FindDateDetailActivity>("data" to date)
                } else if (date.iType == 2) {
                    startActivity<SpeedDateDetailActivity>("data" to date)
                }
        }
        dateAdapter.setHeaderAndEmpty(true)
        if(!TextUtils.isEmpty(iLookType)){
            var tv_datetype_desc = mRecommendHeaderView.findViewById<TextView>(R.id.tv_datetype_desc)
            if(TextUtils.equals(iLookType,"1")){
                tv_datetype_desc.text = "当天快速匹配"
            }else if(TextUtils.equals(iLookType,"5")){
                tv_datetype_desc.text = "每日最新会员"
            }else if(TextUtils.equals(iLookType,"2")){
                tv_datetype_desc.text = "精准匹配合拍的TA"
            }else if(TextUtils.equals(iLookType,"3")){
                tv_datetype_desc.text = "近期快速匹配"
            }else if(TextUtils.equals(iLookType,"4")){
                tv_datetype_desc.text = "边旅行,边约会"
            }
            dateAdapter.addHeaderView(mRecommendHeaderView)
        }
    }

    override fun onFirstVisibleToUser() {
        getFindRecommend(ilookType = iLookType,city = sPlace)
    }

    fun getFindRecommend(ilookType: String="", city: String=""):RecommendDateQuickFragment{
        pageNum = 1
        if(ISNOTBaseRecyclerView()){
            mSwipeRefreshLayout.isRefreshing = true
            if(pageNum==1){
                mBaseRecyclerView.scrollToPosition(0)
            }
            mSwipeRefreshLayout.postDelayed(object:Runnable{
                override fun run() {
                    pullRefresh(ilookType,city)
                }
            },600)
        }
        return this
    }

    fun getData(ilookType: String, city: String) {
        this.iLookType = ilookType
        this.sPlace = city
        Request.findLookAllAboutList(getLocalUserId(), iLookType, sPlace, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                dateAdapter.data.clear()
                setRefreshing(false)
                dateAdapter.loadMoreEnd(true)
                dateAdapter.setEnableLoadMore(true)
            }
            if (data?.list?.results == null || data.list?.results.isEmpty()) {
                dateAdapter.loadMoreEnd(false)
            } else {
                data.list.results?.let { dateAdapter.addData(it) }
                dateAdapter.loadMoreComplete()
            }
            dateAdapter.notifyDataSetChanged()
        }

        if(dateAdapter.data.size==0&&activity!=null){
            dateAdapter.emptyView = LayoutInflater.from(activity).inflate(R.layout.no_empty_layout,null)
        }
    }

    fun pullRefresh(ilookType: String="", city: String="") {
        dateAdapter.setEnableLoadMore(false)//这里的作用是防止下拉刷新的时候还可以上拉加载
        getData(ilookType, city)
    }

    override fun pullDownRefresh() {
        super.pullDownRefresh()
        pageNum =1
        pullRefresh(iLookType,sPlace)
    }

    override fun loadMore() {
        super.loadMore()
        pageNum++
        getData(iLookType, sPlace)
    }

    override fun onDestroy() {
        super.onDestroy()
        dateAdapter.data.clear()
        immersionBar.destroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                RecommendDateQuickFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"