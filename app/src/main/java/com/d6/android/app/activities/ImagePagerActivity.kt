package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.ImagePagerAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.visible
import com.d6.android.app.widget.photodrag.PhotoDragHelper
import kotlinx.android.synthetic.main.activity_image_pager.*
import org.jetbrains.anko.bundleOf
import java.lang.StringBuilder

/**
 * 广场照片详情页
 */
class ImagePagerActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    private var urls: ArrayList<String>? = null
    private var userData:UserData?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_pager)
        immersionBar.fitsSystemWindows(true).init()
        tv_close.setOnClickListener {
            onBackPressed()
        }

        tv_delete.setOnClickListener {
            val p = mImageViewPager.currentItem
            urls?.let {
                delete(it[p])
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

    private fun initData() {
        val position = intent.getIntExtra(CURRENT_POSITION, 0)
        urls = intent.getStringArrayListExtra(URLS)
        val isBlur = intent.getBooleanExtra("isBlur", false)
        val adapter = ImagePagerAdapter(supportFragmentManager, urls)
        adapter.isBlur(isBlur)
        mImageViewPager.adapter = adapter
        mImageViewPager.addOnPageChangeListener(this)
        mImageViewPager.currentItem = position
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
            Request.updateUserInfo(user).request(this){_,_->
                setResult(Activity.RESULT_OK,Intent().putExtras(bundleOf("data" to user)))
                onBackPressed()
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        tv_pages.text = String.format("%d/%d", position + 1, urls!!.size)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
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
        immersionBar.destroy()
    }

    companion object {
        val CURRENT_POSITION = "position"
        val URLS = "urls"
    }

}
