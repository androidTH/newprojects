package com.d6.android.app.base

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent
import kotlin.properties.Delegates

/**
 * recyclerView 的activity 。
 */
abstract class RecyclerNewActivity : NewTitleActivity() {

    val mSwipeRefreshLayout by lazy {
        find<SwipeRefreshRecyclerLayout>(R.id.swipeRefreshLayout)
    }
    protected open var rootFl by Delegates.notNull<RelativeLayout>()
    protected open var footer by Delegates.notNull<LinearLayout>()
    private var emptyView: TextView? = null
    private var adapter :RecyclerView.Adapter<*>? = null

    /**
     * 适配器
     */
    protected abstract fun adapter():RecyclerView.Adapter<*>
    /**
     * layoutManager 默认LinearLayoutManager
     */
    protected open fun layoutManager():RecyclerView.LayoutManager = LinearLayoutManager(this)

    /**
     * 模式。默认上下拉均支持
     */
    protected open fun mode():SwipeRefreshRecyclerLayout.Mode = SwipeRefreshRecyclerLayout.Mode.Both
    /**
     * 下拉刷新
     */
    protected open fun pullDownRefresh(){}

    /**
     * 上拉更多。
     */
    protected open fun loadMore(){}

    protected abstract open fun IsShowFooter():Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_recyclerview_layout)
        immersionBar.init()
        rootFl = find(R.id.rootFL)
        footer = find(R.id.footer)
        if(IsShowFooter()){
            footer.visibility = View.VISIBLE
        }
        mSwipeRefreshLayout.setLayoutManager(layoutManager())
        mSwipeRefreshLayout.setMode(mode())
        mSwipeRefreshLayout.isRefreshing = false
        adapter = adapter()
        mSwipeRefreshLayout.setAdapter(adapter)
        mSwipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshRecyclerLayout.OnRefreshListener{
            override fun onRefresh() {
                pullDownRefresh()
            }

            override fun onLoadMore() {
                loadMore()
            }
        })

        mIvBackClose.setOnClickListener {
            onBackPressed()
        }
    }

    protected fun getItemDecoration(colorId:Int = R.color.dividing_line_color, size:Int=1):HorizontalDividerItemDecoration{
        var item = HorizontalDividerItemDecoration.Builder(this)
                .size(size)
                .color(ContextCompat.getColor(this,colorId))
                .build()
        return item
    }
    /**
     * 水平分割线，默认1px，颜色为dividing_line_color
     */
    protected fun addItemDecoration(colorId:Int = R.color.dividing_line_color, size:Int=1){
        val item = HorizontalDividerItemDecoration.Builder(this)
                .size(size)
                .color(ContextCompat.getColor(this,colorId))
                .build()
        addItemDecoration(item)
    }

    protected fun addItemDecoration(itemDecoration: RecyclerView.ItemDecoration){
        mSwipeRefreshLayout.addItemDecoration(itemDecoration)
    }

    override fun dismissDialog() {
        super.dismissDialog()
        setRefresh(false)
    }
    /**
     * 可要可不要。可以直接在需要的地方用里面那句。
     */
    fun setRefresh(refresh: Boolean) {
        mSwipeRefreshLayout.isRefreshing = refresh
    }

    private val observer:RecyclerView.AdapterDataObserver = object :RecyclerView.AdapterDataObserver(){
        override fun onChanged() {
            super.onChanged()
            toggleEmptyView()
        }
    }

    private fun toggleEmptyView() {
        var count = adapter!!.itemCount
        if (adapter is HFRecyclerAdapter<*>) {
            count = (adapter as HFRecyclerAdapter<*>).getDataItemCount()
        }
        if (emptyView != null) {
            if (count > 0) {
                emptyView!!.visibility = View.GONE
            } else {
                emptyView!!.visibility = View.VISIBLE
            }
        }
    }

    protected fun addEmptyView(text:String,leftRes:Int=0,topRes:Int=0,rightRes:Int=0,bottomRes:Int=0) {
        if (emptyView == null) {
            emptyView = TextView(this)
            val lp = FrameLayout.LayoutParams(wrapContent, wrapContent)
            lp.gravity = Gravity.CENTER
            emptyView!!.layoutParams = lp
            emptyView!!.gravity = Gravity.CENTER
            emptyView!!.textColor = ContextCompat.getColor(this,R.color.textColor99)
            emptyView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f)
            emptyView!!.setCompoundDrawablesWithIntrinsicBounds(leftRes,topRes,rightRes,bottomRes)
            rootFl.addView(emptyView,0)
            if (adapter != null) {
                adapter!!.registerAdapterDataObserver(observer)
                toggleEmptyView()
            }
        }
        emptyView!!.text = text
    }

    override fun onDestroy() {
        try {
            adapter!!.unregisterAdapterDataObserver(observer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //同步置空view
        emptyView = null
        immersionBar.destroy()
        super.onDestroy()
    }

}