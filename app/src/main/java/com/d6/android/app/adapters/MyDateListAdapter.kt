package com.d6.android.app.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.models.MyAppointment
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.JsonObject

/**
 *粉丝
 */
class MyDateListAdapter(mData:ArrayList<MyAppointment>): HFRecyclerAdapter<MyAppointment>(mData, R.layout.item_list_mydate) ,View.OnClickListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val mImages = ArrayList<String>()

    override fun onBind(holder: ViewHolder, position: Int, data: MyAppointment) {
//        var ll_imgs = holder.bind<RelativeLayout>(R.id.ll_imgs)
//        if(data.sAppointmentSignupId.isNullOrEmpty()){
//            ll_imgs.visibility = View.GONE
//        }else{
//            ll_imgs.visibility = View.VISIBLE
            var img_pull_header = holder.bind<SimpleDraweeView>(R.id.iv_pull_persion)
            var img_push_header = holder.bind<SimpleDraweeView>(R.id.iv_push_persion)

            img_pull_header.setImageURI(data.sAppointmentPicUrl)

            img_push_header.setImageURI(data.sPicUrl)

//            holder.setText(R.id.tv_pull_name,data.sAppointUserName)
//            holder.setText(R.id.tv_push_name,data.sUserName)
//        }

        holder.setText(R.id.tv_address_name,data.sPlace)
        holder.setText(R.id.tv_mydate_desc,data.sDesc)
        var tv_mydate_status = holder.bind<TextView>(R.id.tv_mydate_status)
        var tv_date_time = holder.bind<TextView>(R.id.tv_date_time)
        tv_date_time.visibility = View.GONE
        when (data.iStatus) {
            1 -> {//老  0   发起
               tv_date_time.visibility = View.VISIBLE
               if(data!!.sAppointmentSignupId.isNullOrEmpty()&&TextUtils.equals(data.iAppointUserid.toString(),userId)){
                   tv_mydate_status.text="发起"
                   tv_date_time.text = "截止时间:${converTime(data.dEndtime)}"
               }else if(data.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(data.iAppointUserid.toString(),userId)){
                    tv_mydate_status.text="待同意"
                   tv_date_time.text = "截止时间:${converToTime(data.dAppointmentSignupCreatetime)}"
               }else if(data.sAppointmentSignupId.isNotEmpty()&&TextUtils.equals(userId,data.iUserid.toString())){
                    tv_mydate_status.text ="等待对方同意"
                    tv_date_time.text = "截止时间:${converToTime(data.dAppointmentSignupCreatetime)}"
               }
            }
            2 -> { //老  1   赴约
                tv_mydate_status.text="同意"
            }
            3 -> { //老  3取消约会
                //tv_action0.text = "对方已关闭约会"
               tv_mydate_status.text="已拒绝"
            }
            4 -> { //老  4对方拒绝
                tv_mydate_status.text="主动取消"
            }
            5 -> { //老  4对方拒绝
                tv_mydate_status.text="过期自动取消"
            }
        }

        var tv_mydate_msg = holder.bind<TextView>(R.id.tv_mydate_msg)
        if(data.iIsread == 1){
            tv_mydate_msg.visibility =View.VISIBLE
        }else{
            tv_mydate_msg.visibility =View.GONE
        }

        var rv_mydate_imgs = holder.bind<RecyclerView>(R.id.rv_mydate_imgs)

        if (data.sAppointPic.isNullOrEmpty()) {
            rv_mydate_imgs.gone()
        } else {
            rv_mydate_imgs.setHasFixedSize(true)
            rv_mydate_imgs.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            mImages.clear()
            rv_mydate_imgs.visible()
            val images = data.sAppointPic?.split(",")
            if (images != null) {
                mImages.addAll(images.toList())
            }
            rv_mydate_imgs.adapter = SelfReleaselmageAdapter(mImages,1)
        }
    }

    override fun onClick(v: View?) {
        var fans= (v as TextView).tag as Fans
        if(fans.iIsFollow == 0){
            addFollow(fans,v)
        }else {
            delFollow(fans,v)
        }
    }

    private fun addFollow(fans:Fans,tv_focus:TextView){
        Request.getAddFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
            tv_focus.setBackgroundResource(R.drawable.shape_10r_fans)
            tv_focus.setTextColor(context.resources.getColor(R.color.color_DFE1E5))
            tv_focus.setText("已关注")
            fans.iIsFollow = 1
        }
    }

    private fun delFollow(fans:Fans,tv_focus:TextView){
        Request.getDelFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
            tv_focus.setBackgroundResource(R.drawable.shape_10r_nofans)
            tv_focus.setTextColor(context.resources.getColor(R.color.color_F7AB00))
            tv_focus.text ="关注"
            fans.iIsFollow = 0
        }
    }
}