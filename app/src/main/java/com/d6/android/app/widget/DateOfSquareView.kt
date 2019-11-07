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
import kotlinx.android.synthetic.main.view_dateofsquare_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

/**
 * Created on 2017/12/17.
 */
class DateOfSquareView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var myDate: Square? = null
    private val mImages = ArrayList<String>()
    private val imageAdapter by lazy {
        SelfReleaselmageAdapter(mImages,1)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_dateofsquare_view, this, true)
        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_images.adapter = imageAdapter
    }

    fun update(date: Square) {
        this.myDate = myDate
        headView.setImageURI(date.picUrl)
        tv_name.text = date.name
        tv_date_user_sex.isSelected = TextUtils.equals("0",date.sex)
        headView.setOnClickListener(OnClickListener {
            val id = date.userid
            isBaseActivity {
                if(date.iIsAnonymous==1){
                    var mUnknowDialog = UnKnowInfoDialog()
                    mUnknowDialog.arguments = bundleOf("otheruserId" to id.toString())
                    mUnknowDialog.show(it.supportFragmentManager,"unknowDialog")
                }else{
                    it.startActivity<UserInfoActivity>("id" to id.toString())
                }
            }
        })

        tv_datetype_name.text = Const.dateTypes[date.iAppointType!!.toInt()-1]
        var index = date.iAppointType!!.toInt()-1
        if(index!= Const.dateTypesBig.size){
            var drawable = ContextCompat.getDrawable(context,Const.dateTypesBig[index])
            drawable?.setBounds(0, 0, drawable?.getMinimumWidth(), drawable?.getMinimumHeight());// 设置边界
            tv_datetype_name.setCompoundDrawablePadding(dip(3))
            tv_datetype_name.setCompoundDrawables(null,drawable,null,null);
        }

        if(!date.age.toString().isNullOrEmpty()){
            if(date.age!=null){
                date.age?.let {
                    tv_date_user_sex.text = "${date.age}"
//                    if(it>0){
//                        tv_date_user_sex.text = "${date.age}"
//                    }
                }
            }
        }

        var time  = converToDays(date.dEndtime)
        tv_time_long.visibility = View.VISIBLE
        tv_send_date.visibility = View.VISIBLE
        iv_date_timeout.visibility = View.GONE
        if(time[0]==1){
            tv_time_long.text="倒计时：${time[1]}天"
        }else if(time[0]==2){
            tv_time_long.text="倒计时：${time[1]}小时"
        }else if(time[0]==3){
            tv_time_long.text="倒计时：${time[1]}分钟"
        }else if(time[0]==-1){
            tv_time_long.visibility = View.GONE
            tv_send_date.visibility = View.GONE
            iv_date_timeout.visibility = View.VISIBLE
        }else{
            tv_time_long.visibility = View.GONE
            tv_send_date.visibility = View.GONE
            iv_date_timeout.visibility = View.VISIBLE
        }

        tv_self_address.text = "约会地点：${date.city}"

        tv_content.text = date.content

        if (date.imgUrl.isNullOrEmpty()) {
            rv_images.gone()
        } else {
            rv_images.visible()
        }

        mImages.clear()
        val images = date.imgUrl?.split(",")
        if (images != null) {
            mImages.addAll(images.toList())
        }
//        Log.i("fff",myAppointment.sSourceAppointPic)
        imageAdapter.notifyDataSetChanged()
        tv_send_date.setOnClickListener {
            mSendDateClick?.let {
                it.onDateClick(date)
            }
        }
        tv_date_more.setOnClickListener {
            deleteAction?.let {
                it.onDelete(date)
            }
        }

        tv_date_vip.backgroundDrawable = getLevelDrawable(date.userclassesid.toString(),context)

        if(TextUtils.equals(CustomerServiceId,date.userid.toString())||TextUtils.equals(CustomerServiceWomenId,date.userid.toString())){
            iv_self_servicesign.visibility = View.VISIBLE
            img_self_auther.visibility = View.GONE
        }else{
            iv_self_servicesign.visibility = View.GONE
            if(TextUtils.equals("0",date!!.screen)|| date!!.screen.isNullOrEmpty()){
                img_self_auther.visibility = View.GONE
            }else if(TextUtils.equals("1",date!!.screen)){
                img_self_auther.visibility = View.VISIBLE
                img_self_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.video_small)
            }else if(TextUtils.equals("3",date!!.screen)){
                img_self_auther.visibility = View.GONE
                img_self_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.renzheng_small)
            }else{
                img_self_auther.visibility = View.GONE
            }
        }

        date.iAppointmentSignupCount?.let {
            if(it>0){
                tv_date_nums.text = "累计${it}人邀约"
            }else{
                tv_date_nums.text = ""
            }
        }
    }

    public fun sendDateListener(action:(myAppointment: Square)->Unit) {
        mSendDateClick = object : sendDateClickListener {
            override fun onDateClick(myAppointment: Square) {
                action(myAppointment)
            }
        }
    }

    fun setDeleteClick(action:(myAppointment: Square)->Unit){
        this.deleteAction = object :DeleteClick {
            override fun onDelete(myAppointment: Square) {
                action(myAppointment)
            }
        }
    }

    private var mSendDateClick:sendDateClickListener?=null
    private var deleteAction: DeleteClick?=null

    interface sendDateClickListener{
        fun onDateClick(myAppointment: Square)
    }

    interface DeleteClick{
        fun onDelete(myAppointment: Square)
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }
}