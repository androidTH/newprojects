package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.d6.android.app.adapters.MyDateListAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.base.SmartRecyclerActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import org.jetbrains.anko.startActivityForResult

/**
 * 我的约会列表
 */
class MyDateListActivity : SmartRecyclerActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mMyAppointments = ArrayList<MyAppointment>()

    private val myDateAdapter by lazy {
        MyDateListAdapter(mMyAppointments)
    }

    override fun adapter(): RecyclerView.Adapter<*> {
        return myDateAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleBold("我的约会")
        myDateAdapter.setOnItemClickListener { view, position ->
//            val id = mMyAppointments[position].iAppointUserid
//            startActivity<UserInfoActivity>("id" to id.toString())
            if(mMyAppointments.size>position){
                var myAppointment = mMyAppointments[position]
//                startActivity<MyDateDetailActivity>("data" to myAppointment,"from" to Const.FROM_MY_DATELIST)
                startActivityForResult<MyDateDetailActivity>(2000,"index" to position,"data" to myAppointment,"from" to Const.FROM_MY_DATELIST)
            }
        }
        addItemDecoration()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        Request.findMyAppointmentList(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mMyAppointments.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
//                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    mSmartRefreshLayout.finishLoadMore()
                    mSmartRefreshLayout.finishLoadMoreWithNoMoreData()
                    pageNum--
                } else {
                    mSmartRefreshLayout.finishLoadMore()
                }
            } else {
                mMyAppointments.addAll(data.list.results)
                myDateAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 2000){
                data?.let {
                    var position = data.getIntExtra("index",0)
                    mMyAppointments.removeAt(position)
                    myDateAdapter.notifyItemRemoved(position)
                }
            }
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
