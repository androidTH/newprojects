package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.RedMoneyListAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.EnvelopeBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_redmoneydesc.*
import org.jetbrains.anko.toast
import org.jetbrains.anko.startActivity


class RedMoneyDesActivity : BaseActivity() {

    private val mRedMoneyList = ArrayList<EnvelopeBean>()
    private var pageNum = 1

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private lateinit var sEnvelopeId:String

    private lateinit var sendUserId:String

    private lateinit var sEnvelopeDesc:String

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

        listAdapter.setOnItemClickListener { view, position ->
            if(mRedMoneyList.size>0){
                var userId =  mRedMoneyList[position].iUserId
                startActivity<UserInfoActivity>("id" to "${userId}")
            }
        }

        sEnvelopeId = intent.getStringExtra("sEnvelopeId")
        sendUserId = intent.getStringExtra("iUserId")
        sEnvelopeDesc = intent.getStringExtra("sEnvelopeDesc")

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }


        getUserInfo(sendUserId)

        findEnvelopeById()

        getData()
    }

    private fun getUserInfo(id: String) {
        Request.getUserInfo(userId, id).request(this,false,success= { msg, data ->
            data?.let {
                headerview.setImageURI(it.picUrl)
                tv_username.text = "${it.name}的小心心红包[img src=redheart_small/]"
                if(TextUtils.isEmpty(sEnvelopeDesc.trim())){
                    tv_redmoney_desc.visibility = View.GONE
                }
                tv_redmoney_desc.text = "“$sEnvelopeDesc”"
            }
        })
    }

    private fun findEnvelopeById(){
        //返回数据：
        //iIsGet  0 ：未领取  1：已领取
        //iGetLovePoint 领取的爱心数量  iLoveCount   iRemainCount这两个字段
        Request.findEnvelopeById(sEnvelopeId).request(this,false,success = {msg,data->
            data?.let {
                var isGet =it.iIsGet!!.toInt() //it.optInt("iIsGet ",0)
                var iLoveCount =it.iLoveCount!!.toInt()// it.optInt("iLoveCount",0)
                var iRemainCount =it.iRemainCount!!.toInt()// it.optInt("iRemainCount",0)
                if(iRemainCount==iLoveCount){
                    if(isGet==0){
                        tv_redmoneynums.text = "领取0/${iLoveCount}个"
                    }else{
                        tv_redmoneynums.text = "领取${iRemainCount}/${iLoveCount}个"
                    }
                }else{
                    tv_redmoneynums.text = "领取${iLoveCount-iRemainCount}/${iLoveCount}个"
                }
            }
        }){code,msg->
            toast(msg)
        }
    }

    private fun getData() {
        Request.findEnvelopeList(sEnvelopeId,1).request(this) { _, data ->
            data?.let {
                if (pageNum == 1) {
                    mRedMoneyList.clear()
                }
                if (it.list?.results == null || it.list?.results?.isEmpty() as Boolean) {
//                    if (pageNum > 1) {
//
//                    } else {
//
////                        headerView.rv_loveheart_top.visibility = View.GONE
//                    }
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
