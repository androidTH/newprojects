package com.d6.android.app.fragments
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.BangdanListQuickAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.getLoginToken
import com.d6.android.app.utils.getUserSex
import kotlinx.android.synthetic.main.layout_bangdanlist.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * 榜单
 */
class BangDanListQuickFragment : BaseFragment() {

    private val mBangDanListBeans = ArrayList<LoveHeartFans>()

    private var pageNum = 1

    private val mBangdanListQuickAdapter by lazy {
        BangdanListQuickAdapter(mBangDanListBeans)
    }

    private val mHeaderBangDanOrder by lazy {
        layoutInflater.inflate(R.layout.header_bangdan_order,null,false)
    }

    private val footertips by lazy {
        layoutInflater.inflate(R.layout.layout_bangdan_bottom_tips,null,false)
    }

    override fun contentViewId(): Int {
        return R.layout.layout_bangdanlist
    }

    private var titleName:String?= ""
    private var type:Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titleName = it.getString(ARG_PARAM1)
            type = it.getInt(ARG_PARAM2)
        }
    }

    //你开启了在榜单中隐藏身份
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeLayout_bangdanlist.isRefreshing = false
        rv_bangdanlist.setHasFixedSize(true)
        rv_bangdanlist.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        mBangdanListQuickAdapter.addHeaderView(mHeaderBangDanOrder)
        if(TextUtils.equals("0", getUserSex())){
            mBangdanListQuickAdapter.addFooterView(footertips)
        }
        rv_bangdanlist.adapter = mBangdanListQuickAdapter
        mBangdanListQuickAdapter.setOnItemClickListener { adapter, view, position ->
            var loveHeartFans = mBangDanListBeans[position]
            if(loveHeartFans.iListSetting!=2){
                val id = loveHeartFans.iUserid
                startActivity<UserInfoActivity>("id" to "${id}")
            }
        }

        swipeLayout_bangdanlist.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                 pullDownRefresh()
        })

        mBangdanListQuickAdapter.setOnLoadMoreListener(BaseQuickAdapter.RequestLoadMoreListener {
            loadMore()
        },rv_bangdanlist)
        getData()
    }

    override fun onFirstVisibleToUser() {
    }

    private fun getData() {
        Request.findLoveListing(getLoginToken(),type,pageNum).request(this) { _, data ->
            data?.let {
                if (pageNum == 1) {
                    mBangdanListQuickAdapter.data.clear()
                    swipeLayout_bangdanlist.isRefreshing = false
                    mBangdanListQuickAdapter.loadMoreEnd(true)
                    mBangdanListQuickAdapter.setEnableLoadMore(true)
                }
                if (it.list?.results == null || it.list?.results?.isEmpty() as Boolean) {
                    mBangdanListQuickAdapter.loadMoreEnd(false)
                } else {
                    it.list?.results?.let {
                        mBangdanListQuickAdapter.addData(it)
                        mBangdanListQuickAdapter.loadMoreComplete()
                    }
                }
                mBangdanListQuickAdapter.notifyDataSetChanged()

                if(it.iMyOrder>0){
//                    if(TextUtils.equals("0", getUserSex())){
//                        updateTopBangDan(it.iMyOrder)
//                    }
                }
            }
        }
    }

    private fun pullDownRefresh() {
        pageNum=1
        mBangdanListQuickAdapter.setEnableLoadMore(false)
        getData()
    }

    private fun loadMore() {
        if(mBangDanListBeans.size<100){
            pageNum++
            getData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                BangDanListQuickFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1,param1)
                        putInt(ARG_PARAM2, param2)
                    }
                }
    }
}

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"