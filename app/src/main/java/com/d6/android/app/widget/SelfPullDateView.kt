package com.d6.android.app.widget

import android.content.Context
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
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.models.Square
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.CustomerServiceWomenId
import kotlinx.android.synthetic.main.view_self_release_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

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
        tv_name.isSelected = myAppointment.iSex == 0
        headView.setOnClickListener(OnClickListener {
            val id =myAppointment.iAppointUserid
            isBaseActivity {
                it.startActivity<UserInfoActivity>("id" to id.toString())
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

        if(myAppointment.iAppointType!!.toInt() == Const.dateTypesBig.size){
            var drawable =ContextCompat.getDrawable(context,R.mipmap.invitation_nolimit_feed)
            tv_datetype_name.setCompoundDrawables(null,drawable,null,null)
            tv_datetype_name.setCompoundDrawablePadding(dip(3))
        }else{
            var drawable = ContextCompat.getDrawable(context,Const.dateTypesBig[myAppointment.iAppointType!!.toInt()-1])
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
            tv_datetype_name.setCompoundDrawablePadding(dip(3));
            tv_datetype_name.setCompoundDrawables(null,drawable,null,null);
        }

        var sb = StringBuffer()
        if(!myAppointment.iAge.toString().isNullOrEmpty()){
            if(myAppointment.iAge!=null){
                sb.append("${myAppointment.iAge}岁")
            }
        }

        if(!myAppointment.iHeight.toString().isNullOrEmpty()){
            if(myAppointment.iHeight!!.toInt() > 0 ){
                sb.append("·${myAppointment.iHeight}cm")
            }
        }

        if(!myAppointment.iWeight.toString().isNullOrEmpty()){
            if(!myAppointment.iWeight.toString().equals("0")){
                sb.append("·${myAppointment.iWeight}kg")
            }
        }

        if(!sb.toString().isNullOrEmpty()){
            tv_sub_title.text = sb.toString()
            tv_sub_title.visibility = View.VISIBLE
        }else{
            tv_sub_title.visibility = View.GONE
        }

        var time = converTime(myAppointment.dEndtime)
        tv_time_long.text="倒计时:${time}"

        tv_self_address.text = myAppointment.sPlace

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

//        tv_date_vip.visibility = View.VISIBLE
//        if(TextUtils.equals(myAppointment.userclassesid.toString(),"27")){
//            tv_date_vip.backgroundDrawable =  ContextCompat.getDrawable(context,R.mipmap.gril_cj)
//        }else if(TextUtils.equals(myAppointment.userclassesid.toString(),"28")){
//            tv_date_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.gril_zj)
//        }else if(TextUtils.equals(myAppointment.userclassesid.toString(),"29")){
//            tv_date_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.gril_gj)
//        }else if(TextUtils.equals(myAppointment.userclassesid.toString(),"7")){
//            tv_date_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.youke_icon)
//        }else if(TextUtils.equals(myAppointment.userclassesid.toString(),"22")){
//            tv_date_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_ordinary)
//        }else if(TextUtils.equals(myAppointment.userclassesid.toString(),"23")){
//            tv_date_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_silver)
//        }else if(TextUtils.equals(myAppointment.userclassesid.toString(),"24")){
//            tv_date_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_gold)
//        }else if(TextUtils.equals(myAppointment.userclassesid.toString(),"25")){
//            tv_date_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_zs)
//        }else if(TextUtils.equals(myAppointment.userclassesid.toString(),"26")){
//            tv_date_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.vip_private)
//        }else if(TextUtils.equals(myAppointment.userclassesid.toString(),"7")){
//            tv_date_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.youke_icon)
//        }else if(TextUtils.equals(myAppointment.userclassesid.toString(),"30")){
//            tv_date_vip.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.ruqun_icon)
//        }else{
//            tv_date_vip.visibility = View.GONE
//        }

        tv_date_vip.backgroundDrawable = getLevelDrawable(myAppointment.userclassesid.toString(),context)

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