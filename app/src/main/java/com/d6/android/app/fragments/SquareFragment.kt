package com.d6.android.app.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.activities.FilterSquaresActivity
import com.d6.android.app.activities.MainActivity
import com.d6.android.app.activities.SquareTrendDetailActivity
import com.d6.android.app.adapters.NetWorkImageHolder
import com.d6.android.app.adapters.SquareAdapter
import com.d6.android.app.adapters.SquareTypeAdapter
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.eventbus.FlowerMsgEvent
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Banner
import com.d6.android.app.models.Square
import com.d6.android.app.models.SquareTypeBean
import com.d6.android.app.net.Request
import com.d6.android.app.recoder.AudioPlayListener
import com.d6.android.app.utils.AudioPlayUtils
import com.d6.android.app.utils.getLocalUserId
import com.d6.android.app.utils.getProxyUrl
import com.d6.android.app.utils.isCheckOnLineAuthUser
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.google.android.flexbox.FlexboxLayoutManager
import com.pili.pldroid.player.PLOnInfoListener
import com.pili.pldroid.player.PLOnInfoListener.MEDIA_INFO_AUDIO_RENDERING_START
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


    private var mSquareTypes= ArrayList<SquareTypeBean>()

    private var mBanners = ArrayList<Banner>()

    private val mSquares = ArrayList<Square>()
    private val squareAdapter by lazy {
        SquareAdapter(mSquares)
    }

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
            if(position==0){
                 proxyUrl = getProxyUrl(activity,"http://sc1.111ttt.cn/2017/1/05/09/298092035545.mp3")
            }else if(position==1){
                 proxyUrl = getProxyUrl(activity,"http://music.163.com/song/media/outer/url?id=317151.mp3")
            }else{
                proxyUrl = getProxyUrl(activity,"http://music.163.com/song/media/outer/url?id=281951.mp3")
            }

            mAudioMedio.singleAudioPlay(proxyUrl)
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
        }

        mSquareTypes.add(SquareTypeBean(R.mipmap.like_list_icon,"喜欢"))
        mSquareTypes.add(SquareTypeBean(R.mipmap.square_list_icon,"男生动态"))
        mSquareTypes.add(SquareTypeBean(R.mipmap.girl_list_icon,"女生动态"))
        mSquareTypes.add(SquareTypeBean(R.mipmap.tag_list_icon,"夏日的小美好"))
        mSquareTypes.add(SquareTypeBean(R.mipmap.tag_list_icon,"夏日的小美好"))
        mSquareTypes.add(SquareTypeBean(R.mipmap.tag_list_icon,"夏日的小美好"))
        mSquareTypes.add(SquareTypeBean(R.mipmap.tag_list_icon,"夏日的小美好"))

        headerView.rv_choose_squaretype.setHasFixedSize(true)
        headerView.rv_choose_squaretype.layoutManager = FlexboxLayoutManager(context)
        headerView.rv_choose_squaretype.adapter = mSquareTypeAdapter

        mSquareTypeAdapter.setOnItemClickListener { view, position ->
            activity.isCheckOnLineAuthUser(this, getLocalUserId()) {
//                startActivityForResult<ReleaseNewTrendsActivity>(1)
                var mSqureType = mSquareTypes.get(position)
                startActivity<FilterSquaresActivity>("squaretype" to mSqureType)
            }
        }

        mIsDismissDialog = true
        getData()

        setAudioListener()
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
                headerView.mBanner.setPages(
                        object : CBViewHolderCreator {
                            override fun createHolder(itemView: View): NetWorkImageHolder {
                                return NetWorkImageHolder(itemView)
                            }

                            override fun getLayoutId(): Int {
                                return R.layout.item_banner
                            }
                        },mBanners).setPageIndicator(intArrayOf(R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused))
                        .setOnItemClickListener {
                            val banner = mBanners[it]
                            val ids = banner.newsid ?: ""
                            startActivity<SquareTrendDetailActivity>("id" to "${ids}", "position" to it)
                        }
                mIsDismissDialog = false
                getSquareList()
            }
        }) { _, _ ->
            getSquareList()
        }
    }

    private fun getData() {
        if (pageNum == 1) {
            getBanner()
        } else {
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
                    PLOnInfoListener.MEDIA_INFO_STATE_CHANGED_PAUSED ->{
//                        stopPlayAudioView()
                        playSquare?.let {
                            it.isPlaying = false
                            mSquares[playIndex] = it
                            squareAdapter.notifyDataSetChanged()
                        }
                    }

                    MEDIA_INFO_AUDIO_RENDERING_START->{
//                        playSquare?.let {
//                            it.isPlaying = true
//                            mSquares[playIndex] = it
//                            squareAdapter.notifyDataSetChanged()
//                        }
                    }

                    PLOnInfoListener.MEDIA_INFO_AUDIO_FRAME_RENDERING ->{

                    }
                    PLOnInfoListener.MEDIA_INFO_CACHED_COMPLETE ->{
                        playSquare?.let {
                            it.isPlaying = false
                            mSquares[playIndex] = it
                            squareAdapter.notifyDataSetChanged()
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onCompletion() {
//                stopPlayAudioView()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        headerView.mBanner.startTurning()
    }

    override fun onPause() {
        super.onPause()
        headerView.mBanner.stopTurning()
        mAudioMedio.onDestoryAudio()
    }

    private fun getSquareList() {
        Request.getSquareList(getLocalUserId(), classId, pageNum, 2,sex = type).request(this,false,success={ _, data ->
            if (pageNum == 1) {
                mSquares.clear()
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
//                mSquares.get(positon).comments = mSquare.comments
                mSquares.get(positon).iFlowerCount = mSquare.iFlowerCount
                mSquares.get(positon).iIsSendFlower = mSquare.iIsSendFlower
                squareAdapter.notifyDataSetChanged()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(flowerEvent: FlowerMsgEvent){
        if(flowerEvent.getmSquare()!=null){
            var index = mSquares.indexOf(flowerEvent.getmSquare())
            if(mSquares!=null&&mSquares.size>index){
                mSquares.get(index).iFlowerCount = flowerEvent.getmSquare().iFlowerCount
                mSquares.get(index).iIsSendFlower = 1
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
        getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}