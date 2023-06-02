package com.d6zone.android.app.fragments

import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.d6zone.android.app.R
import com.d6zone.android.app.adapters.SquareTagAdapter
import com.d6zone.android.app.base.BaseFragment
import com.d6zone.android.app.models.SquareTag
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_square_main.*
import org.jetbrains.anko.support.v4.dip

/**
 * 动态
 */
class SquareMainFragment : BaseFragment(), ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        scrollToPosition(position)
    }

    private val mSquareTags = ArrayList<SquareTag>()

    override fun contentViewId() = R.layout.fragment_square_main

    override fun onFirstVisibleToUser() {

        mRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.setHasFixedSize(true)
        val adapter = SquareTagAdapter(mSquareTags)
        adapter.setOnItemClickListener { _, p ->
            scrollToPosition(p)
            mViewPager.currentItem = p
        }
        mRecyclerView.adapter = adapter

        mRecyclerView.addItemDecoration(VerticalDividerItemDecoration.Builder(context)
                .color(Color.TRANSPARENT)
                .size(dip(10))
                .build())

//        mSquareTags.add(SquareTag("0","全部","res:///"+R.mipmap.ic_tag_all))
//        mSquareTags.add(SquareTag("1","社区故事","res:///"+R.mipmap.ic_community_story))
//        mSquareTags.add(SquareTag("2","心动故事","res:///"+R.mipmap.ic_heart_story))
//        mSquareTags.add(SquareTag("3","情感故事","res:///"+R.mipmap.ic_emotion_story))
//        mSquareTags.add(SquareTag("4","关注","res:///"+R.mipmap.ic_attention_story))
//        mSquareTags.add(SquareTag("5","关注","res:///"+R.mipmap.ic_attention_story))
//        mRecyclerView.adapter.notifyDataSetChanged()

        mViewPager.addOnPageChangeListener(this)
        mViewPager.offscreenPageLimit = 3
        mViewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                val id = if (TextUtils.equals(mSquareTags[position].content, "全部")) {
                    ""
                } else {
                    mSquareTags[position].id
                }
                return SquareFragment.instance(id)
            }

            override fun getCount() = mSquareTags.size
        }

        getSquareTags()
    }

    fun refresh() {
        if (mViewPager.adapter != null && mViewPager.adapter.count > 0) {
            mViewPager.currentItem = 0
            val fragments = childFragmentManager.fragments
            fragments?.forEach {
                if (it != null && !it.isDetached && it.userVisibleHint && it is SquareFragment) {
                    it.refresh()
                }
            }
        }
    }

    fun filter(p:Int){
        val fragments = childFragmentManager.fragments
        fragments?.forEach {
            if (it != null && !it.isDetached && it.userVisibleHint && it is SquareFragment) {
                it.filter(p)
            }
        }
    }

    private fun getSquareTags() {

//        Request.getSquareTags().request(this){_,data->
//            mSquareTags.clear()
//            data?.let {
//                mSquareTags.addAll(it)
//            }
//            mRecyclerView.adapter.notifyDataSetChanged()
//            mViewPager.adapter.notifyDataSetChanged()
//            mViewPager.currentItem = 0
//            scrollToPosition(0)
//        }
        mSquareTags.add(SquareTag("", "全部"))
        mViewPager.adapter?.notifyDataSetChanged()
        mViewPager.currentItem = 0
        scrollToPosition(0)
    }

    private fun scrollToPosition(position: Int) {
        mSquareTags.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
        mSquareTags[position].isSelected = true
        mRecyclerView.adapter.notifyDataSetChanged()
        mRecyclerView.smoothScrollToPosition(position)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            immersionBar.destroy()
            mViewPager.removeOnPageChangeListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}