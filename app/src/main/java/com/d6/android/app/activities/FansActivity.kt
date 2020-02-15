package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.FansAdapter
import com.d6.android.app.adapters.RecentlyFansAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.dialogs.OpenDatePointNoEnoughDialog
import com.d6.android.app.dialogs.VistorPayPointDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.base_recyclerview_layout.*
import kotlinx.android.synthetic.main.header_receiverliked.view.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class FansActivity : RecyclerActivity() {

    private val mHeaderView by lazy{
        layoutInflater.inflate(R.layout.header_receiverliked,mSwipeRefreshLayout.mRecyclerView,false)
    }

    private var pageNum = 1
    private val mMessages = ArrayList<LoveHeartFans>()
    private val fansAdapter by lazy {
        FansAdapter(mMessages)
    }

    override fun adapter(): RecyclerView.Adapter<*> {
        return fansAdapter
    }

    private val mHeaderFans = ArrayList<LoveHeartFans>()
    private val mHeaderLikedAdapter by lazy {
        RecentlyFansAdapter(mHeaderFans)
    }

    override fun IsShowFooter(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleBold("收到的喜欢",true)
        fansAdapter.setOnItemClickListener { view, position ->
            var fans = mMessages[position]
            if(fans.iIsCode!=1){
                val id = mMessages[position].iSenduserid
                startActivity<UserInfoActivity>("id" to id.toString())
            }else{
                isAuthUser() {
                    DoSeeUserInfo(fans)
                }
            }
        }

        mHeaderLikedAdapter.setOnItemClickListener { view, position ->
            var fans = mHeaderFans[position]
            if(fans.iIsCode!=1){
                val id = mHeaderFans[position].iSenduserid
                startActivity<UserInfoActivity>("id" to id.toString())
            }else{
                isAuthUser(){
                    DoSeeUserInfo(fans)
                }
            }
        }

        fansAdapter.setHeaderView(mHeaderView)

        if(mHeaderFans!=null){
            mHeaderView.rv_receivedliked.setHasFixedSize(true)
            mHeaderView.rv_receivedliked.layoutManager = LinearLayoutManager(this)
            mHeaderView.rv_receivedliked.addItemDecoration(getItemDecoration())
            mHeaderView.rv_receivedliked.adapter = mHeaderLikedAdapter
        }

        addItemDecoration()
        dialog()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        Request.findReceiveLoveList(getLoginToken(),pageNum).request(this) { _, data ->
            data?.let {
                if (pageNum == 1) {
                    mMessages.clear()
                    mHeaderFans.clear()
                    mHeaderView.tv_receivedliked_nums.text ="${it.iAllReceiveLovePoint} [img src=redheart_small/]"
                }
                if (it.list?.results == null || it.list?.results?.isEmpty() as Boolean) {
                    if (pageNum > 1) {
                        mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                        pageNum--
                    } else {
                        mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                        mHeaderView.tv_receiveliked_title.visibility = View.GONE
                        mHeaderView.tv_liked_order.visibility = View.GONE
                        mHeaderView.rv_receivedliked.visibility = View.GONE
                        mHeaderView.tv_liked_order.visibility = View.GONE
                    }
                } else {
                    it.list?.results?.let {
                        mMessages.addAll(it)
                    }

                    it.unreadlist?.let { it1 ->
                        if(it1.size>0){
                            mHeaderFans.addAll(it1)
                            mHeaderLikedAdapter.notifyDataSetChanged()
                        }else{
                            mHeaderView.tv_receiveliked_title.visibility = View.GONE
                            mHeaderView.rv_receivedliked.visibility = View.GONE
                            mHeaderView.rv_receivedliked.visibility = View.GONE
                        }
                    }
                    if(it.unreadlist==null){
                        mHeaderView.tv_receiveliked_title.visibility = View.GONE
                        mHeaderView.rv_receivedliked.visibility = View.GONE
                        mHeaderView.rv_receivedliked.visibility = View.GONE
                    }

                    if(it.list?.totalPage==1){
                        mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    }else{
                        mSwipeRefreshLayout.setLoadMoreText("上拉加载更多")
                    }
                }
                fansAdapter.notifyDataSetChanged()
            }
        }

        tv_bottom_tips.text = "对方送的喜欢[img src=redheart_small/]超过100将升级为超级喜欢，可查看对方身份"
    }

    private fun getOldLiked(){
        Request.getFindMyFans(getLocalUserId(),pageNum).request(this){ _, data->

        }
    }

    private fun DoSeeUserInfo(loveHeartFans:LoveHeartFans){
        Request.getLovePointQueryAuth(getLoginToken(),"${loveHeartFans.iSenduserid}").request(this,false,success={_,data->
            startActivity<UserInfoActivity>("id" to "${loveHeartFans.iSenduserid}")
        }){code,msg->
            if(code==2){
                if(msg.isNotEmpty()){
                    var jsonObject = JSONObject(msg)
                    var iAddPoint = jsonObject.optInt("iAddPoint")
                    var iRemainPoint = jsonObject.optInt("iRemainPoint")
                    var sAddPointDesc = jsonObject.optString("sAddPointDesc")
                    var vistorUserDialog = VistorPayPointDialog()
                    vistorUserDialog.arguments = bundleOf("point" to "${iAddPoint}", "pointdesc" to sAddPointDesc, "type" to 3)
                    vistorUserDialog.setDialogListener { p, s ->
                        Request.loveUserQueryPayPoint(getLoginToken(),"${loveHeartFans.iSenduserid}").request(this,false,success={_,data->
                            startActivity<UserInfoActivity>("id" to "${loveHeartFans.iSenduserid}")
                        })
                    }
                    vistorUserDialog.show(supportFragmentManager, "unknow")
                }
            }else if(code==3){
                if(msg.isNotEmpty()){
                    var jsonObject = JSONObject(msg)
                    var msg = jsonObject.optString("sAddPointDesc")
                    var iAddPoint = jsonObject.getString("iAddPoint")
                    var iRemainPoint = jsonObject.getString("iRemainPoint")
                    var openErrorDialog = OpenDatePointNoEnoughDialog()
                    openErrorDialog.arguments = bundleOf("point" to "${iAddPoint}", "remainPoint" to iRemainPoint,"type" to 1)
                    openErrorDialog.show(supportFragmentManager, "d")
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