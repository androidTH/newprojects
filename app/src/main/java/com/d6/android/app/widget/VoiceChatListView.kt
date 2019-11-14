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
import kotlinx.android.synthetic.main.view_voicechat_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

/**
 * Created on 2017/12/17.
 */
class VoiceChatListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val mImages = ArrayList<String>()
    private val imageAdapter by lazy {
        SelfReleaselmageAdapter(mImages,1)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_voicechat_view, this, true)
        rv_images.setHasFixedSize(true)
        rv_images.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_images.adapter = imageAdapter
    }

    fun update(date: MyAppointment) {
        headView.setImageURI(date.sAppointmentPicUrl)
        tv_name.text = date.sAppointUserName
        tv_voicechat_user_sex.isSelected = TextUtils.equals("0","${date.iSex}")
        headView.setOnClickListener(OnClickListener {
            val id = date.iUserid
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

        if(!date.iAge.toString().isNullOrEmpty()){
            if(date.iAge!=null){
                date.iAge?.let {
                    tv_voicechat_user_sex.text = "${date.iAge}"
                }
            }
        }

        var time  = converToDays(date.dEndtime)
        tv_time_long.visibility = View.VISIBLE
        tv_send_voicechat.visibility = View.VISIBLE
        iv_voicechat_timeout.visibility = View.GONE
        if(time[0]==1){
            tv_time_long.text="倒计时：${time[1]}天"
        }else if(time[0]==2){
            tv_time_long.text="倒计时：${time[1]}小时"
        }else if(time[0]==3){
            tv_time_long.text="倒计时：${time[1]}分钟"
        }else if(time[0]==-1){
            tv_time_long.visibility = View.GONE
            tv_send_voicechat.visibility = View.GONE
            iv_voicechat_timeout.visibility = View.VISIBLE
        }else{
            tv_time_long.visibility = View.GONE
            tv_send_voicechat.visibility = View.GONE
            iv_voicechat_timeout.visibility = View.VISIBLE
        }

//        tv_voicechat_type.text = "约会地点：${date}"

        tv_content.text = date.sDesc

        if (date.sAppointPic.isNullOrEmpty()) {
            rv_images.gone()
        } else {
            rv_images.visible()
        }

        mImages.clear()
        val images = date.sAppointPic?.split(",")
        if (images != null) {
            mImages.addAll(images.toList())
        }
//        Log.i("fff",myAppointment.sSourceAppointPic)
        imageAdapter.notifyDataSetChanged()
        tv_send_voicechat.setOnClickListener {
            mSendDateClick?.let {
                it.onDateClick(date)
            }
        }
        tv_voicechat_more.setOnClickListener {
            deleteAction?.let {
                it.onDelete(date)
            }
        }

        tv_voicechat_vip.backgroundDrawable = getLevelDrawable(date.userclassesid.toString(),context)

        if(TextUtils.equals(CustomerServiceId,date.iUserid.toString())||TextUtils.equals(CustomerServiceWomenId,date.iUserid.toString())){
            iv_voicechat_servicesign.visibility = View.VISIBLE
            img_voicechat_auther.visibility = View.GONE
        }else{
            iv_voicechat_servicesign.visibility = View.GONE
            if(TextUtils.equals("0",date!!.screen)|| date!!.screen.isNullOrEmpty()){
                img_voicechat_auther.visibility = View.GONE
            }else if(TextUtils.equals("1",date!!.screen)){
                img_voicechat_auther.visibility = View.VISIBLE
                img_voicechat_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.video_small)
            }else if(TextUtils.equals("3",date!!.screen)){
                img_voicechat_auther.visibility = View.GONE
                img_voicechat_auther.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.renzheng_small)
            }else{
                img_voicechat_auther.visibility = View.GONE
            }
        }

        date.iAppointmentSignupCount?.let {
            if(it>0){
                tv_voicechat_nums.text = "累计${it}人邀约"
            }else{
                tv_voicechat_nums.text = ""
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