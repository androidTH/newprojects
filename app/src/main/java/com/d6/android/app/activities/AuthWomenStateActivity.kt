package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.AuthTipsQuickAdapter
import com.d6.android.app.adapters.MemberCommentHolder
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.WomenAuthDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.MemberComment
import com.d6.android.app.net.API.BASE_URL
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_auth_women_state.*
import kotlinx.android.synthetic.main.layout_auth_top.*
import org.jetbrains.anko.startActivity


/**
 * 约会认证情况
 */
class AuthWomenStateActivity : BaseActivity() {

    private val from by lazy{
        intent.getStringExtra(Const.NO_VIP_FROM_TYPE)
    }

    var mComments = ArrayList<MemberComment>()

    private var ISNOTBUYMEMBER = 0 //0 没有咨询客服

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_women_state)

        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .titleBar(tv_back)
                .init()

        tv_back.setOnClickListener {
            onBackPressed()
        }


        tv_zxkf_women.setOnClickListener {
            pushCustomerMessage(this, getLocalUserId(), 2, "", next = {
                chatService(this)
            })
        }

        ll_free_rz.setOnClickListener {
//            ns_auth_women.fullScroll(ScrollView.FOCUS_DOWN)
              var mWomenAuthDialog = WomenAuthDialog()
              mWomenAuthDialog.show(supportFragmentManager,"womenAuthDialog")
              mWomenAuthDialog.setDialogListener { p, s ->
                  ISNOTBUYMEMBER = 1
                  getUserInfo()
              }
        }

        if(TextUtils.equals("mine",from)){
            tv_d6vipinfo.text = "听说开通会员后，80%都约到了心仪的TA"
        }else{
            tv_d6vipinfo.text = "D6是一个高端私密交友社区，部分服务仅对会员开放"
        }

        mComments.add(MemberComment(getString(R.string.string_women_firstcomment),
                BASE_URL+"static/image/0074V8z6ly8g1v3pxqs6jj30ro0rojte.jpg"))
        mComments.add(MemberComment(getString(R.string.string_women_secondcomment),
                BASE_URL+"static/image/700a69f8ly8g0fj1kcfdbj20u00u00vy.jpg"))
        mComments.add(MemberComment(getString(R.string.string_women_lastcomment),
                BASE_URL+"static/image/9ba8d31djw8f9ocv5yysfj20e80doaar.jpg"))

        setMemeberComemnt()


        rv_women_memberdesc
    }

    override fun onResume() {
        super.onResume()
        member_banner.startTurning()
    }

    override fun onStop() {
        super.onStop()
        member_banner.stopTurning()
    }

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

        getMemberLevel("7")
    }

    private val mImages = ArrayList<AddImage>()
    private fun getUserInfo() {
        Request.getUserInfo("", getLocalUserId()).request(this, false,success = { _, data ->
            data?.let {
                saveUserInfo(it)
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

    private fun getMemberLevel(userclassId:String?) {
        Request.findUserClasses(getLoginToken()).request(this){ msg, data->
            data?.list?.let {
                var memberBean = it.get(0)
//                it.forEach {
                memberBean?.let {
                        it.sDesc?.let {
                            var mTipsData = it.split("<br/>")
                            rv_women_memberdesc.setHasFixedSize(true)
                            rv_women_memberdesc.isNestedScrollingEnabled = false
                            rv_women_memberdesc.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            rv_women_memberdesc.adapter = AuthTipsQuickAdapter(mTipsData)
                    }
//                }
                }
//
            }
        }
    }

    private fun getDateCount() {
//        Request.getDateSuccessCount().request(this) { _, data ->
//            tv_date_count.text = String.format("目前已有%s人在D6约会成功", data?.asString ?: "1000")
//        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(ISNOTBUYMEMBER==0){
            pushCustomerMessage(this,getLocalUserId(),7,""){
                chatService(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}