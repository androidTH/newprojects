package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.WeChatKFDialog
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

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

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

        tv_wechat_kf.setOnClickListener {
             var mWeChatKfDialog = WeChatKFDialog()
             mWeChatKfDialog.show(supportFragmentManager,"wechatkf")
        }

        getUserInfo()
    }

    private fun getMemberLevel(userclassId:String?,sex:String?) {
        Request.findUserClasses().request(this){msg,data->
            data?.list?.let {
                Log.i("mem","数量${it.size}")
                it.forEach {
                    if(TextUtils.equals(it.ids.toString(),userclassId)){
                        tv_data_address.text = it.sRemark
                        tv_desc.text = it.sDesc
                        if(TextUtils.equals("1",sex.toString())){
                            tv_data_address.text = it.sServiceArea
                            AppUtils.setMemberNums(this, "直推次数: " + it.iRecommendCount!!, 0, 5, tv_ztnums)
                        }else{
                            tv_data_address.visibility= View.GONE
                            tv_ztnums.visibility = View.GONE
                            view_line.visibility = View.GONE
                        }
                    }

                    if (TextUtils.equals(it.ids.toString(), "27")) {

                    } else if (TextUtils.equals(it.ids.toString(), "28")) {

                    } else if (TextUtils.equals(it.ids.toString(), "29")) {

                    } else if (TextUtils.equals(it.ids.toString(), "22")) {

                    } else if (TextUtils.equals(it.ids.toString(), "23")) {

                    } else if (TextUtils.equals(it.ids.toString(), "24")) {

                    } else if (TextUtils.equals(it.ids.toString(), "25")) {

                    } else if (TextUtils.equals(it.ids.toString(), "26")) {

                    } else if (TextUtils.equals(it.ids.toString(), "7")) {

                    } else if(TextUtils.equals(it.ids.toString(), "30")){

                    }else {

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
                 tv_vipendtime.text = "到期时间："
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