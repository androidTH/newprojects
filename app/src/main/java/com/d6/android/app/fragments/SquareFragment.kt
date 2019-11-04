package com.d6.android.app.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.NetWorkImageHolder
import com.d6.android.app.adapters.SquareAdapter
import com.d6.android.app.adapters.SquareBannerQuickAdapter
import com.d6.android.app.adapters.SquareTypeAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.eventbus.FlowerMsgEvent
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Banner
import com.d6.android.app.models.Square
import com.d6.android.app.models.TopicBean
import com.d6.android.app.net.Request
import com.d6.android.app.recoder.AudioPlayListener
import com.d6.android.app.utils.*
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
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
            square.id?.let {
                startActivityForResult<SquareTrendDetailActivity>(1,"id" to "${it}","position" to position)
            }
        }

        squareAdapter.setOnSquareDetailsClick { position, square ->
            square.id?.let {
                startActivityForResult<SquareTrendDetailActivity>(1,"id" to "${it}","position" to position)
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

        mSquareTypes.add(TopicBean("-1",R.mipmap.like_list_bigicon,"喜欢"))
        mSquareTypes.add(TopicBean("1",R.mipmap.square_list_icon,"男生动态"))
        mSquareTypes.add(TopicBean("0",R.mipmap.girl_list_bigicon,"女生动态"))

        headerView.rv_choose_squaretype.setHasFixedSize(true)
        headerView.rv_choose_squaretype.layoutManager = FlexboxLayoutManager(context) as RecyclerView.LayoutManager?

        mSquareTypeAdapter.setOnItemClickListener { view, position ->
//            activity.isCheckOnLineAuthUser(this, getLocalUserId()) {
                var mSqureType = mSquareTypes.get(position)
                startActivity<FilterSquaresActivity>("squaretype" to mSqureType)
//            }
        }

        headerView.rl_date_list.setOnClickListener {
            startActivity<AppointmentActivity>()
        }

        headerView.rl_bangdan.setOnClickListener {
            startActivity<D6LoveHeartListActivity>()
        }

        headerView.sv_date01.setImageURI(getLocalUserHeadPic())
        headerView.sv_date02.setImageURI(getLocalUserHeadPic())
        headerView.sv_date03.setImageURI(getLocalUserHeadPic())

        mIsDismissDialog = true
        getData()
        getTopicBanner()

        setAudioListener()
    }

    private fun getTopicBanner(){
        Request.findTopicBannerList(getLoginToken()).request(this,false,success={_,data->
            if(data!=null){
                data.list?.let {
                    mSquareTypes.addAll(it)
                    headerView.rv_choose_squaretype.adapter = mSquareTypeAdapter
                }
            }
        })
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

    private fun getData() {
        if (pageNum == 1) {
//            getBanner()
            mIsDismissDialog = false
            getSquareList()
        }
    }

    /**
     * 设置音频播放监听
     */
    private fun setAudioListener(){
        mAudioMedio.setmAudioListener(object: AudioPlayListener {
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
//                mSquares.get(positon).comments = mSquare.comments
//                mSquares.get(positon).iFlowerCount = mSquare.iFlowerCount
//                mSquares.get(positon).iIsSendFlower = mSquare.iIsSendFlower
                squareAdapter.notifyDataSetChanged()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(flowerEvent: FlowerMsgEvent){
        if(flowerEvent.getmSquare()!=null){
            var index = mSquares.indexOf(flowerEvent.getmSquare())
            if(mSquares!=null&&mSquares.size>index){
//                mSquares.get(index).iFlowerCount = flowerEvent.getmSquare().iFlowerCount
//                mSquares.get(index).iIsSendFlower = 1

                mSquares.get(index).iLovePoint = flowerEvent.getmSquare().iLovePoint
                mSquares.get(index).iSendLovePoint = 1
                squareAdapter.notifyDataSetChanged()
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}