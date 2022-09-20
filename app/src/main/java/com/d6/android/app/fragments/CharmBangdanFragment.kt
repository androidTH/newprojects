package com.d6.android.app.fragments

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import org.jetbrains.anko.support.v4.startActivity
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.d6.android.app.adapters.CharmBangDanPageAdapter
import com.d6.android.app.adapters.RecommentDatePageAdapter
import com.d6.android.app.dialogs.*
import com.d6.android.app.models.*
import com.d6.android.app.rong.bean.RecommentType
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.ISNOTLOCATION
import com.d6.android.app.utils.Const.User.USER_ADDRESS
import com.d6.android.app.utils.Const.User.USER_PROVINCE
import com.d6.android.app.widget.diskcache.DiskFileUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_service.*
import java.lang.Exception

/**
 * 主页
 */
class CharmBangdanFragment : BaseFragment() ,ViewPager.OnPageChangeListener{

    override fun contentViewId() = R.layout.fragment_charmbangdan
    private val mRecommentTypes = ArrayList<String>()
    private var mFragments = ArrayList<Fragment>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        immersionBar.statusBarColor(R.color.color_black).statusBarDarkFont(false).init()


        mRecommentTypes.add("月榜")
        mRecommentTypes.add("年榜")
        mRecommentTypes.add("总榜")

        mFragments.add(LoveHeartListQuickFragment.newInstance("月榜",2))
        mFragments.add(LoveHeartListQuickFragment.newInstance("年榜",2))
        mFragments.add(LoveHeartListQuickFragment.newInstance("总榜",2))

        viewpager_recommenddate.adapter = CharmBangDanPageAdapter(childFragmentManager,mFragments,mRecommentTypes)
        viewpager_recommenddate.offscreenPageLimit = mFragments.size
        tab_recommentdate.setupWithViewPager(viewpager_recommenddate)
        viewpager_recommenddate.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }
        })

    }

    override fun onFirstVisibleToUser() {

    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.statusBarColor(R.color.color_black).statusBarDarkFont(false).init()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                CharmBangdanFragment().apply {
                    arguments = Bundle().apply {
//                        putParcelable(ARG_PARAM1,param1)
                        putString(ARG_PARAM1,param1)
                        putInt(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"