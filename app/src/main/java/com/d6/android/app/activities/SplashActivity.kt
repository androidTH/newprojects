package com.d6.android.app.activities

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.visible
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.fragment_splash.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.startActivity


class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        immersionBar.init()

        mViewPager.offscreenPageLimit = 3
        mViewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return SplashFragment.instance(position)
            }

            override fun getCount() = 3
        }

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
//                if (position == 0) {
//                    tv_numone.isEnabled = false
//                    tv_numtwo.isEnabled = true
//                    tv_numthree.isEnabled = true
//                } else if (position == 1) {
//                    tv_numone.isEnabled = true
//                    tv_numtwo.isEnabled = false
//                    tv_numthree.isEnabled = true
//                } else {
//                    tv_numone.isEnabled = true
//                    tv_numtwo.isEnabled = true
//                    tv_numthree.isEnabled = false
//                }
            }
        })

    }

    class SplashFragment : BaseFragment() {
        val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)

        companion object {
            fun instance(p: Int): Fragment {
                val fragment = SplashFragment()
                fragment.arguments = bundleOf("p" to p)
                return fragment
            }
        }

        override fun contentViewId() = R.layout.fragment_splash

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            val p = arguments.getInt("p")
            val ids = when (p) {
                0 -> R.mipmap.guide1 //R.mipmap.page_01
                1 -> R.mipmap.guide2 //R.mipmap.page_02
                else -> R.mipmap.guide3// R.mipmap.page_03
            }
            val builder = GenericDraweeHierarchyBuilder(resources)
            val hierarchy = builder
                    .setPlaceholderImage(ids)
                    .build()
            imageView.hierarchy = hierarchy
            imageView.setImageURI("res:///$ids")
            if (p == 2) {
//                splash_title.text ="优质会员任你挑"
//                splash_smalltitle.text = "多金？有颜？总有一个是你的菜"
                next.visible()
            } else if (p == 1) {
//                splash_title.text ="一对一私聊"
//                splash_smalltitle.text = "直接开聊拒绝骚扰"
            } else {
//                splash_title.text ="专属人工客服为你匹配约会"
//                splash_smalltitle.text = "成功率高达80%"
                next.gone()
            }

            next.setOnClickListener {
                SPUtils.instance().put(Const.User.IS_FIRST, false).apply()
                val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)
                if (isLogin) {
                    startActivity<MainActivity>()
                } else {
                    startActivity<SignInActivity>()
                }
                activity.finish()
            }
        }

        override fun onFirstVisibleToUser() {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
