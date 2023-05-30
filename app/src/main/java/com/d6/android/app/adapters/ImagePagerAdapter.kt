package com.d6.android.app.adapters

import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup

import com.d6.android.app.fragments.ImageFragment
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.BLUR_50

import java.util.ArrayList

/**
 *
 */
class ImagePagerAdapter(fm: FragmentManager, urls: List<String>?) : FragmentPagerAdapter(fm) {

    private val urls: List<String> = urls ?: ArrayList()
    private var isBlur = false
    private var mBlurIndex = ArrayList<String>()

    fun isBlur(isBlur: Boolean) {
        this.isBlur = isBlur
    }
    fun setListBlur(isBlur: ArrayList<String>) {
        this.mBlurIndex = isBlur
    }
    override fun getItem(position: Int): Fragment {
        var url = urls[position]
        Log.i("ImagePagerAdapter", "图片默认${url}")
        if(url.contains(Const.Pic_Thumbnail_Size_wh300)){
            url = url.replace(Const.Pic_Thumbnail_Size_wh300,"")
        }else if(url.contains(Const.Pic_Thumbnail_Size_wh400)){
           url = url.replace(Const.Pic_Thumbnail_Size_wh400,"")
        }
        Log.i("ImagePagerAdapter", "图片大小${url}")
        if(mBlurIndex!=null&&mBlurIndex.size>0){
            if(TextUtils.equals("2",mBlurIndex[position])){
                url = url.replace("?imageslim",Const.BLUR_50)
            }
        }
        return ImageFragment.newInstance(url, isBlur,false)
    }

    override fun getItemPosition(`object`: Any?): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int {
        return urls.size
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        super.destroyItem(container, position, `object`)
        container?.let {
            container.removeAllViews()
        }
    }
}
