package com.d6zone.android.app.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.widget.RelativeLayout
import com.d6zone.android.app.R
import com.d6zone.android.app.a.VistorAdapter
import com.d6zone.android.app.base.RecyclerActivity
import com.d6zone.android.app.dialogs.OpenDatePointNoEnoughDialog
import com.d6zone.android.app.dialogs.VistorPayPointDialog
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.Fans
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.utils.Const
import com.d6zone.android.app.utils.SPUtils
import com.d6zone.android.app.utils.getLoginToken
import com.d6zone.android.app.utils.isAuthUser
import com.d6zone.android.app.widget.RxRecyclerViewDividerTool
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class VistorsActivity : RecyclerActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var pageNum = 1
    private val mVistors = ArrayList<Fans>()

    private val mHeaderView by lazy{
        layoutInflater.inflate(R.layout.recyclerview_top_line,mSwipeRefreshLayout.mRecyclerView,false)
    }

    override fun layoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(this,2)
    }

    private val vistorAdapter by lazy {
        VistorAdapter(mVistors)
    }

    override fun IsShowFooter(): Boolean {
        return false
    }

    override fun adapter(): RecyclerView.Adapter<*> {
       return vistorAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var count = intent.getStringExtra("count")
        if(TextUtils.equals("0",count)){
            setTitleBold("访客",true)
        }else{
            setTitleBold("访客·${count}",true)
        }

        vistorAdapter.setOnItemClickListener { view, position ->
            val vistor =  mVistors[position]
            if(vistor.iIsCode!=1){
                startActivity<UserInfoActivity>("id" to "${vistor.iVistorid}")
            }else{
                isAuthUser() {
                    DoSeeUserInfo(vistor,position)
                }
            }
        }

//        vistorAdapter.setHeaderView(mHeaderView)
        rootFl.backgroundColor = ContextCompat.getColor(this,R.color.color_F6F7FA)
        var params= mSwipeRefreshLayout.layoutParams as RelativeLayout.LayoutParams
        params.leftMargin = dip(5)
        params.rightMargin = dip(5)
//        mSwipeRefreshLayout.setPadding(0,dip(10),0,0)
        mSwipeRefreshLayout.layoutParams = params

//        var divider = GridItemDecoration.Builder(this)
//                .setHorizontalSpan(R.dimen.margin_10)
//                .setVerticalSpan(R.dimen.margin_10)
//                .setColorResource(R.color.color_F6F7FA)
//                .setShowLastLine(false)
//                .setShowVerticalLine(true)
//                .build()
        addItemDecoration(RxRecyclerViewDividerTool(dip(10)))
        dialog()

        getData()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun getData() {

        Request.getFindVistors(userId, pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mVistors.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                if (data.list?.totalPage == 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("上拉加载更多")
                }
                mVistors.addAll(data.list.results)
            }
            vistorAdapter.notifyDataSetChanged()
        }
    }

    private fun DoSeeUserInfo(vistor:Fans,positon:Int){
        Request.getAnyonousPointQueryAuth(getLoginToken(),"${vistor.iVistorid}").request(this,false,success={_,data->
            startActivity<UserInfoActivity>("id" to "${vistor.iVistorid}")
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
                        Request.getUserAnonymousPayPoint(getLoginToken(),"${vistor.iVistorid}").request(this,false,success={_,data->
                            vistor.iIsCode = 2
                            vistorAdapter.notifyItemChanged(positon,"vistor")
                            startActivity<UserInfoActivity>("id" to "${vistor.iVistorid}")
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
                    openErrorDialog.arguments = bundleOf("point" to "${iAddPoint}", "remainPoint" to iRemainPoint,"type" to 0)
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