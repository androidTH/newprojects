package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_member.*
import org.jetbrains.anko.startActivity


/**
 * 会员页面
 */
class MemberActivity : BaseActivity() {

    @JvmField
    public var phoneNum:String? = ""
//    private val immersionBar by lazy {
//        ImmersionBar.with(this)
//    }
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val mImages = ArrayList<AddImage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)

        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .titleBar(tv_back)
                .init()

        tv_back.setOnClickListener {
            finish()
        }


        ll_lianxikf.setOnClickListener {
            pushCustomerMessage(this, getLocalUserId(), 5, "", next = {
                chatService(this)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        //getData()
    }

    private var lianxifangshi = 0
    private var qurenzheng = 0
    private var wanshanziliao = 0.0
    private fun getDateCount() {
        Request.getDateSuccessCount().request(this) { _, data ->
//            tv_date_count.text = String.format("目前已有%s人在D6约会成功", data?.asString ?: "1000")
        }
    }

    private fun getUserInfo() {
        dialog()
        Request.getUserInfo("",userId).request(this, success = { _, data ->
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
                startActivity<MyInfoActivity>("data" to it,"images" to mImages)
            }
        }) { _, _ ->
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}