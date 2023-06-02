package com.d6zone.android.app.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.d6zone.android.app.R
import com.d6zone.android.app.activities.*
import com.d6zone.android.app.adapters.SquareAdapter
import com.d6zone.android.app.adapters.SquareBannerQuickAdapter
import com.d6zone.android.app.adapters.SquareTypeAdapter
import com.d6zone.android.app.base.RecyclerFragment
import com.d6zone.android.app.eventbus.FlowerMsgEvent
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.Banner
import com.d6zone.android.app.models.Square
import com.d6zone.android.app.models.TopicBean
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.recoder.AudioPlayListener
import com.d6zone.android.app.utils.*
import com.d6zone.android.app.utils.ThreadUtils.runOnUiThread
import com.google.android.flexbox.FlexboxLayoutManager
import io.rong.eventbus.EventBus
import kotlinx.android.synthetic.main.header_square_list.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast

/**
 * Created on 2017/12/17.
 * 动态
 */
class SquareFragment : RecyclerFragment() {
    companion object {
        fun instance(id: String?): SquareFragment {
            val fragment = SquareFragment()
            val b = Bundle()
            b.putString("id", id ?: "")
            fragment.arguments = b
            return fragment
        }
    }
    private var pageNum = 1

    private val classId by lazy {
        if (arguments == null) {
            ""
        } else
            arguments.getString("id")
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private var mBanners = ArrayList<Banner>()
    private val mSquareTopBannerAdapter by lazy{
        SquareBannerQuickAdapter(mBanners)
    }

    private val mSquares = ArrayList<Square>()
    private val squareAdapter by lazy {
        SquareAdapter(mSquares)
    }

    private var mSquareTypes= ArrayList<TopicBean>()
    private val mSquareTypeAdapter by lazy{
        SquareTypeAdapter(mSquareTypes)
    }

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_square_list,mSwipeRefreshLayout.mRecyclerView,false)
    }

    private val mAudioMedio by lazy{
        AudioPlayUtils(activity)
    }

    private var playIndex = -1
    private var playSquare:Square? = null

    private var type = 2
    override fun setAdapter() = squareAdapter

    override fun onFirstVisibleToUser() {
        EventBus.getDefault().register(this)
        mSwipeRefreshLayout.isRefreshing = true
        squareAdapter.setHeaderView(headerView)
        squareAdapter.setOnItemClickListener { _, position ->
            val square = mSquares[position]
            if(square.classesid!=66&&square.dataType!=2){
                square.id?.let {
                    startActivityForResult<SquareTrendDetailActivity>(1,"id" to "${it}","position" to position)
                }
            }else if(square.dataType==2){
                startActivity<D6LoveHeartListActivity>()
            }
        }

        squareAdapter.setOnSquareDetailsClick { position, square ->
            if(square.classesid!=66){
                square.id?.let {
                    startActivityForResult<SquareTrendDetailActivity>(1,"id" to "${it}","position" to position)
                }
            }
        }

        squareAdapter.setOnSquareAudioToggleClick { position, square ->
            var proxyUrl:String?=null
            playSquare = square
            playSquare?.let {
                if(!it.isPlaying){
                    it.isPlaying = true
                    mSquares[position]= it
                    if(playIndex>=0&&playIndex!=position){
                        mSquares[playIndex].isPlaying = false
                    }
                    squareAdapter.notifyDataSetChanged()
                    playIndex = position
                }
            }
            proxyUrl =  getProxyUrl(activity,square.sVoiceUrl)
            mAudioMedio.singleAudioPlay(proxyUrl)
        }

        headerView.rv_choose_squaretype.setHasFixedSize(true)
        headerView.rv_choose_squaretype.layoutManager = FlexboxLayoutManager(context) as RecyclerView.LayoutManager?

        mSquareTypeAdapter.setOnItemClickListener { view, position ->
//            activity.isCheckOnLineAuthUser(this, getLocalUserId()) {
                var mSqureType = mSquareTypes.get(position)
                startActivity<FilterSquaresActivity>("squaretype" to mSqureType)
//            }
        }

        headerView.rl_date_list.setOnClickListener {
            startActivity<AppointmentActivity>("from"  to "SquareFragment")
        }

        headerView.rl_bangdan.setOnClickListener {
            startActivity<D6LoveHeartListActivity>()
        }

        headerView.rl_audio.setOnClickListener {
            startActivity<AudioChatActivity>("from"  to "SquareFragment")
        }

        mIsDismissDialog = true
        getData()
        getTopicBanner()

        setAudioListener()

        activity.registerReceiver(sIfLovePics, IntentFilter(Const.SQUARE_MESSAGE))
    }

    private fun getTopicBanner(){
        Request.findTopicBannerList(getLoginToken()).request(this,false,success={_,data->
            if(data!=null){
                mSquareTypes.clear()
                data.list?.let {
                    mSquareTypes.add(TopicBean("-1",R.mipmap.like_list_bigicon,"喜欢"))
                    mSquareTypes.add(TopicBean("1",R.mipmap.square_list_icon,"男生动态"))
                    mSquareTypes.add(TopicBean("0",R.mipmap.girl_list_bigicon,"女生动态"))
                    mSquareTypes.addAll(it)
                    headerView.rv_choose_squaretype.adapter = mSquareTypeAdapter
                }
            }
        })
        getSquareTop()
    }

    //筛选
    fun filter(type: Int) {
        this.type = type
//        showDialog(canCancel = false)
        pullDownRefresh()
    }

    fun refresh() {
        if (type != 2) {
            if (activity is MainActivity) {
                (activity as MainActivity).setTrendTitle(2)
            }
            this.type = 2
        }
        initFirstPageData()
    }

    private fun getBanner() {
        Request.getBanners().request(this,false,success = { _, data ->
            if (data?.list?.results != null) {
                mBanners.clear()
                mBanners.addAll(elements = data.list?.results!!)
                headerView.mBanner.setAdapter(mSquareTopBannerAdapter)
                mSquareTopBannerAdapter.setOnItemClickListener { adapter, view, position ->
                    val banner = mBanners[position]
                    val ids = banner.newsid ?: ""
                    startActivity<SquareTrendDetailActivity>("id" to "${ids}", "position" to position)
                }
//                headerView.mBanner.setPages(
//                        object : CBViewHolderCreator {
//                            override fun createHolder(itemView: View): NetWorkImageHolder {
//                                return NetWorkImageHolder(itemView)
//                            }
//
//                            override fun getLayoutId(): Int {
//                                return R.layout.item_banner
//                            }
//                        },mBanners).setPageIndicator(intArrayOf(R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused))
//                        .setOnItemClickListener {
//                            val banner = mBanners[it]
//                            val ids = banner.newsid ?: ""
//                            startActivity<SquareTrendDetailActivity>("id" to "${ids}", "position" to it)
//                        }

                mIsDismissDialog = false
                getSquareList()
            }
        }) { _, _ ->
            getSquareList()
        }
    }

    private fun getSquareTop(){
        Request.findSquareTop().request(this,false,success={_,data->
              data?.let {
                  headerView.ll_topshow.visibility = View.GONE
                  var iAppointmentSignupCount = it.optInt("iAppointmentSignupCount")
                  headerView.tv_date_count.text = "${iAppointmentSignupCount}人约会成功"
                  headerView.rl_bangdan_headerview.visibility = View.VISIBLE
                  headerView.rl_date_headerview.visibility = View.GONE
//                  var coverurl = it.optString("coverurl")
                  var picUrl = it.optString("picUrl")
//                  if(coverurl.isNotEmpty()){
//                      var imglist = coverurl.split(",")
//                      headerView.sv_date01.setImageURI(imglist[0])
//                      headerView.sv_date02.setImageURI(imglist[1])
//                      headerView.sv_date03.setImageURI(imglist[2])
//                  }
                  if(picUrl.isNotEmpty()){
                      var imglist = picUrl.split(",")
                      headerView.sv_list01.setImageURI(imglist[0])
                      headerView.sv_list02.setImageURI(imglist[1])
                      headerView.sv_list03.setImageURI(imglist[2])
                  }
              }
        })
    }

    private fun getData() {
        if (pageNum == 1) {
//            getBanner()
            mIsDismissDialog = false
            getTopicBanner()
            getSquareList()
        }
    }

    /**
     * 设置音频播放监听
     */
    private fun setAudioListener(){
        mAudioMedio.setmAudioListener(object:AudioPlayListener {
            override fun onPrepared(var1: Int) {

            }

            override fun onBufferingUpdate(var1: Int) {

            }

            override fun onInfo(var1: Int, var2: Int) {
                when (var1) {
                    AudioPlayUtils.MEDIA_INFO_STATE_CHANGED_PAUSED ->{
                        playSquare?.let {
                            it.isPlaying = false
                            mSquares[playIndex] = it
                            squareAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onCompletion() {
                playSquare?.let {
                    it.isPlaying = false
                    mSquares[playIndex] = it
                    squareAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
//        headerView.mBanner.startTurning()
    }

    override fun onPause() {
        super.onPause()
//        headerView.mBanner.stopTurning()
        mAudioMedio.onDestoryAudio()
        if(playIndex!=-1){
            playSquare?.let {
                it.isPlaying = false
                mSquares[playIndex] = it
                squareAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getSquareList() {
        Request.getSquareList(getLocalUserId(), classId, pageNum, 3,sex = type).request(this,false,success={ _, data ->
            if (pageNum == 1) {
                mSquares.clear()
                playIndex = -1
                mAudioMedio.onClickStop()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                }
            } else {
                mSquares.addAll(data.list.results)
            }
            squareAdapter.notifyDataSetChanged()
        }){code,msg->
           toast(msg)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {//广场详情回来。
            var bundle = data!!.extras
            var mSquare = (bundle.getSerializable("bean") as Square)
            var positon = bundle.getInt("position")
            if(mSquares!=null&&mSquares.size>positon){
                mSquares.get(positon).commentCount = mSquare.commentCount
                mSquares.get(positon).isupvote = mSquare.isupvote
                mSquares.get(positon).appraiseCount = mSquare.appraiseCount
                mSquares.get(positon).iLovePoint = mSquare.iLovePoint
                squareAdapter.notifyDataSetChanged()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(flowerEvent:FlowerMsgEvent){
        if(flowerEvent.getmSquare()!=null){
            var index = mSquares.indexOf(flowerEvent.getmSquare())
            if(mSquares!=null&&mSquares.size>index&&index!=-1){
                mSquares.get(index).iSendLovePoint = 1
                if(flowerEvent.getmSquare()!=null){
                    mSquares.get(index).sIfLovePics = flowerEvent.getmSquare().sIfLovePics
                    mSquares.get(index).iLovePoint = flowerEvent.getmSquare().iLovePoint
                }
                squareAdapter.notifyDataSetChanged()
            }
        }
    }

    private val sIfLovePics by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                   intent?.let {
                       var sq = it.getSerializableExtra("bean") as Square
                       if (sq != null && mSquares != null && mSquares.size > 0) {
                           var index = mSquares.indexOf(sq)
                           if (index != -1) {
                               mSquares.get(index).sIfLovePics = sq.sIfLovePics
                               mSquares.get(index).sIfSeePics = sq.sIfSeePics
                               mSquares.get(index).iLovePoint = sq.iLovePoint
                               squareAdapter.notifyItemChanged(index + 1, "sq")
                           }
                       }
                   }
                }
            }
        }
    }

    private fun initFirstPageData(){
        pageNum = 1
        if (pageNum == 1) {
            mSwipeRefreshLayout.mRecyclerView.scrollToPosition(0)
        }
        mIsDismissDialog = false
        mSwipeRefreshLayout.isRefreshing = true
        mSwipeRefreshLayout.postDelayed(object:Runnable{
            override fun run() {
                getData()
            }
        },600)

    }

    public override fun pullDownRefresh() {
        super.pullDownRefresh()
        initFirstPageData()
    }

    override fun loadMore() {
        super.loadMore()
        pageNum++
        getSquareList()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        try{
            if(sIfLovePics!=null){
                activity.unregisterReceiver(sIfLovePics)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}