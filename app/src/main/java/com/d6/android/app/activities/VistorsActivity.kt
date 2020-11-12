package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.adapters.VistorAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.widget.GridItemDecoration
import com.d6.android.app.widget.RxRecyclerViewDividerTool
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

class VistorsActivity : RecyclerActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mVistors = ArrayList<Fans>()

    private val mHeaderView by lazy{
        layoutInflater.inflate(R.layout.recyclerview_top_line,mSwipeRefreshLayout.mRecyclerView,false)
    }

    override fun layoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(this,2)
    }

    private val vistorAdapter by lazy {
        VistorAdapter(mVistors)
    }

    override fun IsShowFooter(): Boolean {
        return false
    }

    override fun adapter(): RecyclerView.Adapter<*> {
       return vistorAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "访客"
        vistorAdapter.setOnItemClickListener { view, position ->
            val id =  mVistors[position].iVistorid
            startActivity<UserInfoActivity>("id" to id.toString())
        }

//        vistorAdapter.setHeaderView(mHeaderView)
        rootFl.backgroundColor = ContextCompat.getColor(this,R.color.color_F6F7FA)
        var params= mSwipeRefreshLayout.layoutParams as RelativeLayout.LayoutParams
        params.leftMargin = dip(5)
        params.rightMargin = dip(5)
//        mSwipeRefreshLayout.setPadding(0,dip(10),0,0)
        mSwipeRefreshLayout.layoutParams = params

//        var divider = GridItemDecoration.Builder(this)
//                .setHorizontalSpan(R.dimen.margin_10)
//                .setVerticalSpan(R.dimen.margin_10)
//                .setColorResource(R.color.color_F6F7FA)
//                .setShowLastLine(false)
//                .setShowVerticalLine(true)
//                .build()
        addItemDecoration(RxRecyclerViewDividerTool(dip(10)))
        dialog()
        getData()
    }

    private fun getData() {

        Request.getFindVistors(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mVistors.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mVistors.addAll(data.list.results)
            }
            vistorAdapter.notifyDataSetChanged()
        }
    }

    override fun pullDownRefresh() {
        super.pullDownRefresh()
        pageNum = 1
        getData()
    }

    override fun loadMore() {
        super.loadMore()
        pageNum++
        getData()
    }
}