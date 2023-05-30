package com.d6.android.app.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.SquareDetailCommentAdapter
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Comment
import com.d6.android.app.models.Square
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import kotlinx.android.synthetic.main.activity_square_detail.*
import kotlinx.android.synthetic.main.header_square_detail.view.*
import com.d6.android.app.eventbus.FlowerMsgEvent
import com.d6.android.app.recoder.AudioPlayListener
import com.d6.android.app.utils.AppUtils.Companion.context
import io.rong.eventbus.EventBus
import kotlinx.android.synthetic.main.item_audio.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import java.util.*


/**
 * 动态详情
 */
class SquareTrendDetailActivity : TitleActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener {

    private val id by lazy {
        intent.getStringExtra("id")
    }

    private val mPosition by lazy{
        if(intent.hasExtra("position")){
            intent.getIntExtra("position",-1)
        }else{
            -1
        }
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val mAudioMedio by lazy{
        AudioPlayUtils(this)
    }

    private var pageNum = 1
    private var iIsAnonymous:Int = 2
    private var chooseAnonymous = false

    private val mComments = ArrayList<Comment>()
    private val squareDetailCommentAdapter by lazy {
        SquareDetailCommentAdapter(mComments)
    }
    private var mSquare:Square?=null

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_square_detail, mSwipeRefreshLayout.mRecyclerView, false)
    }

    private var replayUid = ""
    //回复内容
    private var replayContent=""
    private var iReplyCommnetType:Int?= 2 //1、回复的评论类型是匿名 2、回复的评论类型是非匿名

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_square_detail)
        immersionBar.init()
        registerReceiver(sIfLovePics, IntentFilter(Const.SQUARE_MESSAGE))
//        var drawable = ContextCompat.getDrawable(this,R.mipmap.titlemore_small_icon)
//        setRightDrawable(drawable,titleBar.titleView)
        EventBus.getDefault().register(this@SquareTrendDetailActivity)

        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(this))
        mSwipeRefreshLayout.setMode(SwipeRefreshRecyclerLayout.Mode.Both)
        squareDetailCommentAdapter.setHeaderView(headerView)
        mSwipeRefreshLayout.setAdapter(squareDetailCommentAdapter)
        mSwipeRefreshLayout.setOnRefreshListener(this)

        headerView.mTrendDetailView.setPraiseClick {
            if (TextUtils.equals("1", it.isupvote)) {
                cancelPraise(it)
            } else {
                praise(it)
            }
        }

        headerView.mTrendDetailView.setOnSendFlowerClick { square, lovePoint ->
//            var dialogSendRedFlowerDialog = SendRedFlowerDialog()
//            dialogSendRedFlowerDialog.arguments = bundleOf("ToFromType" to 1,"userId" to it.userid.toString(),"squareId" to it.id.toString())
//            dialogSendRedFlowerDialog.show(supportFragmentManager,"sendflower")
            Request.sendLovePoint(getLoginToken(),"${square.userid}",lovePoint,1,"${square.id}","","${square.desc}").request(this,false,success={_,Data->
                EventBus.getDefault().post(FlowerMsgEvent(lovePoint))
                Request.getUserInfo("", getLocalUserId()).request(this,false,success = { _, data ->
                    data?.let {
                        SPUtils.instance().put(Const.User.USERLOVE_NUMS,it.iLovePoint).apply()
                    }
                })
            }){code,msg->
                if (code == 2||code==3) {
                    var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                    mSendRedHeartEndDialog.show(supportFragmentManager, "redheartendDialog")
                }else{
                    toast(msg)
                }
            }
        }

        headerView.mTrendDetailView.setOnCommentClick {
            showSoftInput(et_content)
            replayUid=""
            et_content.setText("")
            et_content.hint = resources.getString(R.string.string_comment_tips)
        }

        headerView.mTrendDetailView.setOnSoftInputClick{
            hideSoftKeyboard(et_content)
        }

        headerView.mTrendDetailView.setDeletClick {
            val shareDialog = ShareFriendsDialog()
            shareDialog.arguments = bundleOf("from" to "square","id" to it.userid.toString(),"sResourceId" to it.id.toString())
            shareDialog.show(supportFragmentManager, "action")
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    startActivity<ReportActivity>("id" to id, "tiptype" to "2")
                } else if (p == 1) {
                    delSquare()
                }
            }
        }

        headerView.mTrendDetailView.onTogglePlay {
//            var proxyUrl = getProxyUrl(this,"http://sc1.111ttt.cn/2017/1/05/09/298092035545.mp3")
            var proxyUrl = getProxyUrl(this,it.sVoiceUrl)
            mAudioMedio.setAudioPath(proxyUrl)
            if(mAudioMedio.onTogglePlay()){
                startPlayAudioView()
            }else{
                stopPlayAudioView()
            }
        }

        squareDetailCommentAdapter.setOnItemClickListener { _, position ->
            val comment = mComments[position]

            if(!TextUtils.equals(replayContent,comment.content)){
                et_content.setText("")
                et_content.hint = resources.getString(R.string.string_replaycomment,comment.name)
                iReplyCommnetType = comment.iIsAnonymous
            }
            replayContent = "${comment.content}"

            replayUid = comment.userId?:""
//            isAuthUser {
//                startActivityForResult<CommentActivity>(0, "id" to id, "uid" to cUid)
//            }
//            showSoftInput()

            showSoftInput(et_content)
        }

        squareDetailCommentAdapter.setDeleteClick {
            val commentDelDialog = CommentDelDialog()
            commentDelDialog.arguments = bundleOf("data" to it)
            commentDelDialog.show(supportFragmentManager, "action")
            commentDelDialog.setDialogListener { p, s ->
                if (p == 1) {
                    delete(it)
                }
            }
        }
//        et_content.setOnClickListener {
//            isAuthUser {
//                startActivityForResult<CommentActivity>(0, "id" to id, "uid" to "")
//            }
//        }

        et_content.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null && p0.isNotEmpty()) {
                    btn_send.backgroundDrawable = ContextCompat.getDrawable(context,R.drawable.shape_10r_orange)
                } else {
                    btn_send.backgroundDrawable = ContextCompat.getDrawable(context,R.drawable.shape_10r_grey)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        btn_send.setOnClickListener {
            hideSoftKeyboard(et_content)
            if (!isFastClick()) {
                comment()
            }
        }

        tv_unknow_choose.setOnClickListener {
            var mSelectUnknowDialog = SelectUnKnowTypeDialog()
            mSelectUnknowDialog.arguments = bundleOf("type" to "SquareTrendDetail","IsOpenUnKnow" to getIsOpenUnKnow())
            mSelectUnknowDialog.show(supportFragmentManager,"unknowdialog")
            mSelectUnknowDialog.setDialogListener { p, s ->
                setInputState(p)
                chooseAnonymous = true
            }
        }

        iIsAnonymous = 2
        headerView.mTrendDetailView.initGiftControl()
        getData()

        setAudioListener()
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
            }

            override fun onCompletion() {
                stopPlayAudioView()
            }
        })
    }

    //开始播放音频
    private fun startPlayAudioView(){
        headerView.mTrendDetailView.iv_playaudio.setImageResource(R.drawable.drawable_play_voice)
        starPlayDrawableAnim(headerView.mTrendDetailView.iv_playaudio)
    }

    //停止播放音频
    private fun stopPlayAudioView(){
        stopPlayDrawableAnim(headerView.mTrendDetailView.iv_playaudio)
        headerView.mTrendDetailView.iv_playaudio.setImageResource(R.mipmap.liveroom_recording3)
    }

    private fun setInputState(p:Int){
        if(p==2){
            tv_unknow_choose.text = "公开"
            var mDrawableLeft = ContextCompat.getDrawable(this,R.mipmap.public_small)
            var mDrawableRight = ContextCompat.getDrawable(this,R.mipmap.ic_arrow_down)
            tv_unknow_choose.setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft,null,mDrawableRight,null)

            tv_unknow_choose.textColor = ContextCompat.getColor(this,R.color.color_666666)
//                    tv_unknow_choose.backgroundDrawable = ContextCompat.getDrawable(this,R.drawable.shape_20r_white_border)
        }else if(p==1){
            tv_unknow_choose.text = "匿名"
            var mDrawableLeft = ContextCompat.getDrawable(this,R.mipmap.key_small)
            var mDrawableRight = ContextCompat.getDrawable(this,R.mipmap.niming_more)

            tv_unknow_choose.setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft,null,mDrawableRight,null)

            tv_unknow_choose.textColor = ContextCompat.getColor(this,R.color.color_8F5A5A)
//                    tv_unknow_choose.backgroundDrawable = ContextCompat.getDrawable(this,R.drawable.shape_20r_5a_border)
        }

        iIsAnonymous = p
    }

    private fun getData() {
        if (id.isNotEmpty()) {
            Request.getSquareDetail(userId, id).request(this, success = { _, data ->
                mSwipeRefreshLayout.isRefreshing = false
                data?.let {
                    mSquare = it
                    squareDetailCommentAdapter.setIsMySquare(it.userid.toString())
                    headerView.mTrendDetailView.update(it)
                    setTitleBold(it.name,true)
                    titleBar.titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17f)
                    titleBar.titleView.setOnClickListener {
                        startActivity<UserInfoActivity>("id" to id)
                    }
                    mComments.clear()
                    if (it.comments == null || it.comments.isEmpty()) {
                        mSwipeRefreshLayout.setLoadMoreText("暂无评论")
                    } else {
                        mComments.addAll(it.comments)
                    }
//                    Collections.reverse(mComments)
                    squareDetailCommentAdapter.setNMIndex(1)
                    squareDetailCommentAdapter.notifyDataSetChanged()
                    mSquare?.comments = mComments
                    updateBean()
                    if(!chooseAnonymous){
                        if(it.iIsAnonymous==1){
                            if(TextUtils.equals("open", getIsOpenUnKnow())){
                                setInputState(1)
                            }else{
                                setInputState(2)
                            }
                        }else{
                            setInputState(2)
                        }
                    }

                }
            }, error = { code, msg ->
                if(code == 2){
                   finish()
                }else{
                    mSwipeRefreshLayout.isRefreshing = false
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                dialog()
                getData()
            }
        }
    }

    private fun loadData() {
        Request.getCommentList(userId,id, pageNum).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            if (pageNum == 1) {
                mComments.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                if (pageNum > 1) {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    pageNum--
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("暂无评论")
                }
            } else {
                mComments.addAll(data.list.results)
            }
            if(data?.list!=null){
                if(data.list.results!=null){
                    squareDetailCommentAdapter.notifyItemChanged(mComments.size-data.list.results?.size!!.toInt())
                }
            }
        }, error = { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        })
    }

    private fun praise(square: Square) {
        dialog()
        Request.addPraise(userId, square.id).request(this,true) { msg, jsonObject ->
            square.isupvote = "1"
            square.appraiseCount = (square.appraiseCount?:0)+1
            headerView.mTrendDetailView.update(square)
            mSquare?.appraiseCount = square.appraiseCount
            mSquare?.isupvote = square.isupvote
            updateBean()
            showTips(jsonObject,"","");
        }

    }

    private fun cancelPraise(square: Square) {
        dialog()
        Request.cancelPraise(userId, square.id).request(this) { msg, _ ->
            square.isupvote = "0"
            square.appraiseCount = if(((square.appraiseCount?:0)-1)<0) 0 else (square.appraiseCount?:0)-1
            mSquare?.appraiseCount = square.appraiseCount
            mSquare?.isupvote = square.isupvote
            updateBean()
            headerView.mTrendDetailView.update(square)
        }
    }

    private fun comment() {
        val content = et_content.text.toString().trim()
        if (content.isEmpty()) {
            toast("评论内容不能为空")
            return
        }
        val replyUid = if (replayUid.isEmpty()) {
            null
        } else {
            replayUid
        }
        dialog()
        Request.addComment(userId, id,content,replyUid,iIsAnonymous,iReplyCommnetType).request(this,false,success={msg,jsonObject->
            et_content.setText("")
            et_content.clearFocus()
            et_content.hint=getString(R.string.string_comment_tips)
            replayUid = ""
            iReplyCommnetType = 2
            toast("评论成功")
            pageNum = 1
            mSquare?.commentCount= mSquare?.commentCount!!.toInt()+1
            Log.i("dddd","success==${jsonObject}")
            getData()
            showTips(jsonObject,"","");
        }){code,msg->
            showToast(msg)
        }
    }

    private fun updateBean(){
        if (mPosition > -1) {
            var intent = Intent()
            var bundle = Bundle()
            bundle.putSerializable("bean", mSquare)
            bundle.putInt("position", mPosition)
            intent.putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
        }
    }

    override fun onRefresh() {
        pageNum = 1
        getData()
    }

    override fun onLoadMore() {
        if (mComments.isEmpty()) {
            mSwipeRefreshLayout.setLoadMoreText("暂无评论")
            mSwipeRefreshLayout.isRefreshing = false
            return
        }
        pageNum++
        loadData()
    }

    private fun delete(data: Comment){
            dialog(canCancel = false)
            Request.delComments(data.id!!.toInt()).request(this) { _, _ ->
                showToast("删除成功")
                mComments.remove(data)
                mSquare?.commentCount= mSquare?.commentCount!!.toInt()-1
                mSquare?.comments=mComments
                updateBean()
                squareDetailCommentAdapter.setNMIndex(1)
                squareDetailCommentAdapter.notifyDataSetChanged()
                mSquare?.let {
                    headerView.mTrendDetailView.update(it)
                }
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(flowerEvent: FlowerMsgEvent){
            mSquare?.let {
                it.iLovePoint = it.iLovePoint!!.toInt() + flowerEvent.count
                it.iSendLovePoint=1
                if(flowerEvent.getmSquare()!=null){
                    it.sIfLovePics = flowerEvent.getmSquare().sIfLovePics
                }
//                it.iFlowerCount = it.iFlowerCount!!.toInt() + flowerEvent.count
//                it.iIsSendFlower=1
                updateBean()
                headerView.mTrendDetailView.updateFlowerCount(it)
                Log.i("imagePage","对象：")
            }
    }

    /**
     * 删除动态
     */
    fun delSquare(){
        dialog(canCancel = false)
        Request.deleteSquare(userId, id).request(this) { _, _ ->
            showToast("删除成功")
            finish()
        }
    }

    private val sIfLovePics by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.let {
                        var sq = it.getSerializableExtra("bean") as Square
                        mSquare?.let {
                            if(sq!=null){
                                it.sIfLovePics = sq.sIfLovePics
                                it.sIfSeePics = sq.sIfSeePics
                                it.iLovePoint = sq.iLovePoint
                            }
                            headerView.mTrendDetailView.update(it)
                            updateBean()
                        }
                        Log.i("BlurMsgEvent","BlurMsgEvent${sq.sIfLovePics}")
                    }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mAudioMedio.onDestoryAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(sIfLovePics)
        EventBus.getDefault().unregister(this@SquareTrendDetailActivity)
    }
}