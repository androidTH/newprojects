package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.DateAuthTipDialog
import com.d6.android.app.dialogs.DateContactAuthDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.gyf.barlibrary.ImmersionBar
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_auth_women_state.*
import kotlinx.android.synthetic.main.layout_auth_top.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.bundleOf


/**
 * 约会认证情况
 */
class AuthWomenStateActivity : BaseActivity() {

    @JvmField
    public var phoneNum: String? = ""
    //    private val immersionBar by lazy {
//        ImmersionBar.with(this)
//    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val from by lazy{
        intent.getStringExtra(Const.NO_VIP_FROM_TYPE)
    }

    private val mImages = ArrayList<AddImage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_women_state)

        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .titleBar(tv_back)
                .init()

        AppUtils.setTvStyle(this, resources.getString(R.string.first_step_info), 0, 11, tv_base_info);
//        AppUtils.setTvStyle( this, resources.getString(R.string.second_step_info),0 ,10 , tv_contact_info);
        AppUtils.setTvStyle(this, resources.getString(R.string.third_step_info), 0, 9, tv_auth);

        tv_back.setOnClickListener {
            finish()
        }

        //第一步认证
        tv_base_info.setOnClickListener {
            //            if (wanshanziliao < 10) {
            getUserInfo()
//            } else {
////                startActivity<MyDateActivity>()
//            }

        }

        //第二步认证
        tv_contact_info.setOnClickListener {
            //            if (lianxifangshi > 0) {
//                return@setOnClickListener
//            }
            val dateContactAuthDialog = DateContactAuthDialog()
            dateContactAuthDialog.arguments = bundleOf("w" to (phoneNum ?: ""))
            dateContactAuthDialog.show(supportFragmentManager, "c")
            dateContactAuthDialog.setDialogListener { p, s ->
                phoneNum = s
            }
        }

        //第三步认证
        tv_auth.setOnClickListener {
            //            if (qurenzheng > 0) {
//                return@setOnClickListener
//            }
            var localUserId = getLocalUserId()
            pushCustomerMessage(this, localUserId, 2, localUserId, next = {
                chatService(this)
            })
//            val dateAuthTipDialog = DateAuthTipDialog()
//            dateAuthTipDialog.show(supportFragmentManager, "t")
        }

        tv_zxkf_women.setOnClickListener {
            pushCustomerMessage(this, getLocalUserId(), 5, "", next = {
                chatService(this)
            })
        }

        if(TextUtils.equals("mine",from)){
            tv_d6vipinfo.text = "听说开通会员后，80%都约到了心仪的TA"
        }else{
            tv_d6vipinfo.text = "D6是一个高端私密交友社区，部分服务仅对会员开放"
        }
    }

    override fun onResume() {
        super.onResume()
        //getData()
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
                    tv_auth_state.text = "未完成"
                } else {
                    tv_auth_state.text = "已完成"
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
//            tv_date_count.text = String.format("目前已有%s人在D6约会成功", data?.asString ?: "1000")
        }
    }

    private fun getUserInfo() {
        dialog()
        Request.getUserInfo("", userId).request(this, success = { _, data ->
            saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)
                mImages.clear()
                if (!it.userpics.isNullOrEmpty()) {
                    val images = it.userpics!!.split(",")
                    images.forEach {
                        mImages.add(AddImage(it))
                    }
                }
                mImages.add(AddImage("res:///" + R.mipmap.ic_add_bg, 1))
                startActivity<MyInfoActivity>("data" to it, "images" to mImages)
            }
        }) { _, _ ->
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}