package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.fragments.FindDateFragment
import com.d6.android.app.fragments.SelfReleaseFragment
import com.d6.android.app.utils.isAuthUser
import kotlinx.android.synthetic.main.activity_newest_find_date.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult


/**
 * 觅约
 */
class NewestFindDateActivity : TitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newest_find_date)
        immersionBar.init()
        title = "最新觅约"

        titleBar.addRightButton(rightId = R.mipmap.ic_add_orange1, onClickListener = View.OnClickListener {
            isAuthUser {
                startActivityForResult<PublishFindDateActivity>(1)
            }
        })

        titleBar.addRightButton(rightId = R.mipmap.ic_filter_orange, onClickListener = View.OnClickListener {
            isAuthUser {
                startActivityForResult<FilterActivity>(0, "type" to 2)
            }
        })

        mViewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            val titles = arrayOf("官方推荐", "自主发布")
            override fun getItem(position: Int): Fragment {
                return if (position == 0) {
                    FindDateFragment.instance(position)
                } else
                    SelfReleaseFragment.instance(position)
            }

            override fun getCount() = titles.size

            override fun getPageTitle(position: Int) = titles[position]

        }
        mTabLayout.setViewPager(mViewPager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {//筛选
                val area = data.getStringExtra("area")
                val areaType = data.getIntExtra("areaType", -1)
                val vipIds = data.getStringExtra("vipIds")
                val fragments = supportFragmentManager.fragments
                fragments?.forEach {
                    if (it != null && it is FindDateFragment) {
                        it.refresh(area, areaType, vipIds)
                    } else if (it != null && it is SelfReleaseFragment) {
                        it.refresh(area, areaType, vipIds)
                    }
                }
            }else if (requestCode == 1) {
                val fragments = supportFragmentManager.fragments
                fragments?.forEach {
                    if (it != null && it is SelfReleaseFragment) {
                        it.refreshByPublishNew()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
