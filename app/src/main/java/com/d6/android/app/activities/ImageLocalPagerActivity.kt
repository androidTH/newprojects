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
import com.d6.android.app.utils.Const.mLocalBlurMap
import com.d6.android.app.utils.Const.mLocalFirePicsMap
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.setLeftDrawable
import com.d6.android.app.utils.visible
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
    private var mFiresHashMap = HashMap<String,Boolean>()
    private var chooseCount = 0
    private var mNoChooseUrls = ArrayList<String>()
    private var type = 0//0删除 1 没有删除
    private var mShowPayPoints = false
    private var mFirePics = false
    private var mDoWork = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localimage_pager)
        ImmersionBar.with(this)
                .statusBarColor(R.color.color_CC111111).init()
        tv_close.setOnClickListener {
            closeImageLocalPager()
        }

        tv_dowork.setOnClickListener {
            try{
                FinishActivityManager.getManager().finishActivity(MultiImageSelectorActivity::class.java)
                if (mNoChooseUrls != null && mNoChooseUrls.size > 0) {
                    mNoChooseUrls.forEach {
                        urls.remove(it)
                    }
                }

                Log.i("imagelocal","dowork")
                var mImagelocals = Imagelocals(urls, type, 0, mPayPointsHashMap, mFiresHashMap)
                ObserverManager.getInstance().notifyObservers(mImagelocals)
                onBackPressed()
            }catch(e:Exception){
                e.printStackTrace()
            }
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
        if(intent.hasExtra("firepics")){
            mFirePics = intent.getBooleanExtra("firepics",false)
        }
        urls.forEach {
            mHashMap.put(key,true)
            if(mShowPayPoints){
                var mblur = mLocalBlurMap[it]
                if(mblur!=null){
                    mPayPointsHashMap.put(it,mblur)
                }else{
                    mPayPointsHashMap.put(it,false)
                }
            }

            if(mFirePics){
                var mFire = mLocalFirePicsMap[it]
                if(mFire!=null){
                    mFiresHashMap.put(it,mFire)
                }else{
                    mFiresHashMap.put(it,false)
                }
            }

            mListFragment.put(key++, ImageLocalFragment.newInstance(it, false))
        }
        adapter = ImageLocalPagerAdapter(supportFragmentManager, mListFragment)
        mImageLocalViewPager.adapter = adapter
        mImageLocalViewPager.addOnPageChangeListener(this)
        mImageLocalViewPager.currentItem = position
//        mImageLocalViewPager.offscreenPageLimit = urls.size
        tv_pages.text = String.format("%d/%d", position + 1, urls!!.size)
        if (urls != null&&urls.size>0) {
            tv_dowork.text = "完成·${urls.size}"
        }else{
            tv_dowork.text = "完成"
        }

        type = intent.getIntExtra(TYPE, 0)
        if (type!=0) {
            tv_delete.visibility = View.GONE
            tv_check.visibility = View.GONE
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
                if (mShowPayPoints) {
//                    Const.mLocalBlurMap.forEach { t, u ->
//                        mPayPointsHashMap.put(t, u)
//                    }
                    if(urls!=null&&urls.size>0){
                        var path = urls[position]
                        var checked = mPayPointsHashMap[path] as Boolean
                        mPayPointsHashMap.put(path, setPayPointPic(checked))
                    }
                }else{
                    rl_paypoints.visibility = View.GONE
                }

                if(mFirePics){
                    if(urls!=null&&urls.size>0){
                        var path = urls[position]
                        var checked = mFiresHashMap[path] as Boolean
                        mFiresHashMap.put(path, checkFirePic(checked))
                    }
                }else{
                    rl_firepcis.visibility = View.GONE
                }

            }else{
                rl_paypoints.visibility = View.GONE
                rl_firepcis.visibility = View.GONE
            }
        } else {
            tv_delete.gone()
            if(mShowPayPoints){
                rl_paypoints.visibility = View.VISIBLE
                if(urls!=null&&urls.size>0){
                    var path = urls[position]
                    var checked = mPayPointsHashMap[path] as Boolean
                    mPayPointsHashMap.put(path, setPayPointPic(checked))
                }
            }else{
                rl_paypoints.visibility = View.GONE
            }

            if(mFirePics){
                rl_firepcis.visibility = View.VISIBLE
                if(urls!=null&&urls.size>0){
                    var path = urls[position]
                    var checked = mFiresHashMap[path] as Boolean
                    mFiresHashMap.put(path, checkFirePic(checked))
                }
            }else{
                rl_firepcis.visibility = View.GONE
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
                if(urls!=null&&urls.size>0){
                    mPayPointsHashMap.put(urls[p],setPayPointPic(!flag))
                    mFiresHashMap.put(urls[p],checkFirePic(!flag))
                }
            }
            chooseCount = setNoChooseUrls()
            tv_dowork.text = "完成·${chooseCount}"
        }

        rl_paypoints.setOnClickListener {
            payPoints()
        }

        sw_paypoints.setOnClickListener {
            payPoints()
        }

        rl_firepcis.setOnClickListener {
           firePoints()
        }

        sw_fire.setOnClickListener {
           firePoints()
        }
    }

    private fun firePoints(){
        var p = mImageLocalViewPager.currentItem
        var checked = mFiresHashMap[urls[p]] as Boolean
        if(urls!=null&&urls.size>0){
            mFiresHashMap.put(urls[p],checkFirePic(!checked))

        }
        if(!checked){
            chooseCount = 0
            mNoChooseUrls.clear()
            mHashMap.put(p,setCheckPic(!checked))
            chooseCount = setNoChooseUrls()
            tv_dowork.text = "完成·${chooseCount}"
        }
    }

    private fun payPoints(){
        var p = mImageLocalViewPager.currentItem
        var checked = mPayPointsHashMap[urls[p]] as Boolean
        if(urls!=null&&urls.size>0){
            mPayPointsHashMap.put(urls[p],setPayPointPic(!checked))

        }
        if(!checked){
            chooseCount = 0
            mNoChooseUrls.clear()
            mHashMap.put(p,setCheckPic(!checked))
            chooseCount = setNoChooseUrls()
            tv_dowork.text = "完成·${chooseCount}"
        }
    }

    private fun delete(p: Int) {
        try{
            var path = urls.removeAt(p)
            mListFragment.removeAt(p)
            adapter?.notifyDataSetChanged()
            mPayPointsHashMap.remove(path)
            mFiresHashMap.remove(path)
            var mImagelocals = Imagelocals(urls,0,p,mPayPointsHashMap,mFiresHashMap)
            ObserverManager.getInstance().notifyObservers(mImagelocals)
            if (urls!=null&&urls.size > 0) {
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
        }catch (e:Exception){
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

    private fun checkFirePic(flag:Boolean):Boolean{
        sw_fire.isChecked = flag
//        var mDrawable: Drawable? = null
//        if(flag){
//            mDrawable = ContextCompat.getDrawable(this, R.mipmap.paypointspic_seleted)
//        }else{
//            mDrawable = ContextCompat.getDrawable(this, R.mipmap.paypoint_pic_seleted)
//        }
//        setLeftDrawable(mDrawable, tv_paypoints)
        return flag
    }

    private fun setPayPointPic(flag:Boolean):Boolean{
         sw_paypoints.isChecked = flag
//        var mDrawable: Drawable? = null
//        if(flag){
//            mDrawable = ContextCompat.getDrawable(this, R.mipmap.paypointspic_seleted)
//        }else{
//            mDrawable = ContextCompat.getDrawable(this, R.mipmap.paypoint_pic_seleted)
//        }
//        setLeftDrawable(mDrawable, tv_paypoints)
        return flag
    }

    //关闭本页面
    fun closeImageLocalPager(){
        if(type==0){
            var mImagelocals = Imagelocals(urls,1,0,mPayPointsHashMap,mFiresHashMap)
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
            if(urls!=null&&urls.size>0){
                var path = urls[position]
                var obj = mPayPointsHashMap[path]
                if(obj!=null){
                    var checked = obj as Boolean
                    mPayPointsHashMap.put(path,setPayPointPic(checked))
                }
            }
        }

        if(mFirePics){
            if(urls!=null&&urls.size>0){
                var path = urls[position]
                var obj = mFiresHashMap[path]
                if(obj!=null){
                    var checked = obj as Boolean
                    mFiresHashMap.put(path,checkFirePic(checked))
                }
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
            if(urls!=null&&urls.size>0){
                var path = urls[mImageLocalViewPager.currentItem]
                urls[mImageLocalViewPager.currentItem] = param.images[param.index]
                if(mListFragment.get(mImageLocalViewPager.currentItem)!=null){
                    var mImageLocal = mListFragment.get(mImageLocalViewPager.currentItem) as ImageLocalFragment
                    mImageLocal.setNewPic(urls[mImageLocalViewPager.currentItem],false)
                }
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

                if(mFirePics){
                    var fire = mFiresHashMap.remove(path)
                    fire?.let { mFiresHashMap.put(urls[mImageLocalViewPager.currentItem], it) }
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
