package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.adapters.RedMoneyListAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_redmoneydesc.*

class RedMoneyDesActivity : BaseActivity() {

    private val mRedMoneyList = ArrayList<LoveHeartFans>()
    private var pageNum = 1

    private val listAdapter by lazy {
        RedMoneyListAdapter(mRedMoneyList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redmoneydesc)
        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .init()
        rv_redmoney_list.setHasFixedSize(true)
        rv_redmoney_list.layoutManager = LinearLayoutManager(this)
        rv_redmoney_list.adapter = listAdapter

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        tv_username.text = "新年快乐心想事成 [img src=redheart_small/]"
    }

    private fun getData() {
        Request.findLoveListing(getLoginToken(),2,1).request(this) { _, data ->
            data?.let {
                if (pageNum == 1) {
                    mRedMoneyList.clear()
                }
                if (it.list?.results == null || it.list?.results?.isEmpty() as Boolean) {
                    if (pageNum > 1) {

                    } else {

//                        headerView.rv_loveheart_top.visibility = View.GONE
                    }
                } else {
                    it.list?.results?.let {
                        mRedMoneyList.addAll(it)
                    }

//                    if(it.list?.totalPage==1){
//                        mSwipeRefreshLayout.setLoadMoreText("没有更多了")
//                    }else{
//                        mSwipeRefreshLayout.setLoadMoreText("上拉加载更多")
//                    }
                }
                listAdapter.notifyDataSetChanged()

//                if(it.iMyOrder>0){
//                    if(TextUtils.equals("0", getUserSex())){
//                        updateTopBangDan(it.iMyOrder)
//                    }
//                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }
}
