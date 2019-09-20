package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.AuthTipsQuickAdapter
import com.d6.android.app.adapters.TeQuanQuickAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.WeChatKFDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MemberServiceBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.GridItemDecoration
import kotlinx.android.synthetic.main.activity_member.*
import kotlinx.android.synthetic.main.include_member.*


/**
 * 会员页面
 */
class MemberActivity : BaseActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private var mListTQ = ArrayList<MemberServiceBean>()
    private val mTeQuanQuickAdapter by lazy{
        TeQuanQuickAdapter(mListTQ)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)

        immersionBar
                .fitsSystemWindows(false)
                .statusBarColor(R.color.trans_parent)
                .titleBar(tv_member_back)
                .init()

        tv_member_back.setOnClickListener {
            finish()
        }


//        ll_lianxikf.setOnClickListener {
//            //8、会员联系客服
//            pushCustomerMessage(this, getLocalUserId(), 8, "", next = {
//                chatService(this)
//            })
//        }

        tv_wechat_zskf.setOnClickListener {
             var mWeChatKfDialog = WeChatKFDialog()
             mWeChatKfDialog.show(supportFragmentManager,"wechatkf")
        }


        setData()
        rv_member_tq.setHasFixedSize(true)
        rv_member_tq.layoutManager = GridLayoutManager(this, 3) as RecyclerView.LayoutManager?
        rv_member_tq.adapter = mTeQuanQuickAdapter
        var divider = GridItemDecoration.Builder(this)
                .setHorizontalSpan(R.dimen.margin_1)
                .setVerticalSpan(R.dimen.margin_1)
                .setColorResource(R.color.color_F5F5F5)
                .setShowLastLine(false)
                .setShowVerticalLine(false)
                .build()
        rv_member_tq.addItemDecoration(divider)
        rv_member_tq.isNestedScrollingEnabled = false
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
        getUserInfo()
    }

    private fun getMemberLevel(userclassId:String?,sex:String?) {
        Request.findUserClasses(getLoginToken()).request(this){ msg, data->
            data?.list?.let {
                it.forEach {
                        if(TextUtils.equals(it.ids.toString(),userclassId.toString())){
                            tv_viptq.text = "会员可享受超12项特权"
                            if(TextUtils.equals("0",sex.toString())){
                                it.sDesc?.let {
                                    var mTipsData = it.split("<br/>")
                                    rv_men_memberdesc.setHasFixedSize(true)
                                    rv_men_memberdesc.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                                    rv_men_memberdesc.adapter = AuthTipsQuickAdapter(mTipsData)
                                }
                                tv_data_address.visibility= View.GONE
                                tv_mem_memberztnums.visibility = View.GONE
                                view_line.visibility = View.GONE
                                view_line02.visibility = View.GONE
                                tv_men_member_remark.visibility = View.GONE

                            }else{
                                if(TextUtils.isEmpty(it.sRemark)){
                                    view_line02.visibility = View.GONE
                                    tv_men_member_remark.visibility = View.GONE
                                }else{
                                    view_line02.visibility = View.VISIBLE
                                    tv_men_member_remark.visibility = View.VISIBLE
                                    tv_men_member_remark.text = it.sRemark
                                }

                                it.sDesc?.let {
                                    var mTipsData = it.split("<br/>")
                                    rv_men_memberdesc.setHasFixedSize(true)
                                    rv_men_memberdesc.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                                    rv_men_memberdesc.adapter = AuthTipsQuickAdapter(mTipsData)
                                }

                                if(TextUtils.equals("1",sex.toString())){
                                    tv_data_address.text = it.sServiceArea
                                    if(it.iRecommendCount!!>0){
                                        view_line.visibility = View.VISIBLE
                                        tv_mem_memberztnums.visibility = View.VISIBLE
                                        AppUtils.setMemberNums(this, 2,"直推次数: " + it.iRecommendCount!!, 0, 5, tv_mem_memberztnums)
                                    }

                                    if(!TextUtils.isEmpty(it.sServiceArea)){
                                        tv_data_address.visibility =View.VISIBLE
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    private fun getUserInfo() {
        Request.getUserInfo("",userId).request(this, success = { _, data ->
             data?.let {
                 vipheaderview.setImageURI(data.picUrl)
                 if(TextUtils.equals(data.userclassesid,"29")){
                     tv_viplevel.text = "高级会员"
                 }else if(TextUtils.equals(data.userclassesid,"27")){
                     tv_viplevel.text = "初级会员"
                 }else{
                     tv_viplevel.text = data.classesname
                 }
                 data.dUserClassEndTime?.let {
                     if(it>0){
                         if(TextUtils.equals("0",sex)){
                             tv_vipendtime.visibility = View.GONE
                         }else{
                             tv_vipendtime.visibility = View.VISIBLE
                             tv_vipendtime.text = "到期时间：${data.dUserClassEndTime.toTime(timeFormat)}"
                         }
                     }else{
                         tv_vipendtime.visibility = View.GONE
                     }
                 }
                 getMemberLevel(data.userclassesid,it.sex)
             }
        }) { _, _ ->
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }
}