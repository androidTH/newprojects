package com.d6.android.app.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.activities.MainActivity
import com.d6.android.app.activities.SquareTrendDetailActivity
import com.d6.android.app.adapters.BannerAdapter
import com.d6.android.app.adapters.NetWorkImageHolder
import com.d6.android.app.adapters.SquareAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Banner
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import kotlinx.android.synthetic.main.header_square_list.view.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult

/**
 * Created on 2017/12/17.
 * 动态
 */
class SquareFragment : RecyclerFragment() {
    companion object {
        fun instance(id: String?): SquareFragment {
            val fragment = SquareFragment()
            val b = Bundle()
            b.putString("id", id ?: "")
            fragment.arguments = b
            return fragment
        }
    }

    private var pageNum = 1
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val classId by lazy {
        if (arguments == null) {
            ""
        } else
            arguments.getString("id")
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private var mBanners = ArrayList<Banner>()

    private val bannerAdapter by lazy {
        BannerAdapter(mBanners)
    }

    private val mSquares = ArrayList<Square>()
    private val squareAdapter by lazy {
        SquareAdapter(mSquares)
    }
    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_square_list,mSwipeRefreshLayout.mRecyclerView,false)
    }

    private var type = 2
    override fun setAdapter() = squareAdapter

    override fun onFirstVisibleToUser() {

//        headerView.mBanner.setHasFixedSize(true)
//        headerView.mBanner.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        headerView.mBanner.adapter = bannerAdapter
//        val helper = PagerSnapHelper()
//        helper.attachToRecyclerView(headerView.mBanner)
//        headerView.mBanner.isNestedScrollingEnabled = false


//        bannerAdapter.setOnItemClickListener { view, position ->
//            val banner = mBanners[position]
//            val ids = banner.newsid ?: ""
//            startActivity<SquareTrendDetailActivity>("id" to ids,"position" to position)
//        }

//        mSwipeRefreshLayout.addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
//                .colorResId(R.color.color_ECECEC)
//                .size(dip(1))
//                .visibilityProvider { position, parent ->
//                    position==0
//                }
//                .build())

        squareAdapter.setHeaderView(headerView)

        squareAdapter.setOnItemClickListener { _, position ->
            val square = mSquares[position]
            square.id?.let {
                startActivityForResult<SquareTrendDetailActivity>(1,"id" to it,"position" to position)
            }
        }

        showDialog()
        getData()
    }
    //筛选
    fun filter(type: Int) {
        this.type = type
        showDialog(canCancel = false)
        pullDownRefresh()
    }

    fun refresh() {
        if (type != 2) {
            if (activity is MainActivity) {
                (activity as MainActivity).setTrendTitle(2)
            }
            this.type = 2

        }
        pullDownRefresh()
    }

    private fun getBanner() {
        Request.getBanners().request(this, success = { _, data ->
            if (data?.list?.results != null) {
                mBanners.clear()
                mBanners.addAll(data.list.results)
                headerView.mBanner.setPages(
                        object : CBViewHolderCreator {
                            override fun createHolder(itemView: View): NetWorkImageHolder {
                                return NetWorkImageHolder(itemView)
                            }

                            override fun getLayoutId(): Int {
                                return R.layout.item_banner
                            }
                        },mBanners).setPageIndicator(intArrayOf(R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused))
                        .setOnItemClickListener {
                            val banner = mBanners[it]
                            val ids = banner.newsid ?: ""
                            startActivity<SquareTrendDetailActivity>("id" to ids, "position" to it)
                        }
//                bannerAdapter.notifyDataSetChanged()
                showDialog()
                getSquareList()
            }
        }) { _, _ ->
            showDialog()
            getSquareList()
        }
    }

    private fun getData() {
        if (pageNum == 1) {
            getBanner()
        } else {
            getSquareList()
        }

    }

    override fun onResume() {
        super.onResume()
        headerView.mBanner.startTurning()
    }

    override fun onPause() {
        super.onPause()
        headerView.mBanner.stopTurning()
    }

    private fun getSquareList() {
        Request.getSquareList(userId, classId, pageNum, 2,sex = type).request(this) { _, data ->
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
            if (pageNum == 1) {
                mSwipeRefreshLayout.mRecyclerView.scrollToPosition(0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {//广场详情回来。
            var bundle = data!!.extras
            var mSquare = (bundle.getSerializable("bean") as Square)
            var positon = bundle.getInt("position")
            mSquares.get(positon).commentCount = mSquare.commentCount
            mSquares.get(positon).isupvote = mSquare.isupvote
            mSquares.get(positon).appraiseCount = mSquare.appraiseCount
            mSquares.get(positon).comments = mSquare.comments
            mSquares.get(positon).iFlowerCount = mSquare.iFlowerCount
            squareAdapter.notifyDataSetChanged()
        }
    }

    public override fun pullDownRefresh() {
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