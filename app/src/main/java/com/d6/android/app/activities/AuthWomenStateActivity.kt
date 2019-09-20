package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.AuthTipsQuickAdapter
import com.d6.android.app.adapters.MemberCommentHolder
import com.d6.android.app.adapters.TeQuanQuickAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.WomenAuthDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.AddImage
import com.d6.android.app.models.MemberComment
import com.d6.android.app.models.MemberServiceBean
import com.d6.android.app.net.API.BASE_URL
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.GridItemDecoration
import com.d6.android.app.widget.convenientbanner.holder.CBViewHolderCreator
import com.d6.android.app.widget.convenientbanner.listener.OnPageChangeListener
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_auth_women_state.*
import kotlinx.android.synthetic.main.layout_auth_top.*
import org.jetbrains.anko.startActivity
import java.lang.reflect.Member


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

    private var mListTQ = ArrayList<MemberServiceBean>()
    private val mTeQuanQuickAdapter by lazy{
        TeQuanQuickAdapter(mListTQ)
    }

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
            tv_d6vipinfo.text = resources.getString(R.string.string_d6_desc)
        }else{
            tv_vipinfo.text ="成为会员后可使用此功能"
             tv_d6vipinfo.text = resources.getString(R.string.string_d6_desc)
        }

        mComments.add(MemberComment(getString(R.string.string_women_firstcomment),
                BASE_URL+"static/image/0074V8z6ly8g1v3pxqs6jj30ro0rojte.jpg"))
        mComments.add(MemberComment(getString(R.string.string_women_secondcomment),
                BASE_URL+"static/image/700a69f8ly8g0fj1kcfdbj20u00u00vy.jpg"))
        mComments.add(MemberComment(getString(R.string.string_women_lastcomment),
                BASE_URL+"static/image/9ba8d31djw8f9ocv5yysfj20e80doaar.jpg"))

        setMemeberComemnt()

        setData()
        rv_grid_tq.setHasFixedSize(true)
        rv_grid_tq.layoutManager = GridLayoutManager(this, 3) as RecyclerView.LayoutManager?
        rv_grid_tq.adapter = mTeQuanQuickAdapter
        var divider = GridItemDecoration.Builder(this)
                .setHorizontalSpan(R.dimen.margin_1)
                .setVerticalSpan(R.dimen.margin_1)
                .setColorResource(R.color.color_F5F5F5)
                .setShowLastLine(false)
                .setShowVerticalLine(false)
                .build()
        rv_grid_tq.addItemDecoration(divider)
        rv_grid_tq.isNestedScrollingEnabled = false
    }

    private fun setData(){
        var mService = MemberServiceBean("0")
        mService.mResId = R.mipmap.vippage_tequan_sf
        mService.mClassName = "会员身份"
        mService.mClassDesc = "显示“会员”专属身份随处可见"
        mService.mClassTag = "APP"
        mService.mClassType = 1


        var mService1 = MemberServiceBean("0")
        mService1.mResId = R.mipmap.vippage_tequan_dt
        mService1.mClassName = "发布动态"
        mService1.mClassDesc = "可发布动态与优质会员互动"
        mService1.mClassTag = "APP"
        mService1.mClassType = 1

        var mService2 = MemberServiceBean("0")
        mService2.mResId = R.mipmap.vippage_tequan_yh
        mService2.mClassName = "自主约会"
        mService2.mClassDesc = "发起约会或申请赴约拒绝低效"
        mService2.mClassTag = "APP"
        mService2.mClassType = 1


        var mService3 = MemberServiceBean("0")
        mService3.mResId = R.mipmap.vippage_tequan_wxq
        mService3.mClassName = "微信群"
        mService3.mClassDesc = "包含地区群、字母、游戏、健身等特色群"
        mService3.mClassTag = "微信"
        mService3.mClassType = 2


        var mService4 = MemberServiceBean("0")
        mService4.mResId = R.mipmap.vippage_tequan_yzhy
        mService4.mClassName = "优质会员群"
        mService4.mClassDesc = "群内女生颜值高，男生均为钻石及以上会员"
        mService4.mClassTag = "微信"
        mService4.mClassType = 2


        var mService5 = MemberServiceBean("0")
        mService5.mResId = R.mipmap.vippage_tequan_pyq
        mService5.mClassName = "朋友圈广告"
        mService5.mClassDesc = "赠送朋友圈广告广而告之"
        mService5.mClassTag = "微信"
        mService5.mClassType = 2


        var mService6 = MemberServiceBean("0")
        mService6.mResId = R.mipmap.vippage_tequan_hy
        mService6.mClassName = "会员入档"
        mService6.mClassDesc = "制作会员卡片，上传至官网、APP，并入档"
        mService6.mClassTag = "人工"
        mService6.mClassType = 3


        var mService7 = MemberServiceBean("0")
        mService7.mResId = R.mipmap.vippage_tequan_lm
        mService7.mClassName = "撩妹培训"
        mService7.mClassDesc = "可参加PUA撩妹教学免费培训"
        mService7.mClassTag = "人工"
        mService7.mClassType = 3

        var mService8 = MemberServiceBean("0")
        mService8.mResId = R.mipmap.vippage_tequan_xxhd
        mService8.mClassName = "线下活动"
        mService8.mClassDesc = "可参与各类线下娱乐活动"
        mService8.mClassTag = "人工"
        mService8.mClassType = 3

        mListTQ.add(mService)
        mListTQ.add(mService1)
        mListTQ.add(mService2)
        mListTQ.add(mService3)
        mListTQ.add(mService4)
        mListTQ.add(mService5)
        mListTQ.add(mService6)
        mListTQ.add(mService7)
        mListTQ.add(mService8)
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