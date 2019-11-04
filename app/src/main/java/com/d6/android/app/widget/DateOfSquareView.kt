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

/**
 * Created on 2017/12/17.
 */
class DateOfSquareView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var myDate: MyAppointment? = null
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

        tv_datetype_name.text = Const.dateTypes[myAppointment.iAppointType!!.toInt()-1]
        var index = myAppointment.iAppointType!!.toInt()-1
        if(index!= Const.dateTypesBig.size){
            var drawable = ContextCompat.getDrawable(context,Const.dateTypesBig[index])
            drawable?.setBounds(0, 0, drawable?.getMinimumWidth(), drawable?.getMinimumHeight());// 设置边界
            tv_datetype_name.setCompoundDrawablePadding(dip(3))
            tv_datetype_name.setCompoundDrawables(null,drawable,null,null);
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

        var time = converTime(myAppointment.dEndtime)
        tv_time_long.text="倒计时：${time}"

        tv_self_address.text = "约会地点：${myAppointment.sPlace}"

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

        myAppointment.iAppointmentSignupCount?.let {
            if(it>0){
                tv_date_nums.text = "累计${it}人邀约"
            }else{
                tv_date_nums.text = ""
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