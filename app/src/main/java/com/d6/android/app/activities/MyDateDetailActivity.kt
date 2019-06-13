package com.d6.android.app.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.*
import com.d6.android.app.extentions.request
import com.d6.android.app.models.IntegralExplain
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.FROM_MY_CHATDATE
import com.d6.android.app.widget.CustomToast
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_mydate_details.*
import kotlinx.android.synthetic.main.item_list_date_status.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

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
    private var iShareUserId:String=""
    private var explainAppoint = ""
    private var index=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mydate_details)
        immersionBar.init()
        titlebar_datedetails.titleView.setText("我的约会")
        titlebar_datedetails.addRightButton(rightId = R.mipmap.ic_more_orange, onClickListener = View.OnClickListener {
            val shareDialog = ShareFriendsDialog()
            shareDialog.arguments = bundleOf("from" to "myDateDetail","id" to iAppointUserid,"sResourceId" to myAppointment!!.sId.toString(),"sAppointmentSignupId" to myAppointment.sAppointmentSignupId)
            shareDialog.show(supportFragmentManager, "action")
            shareDialog.setDialogListener { p, s ->
                if (p == 0) {
                    startActivity<ReportActivity>("id" to myAppointment!!.sId.toString(), "tiptype" to "3")
                }else if(p==1){
                    val mDelMyDateDialog = DelDateDialogList()
                    mDelMyDateDialog.show(supportFragmentManager,"delMyDialog")
                    mDelMyDateDialog.setDialogListener { p, s ->
                        delMyDate()
                    }
                }
            }
        })

        var from= intent.getStringExtra("from")
        if(TextUtils.equals(from,Const.FROM_MY_DATESUCCESS)){
            //来自于约会
            var sId = intent.getStringExtra("sId")
            getData(sId,"")
        }else if(TextUtils.equals(from,FROM_MY_CHATDATE)){
            //来自于聊天
            myAppointment = (intent.getSerializableExtra("data") as MyAppointment)
            iShareUserId = intent.getStringExtra("iShareUserId")
            if(myAppointment !=null){
                iAppointUserid = myAppointment!!.iAppointUserid.toString()
                if(myAppointment!!.sAppointmentSignupId.isNotEmpty()){
                    getData(myAppointment!!.sAppointmentSignupId,"")
                }else{
                    getData("",myAppointment!!.sId.toString())
                }
            }
        }else{
            //来自于约会列表
            myAppointment = (intent.getSerializableExtra("data") as MyAppointment)
            index = intent.getIntExtra("index",0)
            if(myAppointment !=null){
                iAppointUserid = myAppointment!!.iAppointUserid.toString()
                if(myAppointment!!.sAppointmentSignupId.isNotEmpty()){
                    getData(myAppointment!!.sAppointmentSignupId,"")
                }else{
                    getData("",myAppointment!!.sId.toString())
                }
            }
        }

        getLocalFriendsCount()
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
                val images = it.sSourceAppointPic?.split(",")
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

        tv_send_date.setOnClickListener {
            isAuthUser {
                signUpDate(myAppointment)
            }
        }

        tv_giveup_date.setOnClickListener {
            updateDateStatus(myAppointment!!.sAppointmentSignupId,4)
        }

        tv_private_chat.setOnClickListener {
            isNoAuthToChat(this,userId) {
                myAppointment?.let {
                    val name = it.sAppointUserName ?: ""
                    if(it.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(iAppointUserid,userId)){
//                        checkChatCount(it.iUserid.toString()) {
//                            showDatePayPointDialog(name,it.iUserid.toString())
                            if(it.iIsAnonymous==1){
                                createGroupName(it.iUserid.toString(),1) //1 我是匿名
                            }else{
                                RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, it.iUserid.toString(), name)
                            }
//                        }
                    }else if(it.sAppointmentSignupId.isNotEmpty()){
//                        checkChatCount(it.iAppointUserid.toString()) {
//                            showDatePayPointDialog(name,it.iAppointUserid.toString())
                           Log.i("tv_private_chat","state===${it.iIsAnonymous}")
                            if(it.iIsAnonymous==1){
                                createGroupName(it.iAppointUserid.toString(),2) //2 对方匿名
                            }else{
                                RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, it.iAppointUserid.toString(), name)
                            }
//                        }
                    }
                }
            }
        }
    }

    private fun getData(sAppointmentSignupId:String,sAppointmentId:String){

        Request.getAppointDetails(userId,sAppointmentSignupId, sAppointmentId,iShareUserId).request(this,success={msg, data->
            if (data != null) {
                when (data.iStatus) {
                    1 -> {//
                        if(data.sAppointmentSignupId.isNullOrEmpty()&&TextUtils.equals(iAppointUserid,userId)){
                            noPeopleJoinDate(data,-1)
                        }else if(data.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(iAppointUserid,userId)){
                            var param:RelativeLayout.LayoutParams = tv_date_status.layoutParams as RelativeLayout.LayoutParams
                            param.addRule(RelativeLayout.CENTER_VERTICAL);
                            tv_date_status.text="状态：待同意"
                            tv_no_date.visibility = View.VISIBLE
                            tv_agree_date.visibility = View.VISIBLE
                            tv_private_chat.visibility = View.GONE
                            tv_giveup_date.visibility = View.GONE

                            tv_point_nums.visibility = View.GONE

                            setAgreeDate(data,data.dAppointmentSignupCreatetime,"待同意",3,true)

                        }else if(data.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(userId,data.iUserid.toString())){
                            tv_date_status.text="状态：等待对方同意"
                            tv_no_date.visibility = View.GONE
                            tv_agree_date.visibility = View.GONE
                            tv_private_chat.visibility = View.GONE
                            tv_giveup_date.visibility = View.VISIBLE

                            tv_point_nums.text="预付${data.iPoint}积分"
                            setAgreeDate(data,data.dAppointmentSignupCreatetime,"待同意",3,true)
                        }else if(data.iAppointStatus==2){
                            noPeopleJoinDate(data,data.iAppointStatus)
                        }
                    }
                    2 -> { //
                        var param:RelativeLayout.LayoutParams = tv_date_status.layoutParams as RelativeLayout.LayoutParams
                        param.addRule(RelativeLayout.CENTER_VERTICAL);
                        tv_date_status.text="状态:私聊"
                        tv_private_chat.visibility = View.VISIBLE
                        tv_no_date.visibility = View.GONE
                        tv_agree_date.visibility = View.GONE
                        tv_giveup_date.visibility = View.GONE

                        tv_point_nums.visibility = View.GONE
//                        if(data.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(iAppointUserid,userId)){
//                            tv_point_nums.visibility = View.GONE
//                        }else{
//                            tv_point_nums.visibility = View.VISIBLE
//                            tv_point_nums.text="消费${myAppointment.iPoint}积分"
//                        }
                        setDateStatus(data)
                    }
                    3 -> { //
                        //tv_action0.text = "对方已关闭约会"

                        if(data.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(iAppointUserid,userId)){
                            var param:RelativeLayout.LayoutParams = tv_date_status.layoutParams as RelativeLayout.LayoutParams
                            param.addRule(RelativeLayout.CENTER_VERTICAL);
                            tv_date_status.text="状态：已拒绝"
                            tv_point_nums.visibility = View.GONE
                        }else{
                            tv_date_status.text="状态：对方拒绝"
                            tv_point_nums.visibility = View.VISIBLE
                            tv_point_nums.text="返还${myAppointment.iPoint}积分"
                        }

                        tv_private_chat.visibility = View.GONE;
                        tv_no_date.visibility = View.GONE
                        tv_agree_date.visibility = View.GONE
                        tv_giveup_date.visibility = View.GONE
                        setAgreeDate(data,data.dAppointmentSignupUpdatetime,"已拒绝",3)
                        tv_point_nums.text="返还${data.iPoint}积分"
                    }
                    4 -> { //
                        if(data.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(iAppointUserid,userId)){
                            tv_date_status.text="状态：对方放弃"
                            tv_point_nums.visibility = View.GONE
                        }else{
                            tv_date_status.text="状态：主动取消"
                            tv_point_nums.visibility = View.VISIBLE
                            tv_point_nums.text="预付积分已返还"
                        }

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
                        headView0.setOnClickListener {
                            if(data.iIsAnonymous==1){
                                showUnKnowInfo(data.iAppointUserid.toString())
                            }else{
                                startUserInfo(data.iAppointUserid.toString())
                            }
                        }

                        headView1.setImageURI(data.sAppointmentPicUrl)
                        tv_name1.text =  getSpannable("${data.sAppointUserName}:过期自动取消",6)
                        tv_days1.text = data.dAppointmentSignupUpdatetime.interval()//报名约会时间
                        tv_point_nums.text="已返还${data.iPoint}积分"
                        headView1.setOnClickListener {
                            if(data.iIsAnonymous==1){
                                showUnKnowInfo(data.iAppointUserid.toString())
                            }else{
                                startUserInfo(data.iAppointUserid.toString())
                            }
                        }
                    }
                }

                var drawable = ContextCompat.getDrawable(this,Const.dateListTypes[data.iAppointType!!.toInt()-1])
                iv_datetype_img.backgroundDrawable = drawable

                myAppointment = data
                iAppointUserid = myAppointment.iAppointUserid.toString()
                updateUI()
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
        headView0.setOnClickListener {
            if(data.iIsAnonymous==1){
                showUnKnowInfo(data.iAppointUserid.toString())
            }else{
                startUserInfo(data.iAppointUserid.toString())
            }
        }

        headView1.setImageURI(data.sPicUrl)
        tv_name1.text =  getSpannable("${data.sUserName}:发起邀约",4)
        tv_days1.text = data.dAppointmentSignupCreatetime.interval()//报名约会时间
        headView1.setOnClickListener {
            startUserInfo(data.iUserid.toString())
        }

        headView2.setImageURI(data.sAppointmentPicUrl)
        tv_name2.text = getSpannable("${data.sAppointUserName}:${str}",len)
        tv_days2.text = updateTime.interval() //同意约会时间
        headView2.setOnClickListener {
            if(data.iIsAnonymous==1){
                showUnKnowInfo(data.iAppointUserid.toString())
            }else{
                startUserInfo(data.iAppointUserid.toString())
            }
        }
    }

    fun setDateStatus(data:MyAppointment){
        rel_0.visibility = View.VISIBLE
        rel_1.visibility = View.VISIBLE
        rel_2.visibility = View.VISIBLE
        rel_3.visibility = View.VISIBLE
        headView0.setImageURI(data.sAppointmentPicUrl)
        tv_name0.text = getSpannable("${data.sAppointUserName}:发布约会",4);
        tv_days0.text = data.dCreatetime.interval() //约会发布时间
        headView0.setOnClickListener {
            if(data.iIsAnonymous==1){
                showUnKnowInfo(data!!.iAppointUserid.toString())
            }else{
                startUserInfo(data!!.iAppointUserid.toString())
            }
        }

        headView1.setImageURI(data.sPicUrl)
        tv_name1.text = getSpannable("${data.sUserName}:发起邀约",4)
        tv_days1.text = data.dAppointmentSignupCreatetime.interval()//报名约会时间
        headView1.setOnClickListener {
            startUserInfo(data!!.iUserid.toString())
        }

        headView2.setImageURI(data.sAppointmentPicUrl)
        tv_name2.text = getSpannable("${data.sAppointUserName}:同意",2)
        tv_days2.text = data.dAppointmentSignupUpdatetime.interval() //同意约会时间
        headView2.setOnClickListener {
            if(data.iIsAnonymous==1){
                showUnKnowInfo(data!!.iAppointUserid.toString())
            }else{
                startUserInfo(data!!.iAppointUserid.toString())
            }
        }

        headView3.setImageURI(data.sPicUrl)
        tv_name3.text = getSpannable("${data.sUserName}:赴约",2)
        tv_days3.text = data.dAppointmentSignupUpdatetime.interval()
        headView3.setOnClickListener {
            startUserInfo(data!!.iUserid.toString())
        }
    }

    fun updateDateStatus(sAppointmentSignupId:String,iStatus:Int){
        Request.updateDateStatus(sAppointmentSignupId,iStatus,"").request(this, success = {msg, data->
            run {
                if (iStatus == 2) {
                    var param:RelativeLayout.LayoutParams = tv_date_status.layoutParams as RelativeLayout.LayoutParams
                    param.addRule(RelativeLayout.CENTER_VERTICAL);
                    tv_date_status.text = "状态:赴约"
                    tv_no_date.visibility = View.GONE
                    tv_agree_date.visibility = View.GONE
                    tv_private_chat.visibility = View.VISIBLE
                    tv_giveup_date.visibility = View.GONE
                    myAppointment.dAppointmentSignupUpdatetime = System.currentTimeMillis()
                    setDateStatus(myAppointment)
                    tv_point_nums.visibility = View.GONE
                } else if (iStatus == 3) {
                    tv_date_status.text = "状态：已拒绝"
                    tv_private_chat.visibility = View.GONE;
                    tv_no_date.visibility = View.GONE
                    tv_agree_date.visibility = View.GONE
                    tv_giveup_date.visibility = View.GONE
                    setAgreeDate(myAppointment,System.currentTimeMillis(),"已拒绝",3)
                    tv_point_nums.text="返还${myAppointment.iPoint}积分"
                }else if(iStatus == 4){
                    tv_date_status.text="状态：主动取消"
                    tv_private_chat.visibility = View.GONE
                    tv_no_date.visibility = View.GONE
                    tv_agree_date.visibility = View.GONE
                    tv_giveup_date.visibility = View.GONE
                    tv_point_nums.visibility = View.VISIBLE
                    tv_point_nums.text="预付积分已返还"
                    setAgreeDate(myAppointment,System.currentTimeMillis(),"主动取消",4)
                }
            }
        })
    }

    private fun noPeopleJoinDate(data:MyAppointment,iAppointStatus:Int?){
        tv_private_chat.visibility = View.GONE;
        tv_no_date.visibility = View.GONE
        tv_agree_date.visibility = View.GONE
        tv_giveup_date.visibility = View.GONE
        tv_send_date.visibility = View.GONE

        if(iAppointStatus==-1){
            var param:RelativeLayout.LayoutParams = tv_date_status.layoutParams as RelativeLayout.LayoutParams
            param.addRule(RelativeLayout.CENTER_VERTICAL)
            tv_date_status.text="状态：暂无赴约人"
            tv_point_nums.visibility = View.GONE
        }else if(iAppointStatus==2){
            if(data.sAppointmentSignupId.isNullOrEmpty()&&TextUtils.equals(iAppointUserid,iShareUserId)){
                tv_date_status.text="状态：待邀约"
                tv_point_nums.text="预付${data.iPoint}积分"
                tv_point_nums.visibility = View.VISIBLE

                tv_send_date.visibility = View.VISIBLE

            }else{
                var param:RelativeLayout.LayoutParams = tv_date_status.layoutParams as RelativeLayout.LayoutParams
                param.addRule(RelativeLayout.CENTER_VERTICAL)
                tv_point_nums.visibility = View.GONE
                tv_date_status.text="状态：对方未赴约"
            }
        }
        rel_0.visibility = View.VISIBLE
        rel_1.visibility = View.GONE
        rel_2.visibility = View.GONE
        rel_3.visibility = View.GONE

        rel0_line.visibility = View.GONE
        headView0.setImageURI(data.sAppointmentPicUrl)
        tv_name0.text = getSpannable("${data.sAppointUserName}:发布约会",4)
        tv_days0.text = data.dCreatetime.interval()//约会发布时间

        headView0.setOnClickListener {
            if(data.iIsAnonymous==1){
                showUnKnowInfo(data!!.iAppointUserid.toString())
            }else{
                startUserInfo(data!!.iAppointUserid.toString())
            }
        }
    }

    private fun signUpDate(myAppointment:MyAppointment) {
        Request.queryAppointmentPoint(userId).request(this, false, success = { msg, data ->
            explainAppoint = data?.iAppointPoint.toString()
            val dateDialog = OpenDateDialog()
            dateDialog.arguments = bundleOf("data" to myAppointment, "explain" to data!!,"fromType" to "MydateDetail")
            dateDialog.show((this).supportFragmentManager, "d")
            dateDialog.setDialogListener { p, s ->
                getData()
            }
        }) { code, msg ->
            if (code == 2) {
                var openErrorDialog = OpenDateErrorDialog()
                openErrorDialog.arguments = bundleOf("code" to code, "msg" to msg)
                openErrorDialog.show(supportFragmentManager, "d")
            }
        }
    }

    fun getSpannable(str:String,len:Int):SpannableStringBuilder{
        return SpanBuilder(str)
                .click(str.length - len, str.length, MClickSpan(this))
                .build()
    }

    private fun getData() {
            //194ecdb4-4809-4b2d-bf32-42a3342964df
            Request.signUpdate(userId,myAppointment?.sId.toString(),"").request(this,success = { msg, data ->
                var openSuccessDialog = OpenDateSuccessDialog()
                var sId = data?.optString("sId")
                openSuccessDialog.arguments = bundleOf("point" to explainAppoint,"sId" to sId.toString(),"fromType" to "MydateDetail")
                openSuccessDialog.show(supportFragmentManager, "d")
                openSuccessDialog.setDialogListener { p, s ->
                    if(myAppointment!!.sAppointmentSignupId.isNotEmpty()){
                        getData(myAppointment!!.sAppointmentSignupId,"")
                    }else{
                        getData("",myAppointment!!.sId.toString())
                    }
                }
            }) { code, msg ->
                if(code == 3){
                    var openErrorDialog = OpenDateErrorDialog()
                    openErrorDialog.arguments= bundleOf("code" to code)
                    openErrorDialog.show(supportFragmentManager, "d")
                }else{
                    CustomToast.showToast(msg)
                }
            }
    }

    private fun getLocalFriendsCount(){
        Request.findUserFriends(userId,"",1).request(this) { _, data ->
            if(data?.list?.results!=null){
                titlebar_datedetails.hideRightButton(0,false)
            }else {
                titlebar_datedetails.hideRightButton(0,true)
            }
        }
    }

    private fun delMyDate(){
        dialog()
        Request.delAppointment(getLoginToken(),myAppointment.sId.toString()).request(this,false,success={_,_->
            var intent = Intent()
            intent.putExtra("type","delDate")
            intent.putExtra("index",index)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }) {code,msg->
            if(code==2){
               toast(msg)
            }
        }
    }

    private class MClickSpan(val context: Context) : ClickableSpan() {

        override fun onClick(p0: View?) {

        }

        override fun updateDrawState(ds: TextPaint?) {
            ds?.color = ContextCompat.getColor(context, R.color.color_333333)
            ds?.isUnderlineText = false
        }
    }

    fun startUserInfo(mTargetId:String){
        startActivity<UserInfoActivity>("id" to mTargetId)
    }

    fun showUnKnowInfo(mTargetId:String){
        var mUnknowDialog = UnKnowInfoDialog()
        mUnknowDialog.arguments = bundleOf("otheruserId" to mTargetId)
        mUnknowDialog.show(supportFragmentManager,"unknowDialog")
    }

    //创建群组
    private fun createGroupName(id:String,iType:Int){
        Request.doToUserAnonyMousGroup(getLoginToken(),id,iType).request(this,false,success = { msg, jsonObject->
            jsonObject?.let {
                Log.i("createGroupName","json=${it.sId}---sId----${it.iTalkUserid}")
            }
        }){code,msg->
            Log.i("createGroupName","fail${msg}")//保存失败
        }
    }

}
