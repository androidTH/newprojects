package com.d6.android.app.fragments

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.d6.android.app.R
import com.d6.android.app.activities.MainActivity
import com.d6.android.app.adapters.SelfPullDateAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.dialogs.SelfDateDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.User.IS_FIRST_SHOW_SELFDATEDIALOG
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getLocalUserId
import com.d6.android.app.utils.getSelfDateDialog
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout

/**
 * 自主发布约会
 */
class SelfPullDateFragment : RecyclerFragment() {

    private var mIsUpDown:Boolean = false //true 向上 false 向下

    companion object {
        fun instance(type: String): SelfPullDateFragment {
            val fragment = SelfPullDateFragment()
            val b = Bundle()
            b.putString("type", type)
            fragment.arguments = b
            return fragment
        }
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var vipIds: String? = ""
    private var area: String? = ""
    private var areaType: Int? = 1
    private var dateType: String? =""

    private var pageNum = 1
    private val mFindDates = ArrayList<MyAppointment>()

    private val dateAdapter by lazy {
        SelfPullDateAdapter(mFindDates)
    }

    override fun getMode() = SwipeRefreshRecyclerLayout.Mode.Both

    override fun setAdapter() = dateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            dateType= it.getString("type")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSwipeRefreshLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.color_F5F5F5))
//        addItemDecoration()
        dateAdapter.setOnItemClickListener { _, position ->
            val data = mFindDates[position]
//            startActivity<SelfReleaseDetailActivity>("data" to data)
        }

        mSwipeRefreshLayout.mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView?.layoutManager is LinearLayoutManager) {
                    val l = (recyclerView.layoutManager as LinearLayoutManager)
                    val f = l.findFirstVisibleItemPosition()
                    if (f == 0) {
                        val view = l.findViewByPosition(0)
                        val h = view.top
                        if(h<0&&dy>=0){
                            //表示向上滑动
                            if(getSelfDateDialog()){
                                var mSelfDateDialog = SelfDateDialog()
                                mSelfDateDialog.show(childFragmentManager,"RgDateDailog")
                                SPUtils.instance().put(IS_FIRST_SHOW_SELFDATEDIALOG+getLocalUserId(),false).apply()
                            }
                            if(!mIsUpDown){
                                mIsUpDown =!mIsUpDown
                                mRenGongBackground?.showBackground(mIsUpDown)
                            }
                        }else if(h==0&&dy<0){
                            //表示向下滑动
                            mIsUpDown = !mIsUpDown
                            mRenGongBackground?.showBackground(mIsUpDown)
                        }
                    }
                }
            }
        })
        getData()
    }

    override fun onFirstVisibleToUser() {
    }

    fun refresh() {
        pageNum = 1
        getData()
    }

    fun refresh(city: String?, datetype: String) {
        this.area = city
        this.dateType = datetype
        pageNum = 1
        getData()
    }

    private fun getData() {
        Request.findAppointmentList(userId,dateType,area,pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mFindDates.clear()
//                mSwipeRefreshLayout.mRecyclerView.scrollToPosition(0)
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("已经触及底线了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("已经触及底线了")
                }
            } else {
                mFindDates.addAll(data.list.results)
            }
            dateAdapter.notifyDataSetChanged()
            if(mRenGongBackground!=null){
                var count = data?.iAllAppointCount
                if (count != null) {
                    mRenGongBackground?.showAllDateNums("${dateType}",count.toInt())
                }
            }
        }
    }

    var mRenGongBackground:RenGongBackground? = null

    interface RenGongBackground{
        fun showBackground(mUpDown:Boolean)
        fun showAllDateNums(type:String,count:Int)
    }

    fun setRenGongBackGround(renGongBackground: RenGongBackground){
        mRenGongBackground = renGongBackground
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