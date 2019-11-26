package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.JsonObject
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor

/**
 *粉丝
 */
class BangdanListAdapter(mData:ArrayList<LoveHeartFans>): HFRecyclerAdapter<LoveHeartFans>(mData, R.layout.item_list_bangdan) ,View.OnClickListener{

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private var mHashMap:HashMap<Int,Int> = HashMap()

    override fun onBind(holder: ViewHolder, position: Int, data: LoveHeartFans) {
        var tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        var headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
        if(data.iListSetting==2){
//            headView.showBlur(data.sPicUrl)
            headView.setImageURI("res:///"+R.mipmap.shenmiren_icon)
            holder.setText(R.id.tv_name,"匿名")
            holder.bind<TextView>(R.id.tv_name).textColor = ContextCompat.getColor(context,R.color.color_8F5A5A)
            tv_userinfo.visibility = View.VISIBLE
            if(TextUtils.equals("0",data.sSex)){
                tv_userinfo.text= "她隐藏了身份"
            }else{
                tv_userinfo.text= "他隐藏了身份"
            }
//            if(TextUtils.equals("${data.iUserid}", getLocalUserId())){
//                tv_userinfo.text = "你开启了在榜单中隐藏身份"
//                tv_userinfo.visibility = View.VISIBLE
//            }else{
//                tv_userinfo.visibility = View.GONE
//                tv_userinfo.text = "对方送的[img src=redheart_small/]较少，支付积分即可查看身份 "
//                if(!data.gexingqianming.isNullOrEmpty()){
//                    tv_userinfo.visibility = View.VISIBLE
//                    tv_userinfo.text = data.gexingqianming
//                }else if(!data.ziwojieshao.isNullOrEmpty()){
//                    tv_userinfo.text = data.ziwojieshao
//                    tv_userinfo.visibility = View.VISIBLE
//                }else{
//                    tv_userinfo.visibility = View.GONE
//                }
//            }
        }else{
            headView.setImageURI(data.sPicUrl)
            holder.setText(R.id.tv_name,data.sSendUserName)
            holder.bind<TextView>(R.id.tv_name).textColor = ContextCompat.getColor(context,R.color.color_black)
            tv_userinfo.visibility = View.GONE
//        val tv_time =holder.bind<TextView>(R.id.tv_time)
//        tv_time.text = data.dJointime.toTime("MM.dd")

//            if(!data.gexingqianming.isNullOrEmpty()){
//                tv_userinfo.visibility = View.VISIBLE
//                tv_userinfo.text = data.gexingqianming
//            }else if(!data.ziwojieshao.isNullOrEmpty()){
//                tv_userinfo.text = data.ziwojieshao
//                tv_userinfo.visibility = View.VISIBLE
//            }else{
//                tv_userinfo.visibility = View.GONE
//            }
        }


        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        tv_sex.text = data.nianling
        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        if (TextUtils.equals("1", sex)&& TextUtils.equals(data.sSex, "0")) {//0 女 1 男
//            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility =View.GONE
        } else {
//            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility = View.VISIBLE
            tv_vip.backgroundDrawable = getLevelDrawable("${data.userclassesid}",context)
        }

        var tv_receivedliked = holder.bind<TextView>(R.id.tv_receivedliked)
        if(TextUtils.equals("0",data.sSex)){
            tv_receivedliked.text = "${data.iAllLovePoint}"
        }else{
            tv_receivedliked.text = "${data.iAllLovePoint}"
        }

        var tv_order = holder.bind<TextView>(R.id.tv_order)
        if(position==0){
            tv_order.textColor = ContextCompat.getColor(context,R.color.color_FF4500)
        }else if(position==1){
            tv_order.textColor = ContextCompat.getColor(context,R.color.color_BE34FF)
        }else if(position==2){
            tv_order.textColor = ContextCompat.getColor(context,R.color.color_34B1FF)
        }else{
            tv_order.textColor = ContextCompat.getColor(context,R.color.color_888888)
        }

        if(position>0){
            if(mData[position-1].iAllLovePoint==data.iAllLovePoint){
                mHashMap.put(position,mHashMap.get(position-1)!!.toInt())
                if(position<9){
                    tv_order.text = "0${mHashMap.get(position)!!.toInt()}"
                }else{
                    tv_order.text = "${mHashMap.get(position)!!.toInt()}"
                }
            }else{
                mHashMap.put(position,position+1)
                if(position<9){
                    tv_order.text = "0${position+1}"
                }else{
                    tv_order.text = "${position+1}"
                }
            }
        }else{
            if(position<9){
                tv_order.text = "0${position+1}"
            }else{
                tv_order.text = "${position+1}"
            }
            mHashMap.put(position,position+1)
        }
    }

    override fun onClick(v: View?) {
        var fans= (v as TextView).tag as Fans
        if(fans.iIsFollow == 0){
            addFollow(fans,v)
        }else {
            delFollow(fans,v)
        }
//        notifyDataSetChanged()
    }

    private fun addFollow(fans:Fans,tv_focus:TextView){
        Request.getAddFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
            tv_focus.setBackgroundResource(R.drawable.shape_10r_fans)
            tv_focus.setTextColor(context.resources.getColor(R.color.color_DFE1E5))
            tv_focus.setText("已喜欢")
            fans.iIsFollow = 1
        }
    }

    private fun delFollow(fans:Fans,tv_focus:TextView){
        Request.getDelFollow(userId, fans.iUserid.toString()).request((context as BaseActivity)){ s: String?, jsonObject: JsonObject? ->
            tv_focus.setBackgroundResource(R.drawable.shape_10r_nofans)
            tv_focus.setTextColor(context.resources.getColor(R.color.color_F7AB00))
            tv_focus.text ="喜欢"
            fans.iIsFollow = 0
        }
    }
}