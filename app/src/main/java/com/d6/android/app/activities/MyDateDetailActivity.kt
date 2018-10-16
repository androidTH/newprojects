package com.d6.android.app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_mydate_details.*

class MyDateDetailActivity : BaseActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var myAppointment:MyAppointment? = null
    private val mImages = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mydate_details)
        titlebar_datedetails.titleView.setText("我的约会")
        myAppointment = (intent.getParcelableExtra("data") as MyAppointment)
        if(myAppointment !=null){
            getData(myAppointment!!.sAppointmentSignupId, myAppointment!!.sId.toString());
        }

        updateUI()
    }

    fun updateUI(){
        myAppointment?.let {
            tv_mydate_desc.text = it.sDesc
            tv_address_name.text =it.sPlace
            rv_mydate_detailsimgs.setHasFixedSize(true)
            rv_mydate_detailsimgs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            if (it.sAppointPic.isNullOrEmpty()) {
                rv_mydate_detailsimgs.gone()
            }else{
                rv_mydate_detailsimgs.visible()
                val images = it.sAppointPic?.split(",")
                if (images != null) {
                    mImages.addAll(images.toList())
                }
                rv_mydate_detailsimgs.adapter = SelfReleaselmageAdapter(mImages,1)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun getData(sAppointmentSignupId:String,sAppointmentId:String){
        Request.getAppointDetails(userId,sAppointmentSignupId, sAppointmentId).request(this,success={msg, data->
            if (data != null) {
                when (data.iStatus) {
                    1 -> {//
                        tv_date_status.text="状态：发起"
                        if(TextUtils.equals(userId,data.iAppointUserid.toString())){
                            tv_private_chat.visibility = View.GONE;
                            tv_no_date.visibility = View.GONE
                            tv_agree_date.visibility = View.GONE
                            tv_waiting_agree.visibility = View.VISIBLE
                            tv_giveup_date.visibility = View.VISIBLE
                        }else{
                            tv_no_date.visibility = View.VISIBLE
                            tv_agree_date.visibility = View.VISIBLE
                            tv_waiting_agree.visibility = View.GONE
                            tv_giveup_date.visibility = View.GONE
                        }
                    }
                    2 -> { //
                        tv_date_status.text="状态：待同意"
                        tv_private_chat.visibility = View.GONE;
                        tv_no_date.visibility = View.VISIBLE
                        tv_agree_date.visibility = View.VISIBLE
                    }
                    3 -> { //
                        //tv_action0.text = "对方已关闭约会"
                        tv_date_status.text="状态：已拒绝"
                        tv_private_chat.visibility = View.GONE;
                        tv_no_date.visibility = View.GONE
                        tv_agree_date.visibility = View.GONE
                    }
                    4 -> { //
                        tv_date_status.text="状态：主动取消"
                        tv_private_chat.visibility = View.GONE;
                        tv_no_date.visibility = View.GONE
                        tv_agree_date.visibility = View.GONE
                    }
                    5 -> { //
                        tv_date_status.text="状态：过期自动取消"
                        tv_private_chat.visibility = View.GONE;
                        tv_no_date.visibility = View.GONE
                        tv_agree_date.visibility = View.GONE
                    }
                }
            }
        })
    }
}
