package com.d6.android.app.adapters

import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log

import com.d6.android.app.fragments.ImageFragment
import com.d6.android.app.utils.Const

import java.util.ArrayList

/**
 *
 */
class ImagePagerAdapter(fm: FragmentManager, urls: List<String>?) : FragmentPagerAdapter(fm) {

    private val urls: List<String> = urls ?: ArrayList()
    private var isBlur = false

    fun isBlur(isBlur: Boolean) {
        this.isBlur = isBlur
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
        return ImageFragment.newInstance(url, isBlur)
    }

    override fun getCount(): Int {
        return urls.size
    }
}
