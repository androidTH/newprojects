package com.d6.android.app.adapters

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.RegisterFriendsDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Fans
import com.d6.android.app.models.InviteUserBean
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.JsonObject
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf

/**
 *邀请用户
 */
class InviteAdapter(mData:ArrayList<InviteUserBean>): HFRecyclerAdapter<InviteUserBean>(mData, R.layout.item_list_invitefriends) ,View.OnClickListener{

    private val sex by lazy{
       SPUtils.instance().getString(Const.User.USER_SEX)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: InviteUserBean) {
        holder.setText(R.id.tv_name,data.sUserName)
        val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
        headView.setImageURI(data.sPicUrl)
        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        if(!data.sMsgContent.isNullOrEmpty()){
            tv_userinfo.visibility = View.VISIBLE
            tv_userinfo.text = data.sMsgContent
        }else{
            tv_userinfo.visibility = View.GONE
        }

        var img_auther = holder.bind<ImageView>(R.id.img_auther)
        if(TextUtils.equals("3",data.screen)){
            img_auther.visibility=View.GONE
            img_auther.setImageResource(R.mipmap.renzheng_small)
        }else if(TextUtils.equals("1",data.screen)){
            img_auther.visibility=View.VISIBLE
            img_auther.setImageResource(R.mipmap.video_small)
        }else{
            img_auther.visibility=View.GONE
        }

        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        val tv_age = holder.bind<TextView>(R.id.tv_age)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        if(TextUtils.equals("0",data.nianling)||TextUtils.equals("-1",data.nianling)){
            tv_age.visibility = View.GONE
        }else if(data.nianling.isNullOrEmpty()){
            tv_age.visibility = View.GONE
        }else{
            tv_age.isSelected = TextUtils.equals("0", data.sSex)
            tv_age.visibility = View.VISIBLE
            tv_age.text = "${data.nianling}岁"
        }

        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        if (TextUtils.equals("1", sex)&&TextUtils.equals(data.sSex, "0")) {//0 女 1 男
            tv_vip.visibility =View.GONE
        } else {
            tv_vip.visibility = View.VISIBLE
            tv_vip.backgroundDrawable = getLevelDrawable(data.userclassesid.toString(),context)
        }

        var tv_showinfo_desc = holder.bind<TextView>(R.id.tv_showinfo_desc)

        tv_showinfo_desc.tag = data
        tv_showinfo_desc.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var mInviteBean= (v as TextView).tag as InviteUserBean
        Log.i("inviteadapter","点击了事件")
        var mRegisterFriendsDialog = RegisterFriendsDialog()
        mRegisterFriendsDialog.arguments = bundleOf("userId" to "${mInviteBean.iUserid}")
        mRegisterFriendsDialog.show((context as BaseActivity).supportFragmentManager,"信息")
    }

}