package com.d6.android.app.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.d6.android.app.fragments.ImageFragment

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
        return ImageFragment.newInstance(urls[position], isBlur)
    }

    override fun getCount(): Int {
        return urls.size
    }
}
