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
import com.d6.android.app.models.MemberTeQuan
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

    private var mListTQ = ArrayList<MemberTeQuan>()
    private val mTeQuanQuickAdapter by lazy{
        TeQuanQuickAdapter(mListTQ,true)
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

        rv_member_tq.setHasFixedSize(true)
        rv_member_tq.layoutManager = GridLayoutManager(this, 3) as RecyclerView.LayoutManager?
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

    override fun onResume() {
        super.onResume()
        getUserInfo()
    }

    private fun getMemberLevel(userclassId:String?,sex:String?) {
        Request.findYKUserClasses("${userclassId}",getLoginToken()).request(this){ msg, data->
            data?.let {
                mListTQ = it.lstMembers as ArrayList<MemberTeQuan>
                if(SPUtils.instance().getBoolean(Const.User.ISNOTFREECHATTAG, false)){
                    mListTQ.removeAt(5)
                    mListTQ.removeAt(9)
                    mListTQ.removeAt(11)
                }
                tv_viptq.text = "可享受超${mListTQ.size}项特权"
                rv_member_tq.adapter = mTeQuanQuickAdapter
            }
        }

//        Request.findUserClasses(getLoginToken()).request(this){ msg, data->
//            data?.list?.let {
//                it.forEach {
//                        if(TextUtils.equals(it.ids.toString(),userclassId.toString())){
//                            tv_viptq.text = "会员可享受超12项特权"
//                            if(TextUtils.equals("0",sex.toString())){
//                                it.sDesc?.let {
//                                    var mTipsData = it.split("<br/>")
//                                    rv_men_memberdesc.setHasFixedSize(true)
//                                    rv_men_memberdesc.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
//                                    rv_men_memberdesc.adapter = AuthTipsQuickAdapter(mTipsData)
//                                }
//                                tv_data_address.visibility= View.GONE
//                                tv_mem_memberztnums.visibility = View.GONE
//                                view_line.visibility = View.GONE
//                                view_line02.visibility = View.GONE
//                                tv_men_member_remark.visibility = View.GONE
//
//                            }else{
//                                if(TextUtils.isEmpty(it.sRemark)){
//                                    view_line02.visibility = View.GONE
//                                    tv_men_member_remark.visibility = View.GONE
//                                }else{
//                                    view_line02.visibility = View.VISIBLE
//                                    tv_men_member_remark.visibility = View.VISIBLE
//                                    tv_men_member_remark.text = it.sRemark
//                                }
//
//                                it.sDesc?.let {
//                                    var mTipsData = it.split("<br/>")
//                                    rv_men_memberdesc.setHasFixedSize(true)
//                                    rv_men_memberdesc.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
//                                    rv_men_memberdesc.adapter = AuthTipsQuickAdapter(mTipsData)
//                                }
//
//                                if(TextUtils.equals("1",sex.toString())){
//                                    tv_data_address.text = it.sServiceArea
//                                    if(it.iRecommendCount!!>0){
//                                        view_line.visibility = View.VISIBLE
//                                        tv_mem_memberztnums.visibility = View.VISIBLE
//                                        AppUtils.setMemberNums(this, 2,"直推次数: " + it.iRecommendCount!!, 0, 5, tv_mem_memberztnums)
//                                    }
//
//                                    if(!TextUtils.isEmpty(it.sServiceArea)){
//                                        tv_data_address.visibility =View.VISIBLE
//                                    }
//                                }
//                            }
//                            mListTQ = it.lstMembers as ArrayList<MemberTeQuan>
//                            rv_member_tq.adapter = mTeQuanQuickAdapter
//                        }
//
//                }
//            }
//        }
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
                     if (it > 0) {
                         if (TextUtils.equals(data.userclassesid, "31")) {
                             tv_vipendtime.visibility = View.VISIBLE
                             tv_vipendtime.text = "有效期：${data.dUserClassEndTime.toTime(timeFormat)}"
                         } else {
                             tv_vipendtime.visibility = View.GONE
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