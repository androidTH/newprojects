package com.d6.android.app.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.UnKnowInfoDialog
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.CustomerServiceWomenId
import kotlinx.android.synthetic.main.view_self_release_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import java.net.URLDecoder

/**
 * Created on 2017/12/17.
 */
class SelfPullDateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var myDate: MyAppointment? = null
    private val mImages = ArrayList<String>()
    private val imageAdapter by lazy {
        SelfReleaselmageAdapter(mImages,1)
    }
    init {
        LayoutInflater.from(context).inflate(R.layout.view_self_release_view, this, true)
        rv_images.setHasFixedSize(true)
//        rv_images.layoutManager = GridLayoutManager(context, 3)
        rv_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_images.adapter = imageAdapter
//        rv_images.addItemDecoration(SpacesItemDecoration(dip(4)))
    }

    fun update(myAppointment: MyAppointment) {
        this.myDate = myDate
        headView.setImageURI(myAppointment.sAppointmentPicUrl)
        tv_name.text = myAppointment.sAppointUserName
        tv_date_user_sex.isSelected = myAppointment.iSex == 0
        headView.setOnClickListener(OnClickListener {
            val id =myAppointment.iAppointUserid
            isBaseActivity {
                if(myAppointment.iIsAnonymous==1){
                    var mUnknowDialog = UnKnowInfoDialog()
                    mUnknowDialog.arguments = bundleOf("otheruserId" to id.toString())
                    mUnknowDialog.show(it.supportFragmentManager,"unknowDialog")
                }else{
                    it.startActivity<UserInfoActivity>("id" to id.toString())
                }
            }
        })
//        val start = myAppointment.dStarttime.toString()?.parserTime("yyyy-MM-dd")
//        val end = myAppointment.dEndtime.toString()?.parserTime("yyyy-MM-dd")
//        val time = String.format("%s-%s",start?.toTime("MM.dd"),end?.toTime("MM.dd"))
//        val time = String.format("%s",(start - end)/(1000 * 60 * 60 * 24))
//        val s = if (myDate.city.isNullOrEmpty()) {
//            time
//        } else {
//            time+" | " +myDate.city
//        }
//        tv_sub_title.text = SpanBuilder(s)
//                .color(context,0,time.length,R.color.color_369)
//                .build()

        tv_datetype_name.text = Const.dateTypes[myAppointment.iAppointType!!.toInt()-1]
        var index = myAppointment.iAppointType!!.toInt()-1
        if(index!= Const.dateTypesBig.size){
            var drawable = ContextCompat.getDrawable(context,Const.dateTypesBig[index])
            drawable?.setBounds(0, 0, drawable?.getMinimumWidth(), drawable?.getMinimumHeight());// 设置边界
            tv_datetype_name.setCompoundDrawablePadding(dip(3))
            tv_datetype_name.setCompoundDrawables(drawable,null,null,null)
        }

        if(!myAppointment.iAge.toString().isNullOrEmpty()){
            if(myAppointment.iAge!=null){
                myAppointment.iAge?.let {
                    if(it>0){
//                        sb.append("${myAppointment.iAge}岁")
                        tv_date_user_sex.text = "${myAppointment.iAge}"
                    }
                }
            }
        }

        var time  = converToDays(myAppointment.dEndtime)
        tv_send_date.visibility = View.VISIBLE
        tv_time_long.visibility = View.VISIBLE
        iv_date_timeout.visibility = View.GONE
        if(time[0]==1){
            tv_time_long.text = "倒计时·${time[1]}天"
        }else if(time[0]==2){
            tv_time_long.text = "倒计时·${time[1]}小时"
        }else if(time[0]==3){
            tv_time_long.text = "倒计时·${time[1]}分钟"
        }else{
            tv_time_long.visibility = View.GONE
            tv_send_date.visibility = View.INVISIBLE
            iv_date_timeout.visibility = View.VISIBLE
        }

        if(index!=5){
            tv_self_address.visibility = View.VISIBLE
            tv_self_address.text = "约会地点：${myAppointment.sPlace}"
            tv_self_money.visibility = View.VISIBLE
            if(myAppointment.iFeeType==1){
                tv_self_money.text = "全包"
            }else if(myAppointment.iFeeType==0){
                tv_self_money.visibility = View.GONE
            }else{
                tv_self_money.text = "AA"
            }
        }else{
            tv_self_address.visibility = View.GONE
            tv_self_money.visibility = View.GONE
        }

        tv_content.text = myAppointment.sDesc

        if (myAppointment.sAppointPic.isNullOrEmpty()) {
            rv_images.gone()
        } else {
            rv_images.visible()
        }

        mImages.clear()
        val images = myAppointment.sAppointPic?.split(",")
        if (images != null) {
            mImages.addAll(images.toList())
        }
//        Log.i("fff",myAppointment.sSourceAppointPic)
        imageAdapter.notifyDataSetChanged()
        tv_send_date.setOnClickListener {
            mSendDateClick?.let {
                it.onDateClick(myAppointment)
            }
        }
        tv_date_more.setOnClickListener {
            deleteAction?.let {
                it.onDelete(myAppointment)
            }
        }

        tv_date_vip.backgroundDrawable = getLevelDrawable("${myAppointment.userclassesid}",context)

        if(TextUtils.equals(CustomerServiceId,myAppointment.iAppointUserid.toString())||TextUtils.equals(CustomerServiceWomenId,myAppointment.iAppointUserid.toString())){
            iv_self_servicesign.visibility = View.VISIBLE
            img_self_auther.visibility = View.GONE
        }else{
            iv_self_servicesign.visibility = View.GONE
            if(TextUtils.equals("0",myAppointment!!.screen)|| myAppointment!!.screen.isNullOrEmpty()){
                img_self_auther.visibility = View.GONE
            }else if(TextUtils.equals("1",myAppointment!!.screen)){
                img_self_auther.visibility = View.VISIBLE
                img_self_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.video_small)
            }else if(TextUtils.equals("3",myAppointment!!.screen)){
                img_self_auther.visibility = View.GONE
                img_self_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.renzheng_small)
            }else{
                img_self_auther.visibility = View.GONE
            }
        }

        myAppointment.iAppointmentSignupCount?.let {
            if(it>0){
                tv_date_nums.text = "已有${it}人邀约成功"
            }else{
                tv_date_nums.text = ""
            }
        }

//            if (TextUtils.equals("0", myAppointment.screen) || TextUtils.equals("3", it.screen) || it.screen.isNullOrEmpty()) {
//                img_other_auther.visibility = View.GONE
//                img_date_auther.visibility = View.GONE
//                if (TextUtils.equals("3", it.screen)) {
//                    tv_other_auther_sign.visibility = View.GONE
//                } else {
//                    tv_other_auther_sign.visibility = View.GONE
//                }
//            } else if (TextUtils.equals("1", data.screen)) {
//                img_other_auther.visibility = View.VISIBLE
//                img_date_auther.visibility = View.VISIBLE
//                tv_other_auther_sign.visibility = View.GONE
//            }
    }

    public fun sendDateListener(action:(myAppointment: MyAppointment)->Unit) {
        mSendDateClick = object : sendDateClickListener {
            override fun onDateClick(myAppointment: MyAppointment) {
                action(myAppointment)
            }
        }
    }

    fun setDeleteClick(action:(myAppointment: MyAppointment)->Unit){
        this.deleteAction = object :DeleteClick {
            override fun onDelete(myAppointment: MyAppointment) {
                action(myAppointment)
            }
        }
    }

    private var mSendDateClick:sendDateClickListener?=null
    private var deleteAction: DeleteClick?=null

    interface sendDateClickListener{
        fun onDateClick(myAppointment: MyAppointment)
    }

    interface DeleteClick{
        fun onDelete(myAppointment: MyAppointment)
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}