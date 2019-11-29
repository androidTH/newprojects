package com.d6.android.app.fragments
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.BangdanListAdapter
import com.d6.android.app.adapters.RecentlyFansAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.getLevelDrawable
import com.d6.android.app.utils.getLocalUserId
import com.d6.android.app.utils.getLoginToken
import com.d6.android.app.utils.getUserSex
import kotlinx.android.synthetic.main.item_loveheart.view.*
import kotlinx.android.synthetic.main.item_loveheart.view.tv_name
import kotlinx.android.synthetic.main.item_loveheart.view.tv_sex
import kotlinx.android.synthetic.main.item_loveheart.view.tv_userinfo
import kotlinx.android.synthetic.main.item_loveheart.view.user_headView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor

/**
 * 榜单
 */
class ManyLoveHeartListQuickFragment : RecyclerFragment() {

    override fun setAdapter(): RecyclerView.Adapter<*> {
        return listAdapter
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
       return LinearLayoutManager(context)
    }

    private val mMessages = ArrayList<LoveHeartFans>()

    private var pageNum = 1

    private val listAdapter by lazy {
        BangdanListAdapter(mMessages)
    }

    private val mHeaderFans = ArrayList<LoveHeartFans>()
    private val mHeaderLikedAdapter by lazy {
        RecentlyFansAdapter(mHeaderFans)
    }

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.item_loveheart,null,false)
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
        addItemDecoration(1,R.color.dividing_line_color,0)
        listAdapter.setHeaderView(headerView)
        listAdapter.setOnItemClickListener { view, position ->
            var loveHeartFans = mMessages[position]
            if(loveHeartFans.iListSetting!=2){
                val id = loveHeartFans.iUserid
                startActivity<UserInfoActivity>("id" to "${id}")
            }
        }
        headerView.tv_loveheart_title.text = "按照用户送出-收到的 [img src=redheart_small/] 数排名"
    }

    override fun onFirstVisibleToUser() {
//        if (TextUtils.equals("0", getUserSex())) {
//            headerView.rl_list_top.visibility = View.GONE
//        } else {
//            headerView.rl_list_top.visibility = View.VISIBLE
//        }
        getData()
    }

    private fun getData() {
        Request.findLoveListing(getLoginToken(),type,pageNum).request(this) { _, data ->
            data?.let {
                if (pageNum == 1) {
                    mMessages.clear()
//                    mHeaderFans.clear()
                }
                if (it.list?.results == null || it.list?.results?.isEmpty() as Boolean) {
                    if (pageNum > 1) {
                        mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                        pageNum--
                    } else {
                        mSwipeRefreshLayout.setLoadMoreText("暂无数据")
//                        headerView.rv_loveheart_top.visibility = View.GONE
                    }
                } else {
                    it.list?.results?.let {
                        mMessages.addAll(it)
                    }

                    if(it.list?.totalPage==1){
                        mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    }else{
                        mSwipeRefreshLayout.setLoadMoreText("上拉加载更多")
                    }
                }
                listAdapter.notifyDataSetChanged()

                if(it.iMyOrder>0){
                    if(TextUtils.equals("1", getUserSex())){
                        updateTopBangDan(it.iMyOrder)
                    }
                }
            }
        }
    }

    private fun updateTopBangDan(position:Int){
        Request.getUserInfo(getLocalUserId(), getLocalUserId()).request(this, success = { _, data ->
            data?.let {
                       headerView.rl_list_top.visibility = View.VISIBLE
                       headerView.tv_name.text = it.name
                       headerView.user_headView.setImageURI(it.picUrl)
                       if(it.iListSetting==2){
//                    headerView.tv_name.text = "*****"
//                    headerView.user_headView.showBlur(it.picUrl)
                           headerView.tv_userinfo.visibility = View.VISIBLE
                           headerView.tv_userinfo.text = "你开启了在榜单中隐藏身份"
                       }else{
                           headerView.tv_userinfo.visibility = View.GONE
//                    if(!it.signature.isNullOrEmpty()){
//                        headerView.tv_userinfo.visibility = View.VISIBLE
//                        headerView.tv_userinfo.text = it.signature
//                    }else if(!it.intro.isNullOrEmpty()){
//                        headerView.tv_userinfo.text = it.intro
//                        headerView.tv_userinfo.visibility = View.VISIBLE
//                    }else{
//                        headerView.tv_userinfo.visibility = View.GONE
//                    }
                       }


                       headerView.tv_sex.isSelected = TextUtils.equals("0", it.sex)
                       headerView.tv_sex.text = it.age
                       if (TextUtils.equals("1", getUserSex())&& TextUtils.equals(it.sex, "0")) {//0 女 1 男
//            tv_vip.text = String.format("%s", data.userclassesname)
                           headerView.tv_vip.visibility =View.GONE
                       } else {
//            tv_vip.text = String.format("%s", data.userclassesname)
                           headerView.tv_vip.visibility = View.VISIBLE
                           headerView.tv_vip.backgroundDrawable = getLevelDrawable("${it.userclassesid}",context)
                       }

                       headerView.tv_receivedliked.text = "${it.iSendLovePoint}"
                       if(position==1){
                           headerView.tv_order.textColor = ContextCompat.getColor(context,R.color.color_FF4500)
                       }else if(position==2){
                           headerView.tv_order.textColor = ContextCompat.getColor(context,R.color.color_BE34FF)
                       }else if(position==3){
                           headerView.tv_order.textColor = ContextCompat.getColor(context,R.color.color_34B1FF)
                       }else{
                           headerView.tv_order.textColor = ContextCompat.getColor(context,R.color.color_888888)
                       }

                       if(position<9){
                           headerView.tv_order.text = "0${position}"
                       }else{
                           if(position>100||it.iSendLovePoint==0){
                               headerView.tv_order.text = "--"
                           }else{
                               headerView.tv_order.text = "${position}"
                           }
                       }

                       headerView.ll_loveheart.setOnClickListener {
                           if(data.iListSetting!=2){
                               startActivity<UserInfoActivity>("id" to "${data.accountId}")
                           }
                       }
            }
        }) { code, msg ->
            if(code==2){
                toast(msg)
            }
        }
    }
    override fun pullDownRefresh() {
        super.pullDownRefresh()
        pageNum=1
        getData()
    }

    override fun loadMore() {
        super.loadMore()
        if(mMessages.size<100){
            pageNum++
            getData()
        }else{
            mSwipeRefreshLayout.setLoadMoreText("没有更多了")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                ManyLoveHeartListQuickFragment().apply {
                    arguments = Bundle().apply {
//                        putParcelable(ARG_PARAM1,param1)
                        putString(ARG_PARAM1,param1)
                        putInt(ARG_PARAM2, param2)
                    }
                }
    }
}

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"