package com.d6.android.app.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.util.SparseArray
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.ImageLocalPagerAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.fragments.ImageLocalFragment
import com.d6.android.app.models.Imagelocals
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.setLeftDrawable
import com.d6.android.app.utils.visible
import com.d6.android.app.widget.photodrag.PhotoDragHelper
import kotlinx.android.synthetic.main.activity_localimage_pager.*
import com.d6.android.app.widget.ObserverManager
import me.nereo.multi_image_selector.MultiImageSelectorActivity
import me.nereo.multi_image_selector.utils.FinishActivityManager
import org.jetbrains.anko.startActivityForResult
import www.morefuntrip.cn.sticker.Bean.BLBeautifyParam

/**
 * 广场照片详情页
 */
class ImageLocalPagerActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    private var urls = ArrayList<String>()
    private var adapter: ImageLocalPagerAdapter? = null
    private var mListFragment = SparseArray<Fragment>()
    private var mHashMap = HashMap<Int,Boolean>()
    private var chooseCount = 0
    private var mNoChooseUrls = ArrayList<String>()
    private var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localimage_pager)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.colorPrimaryDark).statusBarDarkFont(false).init()
        tv_close.setOnClickListener {
            onBackPressed()
        }

        tv_dowork.setOnClickListener {
            if(type==0){
                var mImagelocals = Imagelocals(urls,1,0)
                ObserverManager.getInstance().notifyObservers(mImagelocals)
                finish()
            }else{
                FinishActivityManager.getManager().finishActivity(MultiImageSelectorActivity::class.java)
                mNoChooseUrls.forEach {
                    urls.remove(it)
                }
                var mImagelocals = Imagelocals(urls,1,0)
                ObserverManager.getInstance().notifyObservers(mImagelocals)
//                tv_dowork.postDelayed(Runnable {
//
//                },300)
                finish()
            }
        }

        tv_edittiezhi.setOnClickListener {
            var path = urls[mImageViewPager.currentItem]
            var param: BLBeautifyParam = BLBeautifyParam()
            param.index = 0
            param.type = Const.User.SELECTIMAGE
            param.images.add(path.replace("file://", ""))
            startActivityForResult<BLBeautifyImageActivity>(BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE, BLBeautifyParam.KEY to param);
        }

        photo_drag_relaivelayout.setDragListener(PhotoDragHelper().setOnDragListener(object : PhotoDragHelper.OnDragListener {

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
        urls.forEach {
            mHashMap.put(key,true)
            mListFragment.put(key++, ImageLocalFragment.newInstance(it, false))
        }
        adapter = ImageLocalPagerAdapter(supportFragmentManager, mListFragment)
        mImageViewPager.adapter = adapter
        if (urls != null) {
//            mImageViewPager.offscreenPageLimit = urls.size
            tv_dowork.text = "完成·${urls.size}"
        }

        type = intent.getIntExtra(TYPE, 0)
        if (type == 1) {
            tv_delete.visibility = View.GONE
            tv_check.visibility = View.VISIBLE
            var mDrawable = ContextCompat.getDrawable(this, R.mipmap.wancheng_color)
            setLeftDrawable(mDrawable, tv_check)
        }else{
            tv_check.visibility = View.GONE
            tv_delete.visibility = View.VISIBLE
        }

        tv_delete.setOnClickListener {
            var p = mImageViewPager.currentItem
            delete(p)
        }

        tv_check.setOnClickListener {
            chooseCount = 0
            mNoChooseUrls.clear()
            var p = mImageViewPager.currentItem
            var flag = mHashMap[p] as Boolean
            mHashMap.put(p,setCheckPic(!flag))
            chooseCount = setNoChooseUrls()
            tv_dowork.text = "完成·${chooseCount}"
        }

        mImageViewPager.addOnPageChangeListener(this)
        mImageViewPager.currentItem = position
        mImageViewPager.offscreenPageLimit = urls.size
        tv_pages.text = String.format("%d/%d", position + 1, urls!!.size)
        val showDelete = intent.getBooleanExtra("delete", false)
        if (showDelete) {
            tv_delete.visible()
            tv_check.visibility = View.GONE
        } else {
            tv_delete.gone()
        }
    }

    private fun delete(p: Int) {
        urls.removeAt(p)
        mListFragment.removeAt(p)
        adapter?.notifyDataSetChanged()
        var mImagelocals = Imagelocals(urls,0,p)
        ObserverManager.getInstance().notifyObservers(mImagelocals)
        if (urls.size > 0) {
            var size = if (urls!!.size < (p + 1)) {
                urls!!.size
            } else {
                p + 1
            }
            tv_pages.text = String.format("%d/%d", size,urls!!.size)
            tv_dowork.text = "完成·${urls.size}"
        } else {
            finish()
        }
    }

    private fun setCheckPic(flag:Boolean):Boolean{
        var mDrawable: Drawable? = null
        if(flag){
            mDrawable = ContextCompat.getDrawable(this, R.mipmap.wancheng_color)
        }else{
            mDrawable = ContextCompat.getDrawable(this, R.mipmap.wancheng_white)
        }
        setLeftDrawable(mDrawable, tv_check)
        return flag
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        tv_pages.text = String.format("%d/%d", position + 1, urls!!.size)
        var flag = mHashMap[position] as Boolean
        setCheckPic(flag)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.img_fade_in, R.anim.img_fade_out)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE && data != null) {
            var param = data.getParcelableExtra<BLBeautifyParam>(BLBeautifyParam.RESULT_KEY)
            urls[mImageViewPager.currentItem] = param.images[param.index]
            var mImageLocal = mListFragment.get(mImageViewPager.currentItem) as ImageLocalFragment
            mImageLocal.setNewPic(urls[mImageViewPager.currentItem],false)
            mHashMap.forEach{
                var flag = it.value
                if(!flag){
                    mNoChooseUrls.add(urls[it.key])
                }
            }
        }
    }

    private fun setNoChooseUrls():Int{
        var chooseCount = 0
        mHashMap.forEach{
            var flag = it.value
            if(flag){
                chooseCount = chooseCount+1
            }else{
                Log.i("MultiImageSelector","${urls[it.key]}")
                mNoChooseUrls.add(urls[it.key])
            }
        }
        return chooseCount
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
        val TYPE = "type"
    }

}
