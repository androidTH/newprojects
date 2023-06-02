package com.d6zone.android.app.adapters

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.d6zone.android.app.R
import com.d6zone.android.app.base.adapters.HFRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.models.EnvelopeBean
import com.d6zone.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.textColor

/**
 *粉丝
 */
class RedMoneyListAdapter(mData:ArrayList<EnvelopeBean>): HFRecyclerAdapter<EnvelopeBean>(mData, R.layout.item_list_redmoney) ,View.OnClickListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private var mHashMap:HashMap<Int,Int> = HashMap()

    override fun onBind(holder: ViewHolder, position: Int, data: EnvelopeBean) {
        var tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        var headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
//        if(data.iListSetting==2){
////            headView.showBlur(data.sPicUrl)
//            headView.setImageURI("res:///"+R.mipmap.shenmiren_icon)
//            holder.setText(R.id.tv_name,"匿名")
//            holder.bind<TextView>(R.id.tv_name).textColor = ContextCompat.getColor(context,R.color.color_8F5A5A)
//            tv_userinfo.visibility = View.VISIBLE
//            if(TextUtils.equals("0",data.sSex)){
//                tv_userinfo.text= "她隐藏了身份"
//            }else{
//                tv_userinfo.text= "他隐藏了身份"
//            }
//        }else{
//            headView.setImageURI(data.PicUrl)
//            holder.setText(R.id.tv_name,data.name)
//            holder.bind<TextView>(R.id.tv_name).textColor = ContextCompat.getColor(context,R.color.color_black)
//            tv_userinfo.visibility = View.GONE
//        }

        headView.setImageURI(data.picUrl)
        holder.setText(R.id.tv_name,data.name)
        holder.bind<TextView>(R.id.tv_name).textColor = ContextCompat.getColor(context,R.color.color_black)
        data.dCreatetime?.let {
            tv_userinfo.text = DateToolUtils.getConversationFormatDate(it,true,context)
        }

//        headView.setOnClickListener {
//            startActivity<UserInfoActivity>("id" to it.accountId.toString())
//        }

//        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
//        val tv_age = holder.bind<TextView>(R.id.tv_age)
//        tv_sex.isSelected = TextUtils.equals("0", data.sex)
//
//        if(data.nianling.isNullOrEmpty()){
//            tv_age.visibility = View.GONE
//        }else{
//            tv_age.isSelected = TextUtils.equals("0", data.sex)
//            tv_age.visibility = View.GONE
//            tv_age.text = "${data.nianling}岁"
//        }
//
//        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
//        if (TextUtils.equals("1", sex)&& TextUtils.equals(data.sex, "0")) {//0 女 1 男
//            tv_vip.visibility =View.GONE
//        } else {
//            tv_vip.visibility = View.GONE
//            tv_vip.backgroundDrawable = getLevelDrawable("${data.userclassesid}",context)
//        }

        var tv_receivedliked = holder.bind<TextView>(R.id.tv_receivedliked)
//        if(TextUtils.equals("0",data.sex)){
//            tv_receivedliked.text = "${data.iLovePoint}"
//        }else{
//            tv_receivedliked.text = "${data.iAllLovePoint}"
//        }

        tv_receivedliked.text = "${data.iLovePoint}"
    }

    override fun onClick(v: View?) {
//        var fans= (v as TextView).tag as Fans
//        if(fans.iIsFollow == 0){
//            addFollow(fans,v)
//        }else {
//            delFollow(fans,v)
//        }
    }

//    private fun addFollow(fans:Fans,tv_focus:TextView){
//        Request.getAddFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
//            tv_focus.setBackgroundResource(R.drawable.shape_10r_fans)
//            tv_focus.setTextColor(context.resources.getColor(R.color.color_DFE1E5))
//            tv_focus.setText("已喜欢")
//            fans.iIsFollow = 1
//        }
//    }
//
//    private fun delFollow(fans:Fans,tv_focus:TextView){
//        Request.getDelFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
//            tv_focus.setBackgroundResource(R.drawable.shape_10r_nofans)
//            tv_focus.setTextColor(context.resources.getColor(R.color.color_F7AB00))
//            tv_focus.text ="喜欢"
//            fans.iIsFollow = 0
//        }
//    }
}