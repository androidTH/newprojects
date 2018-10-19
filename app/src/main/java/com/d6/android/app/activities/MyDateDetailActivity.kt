package com.d6.android.app.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.activity_mydate_details.*
import kotlinx.android.synthetic.main.item_list_date_status.*

class MyDateDetailActivity : BaseActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private var myAppointment:MyAppointment? = null
    private val mImages = ArrayList<String>()
    private var iAppointUserid:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mydate_details)
        titlebar_datedetails.titleView.setText("我的约会")
        myAppointment = (intent.getParcelableExtra("data") as MyAppointment)
        if(myAppointment !=null){
            iAppointUserid = myAppointment!!.iAppointUserid.toString()
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

        tv_waiting_agree.setOnClickListener {
            updateDateStatus(myAppointment!!.sAppointmentSignupId,2)
        }
        tv_giveup_date.setOnClickListener {
            updateDateStatus(myAppointment!!.sAppointmentSignupId,4)
        }
        tv_no_date.setOnClickListener {
            updateDateStatus(myAppointment!!.sAppointmentSignupId,3)
        }

    }

    private fun getData(sAppointmentSignupId:String,sAppointmentId:String){

        Request.getAppointDetails(userId,sAppointmentSignupId, sAppointmentId).request(this,success={msg, data->
            if (data != null) {
                if(data.iStatus == 1){
                    rel_0.visibility = View.VISIBLE
                    rel_1.visibility = View.GONE
                    rel_2.visibility = View.GONE
                    rel_3.visibility = View.GONE
                    headView0.setImageURI(data.sAppointmentPicUrl)
                    tv_name0.text = data.sAppointUserName
                    tv_days0.text = data.dCreatetime.toString().parserTime("yyyy-MM-dd").toString()//约会发布时间

                }else if(data.iStatus == 2){
                    rel_0.visibility = View.VISIBLE
                    rel_1.visibility = View.VISIBLE
                    rel_2.visibility = View.GONE
                    rel_3.visibility = View.GONE
                    headView0.setImageURI(data.sAppointmentPicUrl)
                    tv_name0.text = data.sAppointUserName
                    tv_days0.text = data.dCreatetime.toString().parserTime("yyyy-MM-dd").toString()//约会发布时间

                    headView1.setImageURI(data.sPicUrl)
                    tv_name1.text = data.sUserName
                    tv_days1.text = data.dAppointmentSignupCreatetime.toString().parserTime("yyyy-MM-dd").toString()//报名约会时间

                }else if(data.iStatus == 3){
                    rel_0.visibility = View.VISIBLE
                    rel_1.visibility = View.VISIBLE
                    rel_2.visibility = View.VISIBLE
                    rel_3.visibility = View.VISIBLE
                    headView0.setImageURI(data.sAppointmentPicUrl)
                    tv_name0.text = data.sAppointUserName
                    tv_days0.text = data.dCreatetime.toString().parserTime("yyyy-MM-dd").toString()//约会发布时间

                    headView1.setImageURI(data.sPicUrl)
                    tv_name1.text = data.sUserName
                    tv_days1.text = data.dAppointmentSignupCreatetime.toString().parserTime("yyyy-MM-dd").toString()//报名约会时间

                    headView2.setImageURI(data.sAppointmentPicUrl)
                    tv_name2.text = data.sAppointUserName
                    tv_days2.text = data.dStarttime.toString().parserTime("yyyy-MM-dd").toString()//同意约会时间
                    headView3.setImageURI(data.sPicUrl)
                    tv_name3.text = data.sUserName
                    tv_days3.text = data.dUpdatetime.toString().parserTime("yyyy-MM-dd").toString()
                }

                when (data.iStatus) {
                    1 -> {//
                        tv_date_status.text="状态：发起"
                        if(TextUtils.equals(userId, iAppointUserid)){
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

    fun updateDateStatus(sAppointmentSignupId:String,iStatus:Int){
        Request.updateDateStatus(sAppointmentSignupId,iStatus,"").request(this, success = {msg, data->{

        }})
    }
}
