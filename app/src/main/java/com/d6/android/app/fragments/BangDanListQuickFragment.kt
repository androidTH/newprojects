package com.d6.android.app.fragments
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.BangdanListQuickAdapter
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.extentions.showBlur
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.getLevelDrawable
import com.d6.android.app.utils.getLoginToken
import com.d6.android.app.utils.getUserSex
import kotlinx.android.synthetic.main.header_bangdan_order.view.*
import kotlinx.android.synthetic.main.layout_bangdanlist.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.support.v4.startActivity

/**
 * 榜单
 */
class BangDanListQuickFragment : BaseFragment() ,View.OnClickListener{

    private val mBangDanListBeans = ArrayList<LoveHeartFans>()
    private var mBangDanHeartsListBeans = ArrayList<LoveHeartFans>()

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

    private val footbottom by lazy {
        layoutInflater.inflate(R.layout.layout_bangdan_bottom,null,false)
    }

    override fun contentViewId(): Int {
        return R.layout.layout_bangdanlist
    }

    private var titleName:String?= ""
    private var mHighType:Int = 1
    private var mHighChildType:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titleName = it.getString(ARG_PARAM1)
            mHighType = it.getInt(ARG_PARAM2)
            mHighChildType = it.getInt(ARG_PARAM3)
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
            mBangdanListQuickAdapter.addFooterView(footbottom,1)
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

//        mBangdanListQuickAdapter.setOnLoadMoreListener(BaseQuickAdapter.RequestLoadMoreListener {
//            loadMore()
//        },rv_bangdanlist)
        getData()
    }

    override fun onFirstVisibleToUser() {
    }

    private fun getData() {
        Request.getHighList(mHighType,mHighChildType).request(this) { _, data ->
            data?.let {
                if (pageNum == 1) {
                    mBangdanListQuickAdapter.data.clear()
                    swipeLayout_bangdanlist.isRefreshing = false
                    mBangdanListQuickAdapter.loadMoreEnd(true)
                    mBangdanListQuickAdapter.setEnableLoadMore(true)
                }
                if (it.highList == null || it.highList.isEmpty() as Boolean) {
                    mBangdanListQuickAdapter.loadMoreEnd(false)
                } else {
                    it.highList.let {
                        if(it.size>=4){
                            mBangDanHeartsListBeans.addAll(it.subList(0,3))
                            mBangdanListQuickAdapter.addData(it.subList(3,it.size))
                            mBangdanListQuickAdapter.loadMoreComplete()
                        }else if(it.size<=3&&it.size>0){
                            mBangDanHeartsListBeans.addAll(it)
                        }
                    }
                }
                mBangdanListQuickAdapter.notifyDataSetChanged()
                updateHeader()
            }
        }
    }

    private fun updateHeader(){
       if (mBangDanHeartsListBeans.size<=3&&mBangDanHeartsListBeans.size>0){
           var mLoveHeartFans = mBangDanHeartsListBeans.get(0)
           mHeaderBangDanOrder.ll_middle.setOnClickListener {
               startToActivity("${mLoveHeartFans.iUserid}")
           }
           if(mLoveHeartFans.iListSetting==2){
//            mHeaderBangDanOrder.bangdan_one.setImageURI("res:///"+R.mipmap.shenmiren_icon)
               mHeaderBangDanOrder.bangdan_one.showBlur(mLoveHeartFans.sPicUrl)
               mHeaderBangDanOrder.tv_bangdanone_nick.text = "匿名"
           }else{
               mHeaderBangDanOrder.tv_bangdanone_nick.text = mLoveHeartFans.sSendUserName
               mHeaderBangDanOrder.bangdan_one.setImageURI(mLoveHeartFans.sPicUrl)
           }
           mHeaderBangDanOrder.tv_bangdanone_nicksex.isSelected = TextUtils.equals("0", mLoveHeartFans.sSex)
           if (TextUtils.equals("1", getUserSex())&& TextUtils.equals(mLoveHeartFans.sSex, "0")) {//0 女 1 男
               mHeaderBangDanOrder.tv_bangdanone_vip.visibility = View.GONE
           } else {
               mHeaderBangDanOrder.tv_bangdanone_vip.visibility = View.VISIBLE
               mHeaderBangDanOrder.tv_bangdanone_vip.backgroundDrawable = getLevelDrawable("${mLoveHeartFans.userclassesid}",context)
           }
           if(TextUtils.equals("0",mLoveHeartFans.sSex)){
               mHeaderBangDanOrder.tv_receivedliked_one.text = "收到${mLoveHeartFans.iAllLovePoint} [img src=redheart_small/]"
           }else{
               mHeaderBangDanOrder.tv_receivedliked_one.text = "送出${mLoveHeartFans.iAllLovePoint} [img src=redheart_small/]"
           }

           if(mBangDanHeartsListBeans.size>=2){
               var mLoveHeartFansTwo = mBangDanHeartsListBeans.get(1)
               mHeaderBangDanOrder.ll_bangdan_two.setOnClickListener {
                   startToActivity("${mLoveHeartFansTwo.iUserid}")
               }
               if(mLoveHeartFansTwo.iListSetting==2){
//            mHeaderBangDanOrder.bangdan_two.setImageURI("res:///"+R.mipmap.shenmiren_icon)
                   mHeaderBangDanOrder.bangdan_two.showBlur(mLoveHeartFansTwo.sPicUrl)
                   mHeaderBangDanOrder.tv_bangdantwo_nick.text = "匿名"
               }else{
                   mHeaderBangDanOrder.tv_bangdantwo_nick.text = mLoveHeartFansTwo.sSendUserName
                   mHeaderBangDanOrder.bangdan_two.setImageURI(mLoveHeartFans.sPicUrl)
               }
               mHeaderBangDanOrder.tv_bangdantwo_nicksex.isSelected = TextUtils.equals("0", mLoveHeartFansTwo.sSex)
               if (TextUtils.equals("1", getUserSex())&& TextUtils.equals(mLoveHeartFansTwo.sSex, "0")) {//0 女 1 男
                   mHeaderBangDanOrder.tv_bangdantwo_vip.visibility = View.GONE
               } else {
                   mHeaderBangDanOrder.tv_bangdantwo_vip.visibility = View.VISIBLE
                   mHeaderBangDanOrder.tv_bangdantwo_vip.backgroundDrawable = getLevelDrawable("${mLoveHeartFansTwo.userclassesid}",context)
               }
               if(TextUtils.equals("0",mLoveHeartFans.sSex)){
                   mHeaderBangDanOrder.tv_receivedliked_two.text = "收到${mLoveHeartFansTwo.iAllLovePoint} [img src=redheart_small/]"
               }else{
                   mHeaderBangDanOrder.tv_receivedliked_two.text = "送出${mLoveHeartFansTwo.iAllLovePoint} [img src=redheart_small/]"
               }

           }

           if(mBangDanHeartsListBeans.size==3){
               var mLoveHeartFansThree = mBangDanHeartsListBeans.get(2)
               mHeaderBangDanOrder.ll_bangdan_three.setOnClickListener {
                   startToActivity("${mLoveHeartFansThree.iUserid}")
               }
               if(mLoveHeartFansThree.iListSetting==2){
//            mHeaderBangDanOrder.bangdan_three.setImageURI("res:///"+R.mipmap.shenmiren_icon)
                   mHeaderBangDanOrder.bangdan_three.showBlur(mLoveHeartFansThree.sPicUrl)
                   mHeaderBangDanOrder.tv_bangdanthree_nick.text = "匿名"
               }else{
                   mHeaderBangDanOrder.tv_bangdanthree_nick.text = mLoveHeartFansThree.sSendUserName
                   mHeaderBangDanOrder.bangdan_three.setImageURI(mLoveHeartFansThree.sPicUrl)
               }
               mHeaderBangDanOrder.tv_bangdanthree_nicksex.isSelected = TextUtils.equals("0", mLoveHeartFansThree.sSex)
               if (TextUtils.equals("1", getUserSex())&& TextUtils.equals(mLoveHeartFansThree.sSex, "0")) {//0 女 1 男
                   mHeaderBangDanOrder.tv_bangdanthree_vip.visibility = View.GONE
               } else {
                   mHeaderBangDanOrder.tv_bangdanthree_vip.visibility = View.VISIBLE
                   mHeaderBangDanOrder.tv_bangdanthree_vip.backgroundDrawable = getLevelDrawable("${mLoveHeartFansThree.userclassesid}",context)
               }

               if(TextUtils.equals("0",mLoveHeartFansThree.sSex)){
                   mHeaderBangDanOrder.tv_receivedliked_three.text = "收到${mLoveHeartFansThree.iAllLovePoint} [img src=redheart_small/]"
               }else{
                   mHeaderBangDanOrder.tv_receivedliked_three.text = "送出${mLoveHeartFansThree.iAllLovePoint} [img src=redheart_small/]"
               }
           }

       }
    }

    private fun startToActivity(id:String){
        startActivity<UserInfoActivity>("id" to "${id}")
    }

    override fun onClick(v: View?) {

    }

    private fun pullDownRefresh() {
        pageNum=1
        mBangdanListQuickAdapter.setEnableLoadMore(true)
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
        fun newInstance(param1: String, param2: Int,param3:Int) =
                BangDanListQuickFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1,param1)
                        putInt(ARG_PARAM2, param2)
                        putInt(ARG_PARAM3, param3)
                    }
                }
    }
}

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"