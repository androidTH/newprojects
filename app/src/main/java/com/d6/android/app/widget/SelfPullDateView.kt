package com.d6.android.app.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.adapters.SelfReleaselmageAdapter
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.utils.*
import kotlinx.android.synthetic.main.view_self_release_view.view.*
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

        if(myAppointment.iAppointType!!.toInt() == Const.dateTypesImg.size){
            var drawable = context.resources.getDrawable(R.mipmap.invitation_nolimit_small)
            tv_datetype_name.setCompoundDrawables(drawable,null,null,null)
            tv_datetype_name.setCompoundDrawablePadding(dip(3))
        }else{
            var drawable = context.resources.getDrawable(Const.dateTypesImg[myAppointment.iAppointType!!.toInt()-1])
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
            tv_datetype_name.setCompoundDrawablePadding(dip(3));
            tv_datetype_name.setCompoundDrawables(drawable,null,null,null);
        }

        var sb = StringBuffer()
        if(!myAppointment.iAge.isNullOrEmpty()){
            sb.append("${myAppointment.iAge}岁")
        }

        if(!myAppointment.iHeight.isNullOrEmpty()){
            sb.append("·${myAppointment.iHeight}cm")
        }

        if(myAppointment.iWeight != null){
            if(!myAppointment.iWeight.equals("0")){
                sb.append("·${myAppointment.iWeight}kg")
            }
        }

        tv_sub_title.text = sb.toString()

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
        imageAdapter.notifyDataSetChanged()
        tv_send_date.setOnClickListener {
            mSendDateClick?.let {
                it.onDateClick(myAppointment)
            }
        }
    }

    public fun sendDateListener(action:(myAppointment: MyAppointment)->Unit) {
        mSendDateClick = object : sendDateClickListener {
            override fun onDateClick(myAppointment: MyAppointment) {
                action(myAppointment)
            }
        }
    }
    private var mSendDateClick:sendDateClickListener?=null

    interface sendDateClickListener{
        fun onDateClick(myAppointment: MyAppointment)
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}