package com.d6.android.app.fragments

import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.activities.SquareTrendDetailActivity
import com.d6.android.app.activities.TrendDetailActivity
import com.d6.android.app.adapters.MySquareAdapter
import com.d6.android.app.adapters.SquareAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getTrendDetail
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivityForResult

/**
 * Created on 2017/12/17.
 */
class MySquareFragment : RecyclerFragment() {
    companion object {
        fun instance(type: Int): MySquareFragment {
            val fragment = MySquareFragment()
            val b = Bundle()
            b.putInt("type", type)
            fragment.arguments = b
            return fragment
        }
    }
    private val type by lazy {
        if (arguments == null) {
            0
        } else {
            arguments.getInt("type", 0)
        }
    }
    private val mSquares = ArrayList<Square>()
    private val squareAdapter by lazy {
        MySquareAdapter(mSquares,type)
    }

    private var pageNum = 1
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun setAdapter() = squareAdapter

    override fun onFirstVisibleToUser() {

        mSwipeRefreshLayout.addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                .colorResId(R.color.dividing_line_color)
                .size(dip(8))
                .build())

        squareAdapter.setOnItemClickListener { view, position ->
            val square = mSquares[position]
            square.id?.let {
//                (activity as BaseActivity).getTrendDetail(it){
//                    startActivityForResult<TrendDetailActivity>(1, "data" to it)
//                }
                startActivityForResult<SquareTrendDetailActivity>(1,"id" to it)

            }
        }

        squareAdapter.setOnCommentClick { p ->
            val square = mSquares[p]
            square.id?.let {
//                (activity as BaseActivity).getTrendDetail(it){
//                    startActivityForResult<TrendDetailActivity>(1, "data" to it)
//                }
                startActivityForResult<SquareTrendDetailActivity>(1,"id" to it)
            }
        }

        showDialog()
        getData()
    }

    private fun getData() {

        Request.getMySquares(userId, type, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mSquares.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mSquares.addAll(data.list.results)
            }
            squareAdapter.notifyDataSetChanged()
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