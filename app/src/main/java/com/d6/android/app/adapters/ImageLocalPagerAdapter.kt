package com.d6.android.app.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.util.SparseArray
import android.view.ViewGroup

/**
 *
 */
class ImageLocalPagerAdapter(fm: FragmentManager,listFragments: SparseArray<Fragment>?) : FragmentPagerAdapter(fm) {

    private var mListFragments: SparseArray<Fragment> = listFragments ?: SparseArray()


    override fun getItemPosition(`object`: Any?): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        return super.instantiateItem(container, position)
    }

    override fun getItemId(position: Int): Long {
        return mListFragments.keyAt(position).toLong()
    }

    override fun getItem(position: Int): Fragment {
        return mListFragments.valueAt(position)
    }

    override fun getCount(): Int {
        return mListFragments.size()
    }
}
