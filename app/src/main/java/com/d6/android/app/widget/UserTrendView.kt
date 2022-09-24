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
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.activities.D6LoveHeartListActivity
import com.d6.android.app.activities.SimplePlayer
import com.d6.android.app.adapters.SquareCommentAdapter
import com.d6.android.app.adapters.SquareImageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.SelectGiftListDialog
import com.d6.android.app.dialogs.SendLoveHeartDialog
import com.d6.android.app.dialogs.SendRedHeartEndDialog
import com.d6.android.app.models.Comment
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import com.d6.android.app.widget.frescohelper.FrescoUtils
import com.d6.android.app.widget.frescohelper.IResult
import com.d6.android.app.widget.gift.CustormAnim
import com.d6.android.app.widget.gift.GiftControl
import com.d6.android.app.widget.gift.GiftModel
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.item_audio.view.*
import kotlinx.android.synthetic.main.view_user_trend_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip
import org.jetbrains.anko.*
import org.jetbrains.anko.find
import java.net.URLDecoder

/**
 * Created on 2017/12/17.
 * 我的动态
 */
class UserTrendView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var sendLoveHeartNums = if (TextUtils.equals(getUserSex(), "0")) {
        1
    } else {
        10
    }
    private var square: Square? = null
    private val mImages = ArrayList<String>()
    private val imageAdapter by lazy {
        SquareImageAdapter(mImages,1)
    }

    private val mComments = ArrayList<Comment>()
    private val commentAdapter by lazy {
        SquareCommentAdapter(mComments)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_user_trend_view, this, true)
        rv_images.setHasFixedSize(true)

        rv_images.adapter = imageAdapter
//        rv_images.addItemDecoration(SpacesItemDecoration(dip(4),3))
//
        val recyclerViewClickListener = RecyclerViewClickListener(context,rv_images,RecyclerViewClickListener.OnClickListener {
            square?.let {
                onItemClick?.onClick(rv_images,it)
            }
        })

        rv_images.addOnItemTouchListener(recyclerViewClickListener)
        rv_comment.setHasFixedSize(true)
        rv_comment.layoutManager = LinearLayoutManager(context)
        rv_comment.adapter = commentAdapter
//
        tv_appraise.setOnClickListener {
            square?.let {
                action?.onPraiseClick(it)
            }
        }
//        headView.setOnClickListener {
//            square?.let {
//                val id = it.userid?:""
//                context.startActivity<UserInfoActivity>("id" to id)
//            }
//        }
//        commentAdapter.setOnCommentClick {
//            textClickedListener?.onTextClicked()
//        }
//
        tv_my_square_delete.setOnClickListener {
            square?.let {
                deleteAction?.onDelete(it)
            }
        }


        tv_userinfo_gift.setOnClickListener {
            (context as BaseActivity).isAuthUser(){
                square?.let {
                    showGiftDialog("${it.userid}","${it.id}","${it.name}")
                }
            }
        }


        tv_redflower.setOnClickListener {
//               square?.let {
//                   sendFlowerAction?.onSendFlowerClicker(it)
//               }
                (context as BaseActivity).isAuthUser(){
                    if (!TextUtils.equals(getLocalUserId(), "${square?.userid}")) {
                        if (getLocalUserLoveHeart() > 0) {
                            if (sendLoveHeartNums <= getLocalUserLoveHeart()) {
                                if(TextUtils.equals(getUserSex(),"0")){
                                    sendLoveHeartNums = sendLoveHeartNums+1
                                    addGiftNums(1, false, false,"")
                                }else{
                                    sendLoveHeartNums = sendLoveHeartNums+10
                                    addGiftNums(10, false, true,"")
                                }
                                VibrateHelp.Vibrate((context as BaseActivity), VibrateHelp.time50)
                            } else {
                                var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                                mSendRedHeartEndDialog.show((context as BaseActivity).supportFragmentManager, "redheartendDialog")
                            }
                        } else {
                            var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                            mSendRedHeartEndDialog.show((context as BaseActivity).supportFragmentManager, "redheartendDialog")
                        }
                    }else{
                        (context as BaseActivity).toast(context.getString(R.string.string_liked_give_other))
                    }
                }
        }

        tv_redflower.setOnLongClickListener {
            (context as BaseActivity).isAuthUser(){
                square?.let {
                    var mSendLoveHeartDialog = SendLoveHeartDialog()
                    mSendLoveHeartDialog.arguments = bundleOf("userId" to "${it.userid}")
                    mSendLoveHeartDialog.setDialogListener { p, s ->
                        addGiftNums(p, false, true,"${s}")
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

        rl_vidoe_user.setOnClickListener {
            square?.let {
                (context as BaseActivity).startActivity<SimplePlayer>("videoPath" to it.sVideoUrl,"videoType" to "1")
            }
        }
        rl_usersquare_bg_layout.setOnClickListener {
            square?.let {
                (context as BaseActivity).startActivity<D6LoveHeartListActivity>()
            }
        }
    }

    /**
     * type 1 可以展示删除按钮。
     */
    fun update(square: Square,type:Int?=0) {
        imageAdapter.bindSquare(square)
//
//        val uid = SPUtils.instance().getString(Const.User.USER_ID)
//        if (TextUtils.equals(square.userid, uid) && type == 1) {//是自己。
//            tv_delete.visible()
//        } else {
//            tv_delete.gone()
//        }
        this.square = square
        headView.setImageURI(square.picUrl)
        tv_name.text = square.name
        tv_sex.isSelected = TextUtils.equals("0",square.sex)

        if(square.age.isNullOrEmpty()){
            tv_age.visibility = View.GONE
        }else{
            tv_age.isSelected = TextUtils.equals("0",square.sex)
            tv_age.visibility = View.VISIBLE
            tv_age.text = "${square.age}岁"
        }

        tv_vip.backgroundDrawable = getLevelDrawable("${square.userclassesid}",context)

        if(TextUtils.equals("3",square.screen)){
            img_auther.visibility=View.GONE
            img_auther.setImageResource(R.mipmap.renzheng_small)
        }else if(TextUtils.equals("1",square.screen)){
            img_auther.visibility=View.VISIBLE
            img_auther.setImageResource(R.mipmap.video_small)
        }else{
            img_auther.visibility=View.GONE
        }
        if(true){
            rl_usersquare_bg_layout.visibility = View.VISIBLE
            usersquare_bd_headView.setImageURI(square.picUrl)
            tv_usersquare_bd_username.text = "${square.name}"
            tv_usersquare_bd_usersex.isSelected = TextUtils.equals("0",square.sex)

            if(square.age.isNullOrEmpty()){
                tv_usersquare_bd_userage.visibility = View.GONE
            }else{
                tv_usersquare_bd_userage.visibility = View.VISIBLE
                tv_usersquare_bd_userage.text = "${square.age}岁"
            }

            tv_usersquare_bd_uservip.backgroundDrawable = getLevelDrawable(square.userclassesid.toString(),context)
            tv_usersquare_bd_show.text = "收到100颗[img src=redheart_small/]"
            tv_usersquare_click_bangdan.text = "送[img src=liwu_list_g/]或[img src=heart_gray/]即可为我打榜哦"
        }else{
            rl_usersquare_bg_layout.visibility = View.GONE
        }

//        val sub = if (square.city.isNullOrEmpty()) {
//            square.updatetime?.interval()
//        } else {
//            String.format("%s | %s",square.updatetime?.interval(),square.city)
//        }
        tv_createtime.text = square.updatetime?.interval()

        tv_content.text = square.content
        Log.i("usertrendView","年龄：${square.age},动态vip：${square.userclassesid}")

        if(square.iResourceType==3){

            rv_images.visibility = View.GONE
            rl_root_audio.visibility = View.GONE

            rl_vidoe_user.visibility = View.VISIBLE
            FrescoUtils.loadImage(context,square.sVideoPicUrl,object: IResult<Bitmap> {
                override fun onResult(result: Bitmap?) {
                    result?.let {
//                        var params = sv_video_user.layoutParams
//                        params.height =  WindowManager.LayoutParams.WRAP_CONTENT
                        if(it.height>it.width){
//                            params.width = WindowManager.LayoutParams.WRAP_CONTENT
//                            sv_video_user.layoutParams = params
                            sv_video_user.setImageBitmap(Bitmap.createScaledBitmap(it,BitmapUtils.MINWIDTH,BitmapUtils.MINHEIGHT,false))
                        }else{
                            if(square.sVideoWidth.isNotEmpty()&&square.sVideoHeight.isNotEmpty()){
                                var sWidth = square.sVideoWidth.toInt()
                                var sHeight = square.sVideoHeight.toInt()
                                sv_video_user.setImageBitmap(Bitmap.createScaledBitmap(it,sWidth,sHeight,false))
                            }else{
                                sv_video_user.setImageBitmap(it)
                            }

//                            params.width = WindowManager.LayoutParams.MATCH_PARENT
//                            sv_video_user.layoutParams = params
//                            sv_video_user.setImageBitmap(it)
                        }
                    }
                }
            })
            Log.i("usertrendView","动态类型=${square.iResourceType},音频所属人${square.name},音频链接=${square.sVoiceUrl}")
        }else if(square.iResourceType==4){
            rv_images.visibility = View.GONE
            rl_vidoe_user.visibility = View.GONE

            rl_root_audio.visibility = View.VISIBLE

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

            Log.i("usertrendView","动态类型=${square.iResourceType},音频所属人${square.name},音频链接=${square.sVoiceUrl}")
        }else{
            rl_vidoe_user.visibility = View.GONE
            rl_root_audio.visibility = View.GONE
//            if(square.iResourceType==2||square.iResourceType==1){

            if (square.sSourceSquarePics.isNullOrEmpty()) {
                rv_images.gone()
            } else {
                rv_images.visible()
            }
            mImages.clear()
            val images = square.sSourceSquarePics?.split(",")
            if (images != null) {
                mImages.addAll(images.toList())
            }
            val d = rv_images.getItemDecorationAt(0)
            if (d != null) {
                rv_images.removeItemDecoration(d)
            }
            if (mImages.size == 1 || mImages.size == 2 || mImages.size == 4) {
                rv_images.layoutManager = GridLayoutManager(context, 2)
                rv_images.addItemDecoration(RxRecyclerViewDividerTool(dip(2)))
            } else {
                rv_images.layoutManager = GridLayoutManager(context, 3)
                rv_images.addItemDecoration(RxRecyclerViewDividerTool(dip(2)))//SpacesItemDecoration(dip(4),3)
            }
            imageAdapter.notifyDataSetChanged()
//            }else
        }

        if(TextUtils.isEmpty(square.sTopicName)){
            tv_topic_name.visibility = View.GONE
        }else{
            tv_topic_name.visibility = View.VISIBLE
            tv_topic_name.text = square.sTopicName
        }

        if(TextUtils.isEmpty(square.city)){
            tv_square_city.visibility = View.GONE
        }else{
            tv_square_city.visibility = View.VISIBLE
            tv_square_city.text = square.city
        }

//        tv_appraise.text = square.appraiseCount.toString()
        tv_appraise.isSelected = TextUtils.equals(square.isupvote,"1")
//        tv_comment.text = square.commentCount.toString()
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

        tv_redflower.text = if((square.iLovePoint ?:0)> 0){
            "${square.iLovePoint}"
        }else{
            ""
        }

//        if(TextUtils.equals(square.userid,userId)){
//            tv_redflower.visibility = View.GONE
//        }else{
//            tv_redflower.visibility = View.VISIBLE
//        }

        tv_redflower.isSelected = if ((square.iSendLovePoint?:0) > 0) {
            true
        } else {
            false
        }

        ll_comments.visibility = View.GONE

//        square.commentCount?.let {
//            if (it > 0) {
//                ll_comments.visible()
//                rv_comment.visible()
//            } else {
//                rv_comment.gone()
//                ll_comments.gone()
//            }
//            if (it > 2) {
//                tv_all_comment.text = String.format("查看全部%s条评论",it)
//                tv_all_comment.visible()
//            } else {
//                tv_all_comment.gone()
//            }
//        }
//        mComments.clear()
//        if (square.comments != null) {
//            mComments.addAll(square.comments)
//        }
//        commentAdapter.notifyDataSetChanged()

        initGiftControl()
    }

    private fun showGiftDialog(receivedUserId:String,squareId:String,username:String){
        var mSelectGiftListDialog = SelectGiftListDialog()
        mSelectGiftListDialog.arguments= bundleOf("titleStype" to 3,"receiveUserId" to "${receivedUserId}","squareId" to squareId)
        mSelectGiftListDialog.setDialogListener { p, s ->
            RongIM.getInstance().startConversation((context as BaseActivity), Conversation.ConversationType.PRIVATE, "${receivedUserId}", "${username}")
        }
        mSelectGiftListDialog.show((context as BaseActivity).supportFragmentManager,"gift")
    }
    //礼物
    private var giftControl: GiftControl? = null

    fun initGiftControl(){
        if(giftControl==null){
            giftControl = GiftControl(context)
            giftControl?.let {
                it.setGiftLayout(usersquare_gift_parent, 1)
                        .setHideMode(false)
                        .setCustormAnim(CustormAnim())
                it.setmGiftAnimationEndListener {
                    var lovePoint = it
                    square?.let {
                        it.desc = Desc
                        sendFlowerAction?.onSendFlowerClicker(it,lovePoint)
                    }
                    if(TextUtils.equals(getUserSex(),"0")){
                        sendLoveHeartNums = 1
                    }else{
                        sendLoveHeartNums = 10
                    }
                }
            }
        }
    }

    private fun doLoveHeartAnimation(){
        usersquare_loveheart.showAnimationRedHeart(tv_redflower)
    }

    private var Desc:String=""
    //连击礼物数量
    fun addGiftNums(giftnum: Int, currentStart: Boolean = false,JumpCombo:Boolean = false,desc:String) {
        if (giftnum == 0) {
            return
        } else {
            giftControl?.let {
                //这里最好不要直接new对象
                var giftModel = GiftModel()
                giftModel.setGiftId("礼物Id").setGiftName("礼物名字").setGiftCount(giftnum).setGiftPic("")
                        .setSendUserId("1234").setSendUserName("吕靓茜").setSendUserPic("").setSendGiftTime(System.currentTimeMillis())
                        .setCurrentStart(currentStart)
                Desc = desc
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

    fun hide(@IdRes viewIdRes: Int) {
        find<View>(viewIdRes).gone()
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


    fun setFlowerClick(flower:(square:Square,lovePoint:Int)->Unit){
        this.sendFlowerAction = object : SendFlowerClickListener{
            override fun onSendFlowerClicker(square: Square,lovePoint:Int) {
                flower(square,lovePoint)
            }
        }
    }

    fun setPraiseClick(action:(square:Square)->Unit){
        this.action = object : PraiseClickListener{
            override fun onPraiseClick(square: Square) {
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

    fun setDeleteClick(action:(square:Square)->Unit){
        this.deleteAction = object : DeleteClick{
            override fun onDelete(square: Square) {
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
    private var sendFlowerAction:SendFlowerClickListener?=null
    private var mTogglePlay:TogglePlay?=null

    interface SendFlowerClickListener{
        fun onSendFlowerClicker(square:Square,lovePoint:Int)
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

    interface TogglePlay{
        fun onTogglePlay(square: Square)
    }

    private var textClickedListener: CustomLinkMovementMethod.TextClickedListener? = null
    fun setOnCommentClick(l:()->Unit) {
        textClickedListener = CustomLinkMovementMethod.TextClickedListener { l() }
    }
}