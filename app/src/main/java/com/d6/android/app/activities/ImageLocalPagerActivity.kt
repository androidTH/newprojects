package com.d6.android.app.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
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
import com.gyf.barlibrary.ImmersionBar
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
    private var mPayPointsHashMap = HashMap<String,Boolean>()
    private var chooseCount = 0
    private var mNoChooseUrls = ArrayList<String>()
    private var type = 0
    private var mShowPayPoints = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localimage_pager)
        ImmersionBar.with(this)
                .statusBarColor(R.color.color_CC111111).init()
        tv_close.setOnClickListener {
            closeImageLocalPager()
        }

        tv_dowork.setOnClickListener {
            FinishActivityManager.getManager().finishActivity(MultiImageSelectorActivity::class.java)
            mNoChooseUrls.forEach {
                urls.remove(it)
            }

            var mImagelocals = Imagelocals(urls, 1, 0,mPayPointsHashMap)
            ObserverManager.getInstance().notifyObservers(mImagelocals)
            onBackPressed()
        }

        tv_edittiezhi.setOnClickListener {
            if(urls.size>mImageLocalViewPager.currentItem){
                var path = urls[mImageLocalViewPager.currentItem]
                var param: BLBeautifyParam = BLBeautifyParam()
                param.index = 0
                param.type = Const.User.SELECTIMAGE
                param.images.add(path.replace("file://", ""))
                startActivityForResult<BLBeautifyImageActivity>(BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE, BLBeautifyParam.KEY to param);
            }
        }

        initData()
    }

    var key = 0
    @SuppressLint("NewApi")
    private fun initData() {
        val position = intent.getIntExtra(CURRENT_POSITION, 0)
        urls = intent.getStringArrayListExtra(URLS)
        val showDelete = intent.getBooleanExtra("delete", false)
        mShowPayPoints = intent.getBooleanExtra("paypoints",false)
        urls.forEach {
            mHashMap.put(key,true)
            if(mShowPayPoints){
              mPayPointsHashMap.put(it,false)
            }
            mListFragment.put(key++, ImageLocalFragment.newInstance(it, false))
        }
        adapter = ImageLocalPagerAdapter(supportFragmentManager, mListFragment)
        mImageLocalViewPager.adapter = adapter
        mImageLocalViewPager.addOnPageChangeListener(this)
        mImageLocalViewPager.currentItem = position
        mImageLocalViewPager.offscreenPageLimit = urls.size
        tv_pages.text = String.format("%d/%d", position + 1, urls!!.size)
        if (urls != null) {
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
            tv_dowork.visibility = View.GONE
            tv_delete.visibility = View.VISIBLE
        }

        if (showDelete) {
            tv_delete.visible()
            tv_check.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Const.mLocalBlurMap.size>0&&mShowPayPoints) {
                    Const.mLocalBlurMap.forEach { t, u ->
                        mPayPointsHashMap.put(t, u)
                    }
                    var path = urls[position]
                    var checked = mPayPointsHashMap[path] as Boolean
                    mPayPointsHashMap.put(path, setPayPointPic(checked))
                }else{
                    tv_paypoints.visibility = View.GONE
                }
            }else{
                tv_paypoints.visibility = View.GONE
            }
        } else {
            tv_delete.gone()
            if(mShowPayPoints){
                tv_paypoints.visibility = View.VISIBLE
            }else{
                tv_paypoints.visibility = View.GONE
            }
        }

        tv_delete.setOnClickListener {
            var p = mImageLocalViewPager.currentItem
            if(urls.size>p){
                delete(p)
            } else {
                onBackPressed()
            }
        }

        tv_check.setOnClickListener {
            chooseCount = 0
            mNoChooseUrls.clear()
            var p = mImageLocalViewPager.currentItem
            var flag = mHashMap[p] as Boolean
            mHashMap.put(p,setCheckPic(!flag))
            if(flag){
                mPayPointsHashMap.put(urls[p],setPayPointPic(!flag))
            }
            chooseCount = setNoChooseUrls()
            tv_dowork.text = "完成·${chooseCount}"
        }

        tv_paypoints.setOnClickListener {
            var p = mImageLocalViewPager.currentItem
            var checked = mPayPointsHashMap[urls[p]] as Boolean
            mPayPointsHashMap.put(urls[p],setPayPointPic(!checked))

            if(!checked){
                chooseCount = 0
                mNoChooseUrls.clear()
                mHashMap.put(p,setCheckPic(!checked))
                chooseCount = setNoChooseUrls()
                tv_dowork.text = "完成·${chooseCount}"
            }
        }
    }

    private fun delete(p: Int) {
        try{
            var path = urls.removeAt(p)
            mListFragment.removeAt(p)
            adapter?.notifyDataSetChanged()
            mPayPointsHashMap.remove(path)
            var mImagelocals = Imagelocals(urls,0,p,mPayPointsHashMap)
            ObserverManager.getInstance().notifyObservers(mImagelocals)
            if (urls.size > 0) {
                var size = if (urls!!.size < (p + 1)) {
                    urls!!.size
                } else {
                    p + 1
                }
                tv_pages.text = String.format("%d/%d", size,urls!!.size)

                updatePayPoints(size-1)
                tv_dowork.text = "完成·${urls.size}"
            } else {
                onBackPressed()
            }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
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

    private fun setPayPointPic(flag:Boolean):Boolean{
        var mDrawable: Drawable? = null
        if(flag){
            mDrawable = ContextCompat.getDrawable(this, R.mipmap.paypointspic_seleted)
        }else{
            mDrawable = ContextCompat.getDrawable(this, R.mipmap.paypoint_pic_seleted)
        }
        setLeftDrawable(mDrawable, tv_paypoints)
        return flag
    }

    //关闭本页面
    fun closeImageLocalPager(){
        if(type==0){
            var mImagelocals = Imagelocals(urls,1,0,mPayPointsHashMap)
            ObserverManager.getInstance().notifyObservers(mImagelocals)
            onBackPressed()
        }else{
            onBackPressed()
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        tv_pages.text = String.format("%d/%d", position + 1, urls!!.size)
        var flag = mHashMap[position] as Boolean
        setCheckPic(flag)

        updatePayPoints(position)
        Log.i("pageSelected","${position}")
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun updatePayPoints(position:Int){
        if(mShowPayPoints){
            var path = urls[position]
            var obj = mPayPointsHashMap[path]
            if(obj!=null){
                var checked = obj as Boolean
                mPayPointsHashMap.put(path,setPayPointPic(checked))
            }
        }
    }

    override fun onBackPressed() {
        urls.clear()
        mListFragment.clear()
        finish()
        overridePendingTransition(R.anim.img_fade_in, R.anim.img_fade_out)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BLBeautifyParam.REQUEST_CODE_BEAUTIFY_IMAGE && data != null) {
            var param = data.getParcelableExtra<BLBeautifyParam>(BLBeautifyParam.RESULT_KEY)
            var path = urls[mImageLocalViewPager.currentItem]
            urls[mImageLocalViewPager.currentItem] = param.images[param.index]
            var mImageLocal = mListFragment.get(mImageLocalViewPager.currentItem) as ImageLocalFragment
            mImageLocal.setNewPic(urls[mImageLocalViewPager.currentItem],false)
            mHashMap.forEach{
                var flag = it.value
                if(!flag){
                    mNoChooseUrls.add(urls[it.key])
                }
            }
            if(mShowPayPoints){
              var blur = mPayPointsHashMap.remove(path)
                blur?.let { mPayPointsHashMap.put(urls[mImageLocalViewPager.currentItem], it) }
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
            if (mImageLocalViewPager != null) {
                mImageLocalViewPager!!.removeOnPageChangeListener(this)
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
