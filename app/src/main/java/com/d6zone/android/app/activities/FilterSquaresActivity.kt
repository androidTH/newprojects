package com.d6zone.android.app.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.SquareAdapter
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.eventbus.FlowerMsgEvent
import com.d6zone.android.app.extentions.request
import com.d6zone.android.app.models.Square
import com.d6zone.android.app.models.TopicBean
import com.d6zone.android.app.net.Request
import com.d6zone.android.app.recoder.AudioPlayListener
import com.d6zone.android.app.utils.*
import com.d6zone.android.app.widget.SwipeRefreshRecyclerLayout
import io.rong.eventbus.EventBus
import kotlinx.android.synthetic.main.activity_filtersquares.*
import kotlinx.android.synthetic.main.layout_filtersqure_header.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.lang.Exception

/**
 * 过滤动态
 */
class FilterSquaresActivity : BaseActivity() {

    private var pageNum = 1

    private val mTopicType by lazy {
        intent.getParcelableExtra("squaretype") as TopicBean
    }

    private val mSquares = ArrayList<Square>()
    private var sex = 2
    private var sTopicId = ""
    private var sCity = ""
    private var mTypeName:String = ""

    //播放录音
    private var playIndex = -1
    private var playSquare:Square? = null

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.layout_filtersqure_header, swipeRefreshLayout_square.mRecyclerView, false)
    }

    private val mAudioMedio by lazy{
        AudioPlayUtils(this)
    }

    private val squareAdapter by lazy {
        SquareAdapter(mSquares)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtersquares)
        immersionBar.init()
        EventBus.getDefault().register(this@FilterSquaresActivity)
        registerReceiver(sIfLovePics, IntentFilter(Const.SQUARE_MESSAGE))

        squareAdapter.setOnItemClickListener { _, position ->
            val square = mSquares[position]
            square.id?.let {
                startActivityForResult<SquareTrendDetailActivity>(2,"id" to "${it}","position" to position)
            }
        }

        squareAdapter.setOnSquareDetailsClick { position, square ->
            square.id?.let {
                startActivityForResult<SquareTrendDetailActivity>(2,"id" to "${it}","position" to position)
            }
        }

        add_square.setOnClickListener {
//            var view = it
//            mCirclePop?.let {
//                it.showAtAnchorView(view, YGravity.BELOW, XGravity.ALIGN_RIGHT, -30,-10)
//            }

            isCheckOnLineAuthUser(this, getLocalUserId()) {
                startActivityForResult<ReleaseNewTrendsActivity>(1,"topicname" to "${mTopicType.sTopicName}","topicId" to "${sTopicId}")
            }
        }

        iv_back_close.setOnClickListener {
            hideSoftKeyboard(it)
            finish()
        }

        initRecyclerView()
        setHeaderView()
//        dialog()
        pullDownRefresh()

        setAudioListener()

//        initPopup()
    }

    private fun setHeaderView(){
        try {
            filter_squretitle.text = "${mTopicType.sTopicName}"
            if (mTopicType.mResId != -1) {
                var leftDrawable = ContextCompat.getDrawable(this, mTopicType.mResId)
                setLeftDrawable(leftDrawable, filter_squretitle)
                headerView.rl_topicinfo.visibility = View.GONE
                if(TextUtils.equals("-1",mTopicType.sId)){
                    sex = 2
                    sTopicId = "1"
                }else if(TextUtils.equals("-2",mTopicType.sId)){
                    sCity = mTopicType.city
                }else{
                    sex = mTopicType.sId.toInt()
                }
            }else {
                if(mTopicType.sTopicDesc.isNotEmpty()){
                    headerView.rl_topicinfo.visibility = View.VISIBLE
                    headerView.tv_topic_desc.text = mTopicType.sTopicDesc
                    squareAdapter.setHeaderView(headerView)
                }else{
                    headerView.rl_topicinfo.visibility = View.GONE
                }

                var leftDrawable = ContextCompat.getDrawable(this, R.mipmap.tag_list_bigicon)
                setLeftDrawable(leftDrawable, filter_squretitle)
                sTopicId = mTopicType.sId
                mTypeName = mTopicType.sTopicName
            }
        } catch (e: Exception) {
            e.printStackTrace()
            sex = 2
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

        squareAdapter.setOnSquareAudioToggleClick { position, square ->
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

            var proxyUrl =  getProxyUrl(this,square.sVoiceUrl)
            mAudioMedio.singleAudioPlay(proxyUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        mAudioMedio.onDestoryAudio()
        if(playIndex!=-1){
            playSquare?.let {
                it.isPlaying = false
                mSquares[playIndex] = it
                squareAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                pullDownRefresh()
            }else if(requestCode==2){
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(flowerEvent: FlowerMsgEvent){
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

    override fun onBackPressed() {
        super.onBackPressed()
        mAudioMedio.onDestoryAudio()
    }

    private fun initRecyclerView() {
        swipeRefreshLayout_square.setLayoutManager(LinearLayoutManager(this))
        swipeRefreshLayout_square.setOnRefreshListener(object : SwipeRefreshRecyclerLayout.OnRefreshListener {
            override fun onRefresh() {
                pullDownRefresh()
            }

            override fun onLoadMore() {
                loadMore()
            }
        })
        swipeRefreshLayout_square.setMode(SwipeRefreshRecyclerLayout.Mode.Both)
        swipeRefreshLayout_square.isRefreshing = false
        swipeRefreshLayout_square.setAdapter(squareAdapter)
        swipeRefreshLayout_square.mRecyclerView.itemAnimator.changeDuration = 0
    }

    private fun getSquareList() {
        Request.getFindSquareList(getLocalUserId(), "", pageNum, 3, sex = sex,sTopicId = sTopicId,sCity = sCity).request(this, false, success = { _, data ->
            if (pageNum == 1) {
                mSquares.clear()
                playIndex = -1
                mAudioMedio.onClickStop()
                rl_friends_empty.visibility = View.GONE
                swipeRefreshLayout_square.visibility = View.VISIBLE
            }
            swipeRefreshLayout_square.isRefreshing = false
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    swipeRefreshLayout_square.setLoadMoreText("没有更多了")
                    pageNum--
                }
                if(data?.list?.totalPage==0){
                    rl_friends_empty.visibility = View.VISIBLE
                    if(data.iFollowCount>0){
                        tv_filtersquare_empty.text = "暂无动态"
                    }else{
                        tv_filtersquare_empty.text = "你还没有喜欢的TA，喜欢后TA的动态将会出现在这里"
                    }
                    swipeRefreshLayout_square.visibility = View.GONE
                }
            } else {
                mSquares.addAll(data.list.results)
                if(data?.list.totalPage==1){
                    swipeRefreshLayout_square.setLoadMoreText("没有更多了")
                }
            }
            squareAdapter.notifyDataSetChanged()
        }) { code, msg ->
            toast(msg)
            swipeRefreshLayout_square.setLoadMoreText("没有更多了")
        }
    }

    private val sIfLovePics by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runOnUiThread {
                    intent?.let {
                        var sq = it.getSerializableExtra("bean") as Square
                        if(mSquares!=null){
                            var index = mSquares.indexOf(sq)
                            if (mSquares.size > index&&index>=0) {
                                if (sq != null) {
                                    mSquares.get(index).sIfLovePics = sq.sIfLovePics
                                    mSquares.get(index).sIfSeePics = sq.sIfSeePics
                                    mSquares.get(index).iLovePoint = sq.iLovePoint
                                }
//                            squareAdapter.notifyItemChanged(index+1,"sq")
                                squareAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    fun pullDownRefresh() {
        pageNum = 1
        getSquareList()
    }

    fun loadMore() {
        pageNum++
        getSquareList()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(sIfLovePics)
        EventBus.getDefault().unregister(this@FilterSquaresActivity)
    }
}