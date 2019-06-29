package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
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
    private var wanshanziliao = 0 //资料完成度

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
            tv_vipinfo.text = "免费认证开通会员"
            tv_d6vipinfo.text = "D6定位高端私密私人定制交友社区，以高品质、高素质的会员为基础，全球拥有60000+的优质华人会员。平台将实行会员制，成为会员后即可享受私人定制服务"
        }else{
            tv_vipinfo.text ="成为会员后可使用此功能"
             tv_d6vipinfo.text = "D6定位高端私密私人定制交友社区，以高品质、高素质的会员为基础，全球拥有60000+的优质华人会员。平台将实行会员制，成为会员后即可享受私人定制服务"
        }

        mComments.add(MemberComment(getString(R.string.string_women_firstcomment),
                BASE_URL+"static/image/0074V8z6ly8g1v3pxqs6jj30ro0rojte.jpg"))
        mComments.add(MemberComment(getString(R.string.string_women_secondcomment),
                BASE_URL+"static/image/700a69f8ly8g0fj1kcfdbj20u00u00vy.jpg"))
        mComments.add(MemberComment(getString(R.string.string_women_lastcomment),
                BASE_URL+"static/image/9ba8d31djw8f9ocv5yysfj20e80doaar.jpg"))

        setMemeberComemnt()
    }

    override fun onResume() {
        super.onResume()
        member_banner.startTurning()
        getUserInfoPercent()
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
                }
            }
        }
    }


    private fun getUserInfoPercent(){
        Request.getAuthState(getLocalUserId()).request(this,false, success = { _, data ->
            if (data != null) {
                wanshanziliao = data.optDouble("wanshanziliao").toInt()*10
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (wanshanziliao < 80) {
            pushCustomerMessage(this, getLocalUserId(), 7, "") {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}