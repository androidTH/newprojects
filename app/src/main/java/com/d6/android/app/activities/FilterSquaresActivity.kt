package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.d6.android.app.R
import com.d6.android.app.adapters.SquareAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Square
import com.d6.android.app.models.SquareTypeBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.getLocalUserId
import com.d6.android.app.utils.hideSoftKeyboard
import com.d6.android.app.utils.isCheckOnLineAuthUser
import com.d6.android.app.utils.setLeftDrawable
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import kotlinx.android.synthetic.main.activity_filtersquares.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.lang.Exception

/**
 * 过滤动态
 */
class FilterSquaresActivity : BaseActivity() {

    private var pageNum = 1

    private val mSquareType by lazy{
        intent.getParcelableExtra("squaretype") as SquareTypeBean
    }

    private val mSquares = ArrayList<Square>()
    private var type = 2
    private val squareAdapter by lazy {
        SquareAdapter(mSquares)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtersquares)
        immersionBar.init()

        add_square.setOnClickListener {
            isCheckOnLineAuthUser(this, getLocalUserId()) {
                startActivityForResult<ReleaseNewTrendsActivity>(1)
            }
        }

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        try{
            filter_squretitle.text = mSquareType.getmName()
            var leftDrawable = ContextCompat.getDrawable(this,mSquareType.getmResId())
            setLeftDrawable(leftDrawable,filter_squretitle)
        }catch (e:Exception){
            e.printStackTrace()
        }

        initRecyclerView()
        dialog()
        pullDownRefresh()
    }

    private fun initRecyclerView() {
        swipeRefreshLayout_square.setLayoutManager(LinearLayoutManager(this))
        swipeRefreshLayout_square.setMode(SwipeRefreshRecyclerLayout.Mode.Both)
        swipeRefreshLayout_square.isRefreshing = false
        swipeRefreshLayout_square.setAdapter(squareAdapter)
        swipeRefreshLayout_square.setOnRefreshListener(object : SwipeRefreshRecyclerLayout.OnRefreshListener {
            override fun onRefresh() {
                pullDownRefresh()
            }

            override fun onLoadMore() {
                loadMore()
            }
        })
        swipeRefreshLayout_square.mRecyclerView.itemAnimator.changeDuration = 0
    }

    private fun getSquareList() {
        Request.getSquareList(getLocalUserId(), "", pageNum, 2,sex = type).request(this,false,success={ _, data ->
            if (pageNum == 1) {
                mSquares.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    swipeRefreshLayout_square.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    swipeRefreshLayout_square.setLoadMoreText("暂无数据")
                }
            } else {
                mSquares.addAll(data.list.results)
            }
            squareAdapter.notifyDataSetChanged()
        }){code,msg->
            toast(msg)
        }
    }

    fun pullDownRefresh() {
        pageNum = 1
        getSquareList()
    }

    fun loadMore() {
        pageNum++
        getSquareList()
    }
}