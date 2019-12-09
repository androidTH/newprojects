package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.KeyEvent
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.ImageNewPagerAdapter
import com.d6.android.app.adapters.ImagePagerAdapter
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
import kotlinx.android.synthetic.main.activity_image_pager.*
import kotlinx.android.synthetic.main.activity_image_pager.ll_gift_parent
import kotlinx.android.synthetic.main.activity_image_pager.loveheart
import kotlinx.android.synthetic.main.activity_qr.*
import kotlinx.android.synthetic.main.fragment_date.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.toast
import java.lang.StringBuilder

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

    private var mListFragment = SparseArray<Fragment>()
    //礼物
    private var giftControl: GiftControl? = null

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
                mSendLoveHeartDialog.arguments = bundleOf("userId" to "${userId}", "ToFromType" to 2)
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
            showPayPoints(position)
        }
        urls.forEach {
            var url = it
            if(it.contains(Const.Pic_Thumbnail_Size_wh300)){
                url = it.replace(Const.Pic_Thumbnail_Size_wh300,"")
            }else if(it.contains(Const.Pic_Thumbnail_Size_wh400)){
                url = it.replace(Const.Pic_Thumbnail_Size_wh400,"")
            }
            if(mBlurIndex!=null&&mBlurIndex.size>0){
                if(TextUtils.equals("2",mBlurIndex[key])&&!TextUtils.equals(userId, getLocalUserId())){
                    url = it.replace("?imageslim",Const.BLUR_50)
                }
            }
            Log.i("ImagePagerAdapter", "图片大小${url}")
            mListFragment.put(key++, ImageFragment.newInstance(url, false))
        }

        val adapter = ImageNewPagerAdapter(supportFragmentManager,mListFragment)
//        adapter.isBlur(isBlur)
//        adapter.setListBlur(mBlurIndex)
        mImageViewPager.adapter = adapter
        if(urls!=null){
            mImageViewPager.offscreenPageLimit = urls.size
        }
        mImageViewPager.addOnPageChangeListener(this)
        mImageViewPager.currentItem = position

        urls.let {
//            PayPoint_Path = urls[position].replace("?imageslim", "")
            PayPoint_Path = urls[position].split("?")[0]
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
        showPayPoints(position)
        urls.let {
            Log.i("imagepager","图片地址：${urls[position]}")
//            PayPoint_Path = urls[position].replace("?imageslim", "")
            PayPoint_Path = urls[position].split("?")[0]
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun showPayPoints(position: Int){
        if(mBlurIndex!=null&&mBlurIndex.size>0){
            var blurType = mBlurIndex[position]
            if(TextUtils.equals("2",blurType)){
                rl_paypoints.visibility = View.VISIBLE
                rl_tips.visibility = View.VISIBLE
                if(!TextUtils.equals(userId, getLocalUserId())){
//                    tv_tips.text = "打赏后可见"
                    iv_unflock.visibility = View.GONE
                    rl_tips.visibility = View.GONE
                }else{
                    rl_paypoints.visibility = View.GONE
                    rl_tips.visibility = View.VISIBLE
                    tv_tips.text = "该图片设置了打赏后可见，别人打赏才能查看"
                }
            }else if(TextUtils.equals("3",blurType)){
                rl_paypoints.visibility = View.GONE
                rl_tips.visibility = View.VISIBLE
                if(!TextUtils.equals(userId, getLocalUserId())){
//                    tv_tips.text = "解锁状态"
                    iv_unflock.visibility = View.VISIBLE
                    rl_tips.visibility = View.GONE
                }else{
                    tv_tips.text = "该图片设置了打赏后可见，别人打赏才能查看"
                }
            }else{
                rl_paypoints.visibility = View.GONE
                rl_tips.visibility = View.GONE
            }
        }else{
            rl_paypoints.visibility = View.GONE
        }
    }

    private fun sendPayPoint(loveHeartNums:Int){
        Request.sendLovePoint(getLoginToken(), "${userId}", loveHeartNums, 5,"${squareId}","${PayPoint_Path}").request(this, false, success = { _, data ->
            rl_paypoints.visibility = View.GONE
            rl_tips.visibility = View.GONE
            iv_unflock.visibility = View.VISIBLE

            mBlurIndex[mImageViewPager.currentItem] = "3"
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

            var url = urls[mImageViewPager.currentItem]
            var mImageLocal = mListFragment.get(mImageViewPager.currentItem) as ImageFragment
            mImageLocal.updatePicUrl(this@ImagePagerActivity,url,false)
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
        val SOURCEID = "sourceId"
        val mBEAN = "bean"
    }

}
