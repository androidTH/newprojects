package com.d6.android.app.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.extentions.request
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_mydate_details.*
import kotlinx.android.synthetic.main.item_list_date_status.*

/**
 * 约会详情页
 */
class MyDateDetailActivity : BaseActivity() {
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private lateinit var myAppointment:MyAppointment
    private val mImages = ArrayList<String>()
    private var iAppointUserid:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mydate_details)
        immersionBar.init()
        titlebar_datedetails.titleView.setText("我的约会")
        myAppointment = (intent.getParcelableExtra("data") as MyAppointment)
        if(myAppointment !=null){
            iAppointUserid = myAppointment!!.iAppointUserid.toString()
            if(myAppointment!!.sAppointmentSignupId.isNotEmpty()){
                getData(myAppointment!!.sAppointmentSignupId,"")
            }else{
                getData("",myAppointment!!.sId.toString());
            }
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

        tv_no_date.setOnClickListener {
            updateDateStatus(myAppointment!!.sAppointmentSignupId,3)
        }

        tv_agree_date.setOnClickListener {
            updateDateStatus(myAppointment!!.sAppointmentSignupId,2)
        }

        tv_giveup_date.setOnClickListener {
            updateDateStatus(myAppointment!!.sAppointmentSignupId,4)
        }

        tv_private_chat.setOnClickListener {
            isAuthUser {
                myAppointment?.let {
                    val name = it.sAppointUserName ?: ""
                    if(it.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(iAppointUserid,userId)){
                        checkChatCount(it.iUserid.toString()) {
                            RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, it.iUserid.toString(), name)
                        }
                    }else if(it.sAppointmentSignupId.isNotEmpty()){
                        checkChatCount(it.iAppointUserid.toString()) {
                            RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, it.iAppointUserid.toString(), name)
                        }
                    }
                }
            }
        }

    }

    private fun getData(sAppointmentSignupId:String,sAppointmentId:String){

        Request.getAppointDetails(userId,sAppointmentSignupId, sAppointmentId).request(this,success={msg, data->
            if (data != null) {
                if(data.iPoint == null){
                    tv_point_nums.visibility = View.GONE
                }else{
                    tv_point_nums.text="预付${data.iPoint}积分"
                }
                when (data.iStatus) {
                    1 -> {//
                        if(data!!.sAppointmentSignupId.isNullOrEmpty()&&TextUtils.equals(iAppointUserid,userId)){
                            tv_private_chat.visibility = View.GONE;
                            tv_no_date.visibility = View.GONE
                            tv_agree_date.visibility = View.GONE
                            tv_giveup_date.visibility = View.GONE
                            tv_date_status.text="状态：发起"

                            rel_0.visibility = View.VISIBLE
                            rel_1.visibility = View.GONE
                            rel_2.visibility = View.GONE
                            rel_3.visibility = View.GONE

                            rel0_line.visibility = View.GONE
                            headView0.setImageURI(data.sAppointmentPicUrl)
                            tv_name0.text = getSpannable("${data.sAppointUserName}:发布约会",4)
                            tv_days0.text = data.dCreatetime.interval()//约会发布时间

                        }else if(data.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(iAppointUserid,userId)){
                            tv_date_status.text="状态：待同意"
                            tv_no_date.visibility = View.VISIBLE
                            tv_agree_date.visibility = View.VISIBLE
                            tv_private_chat.visibility = View.GONE
                            tv_giveup_date.visibility = View.GONE

                            setAgreeDate(data,data.dAppointmentSignupCreatetime,"待同意",3,true)

                        }else if(data.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(userId,data.iUserid.toString())){
                            tv_date_status.text="状态：等待对方同意"
                            tv_no_date.visibility = View.GONE
                            tv_agree_date.visibility = View.GONE
                            tv_private_chat.visibility = View.GONE
                            tv_giveup_date.visibility = View.VISIBLE

                            setAgreeDate(data,data.dAppointmentSignupCreatetime,"待同意",3,true)
                        }
                    }
                    2 -> { //
                        tv_date_status.text="状态:私聊"
                        tv_private_chat.visibility = View.VISIBLE
                        tv_no_date.visibility = View.GONE
                        tv_agree_date.visibility = View.GONE
                        tv_giveup_date.visibility = View.GONE
                        setDateStatus(data)
                        tv_point_nums.text="消费${data.iPoint}积分"
                    }
                    3 -> { //
                        //tv_action0.text = "对方已关闭约会"
                        tv_date_status.text="状态：已拒绝"
                        tv_private_chat.visibility = View.GONE;
                        tv_no_date.visibility = View.GONE
                        tv_agree_date.visibility = View.GONE
                        tv_giveup_date.visibility = View.GONE
                        setAgreeDate(data,data.dAppointmentSignupUpdatetime,"已拒绝",3)
                        tv_point_nums.text="消费${data.iPoint}积分"
                    }
                    4 -> { //
                        tv_date_status.text="状态：主动取消"
                        tv_private_chat.visibility = View.GONE;
                        tv_no_date.visibility = View.GONE
                        tv_agree_date.visibility = View.GONE
                        tv_giveup_date.visibility = View.GONE

                        setAgreeDate(data,data.dAppointmentSignupUpdatetime,"主动取消",4)
                    }
                    5 -> { //
                        tv_date_status.text="状态：过期自动取消"
                        tv_private_chat.visibility = View.GONE;
                        tv_no_date.visibility = View.GONE
                        tv_agree_date.visibility = View.GONE
                        tv_giveup_date.visibility = View.GONE

                        rel_0.visibility = View.VISIBLE
                        rel_1.visibility = View.VISIBLE
                        rel_2.visibility = View.GONE
                        rel_3.visibility = View.GONE
                        rel1_line.visibility = View.GONE

                        headView0.setImageURI(data.sAppointmentPicUrl)
                        tv_name0.text = getSpannable("${data.sAppointUserName}:发布约会",4)
                        tv_days0.text = data.dCreatetime.interval()//约会发布时间

                        headView1.setImageURI(data.sAppointmentPicUrl)
                        tv_name1.text =  getSpannable("${data.sAppointUserName}:过期自动取消",6)
                        tv_days1.text = data.dAppointmentSignupUpdatetime.interval()//报名约会时间

                        tv_point_nums.text="已返还${data.iPoint}积分"
                    }
                }
                myAppointment = data
            }
        })
    }

    fun setAgreeDate(data:MyAppointment,updateTime:Long,str:String,len:Int,flag:Boolean =false){
        rel_0.visibility = View.VISIBLE
        rel_1.visibility = View.VISIBLE
        if(flag){
            rel_2.visibility = View.GONE
            rel1_line.visibility = View.GONE
        }else{
            rel_2.visibility = View.VISIBLE
            rel1_line.visibility = View.VISIBLE
        }

        rel_3.visibility = View.GONE

        rel2_line.visibility = View.GONE

        headView0.setImageURI(data.sAppointmentPicUrl)
        tv_name0.text = getSpannable("${data.sAppointUserName}:发布约会",4)
        tv_days0.text = data.dCreatetime.interval()//约会发布时间//stampToTime(data.dCreatetime)

        headView1.setImageURI(data.sPicUrl)
        tv_name1.text =  getSpannable("${data.sUserName}:发起邀约",4)
        tv_days1.text = data.dAppointmentSignupCreatetime.interval()//报名约会时间

        headView2.setImageURI(data.sPicUrl)
        tv_name2.text = getSpannable("${data.sUserName}:${str}",len)
        tv_days2.text = updateTime.interval() //同意约会时间
    }

    fun setDateStatus(data:MyAppointment){
        rel_0.visibility = View.VISIBLE
        rel_1.visibility = View.VISIBLE
        rel_2.visibility = View.VISIBLE
        rel_3.visibility = View.VISIBLE
        headView0.setImageURI(data.sAppointmentPicUrl)
        tv_name0.text = getSpannable("${data.sAppointUserName}:发布约会",4);
        tv_days0.text = data.dCreatetime.interval() //约会发布时间

        headView1.setImageURI(data.sPicUrl)
        tv_name1.text = getSpannable("${data.sUserName}:发起邀约",4)
        tv_days1.text = data.dAppointmentSignupCreatetime.interval()//报名约会时间

        headView2.setImageURI(data.sAppointmentPicUrl)
        tv_name2.text = getSpannable("${data.sAppointUserName}:同意",2)
        tv_days2.text = data.dAppointmentSignupUpdatetime.interval() //同意约会时间

        headView3.setImageURI(data.sPicUrl)
        tv_name3.text = getSpannable("${data.sUserName}:赴约",2)
        tv_days3.text = data.dAppointmentSignupUpdatetime.interval()
    }

    fun updateDateStatus(sAppointmentSignupId:String,iStatus:Int){
        Request.updateDateStatus(sAppointmentSignupId,iStatus,"").request(this, success = {msg, data->
            run {
                if (iStatus == 2) {
                    tv_date_status.text = "状态:赴约"
                    tv_no_date.visibility = View.GONE
                    tv_agree_date.visibility = View.GONE
                    tv_private_chat.visibility = View.VISIBLE
                    tv_giveup_date.visibility = View.GONE
                    myAppointment.dAppointmentSignupUpdatetime = System.currentTimeMillis()
                    setDateStatus(myAppointment)
                    tv_point_nums.text="消费${myAppointment.iPoint}积分"
                } else if (iStatus == 3) {
                    tv_date_status.text = "状态：已拒绝"
                    tv_private_chat.visibility = View.GONE;
                    tv_no_date.visibility = View.GONE
                    tv_agree_date.visibility = View.GONE
                    tv_giveup_date.visibility = View.GONE
                    setAgreeDate(myAppointment,System.currentTimeMillis(),"已拒绝",3)
                    tv_point_nums.text="消费${myAppointment.iPoint}积分"
                }else if(iStatus == 4){
                    tv_date_status.text="状态：主动取消"
                    tv_private_chat.visibility = View.GONE;
                    tv_no_date.visibility = View.GONE
                    tv_agree_date.visibility = View.GONE
                    tv_giveup_date.visibility = View.GONE
                    setAgreeDate(myAppointment,System.currentTimeMillis(),"主动取消",4)
                }
            }
        })
    }

    fun getSpannable(str:String,len:Int):SpannableStringBuilder{
        return SpanBuilder(str)
                .click(str.length - len, str.length, MClickSpan(this))
                .build()
    }
    private class MClickSpan(val context: Context) : ClickableSpan() {

        override fun onClick(p0: View?) {

        }

        override fun updateDrawState(ds: TextPaint?) {
            ds?.color = ContextCompat.getColor(context, R.color.color_333333)
            ds?.isUnderlineText = false
        }
    }
}
