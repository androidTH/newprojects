package com.d6.android.app.widget

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.IdRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.activities.FilterSquaresActivity
import com.d6.android.app.activities.SimplePlayer
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.SquareCommentAdapter
import com.d6.android.app.adapters.SquareImageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.SendLoveHeartDialog
import com.d6.android.app.dialogs.SendRedHeartEndDialog
import com.d6.android.app.dialogs.UnKnowInfoDialog
import com.d6.android.app.models.Comment
import com.d6.android.app.models.Square
import com.d6.android.app.models.TopicBean
import com.d6.android.app.utils.*
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.d6.android.app.widget.gift.CustormAnim
import com.d6.android.app.widget.gift.GiftControl
import com.d6.android.app.widget.gift.GiftModel
import com.d6.android.app.widget.popup.mLoveHeartPopu
import kotlinx.android.synthetic.main.item_audio.view.*
import kotlinx.android.synthetic.main.view_trend_view.view.*
import org.jetbrains.anko.*
import java.lang.Exception
import java.net.URLDecoder

/**
 * Created on 2017/12/17.
 * 动态列表
 */
class TrendView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var square: Square? = null
    private val mImages = ArrayList<String>()
//    private var localLoveHeartNums =
    private var sendLoveHeartNums = 1

    private val imageAdapter by lazy {
        SquareImageAdapter(mImages,1)
    }
    private val mComments = ArrayList<Comment>()
    private val commentAdapter by lazy {
        SquareCommentAdapter(mComments)
    }
            init {
                LayoutInflater.from(context).inflate(R.layout.view_trend_view, this, true)
                rv_images.setHasFixedSize(true)
        //        rv_images.layoutManager = GridLayoutManager(context,3)
                rv_images.adapter = imageAdapter
        //        rv_images.addItemDecoration(SpacesItemDecoration(dip(4),3))
                val recyclerViewClickListener = RecyclerViewClickListener(context,rv_images,RecyclerViewClickListener.OnClickListener {
                    square?.let {
                        onItemClick?.onClick(rv_images,it)
                    }
                })

                rv_images.addOnItemTouchListener(recyclerViewClickListener)

                rv_comment.setHasFixedSize(true)
                rv_comment.layoutManager = LinearLayoutManager(context)
                rv_comment.adapter = commentAdapter

                commentAdapter.setOnCommentClick {
                    square?.let {
                        mOnSquareDetailsClick?.onSquareDetails(it)
                    }
                }

                tv_appraise.setOnClickListener {
                    square?.let {
                        action?.onPraiseClick(it)
                    }
                }

                headView.setOnClickListener {
                    square?.let {
                        val id = it.userid?:""
                        if(it.iIsAnonymous==1){
                            var mUnknowDialog = UnKnowInfoDialog()
                            mUnknowDialog.arguments = bundleOf("otheruserId" to id)
                            mUnknowDialog.show((context as BaseActivity).supportFragmentManager,"unknowDialog")
                        }else{
                            context.startActivity<UserInfoActivity>("id" to id)
                        }
                    }
                }

                ll_comments.setOnClickListener {
                  square?.let {
                      mOnSquareDetailsClick?.onSquareDetails(it)
                  }
                }

                tv_delete.setOnClickListener {
                    square?.let {
                        deleteAction?.onDelete(it)
                    }
                }

                tv_redflower.setOnClickListener {
                    (context as BaseActivity).isAuthUser() {
                        if(!TextUtils.equals(getLocalUserId(),"${square?.userid}")){
                            if(getLocalUserLoveHeart()>0){
                                if(sendLoveHeartNums <= getLocalUserLoveHeart()){
                                    sendLoveHeartNums = sendLoveHeartNums+1
                                    addGiftNums(1, false, false)
                                    VibrateHelp.Vibrate((context as BaseActivity), VibrateHelp.time50)
                                }else{
                                    var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                                    mSendRedHeartEndDialog.show((context as BaseActivity).supportFragmentManager, "redheartendDialog")
                                }
                            }else{
                                var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                                mSendRedHeartEndDialog.show((context as BaseActivity).supportFragmentManager, "redheartendDialog")
                            }
                        }else{
                            (context as BaseActivity).toast(context.getString(R.string.string_liked_give_other))
                        }
//                        var view = it
//                        mAbovePop?.let {
//                            if (!it.isShowing) {
//                                it.addGiftNums(1, false, false)
//                                it.showAtAnchorView(view, YGravity.ABOVE, XGravity.CENTER)
//                            } else {
//                                it.addGiftNums(1, false, false)
//                            }
//                        }
                    }
                }

                tv_redflower.setOnLongClickListener {
                    (context as BaseActivity).isAuthUser(){
                        square?.let {
                            var mSendLoveHeartDialog = SendLoveHeartDialog()
                            mSendLoveHeartDialog.arguments = bundleOf("userId" to "${it.userid}","iIsAnonymous" to "${it.iIsAnonymous}")
                            mSendLoveHeartDialog.setDialogListener { p, s ->
                                addGiftNums(p, false,true)
                            }
                            mSendLoveHeartDialog.show((context as BaseActivity).supportFragmentManager, "sendloveheartDialog")
                        }
                    }
                    true
                }

                rl_play_audio.setOnClickListener {
                    square?.let {
                        mTogglePlay?.onTogglePlay(it)
                    }
                }

                rl_vidoe.setOnClickListener {
                    square?.let {
                        (context as BaseActivity).startActivity<SimplePlayer>("videoPath" to it.sVideoUrl,"videoType" to "1")
                    }
                }

                tv_square_address.setOnClickListener {
                    square?.let {
                        var mSquareType = TopicBean("-2",R.mipmap.local_feedlist_icon,"${it.city}")
                        mSquareType.city = "${it.city}"
                        (context as BaseActivity).startActivity<FilterSquaresActivity>("squaretype" to mSquareType)
                    }
                }

                tv_topic.setOnClickListener {
                    square?.let {
                        var mSquareType:TopicBean
                        if(!it.sTopicId.isNullOrEmpty()){
                            mSquareType = TopicBean(it.sTopicId,-1,it.sTopicName)
                        }else{
                            mSquareType = TopicBean("",-1,it.sTopicName)
                        }
                        (context as BaseActivity).startActivity<FilterSquaresActivity>("squaretype" to mSquareType)
                    }
                }
            }

    /**
     * type 1 可以展示删除按钮。
     */
    fun update(square: Square,type:Int?=0) {
        imageAdapter.bindSquare(square)
//        val uid = SPUtils.instance().getString(Const.User.USER_ID)
//        if (TextUtils.equals(square.userid, uid) && type == 1) {//是自己。
//            tv_delete.visible()
//        } else {
//            tv_delete.gone()
//        }
        this.square = square
//        headView.hierarchy = getHierarchy(square.sex.toString())
        headView.setImageURI(square.picUrl)

        tv_name.text = square.name

        tv_sex.isSelected = TextUtils.equals("0",square.sex)
//        tv_vip.text = square.userclassesname
        tv_vip.backgroundDrawable = getLevelDrawable(square.userclassesid.toString(),context)

        if(TextUtils.equals("3",square.screen)){
            img_auther.visibility=View.GONE
            img_auther.setImageResource(R.mipmap.renzheng_small)
        }else if(TextUtils.equals("1",square.screen)){
            img_auther.visibility=View.VISIBLE
            img_auther.setImageResource(R.mipmap.video_small)
        }else{
            img_auther.visibility=View.GONE
        }

        if(square.orders<=0){
            tv_topfeed.visibility = View.VISIBLE
        }else{
            tv_topfeed.visibility = View.INVISIBLE
        }

        if(TextUtils.isEmpty(square.sTopicName)){
            tv_topic.visibility = View.GONE
        }else{
            tv_topic.visibility = View.VISIBLE
            tv_topic.text = square.sTopicName
        }

        if(TextUtils.isEmpty(square.city)){
            tv_square_address.visibility = View.GONE
        }else{
            tv_square_address.visibility = View.VISIBLE
            tv_square_address.text = square.city
        }

//        if(TextUtils.equals(square.sex, "1")){
//            tv_vip.visibility = View.VISIBLE
//        }else{
//            tv_vip.visibility = View.GONE
//        }

        if(square.iIsCommentTop==2){
            tv_sub_title.text = context.getString(R.string.string_newcomments)
        }else{
//            val sub = if (square.city.isNullOrEmpty()) {
//                square.updatetime?.interval()
//            } else {
//                String.format("%s | %s",square.updatetime?.interval(),square.city)
//            }

            tv_sub_title.text = square.updatetime?.interval()
        }

        if(square.content.isNullOrEmpty()){
            tv_content.visibility = View.GONE
        }else{
            tv_content.visibility = View.VISIBLE
            tv_content.text = square.content
        }
        //1、文字  2、图片 4、语音 ，新发布的这样区分，之前的为0
        if(square.iResourceType==3){
            //3、视频
            rv_images.visibility = View.GONE
            rl_root_audio.visibility = View.GONE

            if(square.sVideoPicUrl.isNotEmpty()){
                rl_vidoe.visibility = View.VISIBLE
//                sv_video.setImageURI(square.sVideoPicUrl)
                FrescoUtils.loadImage(context,square.sVideoPicUrl,object: IResult<Bitmap> {
                    override fun onResult(result: Bitmap?) {
                        result?.let {
//                            var params = sv_video.layoutParams
//                            params.height =  WindowManager.LayoutParams.WRAP_CONTENT
                            if(it.height>it.width){
//                                params.width = WindowManager.LayoutParams.WRAP_CONTENT
//                                sv_video.layoutParams = params
                                sv_video.setImageBitmap(Bitmap.createScaledBitmap(it,BitmapUtils.MINWIDTH,BitmapUtils.MINHEIGHT,false))
                            }else{
                                if(square.sVideoWidth.isNotEmpty()&&square.sVideoHeight.isNotEmpty()){
                                    var sWidth = square.sVideoWidth.toInt()
                                    var sHeight = square.sVideoHeight.toInt()
                                    sv_video.setImageBitmap(Bitmap.createScaledBitmap(it,sWidth,sHeight,false))
                                }else{
                                    sv_video.setImageBitmap(it)
                                }
//                                params.width = WindowManager.LayoutParams.MATCH_PARENT
//                                sv_video.layoutParams = params
//                                sv_video.setImageBitmap(it)
                            }
                        }
                    }
                })
            }else{
                rl_vidoe.visibility = View.GONE
            }
            Log.i("trendView","动态类型=${square.iResourceType},视频所属人:${square.name},内容：${square.content},${square.sVideoUrl}视频链接," +
                    "图片链接=${square.sVideoPicUrl},图片宽高${square.sVideoWidth},${square.sVideoHeight}")
        }else if(square.iResourceType==4){
            rv_images.visibility = View.GONE
            rl_vidoe.visibility = View.GONE
            //4、语音
            if(!TextUtils.isEmpty(square.sVoiceUrl)){
                rl_root_audio.visibility = View.VISIBLE
            }else{
                rl_root_audio.visibility = View.GONE
            }
            if(square.isPlaying){
                startPlayAudioView(iv_playaudio)
            }else{
                stopPlayAudioView(iv_playaudio)
            }

            if(!TextUtils.equals("",square.sVoiceLength)){
                var voicelength:Int
                try{
                    voicelength = square.sVoiceLength.toInt()
                }catch (e:Exception){
                    voicelength = 0
                }
                var param = rl_play_audio.layoutParams
                param.width = (resources.getDimensionPixelSize(R.dimen.width_100) + resources.getDimensionPixelSize(R.dimen.width_100)/60*voicelength)
                rl_play_audio.layoutParams = param
                tv_audio_time.text="${square.sVoiceLength}”"
            }else{
                tv_audio_time.text="0”"
            }
            Log.i("trendView","动态类型=${square.iResourceType},音频所属人:${square.name},内容：${square.content},音频链接=${square.sVoiceUrl}")
        }else{
            // 1或者2
            rl_vidoe.visibility = View.GONE
            rl_root_audio.visibility = View.GONE

            //2、图片
            if (square.imgUrl.isNullOrEmpty()) {
                rv_images.gone()
            } else {
                rv_images.visible()
            }
            mImages.clear()
            val images = square.imgUrl?.split(",")
            if (images != null) {
                mImages.addAll(images.toList())
            }
            val d = rv_images.getItemDecorationAt(0)
            if (d != null) {
                rv_images.removeItemDecoration(d)
            }
            if (mImages.size == 1 || mImages.size == 2 || mImages.size == 4) {
                rv_images.layoutManager = GridLayoutManager(context, 2)
                rv_images.addItemDecoration(RxRecyclerViewDividerTool(dip(2)))//SpacesItemDecoration(dip(4),2)
            } else {
                rv_images.layoutManager = GridLayoutManager(context, 3)
                rv_images.addItemDecoration(RxRecyclerViewDividerTool(dip(2)))//SpacesItemDecoration(dip(4),3)
            }
            imageAdapter.notifyDataSetChanged()

            Log.i("trendView","${square.content},图片位置：${square.sIfLovePics}")
        }

        tv_appraise.isSelected = TextUtils.equals(square.isupvote,"1")
        tv_comment.text = if ((square.commentCount?:0) > 0) {
            square.commentCount.toString()
        } else {
            ""
        }
        tv_appraise.text = if ((square.appraiseCount ?:0)> 0) {
            square.appraiseCount.toString()
        } else {
            ""
        }

        tv_redflower.text = if((square.iLovePoint?:0)>0){
           "${square.iLovePoint}"
        }else{
            ""
        }

        //iIsSendFlower 大于0送过花，等于0没送过
        tv_redflower.isSelected = if ((square.iSendLovePoint?:0) > 0) {
            true
        } else {
            false
        }
        square.commentCount?.let {
            if (it > 0) {
                ll_comments.visible()
                rv_comment.visible()
            } else {
                rv_comment.gone()
                ll_comments.gone()
            }
            if (it > 3) {
                tv_all_comment.text = String.format("查看全部%s条评论",it)
                tv_all_comment.visible()
            } else {
                tv_all_comment.gone()
            }
        }
        mComments.clear()
        if (square.comments != null) {
            if(square.comments.size>3){
                mComments.addAll(square.comments.subList(0,3))
            }else{
                mComments.addAll(square.comments)
            }
        }
        commentAdapter.setSquareUserId(square.userid.toString(),1)
        commentAdapter.notifyDataSetChanged()

        initGiftControl()
    }

    private var mAbovePop: mLoveHeartPopu? = null
    private fun aa(){
//        mAbovePop = EasyPopup.create()
//                .setContentView(context, R.layout.layout_any)
//                .setFocusAndOutsideEnable(true)
//                .setOnDismissListener(PopupWindow.OnDismissListener {
//
//                })
//                .setOnViewListener { view, popup ->
//
//                }
//                .apply()

        mAbovePop = mLoveHeartPopu.create(context)
                .setFocusAndOutsideEnable(true)
                .setOnDismissListener(PopupWindow.OnDismissListener {

                })
                .apply()
    }

    //礼物
    private var giftControl: GiftControl? = null

    fun initGiftControl(){
        if(giftControl==null){
            giftControl = GiftControl(context)
            giftControl?.let {
                it.setGiftLayout(square_gift_parent, 1)
                        .setHideMode(false)
                        .setCustormAnim(CustormAnim())
                it.setmGiftAnimationEndListener {
                    var lovePoint = it
                    square?.let {
                        SendFlowerAction?.onSendFlowerClick(it,lovePoint)
                    }
                    sendLoveHeartNums = 1
                }
            }
        }
    }

    private fun doLoveHeartAnimation(){
        square_loveheart.showAnimationRedHeart(tv_redflower)
    }

    //连击礼物数量
    fun addGiftNums(giftnum: Int, currentStart: Boolean = false,JumpCombo:Boolean = false) {
        if (giftnum == 0) {
            return
        } else {
            giftControl?.let {
                //这里最好不要直接new对象
                var giftModel = GiftModel()
                giftModel.setGiftId("礼物Id").setGiftName("礼物名字").setGiftCount(giftnum).setGiftPic("")
                        .setSendUserId("1234").setSendUserName("吕靓茜").setSendUserPic("").setSendGiftTime(System.currentTimeMillis())
                        .setCurrentStart(currentStart)
                if (currentStart) {
                    giftModel.setHitCombo(giftnum)
                }
                if(JumpCombo){
                    giftModel.setJumpCombo(giftnum)
                }
                it.loadGift(giftModel)
                Log.d("TAG", "onClick: " + it.getShowingGiftLayoutCount())
            }

            doLoveHeartAnimation()
        }
    }

    //开始播放音频
    fun startPlayAudioView(view:ImageView){
        iv_playaudio.setImageResource(R.drawable.drawable_play_voice)
        starPlayDrawableAnim(view)
    }

    //停止播放音频
    fun stopPlayAudioView(view:ImageView){
        stopPlayDrawableAnim(view)
        iv_playaudio.setImageResource(R.mipmap.liveroom_recording3)
    }

    fun hide(@IdRes viewIdRes: Int) {
        find<View>(viewIdRes).gone()
    }

    fun sendFlowerClick(flowerAction:(square:Square,lovePoint:Int)->Unit){
        this.SendFlowerAction = object :SendFlowerClickListener{
            override fun onSendFlowerClick(square: Square,lovePoint:Int) {
                flowerAction(square,lovePoint)
            }
//            override fun onSendLoveLongClick(square: Square, lovePoint: Int) {
//                loveHeartAction(square,lovePoint)
//            }
        }
    }

    fun setPraiseClick(action:(square:Square)->Unit){
        this.action = object : PraiseClickListener{
            override fun onPraiseClick(square: Square) {
                action(square)
            }
        }
    }

    fun setDeleteClick(action:(square:Square)->Unit){
        this.deleteAction = object : DeleteClick{
            override fun onDelete(square: Square) {
                action(square)
            }
        }
    }

    fun setOnItemClick(action:(view:View,square:Square)->Unit){
        this.onItemClick = object : OnItemClick{
            override fun onClick(view:View,square: Square) {
                action(view,square)
            }
        }
    }

    fun setOnSquareDetailsClick(action:(square:Square)->Unit) {
        this.mOnSquareDetailsClick = object : OnSquareDetailsClick {
            override fun onSquareDetails(square: Square) {
                    action(square)
            }
        }
    }

    fun onTogglePlay(action:(square:Square)->Unit) {
        this.mTogglePlay = object : TogglePlay {
            override fun onTogglePlay(square: Square) {
                action(square)
            }
        }
    }

    private var action:PraiseClickListener?=null
    private var deleteAction:DeleteClick?=null
    private var onItemClick:OnItemClick?=null
    private var mOnSquareDetailsClick:OnSquareDetailsClick?=null
    private var SendFlowerAction:SendFlowerClickListener?=null
    private var mTogglePlay:TogglePlay?=null


    interface SendFlowerClickListener{
        fun onSendFlowerClick(square: Square,lovePoint:Int)
//        fun onSendLoveLongClick(square: Square,lovePoint:Int)
    }

    interface PraiseClickListener{
        fun onPraiseClick(square: Square)
    }

    interface DeleteClick{
        fun onDelete(square: Square)
    }

    interface OnItemClick{
        fun onClick(view: View,square: Square)
    }

    interface OnSquareDetailsClick{
        fun onSquareDetails(square: Square)
    }

    interface TogglePlay{
        fun onTogglePlay(square: Square)
    }

    private var textClickedListener: CustomLinkMovementMethod.TextClickedListener? = null

    fun setOnCommentClick(l:()->Unit) {
        textClickedListener = CustomLinkMovementMethod.TextClickedListener { l() }
    }

}