package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor

/**
 *粉丝
 */
class BangdanListQuickAdapter(mData:ArrayList<LoveHeartFans>): BaseQuickAdapter<LoveHeartFans,BaseViewHolder>(R.layout.item_list_bangdan_quick,mData) ,View.OnClickListener{

    private val sex by lazy{
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private var mHashMap:HashMap<Int,Int> = HashMap()

    override fun convert(helper: BaseViewHolder, data: LoveHeartFans) {
        var tv_userinfo = helper.getView<TextView>(R.id.tv_userinfo)
        var headView = helper.getView<SimpleDraweeView>(R.id.user_headView)
        if(data.iListSetting==2){
            headView.setImageURI("res:///"+R.mipmap.shenmiren_icon)
            helper.setText(R.id.tv_name,"匿名")
            helper.getView<TextView>(R.id.tv_name).textColor = ContextCompat.getColor(mContext,R.color.color_8F5A5A)
            tv_userinfo.visibility = View.VISIBLE
            if(TextUtils.equals("0",data.sSex)){
                tv_userinfo.text= "她隐藏了身份"
            }else{
                tv_userinfo.text= "他隐藏了身份"
            }
        }else{
            headView.setImageURI(data.sPicUrl)
            helper.setText(R.id.tv_name,data.sSendUserName)
            helper.getView<TextView>(R.id.tv_name).textColor = ContextCompat.getColor(mContext,R.color.color_black)
            tv_userinfo.visibility = View.GONE
        }

        val tv_sex = helper.getView<TextView>(R.id.tv_sex)
        val tv_age = helper.getView<TextView>(R.id.tv_age)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)

        if(data.nianling.isNullOrEmpty()){
            tv_age.visibility = View.GONE
        }else{
            tv_age.isSelected = TextUtils.equals("0", data.sSex)
            tv_age.visibility = View.GONE
            tv_age.text = "${data.nianling}岁"
        }

        val tv_vip = helper.getView<TextView>(R.id.tv_vip)
        if (TextUtils.equals("1", sex)&& TextUtils.equals(data.sSex, "0")) {//0 女 1 男
            tv_vip.visibility =View.GONE
        } else {
            tv_vip.visibility = View.VISIBLE
            tv_vip.backgroundDrawable = getLevelDrawable("${data.userclassesid}",mContext)
        }

        var tv_receivedliked = helper.getView<TextView>(R.id.tv_receivedliked)
        if(TextUtils.equals("0",data.sSex)){
            tv_receivedliked.text = "收到${data.iAllLovePoint}"
        }else{
            tv_receivedliked.text = "送出${data.iAllLovePoint}"
        }

        var tv_order = helper.getView<TextView>(R.id.tv_order)
//        tv_order.text = "${data.sPosition}"
        var position = (helper.adapterPosition-headerLayoutCount)
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

    }

}