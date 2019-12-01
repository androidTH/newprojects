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
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.ImageFragment
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.photodrag.PhotoDragHelper
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.activity_image_pager.*
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
    private var PayPoint_Path = ""
    private var mBlurIndex = ArrayList<String>()

    private var mListFragment = SparseArray<Fragment>()

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
                mSendLoveHeartDialog.arguments = bundleOf("userId" to "${userId}")
                mSendLoveHeartDialog.setDialogListener { p, s ->
                    sendPayPoint(p)
//                    urls[mImageViewPager.currentItem] = url.replace(Const.BLUR_50,"?imageslim")
                    //(mImageViewPager.adapter as ImagePagerAdapter).setListBlur(mBlurIndex)
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
            if(!TextUtils.equals(userId, getLocalUserId())){
                var sIflovepics = intent.getStringExtra(SIfLovePics)
                mBlurIndex.addAll(sIflovepics.split(",").toList())
                showPayPoints(position)
            }else{
                rl_paypoints.visibility = View.GONE
                rl_tips.visibility = View.GONE
            }
        }
        urls.forEach {
            var url = it
            if(it.contains(Const.Pic_Thumbnail_Size_wh300)){
                url = it.replace(Const.Pic_Thumbnail_Size_wh300,"")
            }else if(it.contains(Const.Pic_Thumbnail_Size_wh400)){
                url = it.replace(Const.Pic_Thumbnail_Size_wh400,"")
            }
            if(mBlurIndex!=null&&mBlurIndex.size>0){
                if(TextUtils.equals("2",mBlurIndex[key])){
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
        if(!TextUtils.equals(userId, getLocalUserId())){
            showPayPoints(position)
            urls.let {
                PayPoint_Path = urls[position]
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun showPayPoints(position: Int){
        if(mBlurIndex!=null&&mBlurIndex.size>0){
            if(TextUtils.equals("2",mBlurIndex[position])){
                rl_paypoints.visibility = View.VISIBLE
                rl_tips.visibility = View.VISIBLE
            }else{
                rl_paypoints.visibility = View.GONE
                rl_tips.visibility = View.GONE
            }
        }
    }

    private fun sendPayPoint(loveHeartNums:Int){
        Request.sendLovePoint(getLoginToken(), "${userId}", loveHeartNums, 1,"${PayPoint_Path}").request(this, false, success = { _, data ->
            rl_paypoints.visibility = View.GONE
            rl_tips.visibility = View.GONE
            mBlurIndex[mImageViewPager.currentItem] = "1"
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
    }

}
