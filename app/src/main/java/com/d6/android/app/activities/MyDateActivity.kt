package com.d6.android.app.activities

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.fragments.DateMeFragment
import com.d6.android.app.fragments.MeDateFragment
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_my_date.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity

/**
 * 我的约会
 */

class MyDateActivity : TitleActivity() {

    private val whetherOrNotToBeCertified by lazy {
        intent.getStringExtra("whetherOrNotToBeCertified")
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }


    private val titles = arrayListOf("我约的人", "别人约我")

    var b1: Boolean = true
    var b2: Boolean = true
    private val adapter by lazy {
        object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
//                return if (position == 0) {
//                    MeDateFragment()
//                } else {
//                    DateMeFragment()
//                }
                return if (position==0 && SPUtils.instance().getBoolean(Const.User.IASKSOMEONEELSE)) {
                    //我约的人
                    MeDateFragment()
                } else if (position==0 && SPUtils.instance().getBoolean(Const.User.SOMEONE_ELSE_MAKES_AN_APPOINTMENT_WITH_ME)) {
                    MeDateFragment()
                } else {
                    if(position==0){
                        MeDateFragment()
                    }else{
                        //被约会
                        DateMeFragment()
                    }
                }

            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence {
                // return titles[position]
//                return if (SPUtils.instance().getBoolean(Const.User.SOMEONE_ELSE_MAKES_AN_APPOINTMENT_WITH_ME)) {
//                    titles[position]
//                } else if (SPUtils.instance().getBoolean(Const.User.IASKSOMEONEELSE)) {
//                    titles[position - position]
//                } else {
//                    titles[position]
//                }
                return titles[position]
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_date)
        title = "我的约会"
        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            mSwipeRefreshLayout.isEnabled = verticalOffset >= 0
        }
        mViewPager.adapter = adapter
        mTabLayout.setViewPager(mViewPager)
        headView.setImageURI(SPUtils.instance().getString(Const.User.USER_HEAD));
        tv_toggle_set.setOnClickListener {
            if (rl_set.visibility == View.VISIBLE) {
                tv_toggle_set.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_arrow_down_orange, 0)
                rl_set.gone()
            } else {
                tv_toggle_set.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_arrow_right_orange, 0)
                rl_set.visible()
            }
        }

        //判断
        if (TextUtils.equals("0", SPUtils.instance().getString("select"))) {
//            tv_date_hint!!.visibility = View.VISIBLE
            update(flag = "0")
        }
        tv_date_content1.setOnClickListener {
            val dateDeclarationDialog = DateDeclarationDialog()
            dateDeclarationDialog.show(supportFragmentManager, "ddd")
        }

        tv_date_switch.setOnClickListener {
            val isSelected = it.isSelected
            val flag = if (isSelected) {
                "0"
            } else {
                "1"
            }
            if (TextUtils.equals("0", flag)) {
                rl_set.gone()
            } else {
                rl_set.visible()
            }
            update(flag = flag)
        }

        tv_date_content1.setOnClickListener {
            val dateDeclarationDialog = DateDeclarationDialog()
            dateDeclarationDialog.dateDeclarationView(tv_date_content)
            mData?.let {
                dateDeclarationDialog.arguments = bundleOf("data" to (it.egagementtext ?: ""))
            }
            dateDeclarationDialog.setDialogListener { p, s ->

            }
            dateDeclarationDialog.show(supportFragmentManager, "ddd")
        }

        tv_date_type1.setOnClickListener {
            val filterDateTypeDialog = FilterDateTypeDialog()
            filterDateTypeDialog.show(supportFragmentManager, "ftd")
            filterDateTypeDialog.setDialogListener { p, s ->
                tv_date_type.text = s
                update(type = s)
            }
        }

        tv_date_location1.setOnClickListener {
            val filterCityDialog = FilterCityDialog()
            filterCityDialog.show(supportFragmentManager, "fcd")
            filterCityDialog.setDialogListener { p, s ->
                var city: String? = null
                var outCity: String? = null
                if (p == 1) {
                    city = s
                    outCity = null
                } else if (p == 2) {
                    city = null
                    outCity = s
                } else if (p == -2) {//取消选择
                    city = null
                    outCity = null
                }
                tv_date_location.text = s
                update(city = city, outCity = outCity)
            }
        }
        tv_contact1.setOnClickListener {
            val dateContactAuthDialog = DateContactAuthDialog()
            dateContactAuthDialog.dateDeclarationView(tv_contact)
            mData?.let {
                dateContactAuthDialog.arguments = bundleOf("p" to (it.phone
                        ?: ""), "w" to (it.egagementwx ?: ""))
            }
            dateContactAuthDialog.show(supportFragmentManager, "dcad")
        }

        getData()
    }

    private var mData: UserData? = null

    private val types = arrayOf("不限", "救火", "征求", "急约", "旅行约")

    private fun getData() {
        dialog()
        Request.getUserInfo(userId).request(this, success = { _, data ->
            saveUserInfo(data)
            this.mData = data
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
                tv_contact.text = data.phone
                tv_date_content.text = data.egagementtext
                tv_date_type.text = types[data.egagementtype ?: 0]
                tv_date_location.text = data.userhandlookwhere + data.userlookwhere
                tv_date_switch.isSelected = TextUtils.equals(data.openEgagementflag, "1")
//                tv_date_content.text = data.egagementwx
            }
        }) { _, _ ->
        }
    }

    private fun update(type: String? = null, city: String? = null, outCity: String? = null, flag: String? = null) {
        dialog()
        Request.updateDateInfo(userId, egagementtype = type, userhandlookwhere = outCity, userlookwhere = city, openEgagementflag = flag).request(this) { _, data ->
            tv_date_switch.isSelected = TextUtils.equals(flag, "1")
            if (TextUtils.equals(flag, "1")) {
//                tv_date_hint!!.visibility = View.VISIBLE
                SPUtils.instance().put("select", "1").apply()
            } else {
//                tv_date_hint!!.visibility = View.VISIBLE
                SPUtils.instance().put("select", "0").apply()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog()
        Request.getAuthState(userId).request(this) { _, data ->
            dismissDialog()
            if (data != null) {
                val wanshanziliao = data.optDouble("wanshanziliao")
                val lianxifangshi = data.optInt("lianxifangshi")
                val qurenzheng = data.optInt("qurenzheng")
                if (wanshanziliao < 8 || lianxifangshi == 0 || qurenzheng == 0) {//资料完善程度大于=80%
                    // 0
                    showOrNotAuth("0")
                    return@request
                }
                //1
                showOrNotAuth("1")
            } else {//startActivity<DateAuthStateActivity>
                //0
                showOrNotAuth("0")
            }
        }
    }

    //更新页面ui
    fun showOrNotAuth(BeCertified :String?){
        if (TextUtils.equals(BeCertified, "0")) {
            re_auth_interface!!.visibility = View.VISIBLE
            headView1!!.visibility = View.VISIBLE
            tv_rel!!.visibility = View.GONE
//            tv_rz_tip!!.visibility = View.VISIBLE
//            tv_rz_tip.setText("只有完善信息和认证之后才" + "\n" + "     会收到别人的邀约哦~");
            tv_to_authenticate!!.visibility = View.VISIBLE
            tv_date_rel!!.visibility = View.GONE
            rl_set!!.visibility = View.GONE
            tv_to_authenticate.setOnClickListener() {
                this.startActivity<DateAuthStateActivity>()
            }
        } else if (TextUtils.equals(BeCertified, "1")) {
            re_auth_interface!!.visibility = View.GONE
            headView1!!.visibility = View.GONE
            tv_rel!!.visibility = View.GONE
            tv_rz_tip!!.visibility = View.GONE
            tv_to_authenticate!!.visibility = View.GONE
            tv_date_rel!!.visibility = View.VISIBLE
            rl_set!!.visibility = View.VISIBLE
        }
    }
}
