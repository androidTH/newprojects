package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.SparseArray
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.ImageNewPagerAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.SendLoveHeartDialog
import com.d6.android.app.dialogs.SendRedHeartEndDialog
import com.d6.android.app.eventbus.BlurMsgEvent
import com.d6.android.app.eventbus.FlowerMsgEvent
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.ImageFragment
import com.d6.android.app.models.Square
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.gift.CustormAnim
import com.d6.android.app.widget.gift.GiftControl
import com.d6.android.app.widget.gift.GiftModel
import com.d6.android.app.widget.photodrag.PhotoDragHelper
import com.facebook.drawee.backends.pipeline.Fresco
import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber
import kotlinx.android.synthetic.main.activity_image_pager.*
import kotlinx.android.synthetic.main.activity_image_pager.ll_gift_parent
import kotlinx.android.synthetic.main.activity_image_pager.loveheart
import kotlinx.android.synthetic.main.activity_qr.*
import kotlinx.android.synthetic.main.fragment_date.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

/**
 * 广场照片详情页
 */
class ImagePagerActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    private var urls = ArrayList<String>()
    private var userData:UserData?=null
    private var userId:String=""
    private var squareId:String=""
    private var PayPoint_Path = ""
    private lateinit var mSquare: Square
    private var mBlurIndex = ArrayList<String>()
    private var mFirePicsIndex = ArrayList<String>()

    private var mListFragment = SparseArray<Fragment>()
    //礼物
    private var giftControl: GiftControl? = null
    private var iIsAnonymous:String = "2"
    private var timer:CountDownTimer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_pager)
        noTitleBar()
        tv_close.setOnClickListener {
            onBackPressed()
        }

        tv_delete.setOnClickListener {
            val p = mImageViewPager.currentItem
            urls?.let {
                if(it.size>p){
                    delete(it[p])
                }
            }
        }

        tv_paypoints.setOnClickListener {
            isAuthUser() {
                var mSendLoveHeartDialog = SendLoveHeartDialog()
                mSendLoveHeartDialog.arguments = bundleOf("userId" to "${userId}", "ToFromType" to 2,"iIsAnonymous" to iIsAnonymous)
                mSendLoveHeartDialog.setDialogListener { p, s ->
                    sendPayPoint(p)
                }
                mSendLoveHeartDialog.show(supportFragmentManager, "sendloveheartDialog")
            }
        }

        photo_drag_relaivelayout.setDragListener(PhotoDragHelper().setOnDragListener(object : PhotoDragHelper.OnDragListener{

            override fun getDragView(): View {
                return mImageViewPager
            }

            override fun onAlpha(alpha: Float) {
                photo_drag_relaivelayout.setAlpha(alpha)
            }

            override fun onAnimationEnd(mSlop: Boolean) {
                if (mSlop) {
                    onBackPressed()
                }
            }
        }))

        initData()
        tv_close.postDelayed(object:Runnable{
            override fun run() {
                initGift()
            }
        },200)

    }
    var key = 0
    private fun initData() {
        val position = intent.getIntExtra(CURRENT_POSITION, 0)
        urls = intent.getStringArrayListExtra(URLS)
        val isBlur = intent.getBooleanExtra("isBlur", false)
        tv_pages.text = String.format("%d/%d", position + 1, urls!!.size)
        val showDelete = intent.getBooleanExtra("delete",false)
        if(intent.hasExtra(ISANONYMOUS)){
            iIsAnonymous = intent.getStringExtra(ISANONYMOUS)
        }
        if (showDelete) {
            tv_delete.visible()
        } else {
            tv_delete.gone()
        }
        if (intent.hasExtra("data")) {
            userData = intent.getSerializableExtra("data") as UserData
        }
        if(intent.hasExtra("userId")){
            userId = intent.getStringExtra(USERID)
            squareId = intent.getStringExtra(SOURCEID)
            mSquare = intent.getSerializableExtra(mBEAN) as Square
            var sIflovepics = intent.getStringExtra(SIfLovePics)
            mBlurIndex.addAll(sIflovepics.split(",").toList())

            var sIfSeepics = intent.getStringExtra(SIfSeePics)
            mFirePicsIndex.addAll(sIfSeepics.split(",").toList())

            showPayPoints(position)
        }
        urls.forEach {
            var url = it
            if(it.contains(Const.Pic_Thumbnail_Size_wh300)){
                url = it.replace(Const.Pic_Thumbnail_Size_wh300,"")
            }else if(it.contains(Const.Pic_Thumbnail_Size_wh400)){
                url = it.replace(Const.Pic_Thumbnail_Size_wh400,"")
            }
            if (mBlurIndex != null && mBlurIndex.size > key) {
                if (TextUtils.equals("2", mBlurIndex[key]) && !TextUtils.equals(userId, getLocalUserId())) {
                    url = it.replace("?imageslim", Const.BLUR_60)
                }
            }

            var isFirePic = false
            if(mFirePicsIndex!=null&&mFirePicsIndex.size>key){
                if(TextUtils.equals("2",mFirePicsIndex[key])){
                    url = it.replace("?imageslim",Const.BLUR_60)
                }else if(TextUtils.equals("3",mFirePicsIndex[key])){
                    if(TextUtils.equals("2",mBlurIndex[key])&&TextUtils.equals(userId, getLocalUserId())){
                        isFirePic = true
                    }else if(!TextUtils.equals("2",mBlurIndex[key])){
                        isFirePic = true
                    }
                }
            }
            Log.i("ImagePagerAdapter", "图片大小${url}")
            mListFragment.put(key++, ImageFragment.newInstance(url, false,isFirePic))
        }

        val adapter = ImageNewPagerAdapter(supportFragmentManager,mListFragment)
        mImageViewPager.adapter = adapter
        if(urls!=null){
            mImageViewPager.offscreenPageLimit = urls.size
        }
        mImageViewPager.addOnPageChangeListener(this)
        mImageViewPager.currentItem = position

        urls.let {
            //            PayPoint_Path = urls[position].replace("?imageslim", "")
            if(urls!=null&&urls.size>0){
                if(urls[position].contains("?")){
                    PayPoint_Path = urls[position].split("?")[0]
                }else{
                    PayPoint_Path = urls[position]
                }
            }
        }
    }

    fun startCountDownTimer(){
        rl_firepics.visibility = View.GONE
        rl_countdowntimer.visibility = View.GONE
        rl_firepics_tips.visibility = View.GONE
        if(mFirePicsIndex!=null&&mFirePicsIndex.size>0){
            var index=mImageViewPager.currentItem
            var fireType = mFirePicsIndex[index]
            if(TextUtils.equals("2",fireType)){
                var url = urls[index]
                var mImageLocal = mListFragment.get(mImageViewPager.currentItem) as ImageFragment
                mImageLocal.updatePicUrl(this@ImagePagerActivity,url,false)

                if(timer!=null){
                    timer?.let {
                        it.cancel()
                        timer=null
                    }
                }
                if(timer==null){
                    rl_firepics.visibility = View.GONE
                    timer = object : CountDownTimer(4000, 1000) {
                        override  fun onTick(millisUntilFinished: Long) {
                            if(millisUntilFinished>=1000){
                                rl_countdowntimer.visibility = View.VISIBLE
                                var str = "剩余 ${millisUntilFinished/1000}s"
                                var style = SpannableStringBuilder(str)
                                style.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ImagePagerActivity,R.color.color_F7AB00)), str.length-2, str.length-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                tv_countdown.text = style
                            }else{
                                timer?.let {
                                    it.cancel()
                                }
                                doFirePicsBg()
                            }
                        }

                        override fun onFinish() {
                            doFirePicsBg()
                        }
                    }
                    timer?.let {
                        it.start()
                    }
                }
            }else{
                rl_firepics.visibility = View.GONE
                if(timer!=null){
                    timer?.let {
                        it.cancel()
                        timer=null
                    }
                }
            }
        }
    }

    fun cancelTimer(){
        if(timer!=null){
            timer?.let {
                it.cancel()
                timer=null
            }
            doFirePicsBg()
        }
    }

    private fun doFirePicsBg(){
        if(urls!=null&&urls.size>0){

            mFirePicsIndex[mImageViewPager.currentItem] = "3"
            rl_countdowntimer.visibility = View.GONE
            rl_firepics.visibility = View.VISIBLE

            var mImageLocal = mListFragment.get(mImageViewPager.currentItem) as ImageFragment
            mImageLocal.doFirePics(true)
            UpdateUserFirePic(PayPoint_Path)

            var sb = StringBuffer()
            mFirePicsIndex.forEach {
                sb.append(it).append(",")
            }
            sb.deleteCharAt(sb.length - 1)
            mSquare.sIfSeePics = sb.toString()

            var intent = Intent(Const.SQUARE_MESSAGE)
            intent.putExtra("bean",mSquare)
            sendBroadcast(intent)
        }
    }

    private fun UpdateUserFirePic(sPicUrl:String){
        Request.insertUserVisitPic(sPicUrl).request(this,false,success={data,_msg->

        }){code,msg->
            toast(msg)
        }
    }

    private fun delete(url:String) {
        userData?.let {user->
            val s = urls?:ArrayList()
            if (s.isNotEmpty()) {
                s.remove(url)
            }
            val imgs = StringBuilder()
            s.forEach {
                imgs.append(it).append(",")
            }
            if (imgs.isNotEmpty()) {
                imgs.deleteCharAt(imgs.length-1)
            }
            dialog()
            user.userpics = imgs.toString()
            Request.updateUserInfo(user).request(this){_,data->
                data?.let {
                    setResult(Activity.RESULT_OK,Intent().putExtras(bundleOf("data" to it)))
                }
                onBackPressed()
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        tv_pages.text = String.format("%d/%d", position + 1, urls!!.size)
        urls.let {
            //            PayPoint_Path = urls[position].replace("?imageslim", "")
            if(urls!=null&&urls.size>0){
                if(urls[position].contains("?")){
                    PayPoint_Path = urls[position].split("?")[0]
                }else{
                    PayPoint_Path = urls[position]
                }
            }
        }

        showPayPoints(position)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun showPayPoints(position: Int){
        if(mBlurIndex!=null&&mBlurIndex.size>0){
            var blurType = mBlurIndex[position]
            if(TextUtils.equals("2",blurType)){//2 不可见
                rl_paypoints.visibility = View.VISIBLE
                rl_tips.visibility = View.VISIBLE
                if(!TextUtils.equals(userId, getLocalUserId())){
//                    tv_tips.text = "打赏后可见"
                    iv_unflock.visibility = View.GONE
                    rl_tips.visibility = View.GONE
                    rl_countdowntimer.visibility = View.GONE
                    rl_firepics.visibility = View.GONE
                    rl_firepics_tips.visibility = View.GONE
                }else{
                    rl_paypoints.visibility = View.GONE
                    rl_tips.visibility = View.VISIBLE
                    tv_tips.text = "该图片设置了打赏后可见，别人打赏才能查看"
                    showFirePics(position)
                }
            }else if(TextUtils.equals("3",blurType)){//3 可见
                rl_paypoints.visibility = View.GONE
                rl_tips.visibility = View.VISIBLE
                if(!TextUtils.equals(userId, getLocalUserId())){
//                    tv_tips.text = "解锁状态"
                    iv_unflock.visibility = View.VISIBLE
                    rl_tips.visibility = View.GONE
                }else{
                    tv_tips.text = "该图片设置了打赏后可见，别人打赏才能查看"
                }
                showFirePics(position)
            }else{
                rl_paypoints.visibility = View.GONE
                rl_tips.visibility = View.GONE
                rl_countdowntimer.visibility = View.GONE
                iv_unflock.visibility = View.GONE

                showFirePics(position)
            }
        }else{
            rl_paypoints.visibility = View.GONE
            showFirePics(position)
        }
    }

    private fun showFirePics(position: Int){
        if(mFirePicsIndex!=null&&mFirePicsIndex.size>position){
            var firePicsIndex = mFirePicsIndex[position]
            if(TextUtils.equals("3",firePicsIndex)){
                rl_firepics_tips.visibility = View.GONE
                rl_firepics.visibility = View.VISIBLE
            }else if(TextUtils.equals("2",firePicsIndex)) {//2 不可见
                rl_firepics_tips.visibility = View.VISIBLE
                rl_firepics.visibility = View.GONE
            }else if(TextUtils.equals("1",firePicsIndex)){
                rl_firepics_tips.visibility = View.GONE
                rl_firepics.visibility = View.GONE
            }
        }
    }

    private fun sendPayPoint(loveHeartNums:Int){
        Request.sendLovePoint(getLoginToken(), "${userId}", loveHeartNums, 5,"${squareId}","${PayPoint_Path}").request(this, false, success = { _, data ->
            //            rl_paypoints.visibility = View.GONE
//            rl_tips.visibility = View.GONE
//            iv_unflock.visibility = View.VISIBLE

            mBlurIndex[mImageViewPager.currentItem] = "3"
            showPayPoints(mImageViewPager.currentItem)

            var sb = StringBuffer()
            mBlurIndex.forEach {
                sb.append(it).append(",")
            }
            sb.deleteCharAt(sb.length - 1)
            mSquare.sIfLovePics = sb.toString()
            mSquare.iLovePoint = mSquare.iLovePoint?.let {
                it+loveHeartNums
            }

            var intent = Intent(Const.SQUARE_MESSAGE)
            intent.putExtra("bean",mSquare)
            sendBroadcast(intent)

            if(TextUtils.equals("1",mFirePicsIndex[mImageViewPager.currentItem])){
                var url = urls[mImageViewPager.currentItem]
                var mImageLocal = mListFragment.get(mImageViewPager.currentItem) as ImageFragment
                mImageLocal.updatePicUrl(this@ImagePagerActivity,url,false)
            }

        }) { code, msg ->
            if (code == 2||code==3) {
                var mSendRedHeartEndDialog = SendRedHeartEndDialog()
                mSendRedHeartEndDialog.show(supportFragmentManager, "redheartendDialog")
            }else{
                toast(msg)
            }
        }

        addGiftNums(loveHeartNums,false, true)
    }

    private fun initGift() {
        giftControl = GiftControl(this)
        giftControl?.let {
            it.setGiftLayout(ll_gift_parent, 1)
                    .setHideMode(false)
                    .setCustormAnim(CustormAnim())
            it.setmGiftAnimationEndListener {

            }
        }
    }

    //连击礼物数量
    private fun addGiftNums(giftnum: Int, currentStart: Boolean = false, JumpCombo: Boolean = false) {
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
                if (JumpCombo) {
                    giftModel.setJumpCombo(giftnum)
                }
                it.loadGift(giftModel)
                Log.d("TAG", "onClick: " + it.getShowingGiftLayoutCount())
            }
            doAnimation()
        }
    }

    private fun doAnimation() {
        loveheart.showAnimationRedHeart(null)
    }

    override fun onBackPressed() {
        urls.clear()
        Fresco.getImagePipeline().clearMemoryCaches()
        photo_drag_relaivelayout.removeAllViews()
        Log.i("onBackPressed", "------")
        finish()
        overridePendingTransition(R.anim.img_fade_in, R.anim.img_fade_out)
    }

    override fun onDestroy() {
        try {
            if (mImageViewPager != null) {
                mImageViewPager!!.removeOnPageChangeListener(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    companion object {
        val CURRENT_POSITION = "position"
        val URLS = "urls"
        val USERID = "userId"
        val SIfLovePics = "sIfLovePics"
        val SIfSeePics = "sIfSeePics"
        val SOURCEID = "sourceId"
        val mBEAN = "bean"
        val ISANONYMOUS="iIsAnonymous"
    }

}
