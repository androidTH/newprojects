package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.DateAuthTipDialog
import com.d6.android.app.dialogs.DateContactAuthDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.gyf.barlibrary.ImmersionBar
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_date_auth_state.*
import org.jetbrains.anko.startActivity
import android.widget.TextView
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.text.SpannableString



/**
 * 约会认证情况
 */
class DateAuthStateActivity : BaseActivity() {
    private val immersionBar by lazy {
        ImmersionBar.with(this)
    }
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_auth_state)
        immersionBar
                .fitsSystemWindows(false)
                .titleBar(tv_back)
                .init()

        AppUtils.setTvStyle( this, resources.getString(R.string.first_step_info),0 ,10 , tv_base_info);
        AppUtils.setTvStyle( this, resources.getString(R.string.second_step_info),0 ,10 , tv_contact_info);
        AppUtils.setTvStyle( this, resources.getString(R.string.third_step_info),0 ,8 , tv_auth);

        tv_back.setOnClickListener {
            finish()
        }

        tv_base_info.setOnClickListener {
//            if (wanshanziliao < 10) {
                getUserInfo()
//            } else {
////                startActivity<MyDateActivity>()
//            }

        }

        tv_contact_info.setOnClickListener {
//            if (lianxifangshi > 0) {
//                return@setOnClickListener
//            }
            val dateContactAuthDialog = DateContactAuthDialog()
            dateContactAuthDialog.show(supportFragmentManager, "c")
        }

        tv_auth.setOnClickListener {
//            if (qurenzheng > 0) {
//                return@setOnClickListener
//            }
            val dateAuthTipDialog = DateAuthTipDialog()
            dateAuthTipDialog.show(supportFragmentManager, "t")
        }

    }

    override fun onResume() {
        super.onResume()
        dialog()
        getData()
    }

    private var lianxifangshi = 0
    private var qurenzheng = 0
    private var wanshanziliao = 0.0

    private fun getData() {
        Request.getAuthState(userId).request(this, success = { _, data ->
            getDateCount()
            if (data != null) {
                wanshanziliao = data.optDouble("wanshanziliao")
                tv_percent.text = "${wanshanziliao * 10}%"
                lianxifangshi = data.optInt("lianxifangshi")
                if (lianxifangshi == 0) {
                    tv_contact_state.text = "未完成"
                } else {
                    tv_contact_state.text = "已完成"
                    tv_contact_state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
                qurenzheng = data.optInt("qurenzheng")
                if (qurenzheng == 0) {
                    tv_auth_state.text = "未认证"
                } else {
                    tv_auth_state.text = "已认证"
                    tv_contact_state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            } else {

            }
        }) { _, _ ->
            getDateCount()
        }
    }

    private fun getDateCount() {
        Request.getDateSuccessCount().request(this) { _, data ->
            tv_date_count.text = String.format("目前已有%s人在D6约会成功", data?.asString ?: "1000")
        }
    }

    private fun getUserInfo() {
        dialog()
        Request.getUserInfo(userId).request(this, success = { _, data ->
            saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)

                startActivity<MyInfoActivity>("data" to it)
            }
        }) { _, _ ->
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}