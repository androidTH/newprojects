package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.ScrollView
import com.d6.android.app.R
import com.d6.android.app.adapters.MemberCommentHolder
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.DateContactAuthDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.MemberComment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
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

    var mComments = ArrayList<MemberComment>()

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

        ll_free_rz.setOnClickListener {
            ns_auth_women.fullScroll(ScrollView.FOCUS_DOWN)
        }

        if(TextUtils.equals("mine",from)){
            tv_d6vipinfo.text = "听说开通会员后，80%都约到了心仪的TA"
        }else{
            tv_d6vipinfo.text = "D6是一个高端私密交友社区，部分服务仅对会员开放"
        }

        mComments.add(MemberComment(getString(R.string.string_women_firstcomment),
                "https://tvax1.sinaimg.cn/crop.0.0.996.996.180/0074V8z6ly8g1v3pxqs6jj30ro0rojte.jpg"))
        mComments.add(MemberComment(getString(R.string.string_women_secondcomment),
                "https://tvax4.sinaimg.cn/crop.0.0.1080.1080.180/700a69f8ly8g0fj1kcfdbj20u00u00vy.jpg"))
        mComments.add(MemberComment(getString(R.string.string_women_lastcomment),
                "https://tva1.sinaimg.cn/crop.10.0.492.492.180/9ba8d31djw8f9ocv5yysfj20e80doaar.jpg"))

        setMemeberComemnt()
    }

    override fun onResume() {
        super.onResume()
        member_banner.startTurning()
        getAuthPercent()
    }

    override fun onStop() {
        super.onStop()
        member_banner.stopTurning()
    }

    private var lianxifangshi = 0
    private var qurenzheng = 0
    private var wanshanziliao = 0.0

    private fun setMemeberComemnt(){
        member_banner.setPages(
                object : CBViewHolderCreator {
                    override fun createHolder(itemView: View): MemberCommentHolder {
                        return MemberCommentHolder(itemView)
                    }

                    override fun getLayoutId(): Int {
                        return R.layout.item_vipcomment
                    }
                },mComments)

        member_banner.setOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageSelected(index: Int) {
                when(index){
                    0-> {
                        tv_numone.isEnabled = false
                        tv_numtwo.isEnabled = true
                        tv_numthree.isEnabled = true
                    }
                    1->{
                        tv_numone.isEnabled = true
                        tv_numtwo.isEnabled = false
                        tv_numthree.isEnabled = true
                    }
                    2->{
                        tv_numone.isEnabled = true
                        tv_numtwo.isEnabled = true
                        tv_numthree.isEnabled = false
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            }
        })
    }


    private fun getAuthPercent() {
        Request.getAuthState(userId).request(this, success = { _, data ->
//            getDateCount()
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
//            getDateCount()
        }
    }

    private fun getDateCount() {
//        Request.getDateSuccessCount().request(this) { _, data ->
//            tv_date_count.text = String.format("目前已有%s人在D6约会成功", data?.asString ?: "1000")
//        }
    }

    private fun getUserInfo() {
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
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}