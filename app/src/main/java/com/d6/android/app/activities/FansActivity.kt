package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.d6.android.app.R
import com.d6.android.app.adapters.FansAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.dialogs.OpenDatePointNoEnoughDialog
import com.d6.android.app.dialogs.VistorPayPointDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getLoginToken
import kotlinx.android.synthetic.main.header_receiverliked.view.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class FansActivity : RecyclerActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

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
        FansAdapter(mHeaderFans)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleBold("收到的喜欢",true)
        fansAdapter.setOnItemClickListener { view, position ->
            var fans = mMessages[position]
            if(fans.iIsCode!=1){
                val id = mMessages[position].iUserid
                startActivity<UserInfoActivity>("id" to id.toString())
            }else{
                DoSeeUserInfo(fans)
            }
        }

        mHeaderLikedAdapter.setOnItemClickListener { view, position ->
            var fans = mHeaderFans[position]
            if(fans.iIsCode!=1){
                val id = mMessages[position].iUserid
                startActivity<UserInfoActivity>("id" to id.toString())
            }else{
                DoSeeUserInfo(fans)
            }
        }
        fansAdapter.setHeaderView(mHeaderView)

        if(mHeaderFans!=null){
            mHeaderView.rv_receivedliked.setHasFixedSize(true)
            mHeaderView.rv_receivedliked.layoutManager = LinearLayoutManager(this)
            mHeaderView.rv_receivedliked.adapter = mHeaderLikedAdapter
        }

        addItemDecoration()
        dialog()
        getData()
    }

    private fun getData() {
        //Request.getFindMyFans
        Request.findReceiveLoveList(getLoginToken(),pageNum).request(this) { _, data ->
            data?.let {
                if (pageNum == 1) {
                    mMessages.clear()
                    mHeaderFans.clear()
                    mHeaderView.tv_receivedliked_nums.text = "${it.iAllReceiveLovePoint}"
                }
                if (it.list?.results == null || it.list?.results?.isEmpty() as Boolean) {
                    if (pageNum > 1) {
                        mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                        pageNum--
                    } else {
                        mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                    }
                } else {
                    it.list?.results?.let {
                        mMessages.addAll(it)
                    }
                    it.unreadlist?.let { it1 -> mHeaderFans.addAll(it1) }
                    mHeaderLikedAdapter.notifyDataSetChanged()
                }
                fansAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun DoSeeUserInfo(loveHeartFans:LoveHeartFans){
        Request.getLovePointQueryAuth(getLoginToken(),"${loveHeartFans.iUserid}").request(this,false,success={_,data->
            startActivity<UserInfoActivity>("id" to "${loveHeartFans.iUserid}")

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
                        Request.loveUserQueryPayPoint(getLoginToken(),"${loveHeartFans.iUserid}").request(this,false,success={_,data->
                            startActivity<UserInfoActivity>("id" to "${loveHeartFans.iUserid}")
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
                    openErrorDialog.arguments = bundleOf("point" to "${iAddPoint}", "remainPoint" to iRemainPoint)
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