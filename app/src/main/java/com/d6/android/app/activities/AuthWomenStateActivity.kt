package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.MemberCommentHolder
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.WomenAuthDialog
import com.d6.android.app.models.MemberComment
import com.d6.android.app.utils.*
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import kotlinx.android.synthetic.main.activity_auth_women_state.*
import kotlinx.android.synthetic.main.layout_auth_top.*


/**
 * 约会认证情况
 */
class AuthWomenStateActivity : BaseActivity() {


    private val from by lazy{
        intent.getStringExtra(Const.NO_VIP_FROM_TYPE)
    }

    var mComments = ArrayList<MemberComment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_women_state)

        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .titleBar(tv_back)
                .init()

        tv_back.setOnClickListener {
            finish()
        }


        tv_zxkf_women.setOnClickListener {
            pushCustomerMessage(this, getLocalUserId(), 5, "", next = {
                chatService(this)
            })
        }

        ll_free_rz.setOnClickListener {
//            ns_auth_women.fullScroll(ScrollView.FOCUS_DOWN)
              var mWomenAuthDialog = WomenAuthDialog()
              mWomenAuthDialog.show(supportFragmentManager,"womenAuthDialog")
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
    }

    private fun getDateCount() {
//        Request.getDateSuccessCount().request(this) { _, data ->
//            tv_date_count.text = String.format("目前已有%s人在D6约会成功", data?.asString ?: "1000")
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}