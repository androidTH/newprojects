package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.gone
import com.d6.android.app.utils.visible
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
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
        mViewPager.adapter = object :FragmentStatePagerAdapter(supportFragmentManager){
            override fun getItem(position: Int): Fragment {
                return SplashFragment.instance(position)
            }
            override fun getCount() = 3
        }
    }

    class SplashFragment :BaseFragment(){
        val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)
        companion object {
            fun instance(p:Int):Fragment{
                val fragment = SplashFragment()
                fragment.arguments = bundleOf("p" to p)
                return fragment
            }
        }

        override fun contentViewId() = R.layout.fragment_splash

        override fun onFirstVisibleToUser() {

            val p = arguments.getInt("p")
            val ids = when(p){
                0-> R.mipmap.guide1
                1->R.mipmap.guide2
                else-> R.mipmap.guide3
            }
            val builder = GenericDraweeHierarchyBuilder(resources)
            val hierarchy = builder
                    .setPlaceholderImage(ids)
                    .build()
            imageView.hierarchy = hierarchy
            imageView.setImageURI("res:///$ids")
            if (p == 2) {
                next.visible()
            } else {
                next.gone()
            }
            next.setOnClickListener{
                SPUtils.instance().put(Const.User.IS_FIRST,false).apply()
                val isLogin = SPUtils.instance().getBoolean(Const.User.IS_LOGIN)
                if (isLogin) {
                    startActivity<MainActivity>()
                } else {
                    startActivity<SignInActivity>()
                }
                activity.finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
