package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.d6.android.app.R
import com.d6.android.app.adapters.GroupUsersListAdapter
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_groupsetting.*
import org.jetbrains.anko.startActivity

class GroupSettingActivity : TitleActivity() {

    private var pageNum = 1
    private var mGroupUserList = ArrayList<Fans>()

    private val mGroupUserListAdapter by lazy {
        GroupUsersListAdapter(mGroupUserList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupsetting)
        immersionBar.init()
        setTitleBold("群设置")
        rv_grouplist.setHasFixedSize(true)
        rv_grouplist.isNestedScrollingEnabled = true
        rv_grouplist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_grouplist.adapter = mGroupUserListAdapter

        sw_message_notfaction.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

            } else {

            }
        }

        tv_moreusers.setOnClickListener {
            startActivity<GroupUsersActivity>()
        }

        btn_group_leave.setOnClickListener {
            startActivity<GroupUsersActivity>()
        }

        getUserInfo()
        mGroupUserListAdapter.setOnItemClickListener { _, position ->
            val id = mGroupUserList[position].iUserid
            startActivity<UserInfoActivity>("id" to id.toString())
        }

        getGroupUsersData()
    }

    private fun getGroupUsersData() {
        Request.getFindMyFollows(getLocalUserId(), pageNum).request(this) { _, data ->
            if (pageNum == 1) {
                mGroupUserList.clear()
            }
            if (data?.list?.results == null || data.list.results.isEmpty()) {

            } else {
                mGroupUserList.addAll(data.list.results)
            }
            mGroupUserListAdapter.notifyDataSetChanged()
        }
    }


    private fun getUserInfo() {
        Request.getUserInfo("", getLocalUserId()).request(this, success = { _, data ->
            SPUtils.instance().put(Const.USERINFO, GsonHelper.getGson().toJson(data)).apply()
            saveUserInfo(data)
            data?.let {
            }
        })
    }

}
