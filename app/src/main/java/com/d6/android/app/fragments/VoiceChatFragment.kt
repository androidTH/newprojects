package com.d6.android.app.fragments

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import com.d6.android.app.R
import com.d6.android.app.activities.MainActivity
import com.d6.android.app.adapters.SelfPullDateAdapter
import com.d6.android.app.adapters.VoiceChatListAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.dialogs.SelfDateDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.IS_FIRST_SHOW_SELFDATEDIALOG
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout

/**
 * 自主发布约会
 */
class VoiceChatFragment : RecyclerFragment() {
    private var mIsUpDown:Boolean = false //true 向上 false 向下
    companion object {
        fun instance(type: String,selectedSex:Int):VoiceChatFragment {
            val fragment = VoiceChatFragment()
            val b = Bundle()
            b.putInt("sex",selectedSex)
            fragment.arguments = b
            return fragment
        }
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private var mSex:String = ""

    private var pageNum = 1
    private val mVoiceChatDates = ArrayList<MyAppointment>()

    private val mVoiceChatAdapter by lazy {
        VoiceChatListAdapter(mVoiceChatDates)
    }

    override fun getMode() = SwipeRefreshRecyclerLayout.Mode.Both

    override fun setAdapter() = mVoiceChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mSex = if(it.getInt("sex")!=-1){
                "${it.getInt("sex")}"
            }else{
                ""
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSwipeRefreshLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.color_F5F5F5))
        mVoiceChatAdapter.setOnItemClickListener { _, position ->
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
                            if(!mIsUpDown){
                                mIsUpDown =!mIsUpDown
                            }
                        }else if(h==0&&dy<0){
                            //表示向下滑动
                            mIsUpDown = !mIsUpDown
                        }
                    }
                }
            }
        })

        mSwipeRefreshLayout.postDelayed(object:Runnable{
            override fun run() {
                getData()
            }
        },200)
    }

    override fun onFirstVisibleToUser() {
    }

    fun refresh() {
        pageNum = 1
        getData()
    }

    fun refresh(sex:Int= -1) {
        this.mSex = if(sex!=-1){
            "${sex}"
        }else{
            ""
        }
        mSwipeRefreshLayout.isRefreshing = true
        mSwipeRefreshLayout.postDelayed(object:Runnable{
            override fun run() {
                pullDownRefresh()
            }
        },600)
    }

    private fun getData() {
        Request.findAppointmentList(getLocalUserId(),"","","${mSex}",pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mVoiceChatDates.clear()
                mSwipeRefreshLayout.mRecyclerView.scrollToPosition(0)
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("已经触及底线了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("已经触及底线了")
                }
            } else {
                mVoiceChatDates.addAll(data.list.results)
            }
            mVoiceChatAdapter.notifyDataSetChanged()
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