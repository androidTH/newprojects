package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.util.SparseArray
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.ImageLocalPagerAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.ImageLocalFragment
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.setLeftDrawable
import com.d6.android.app.utils.visible
import com.d6.android.app.widget.photodrag.PhotoDragHelper
import kotlinx.android.synthetic.main.activity_localimage_pager.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.toast
import java.lang.StringBuilder

/**
 * 广场照片详情页
 */
class ImageLocalPagerActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    private var urls = ArrayList<String>()
    private var adapter: ImageLocalPagerAdapter? = null
    private var mListFragment = SparseArray<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localimage_pager)
        immersionBar.fitsSystemWindows(true).statusBarColor(R.color.colorPrimaryDark).statusBarDarkFont(false).init()
        tv_close.setOnClickListener {
            onBackPressed()
        }

        tv_delete.setOnClickListener {
            val p = mImageViewPager.currentItem
            urls?.let {
                delete(p)
            }
        }

        tv_edittiezhi.setOnClickListener {

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
            mListFragment.put(key++, ImageLocalFragment.newInstance(it, false))
        }
        adapter = ImageLocalPagerAdapter(supportFragmentManager, mListFragment)
        mImageViewPager.adapter = adapter
        if (urls != null) {
            mImageViewPager.offscreenPageLimit = urls.size
        }
        var type = intent.getIntExtra(TYPE, 0)
        if (type == 1) {
            tv_delete.text = ""
            var mDrawable = ContextCompat.getDrawable(this, R.mipmap.invitation_icon_seleted)
            setLeftDrawable(mDrawable, tv_delete)
        }

        mImageViewPager.addOnPageChangeListener(this)
        mImageViewPager.currentItem = position
        mImageViewPager.offscreenPageLimit = urls.size
        tv_pages.text = String.format("%d/%d", position + 1, urls!!.size)
        val showDelete = intent.getBooleanExtra("delete", false)
        if (showDelete) {
            tv_delete.visible()
        } else {
            tv_delete.gone()
        }
    }

    private fun delete(p: Int) {
        urls.removeAt(p)
        mListFragment.removeAt(p)
        adapter?.notifyDataSetChanged()
        if (urls.size > 0) {
            var size = if (urls!!.size < (p + 1)) {
                urls!!.size
            } else {
                p + 1
            }
            tv_pages.text = String.format("%d/%d", size,urls!!.size)
        } else {
            finish()
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
        val TYPE = "type"
    }

}
