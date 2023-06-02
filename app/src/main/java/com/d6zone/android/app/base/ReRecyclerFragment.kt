package com.d6zone.android.app.base

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.d6zone.android.app.R
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import org.jetbrains.anko.support.v4.find
import kotlin.properties.Delegates

/**
 * RecyclerView的fragment
 */
abstract class ReRecyclerFragment : BaseFragment() {
    /**
     *
     * 如果使用在viewpager等只回收view不回收数据的控件内，这里不能是用by lazy和val ，会导致该声明的既不能重新赋值，也指向错误的view地址，从而出现未知bug。
     * 如：RecyclerView: No adapter attached; skipping layout Error
     */
    var mSwipeRefreshLayout by Delegates.notNull<SwipeRefreshLayout>()
    lateinit var mBaseRecyclerView:RecyclerView
    fun ISNOTBaseRecyclerView()=::mBaseRecyclerView.isInitialized

    override fun contentViewId(): Int = R.layout.re_base_recyclerview_layout

    private var adapter: BaseQuickAdapter<*,*>? = null
    protected abstract fun setAdapter(): BaseQuickAdapter<*,*>

    /**
     * 下拉刷新
     */
    protected open fun pullDownRefresh() {}

    /**
     * 加载更多
     */
    protected open fun loadMore() {}

    /**
     * @return  默认垂直布局。
     */
    abstract fun getLayoutManager(): RecyclerView.LayoutManager

    open fun getHasFixedSize():Boolean{
        return true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSwipeRefreshLayout = find(R.id.base_swipeLayout)
        mBaseRecyclerView = find(R.id.base_rv_list)
        mSwipeRefreshLayout.setBackgroundColor(Color.WHITE)
        setRefreshing(false)
        mBaseRecyclerView.setHasFixedSize(getHasFixedSize())
        mBaseRecyclerView.layoutManager = getLayoutManager()

        mSwipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            pullDownRefresh()
        })

        adapter = setAdapter()
        mBaseRecyclerView.adapter = adapter
        adapter!!.setOnLoadMoreListener(BaseQuickAdapter.RequestLoadMoreListener {
            loadMore()
        }, mBaseRecyclerView)
    }

    override fun dismissDialog() {
        super.dismissDialog()
        setRefreshing(false)
    }
    /**
     * 设置刷新状态
     * @param refreshing 刷新状态
     */
    fun setRefreshing(refreshing: Boolean) {
        mSwipeRefreshLayout.isRefreshing = refreshing
    }

    fun addItemDecoration(size: Int = 1, colorRes: Int = R.color.dividing_line_color, decoration: Int = 0) {
        val itemDe = if (decoration != 0) {
            VerticalDividerItemDecoration.Builder(context)
                    .size(size)
                    .color(ContextCompat.getColor(context, colorRes))
                    .build()
        } else {
            HorizontalDividerItemDecoration.Builder(context)
                    .size(size)
                    .color(ContextCompat.getColor(context, colorRes))
                    .build()
        }

        addItemDecoration(itemDe)
    }

    fun addItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        mBaseRecyclerView.addItemDecoration(itemDecoration)
    }
}