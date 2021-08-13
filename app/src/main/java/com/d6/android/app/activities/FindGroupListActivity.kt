package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.*
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.FindGroupQuickFragment
import com.d6.android.app.net.Request
import com.d6.android.app.fragments.InvateDatePageFragment
import com.d6.android.app.models.*
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_invatedate.*
import kotlinx.android.synthetic.main.activity_userslist.*
import kotlinx.android.synthetic.main.header_grouplist.view.*
import org.jetbrains.anko.toast
import org.jetbrains.anko.startActivity

/**
 * 群列表
 */
class FindGroupListActivity : BaseActivity(){

    private var mFragments = ArrayList<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findgrouplist)
        immersionBar.init()
        tv_userlist_back.setOnClickListener {
            finish()
        }
        mFragments.add(FindGroupQuickFragment.newInstance("",0))
        mViewPager.adapter = InvateDateFragmentAdapter(supportFragmentManager,mFragments)
    }
}
