package com.d6.android.app.activities

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.WeChatKFDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_member.*
import kotlinx.android.synthetic.main.include_member.*


/**
 * 会员页面
 */
class MemberActivity : BaseActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sLoginToken  by lazy{
        SPUtils.instance().getString(Const.User.SLOGINTOKEN)
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


        ll_lianxikf.setOnClickListener {
            pushCustomerMessage(this, getLocalUserId(), 5, "", next = {
                chatService(this)
            })
        }

        tv_wechat_kf.setOnClickListener {
             var mWeChatKfDialog = WeChatKFDialog()
             mWeChatKfDialog.show(supportFragmentManager,"wechatkf")
        }

        getUserInfo()
    }

    private fun getMemberLevel(userclassId:String?,sex:String?) {
        Request.findUserClasses(sLoginToken).request(this){msg,data->
            data?.list?.let {
                Log.i("mem","数量${it.size}")
                if(TextUtils.equals("0",sex.toString())){
                    var memberBean = it.get(0)
                    if(TextUtils.equals(memberBean.ids.toString(),userclassId.toString())){

                        memberBean.sDesc?.let {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                tv_men_memberdesc.text = Html.fromHtml(it.replace("FF4B00", "666666"),Html.FROM_HTML_MODE_COMPACT)
                            }else{
                                tv_men_memberdesc.text = Html.fromHtml(it.replace("FF4B00", "666666"))
                            }
                        }
                        tv_data_address.visibility= View.GONE
                        tv_mem_memberztnums.visibility = View.GONE
                        view_line.visibility = View.GONE
                        view_line02.visibility = View.GONE
                        tv_men_member_remark.visibility = View.GONE
                    }
                }else{
                    it.forEach {
                        if(TextUtils.equals(it.ids.toString(),userclassId.toString())){
                            if(TextUtils.isEmpty(it.sRemark)){
                                view_line02.visibility = View.GONE
                                tv_men_member_remark.visibility = View.GONE
                            }else{
                                view_line02.visibility = View.VISIBLE
                                tv_men_member_remark.visibility = View.VISIBLE
                                tv_men_member_remark.text = it.sRemark
                            }

                            it.sDesc?.let {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    tv_men_memberdesc.text = Html.fromHtml(it.replace("FF4B00", "666666"),Html.FROM_HTML_MODE_COMPACT)
                                }else{
                                    tv_men_memberdesc.text = Html.fromHtml(it.replace("FF4B00", "666666"))
                                }
                            }

                            tv_mem_memberztnums.visibility = View.VISIBLE
                            tv_data_address.visibility =View.VISIBLE
                            view_line.visibility = View.VISIBLE

                            if(TextUtils.equals("1",sex.toString())){
                                tv_data_address.text = it.sServiceArea
                                AppUtils.setMemberNums(this, 2,"直推次数: " + it.iRecommendCount!!, 0, 5, tv_mem_memberztnums)
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
                 tv_viplevel.text = data.classesname
                 data.dUserClassEndTime?.let {
                     if(it>0){
                         tv_vipendtime.text = "到期时间：${data.dUserClassEndTime.toTime(timeFormat)}"
                     }else{
                         tv_vipendtime.text = ""
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