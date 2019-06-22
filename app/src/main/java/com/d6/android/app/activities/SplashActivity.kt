package com.d6.android.app.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.utils.*
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.fragment_splash.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
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

            override fun getCount() = 4
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

        tv_protocols.movementMethod = LinkMovementMethod.getInstance()
        val s = "点击登录/注册即表示同意 用户协议"
        tv_protocols.text = SpanBuilder(s)
                .click(s.length - 5, s.length, MClickSpan(this))
                .build()
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
                0 -> R.mipmap.windows_tz1 //R.mipmap.page_01
                1 -> R.mipmap.windows_tz2 //R.mipmap.page_02
                2 -> R.mipmap.windows_tz3 //R.mipmap.page_02
                else -> R.mipmap.windows_tz4// R.mipmap.page_03
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

    private class MClickSpan(val context: Context) : ClickableSpan() {
        override fun onClick(p0: View?) {
            context.startActivity<WebViewActivity>("title" to "用户协议", "url" to "file:///android_asset/yonghuxieyi.html")
        }

        override fun updateDrawState(ds: TextPaint?) {
            ds?.color = ContextCompat.getColor(context, R.color.color_F7AB00)
            ds?.isUnderlineText = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
