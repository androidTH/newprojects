package com.d6zone.android.app.widget

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.d6zone.android.app.R
import com.d6zone.android.app.activities.D6LoveHeartListActivity
import com.d6zone.android.app.activities.SimplePlayer
import com.d6zone.android.app.activities.UserInfoActivity
import com.d6zone.android.app.adapters.SquareImageAdapter
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.dialogs.SelectGiftListDialog
import com.d6zone.android.app.dialogs.SendLoveHeartDialog
import com.d6zone.android.app.dialogs.SendRedHeartEndDialog
import com.d6zone.android.app.dialogs.UnKnowInfoDialog
import com.d6zone.android.app.models.Square
import com.d6zone.android.app.utils.*
import com.d6zone.android.app.widget.frescohelper.FrescoUtils
import com.d6zone.android.app.widget.frescohelper.IResult
import com.d6zone.android.app.widget.gift.CustormAnim
import com.d6zone.android.app.widget.gift.GiftControl
import com.d6zone.android.app.widget.gift.GiftModel
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.item_audio.view.*
import kotlinx.android.synthetic.main.view_trend_detail_view.view.*
import org.jetbrains.anko.*
import java.lang.Exception

/**
 * Created on 2017/12/17.
 */
class TrendDetailView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var square: Square? = null
    private val mImages = ArrayList<String>()
    private val imageAdapter by lazy {
        SquareImageAdapter(mImages,1)
    }

    private var localLoveHeartNums = SPUtils.instance().getInt(Const.User.USERLOVE_NUMS, 0)
    private var sendLoveHeartNums = if(TextUtils.equals(getUserSex(),"0")){
        1
    }else{
        10
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_trend_detail_view, this, true)

        rv_images.setHasFixedSize(true)
//        rv_images.layoutManager = GridLayoutManager(context,3)
        rv_images.adapter = imageAdapter
//        rv_images.addItemDecoration(SpacesItemDecoration(dip(4),3))

        root_commentlayout.setOnClickListener {
            square?.let {
                actiToggleSoftClick?.onToggleSoftInput(it)
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
                    mUnknowDialog.arguments = bundleOf("otheruserId" to "${id}")
                    mUnknowDialog.show((context as BaseActivity).supportFragmentManager,"unknowDialog")
                }else{
                    context.startActivity<UserInfoActivity>("id" to id)
                }
            }
        }

        tv_squaredetails_gift.setOnClickListener {
            (context as BaseActivity).isAuthUser(){
                if(!TextUtils.equals(getLocalUserId(),"${square?.userid}")){
                    square?.let {
                        showGiftDialog("${it.userid}","${it.id}","${it.name}")
                    }
                }else{
                    (context as BaseActivity).toast(context.getString(R.string.string_send_gift))
                }
            }
        }

        tv_redflower.setOnClickListener {
            (context as BaseActivity).isAuthUser(){
                if(!TextUtils.equals(getLocalUserId(),"${square?.userid}")){
                    if(localLoveHeartNums>0){
                        if(sendLoveHeartNums <= localLoveHeartNums){
                            if(TextUtils.equals(getUserSex(),"0")){
                                sendLoveHeartNums = sendLoveHeartNums+1
                                addGiftNums(1, false, false,"")
                            }else{
                                sendLoveHeartNums = sendLoveHeartNums+10
                                addGiftNums(10, false, true,"")
                            }
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
            }
        }

        tv_redflower.setOnLongClickListener {
            (context as BaseActivity).isAuthUser(){
                square?.let {
                    var mSendLoveHeartDialog = SendLoveHeartDialog()
                    mSendLoveHeartDialog.arguments = bundleOf("userId" to "${it.userid}","iIsAnonymous" to "${it.iIsAnonymous}")
                    mSendLoveHeartDialog.setDialogListener { p, s ->
                        addGiftNums(p, false, true,"${s}")
                    }
                    mSendLoveHeartDialog.show((context as BaseActivity).supportFragmentManager, "sendloveheartDialog")
                }
            }
            true
        }

        tv_comment.setOnClickListener {
            square?.let {
                actionCommentClick?.onCommentClick(it)
            }
        }

        tv_delete.setOnClickListener {
            square?.let {
                mDeleteClick?.onDelete(it)
            }
        }

        rl_play_audio.setOnClickListener {
          square?.let {
              mTogglePlay?.onTogglePlay(it)
          }
        }

        rl_vidoe_details.setOnClickListener {
            square?.let {
                (context as BaseActivity).startActivity<SimplePlayer>("videoPath" to it.sVideoUrl,"videoType" to "1")
            }
        }

        rl_squaredetails_bg_layout.setOnClickListener {
            square?.let {
                var mPageIndex = it.orderType-1
                if(it.orderType<0){
                    mPageIndex = 0
                }
                (context as BaseActivity).startActivity<D6LoveHeartListActivity>("pageIndex" to mPageIndex)
            }
        }
    }

    private fun showGiftDialog(receivedUserId:String,squareId:String,username:String){
        var mSelectGiftListDialog = SelectGiftListDialog()
        mSelectGiftListDialog.arguments= bundleOf("titleStype" to 3,"receiveUserId" to "${receivedUserId}","squareId" to squareId)
        mSelectGiftListDialog.setDialogListener { p, s ->
            RongIM.getInstance().startConversation((context as BaseActivity), Conversation.ConversationType.PRIVATE, "${receivedUserId}", "${username}")
        }
        mSelectGiftListDialog.show((context as BaseActivity).supportFragmentManager,"gift")
    }

    fun update(square: Square) {
        this.square = square
        imageAdapter.bindSquare(square)
//        headView.hierarchy = getHierarchy(square.sex.toString())
        headView.setImageURI(square.picUrl)
        tv_name.text = square.name
        tv_sex.isSelected = if(TextUtils.equals("0",square.sex)) true else false
        tv_age.isSelected = if(TextUtils.equals("0",square.sex)) true else false
        if(square.age.isNullOrEmpty()){
            tv_age.visibility = View.GONE
        }else{
            tv_age.visibility = View.VISIBLE
            tv_age.text = "${square.age}岁"
        }

        if(TextUtils.equals("3",square.screen)){
            img_auther.visibility= View.GONE
            img_auther.setImageResource(R.mipmap.renzheng_small)
        }else if(TextUtils.equals("1",square.screen)){
            img_auther.visibility= View.VISIBLE
            img_auther.setImageResource(R.mipmap.video_small)
        }else{
            img_auther.visibility= View.GONE
        }

        if(square.rankOrder==2&&TextUtils.equals("0",square.sex)){
            rl_squaredetails_bg_layout.visibility = View.VISIBLE
            if(square.orderNum!=0){
                tv_squaredetails_bd_pisition.text = "魅力榜·排名第${square.orderNum}"
            }else{
                tv_squaredetails_bd_pisition.text = "魅力榜"
            }

            squaredetails_bd_headView.setImageURI(square.picUrl)
            tv_squaredetails_bd_username.text = "${square.name}"
            tv_squaredetails_bd_usersex.isSelected = TextUtils.equals("0",square.sex)

            if(square.age.isNullOrEmpty()){
                tv_squaredetails_bd_userage.visibility = View.GONE
            }else{
                tv_squaredetails_bd_userage.visibility = View.VISIBLE
                tv_squaredetails_bd_userage.text = "${square.age}岁"
            }

            tv_squaredetails_bd_uservip.backgroundDrawable = getLevelDrawable(square.userclassesid.toString(),context)
            if(square.lovePointNum!=0){
                tv_squaredetails_bd_show.text = "收到${square.lovePointNum}[img src=redheart_small/]"
            }
            tv_squaredetails_click_bangdan.text = "送[img src=liwu_list_g/]或[img src=small_gray_like/]即可为我打榜哦"
        }else{
            rl_squaredetails_bg_layout.visibility = View.GONE
            tv_squaredetails_click_bangdan.text = ""
        }

        if (square.userclassesname.toString().startsWith("入门")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_cj)
        } else if (square.userclassesname.toString().startsWith("中级")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_zj)
        } else if (square.userclassesname.toString().startsWith("优质")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_gj)
        } else if (square.userclassesname.toString().startsWith("普通")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
        } else if (square.userclassesname.toString().startsWith("白银")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
        } else if (square.userclassesname.toString().startsWith("黄金")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
        } else if (square.userclassesname.toString().startsWith( "钻石")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
        } else if (square.userclassesname.toString().startsWith("私人")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_private)
        }else if (square.userclassesname.toString().startsWith("游客")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.youke_icon)
        }else if (square.userclassesname.toString().startsWith("入群")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.ruqun_icon)
        }else if (square.userclassesname.toString().startsWith("app")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.app_vip)
        }

//        val sub = if (square.city.isNullOrEmpty()) {
//            square.updatetime?.interval()
//        } else {
//            String.format("%s | %s",square.updatetime?.interval(),square.city)
//        }
        try{
            tv_sub_title.text = square.updatetime?.interval()
        }catch (e:Exception){
            e.printStackTrace()
        }
        tv_content.text = square.content

        if(square.iResourceType==3){
            rv_images.visibility = View.GONE
            rl_root_audio.visibility = View.GONE

            rl_vidoe_details.visibility = View.VISIBLE

            FrescoUtils.loadImage(context,square.sVideoPicUrl,object: IResult<Bitmap> {
                override fun onResult(result: Bitmap?) {
                    result?.let {
//                        var params = sv_video_details.layoutParams
//                        params.height =  WindowManager.LayoutParams.WRAP_CONTENT
                        if(it.height>it.width){
//                            params.width = WindowManager.LayoutParams.WRAP_CONTENT
//                            sv_video_details.layoutParams = params
                            sv_video_details.setImageBitmap(Bitmap.createScaledBitmap(it,BitmapUtils.MINWIDTH,BitmapUtils.MINHEIGHT,false))
                        }else{
                            if(square.sVideoWidth.isNotEmpty()&&square.sVideoHeight.isNotEmpty()){
                                var sWidth = square.sVideoWidth.toInt()
                                var sHeight = square.sVideoHeight.toInt()
                                sv_video_details.setImageBitmap(Bitmap.createScaledBitmap(it,sWidth,sHeight,false))
                            }else{
                                sv_video_details.setImageBitmap(it)
                            }
//                            params.width = WindowManager.LayoutParams.MATCH_PARENT
//                            sv_video_details.layoutParams = params
//                            sv_video_details.setImageBitmap(it)
                        }
                    }
                }
            })

        }else if(square.iResourceType==4){
            rv_images.visibility = View.GONE
            rl_vidoe_details.visibility = View.GONE

            if(!TextUtils.isEmpty(square.sVoiceUrl)){
                rl_root_audio.visibility = View.VISIBLE
            }else{
                rl_root_audio.visibility = View.GONE
            }
            if (!TextUtils.equals("", square.sVoiceLength)) {
                var voicelength:Int
                try {
                    voicelength = square.sVoiceLength.toInt()
                } catch (e: Exception) {
                    voicelength = 0
                }
                var param = rl_play_audio.layoutParams
                param.width = (resources.getDimensionPixelSize(R.dimen.width_100) + resources.getDimensionPixelSize(R.dimen.width_100) / 60 * voicelength)
                rl_play_audio.layoutParams = param
                tv_audio_time.text = "${square.sVoiceLength}”"
            } else {
                tv_audio_time.text = "0”"
            }
        }else{
            rl_vidoe_details.visibility = View.GONE
            rl_root_audio.visibility = View.GONE

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
                rv_images.addItemDecoration(SpacesItemDecoration(dip(4), 2))
            } else {
                rv_images.layoutManager = GridLayoutManager(context, 3)
                rv_images.addItemDecoration(SpacesItemDecoration(dip(4), 3))
            }
            imageAdapter.notifyDataSetChanged()
            Log.i("trendView","${square.content},图片位置：${square.sIfLovePics}")
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

        tv_redflower.text = if((square.iLovePoint?:0)>0){
            "${square.iLovePoint}"
        }else{
            ""
        }

        tv_redflower.isSelected = if ((square.iSendLovePoint?:0) > 0) {
            true
        } else {
            false
        }
    }

    //礼物
    private var giftControl: GiftControl? = null

    fun initGiftControl(){
        if(giftControl==null){
            giftControl = GiftControl(context)
            giftControl?.let {
                it.setGiftLayout(squaredetails_gift_parent, 1)
                        .setHideMode(false)
                        .setCustormAnim(CustormAnim())
                it.setmGiftAnimationEndListener {
                    var lovePoint = it
                    square?.let {
                        it.desc = sDesc
                        sendFlowerClick?.onSendFlowerClick(it,lovePoint)
                    }
                    localLoveHeartNums = localLoveHeartNums - lovePoint
                    if(localLoveHeartNums<=0){
                        localLoveHeartNums=0
                    }
                    if(TextUtils.equals(getUserSex(),"0")){
                        sendLoveHeartNums = 1
                    }else{
                        sendLoveHeartNums = 10
                    }
//                    var squareId = square?.let {
//                        it.id
//                    }
                }
            }
        }
    }

    private fun doLoveHeartAnimation(){
        squaredetails_loveheart.showAnimationRedHeart(tv_redflower)
    }

    private var sDesc:String = ""
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
                sDesc = desc
                if (currentStart) {
                    giftModel.setHitCombo(giftnum)
                }
                if(JumpCombo){
                    giftModel.setJumpCombo(giftnum)
                }
                it.loadGift(giftModel)
            }

            doLoveHeartAnimation()
        }
    }

    fun updateFlowerCount(square: Square){
//        tv_redflower.text = if((square.iFlowerCount?:0)>0){
//            square.iFlowerCount.toString()
//        }else{
//            ""
//        }
        tv_redflower.isSelected = if ((square.iSendLovePoint?:0) > 0) {
            true
        } else {
            false
        }

        tv_redflower.text = if((square.iLovePoint?:0)>0){
            "${square.iLovePoint}"
        }else{
            ""
        }
    }

    fun setPraiseClick(action:(square:Square)->Unit){
        this.action = object : Action{
            override fun onPraiseClick(square: Square) {
                action(square)
            }
        }
    }

    fun setOnSendFlowerClick(sendFlowerClick:(square:Square,lovePoint:Int)->Unit){
        this.sendFlowerClick = object :DoSendFlowerClick{
            override fun onSendFlowerClick(square: Square,lovePoint:Int) {
                sendFlowerClick(square,lovePoint)
            }
        }
    }

    fun setOnCommentClick(action:(square:Square)->Unit){
        this.actionCommentClick = object : DoCommentClick{
            override fun onCommentClick(square: Square) {
                action(square)
            }
        }
    }

    fun setOnSoftInputClick(action:(square:Square)->Unit){
        this.actiToggleSoftClick = object : onToggleSoftInput{
            override fun onToggleSoftInput(square: Square) {
                action(square)
            }
        }
    }

    fun setDeletClick(action:(square:Square)->Unit) {
        this.mDeleteClick = object : DeleteClick {
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

    private var action:Action?=null
    private var actionCommentClick:DoCommentClick?=null
    private var actiToggleSoftClick:onToggleSoftInput?=null
    private var sendFlowerClick:DoSendFlowerClick?=null
    private var mDeleteClick:DeleteClick?=null
    private var mTogglePlay:TogglePlay?=null

    interface Action{
        fun onPraiseClick(square: Square)
    }

    interface DoSendFlowerClick{
        fun onSendFlowerClick(square:Square,lovePoint:Int)
    }

    interface DoCommentClick{
        fun onCommentClick(square: Square)
    }

    interface  onToggleSoftInput{
        fun onToggleSoftInput(square: Square)
    }

    interface DeleteClick{
        fun onDelete(square: Square)
    }

    interface TogglePlay{
        fun onTogglePlay(square: Square)
    }
}