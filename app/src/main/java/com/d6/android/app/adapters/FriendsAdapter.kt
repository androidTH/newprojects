package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.FriendBean
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.CustomerServiceWomenId
import com.d6.android.app.utils.getLevelDrawable
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivity

/**
 *粉丝
 */
class FriendsAdapter(mData:ArrayList<FriendBean>): HFRecyclerAdapter<FriendBean>(mData, R.layout.item_list_friends) ,View.OnClickListener{
    override fun onClick(v: View?) {

    }

    override fun onBind(holder: ViewHolder, position: Int, data: FriendBean) {
        holder.setText(R.id.tv_name,data.sUserName)
        val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
        headView.setImageURI(data.sPicUrl)
        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        if(!data.ziwojieshao.isNullOrEmpty()){
            tv_userinfo.text = data.ziwojieshao
            tv_userinfo.visibility = View.VISIBLE
        }else{
            tv_userinfo.visibility = View.GONE
        }
        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        tv_sex.text = data.nianling

        var img_friends_auther = holder.bind<ImageView>(R.id.img_friends_auther)
        if(TextUtils.equals("3",data.screen)){
            img_friends_auther.visibility=View.GONE
            img_friends_auther.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.renzheng_small)
        }else if(TextUtils.equals("1",data.screen)){
            img_friends_auther.visibility=View.VISIBLE
            img_friends_auther.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.video_small)
        }else{
            img_friends_auther.visibility=View.GONE
        }

        var mTvFriends = holder.bind<TextView>(R.id.tv_choose_friends)

        if(data.iIsChecked == 0){
            mTvFriends.visibility = View.GONE
        }else{
            mTvFriends.visibility = View.VISIBLE
        }

        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
//        if(TextUtils.equals("0", data.sSex)){
            tv_vip.backgroundDrawable =  getLevelDrawable(data.userclassesid.toString(),context)
//        }

        tv_vip.backgroundDrawable =  getLevelDrawable(data.userclassesid.toString(),context)

        var iv_friends_servicesign = holder.bind<ImageView>(R.id.iv_friends_servicesign)

        if(TextUtils.equals(CustomerServiceId,data.iUserid.toString())|| TextUtils.equals(CustomerServiceWomenId,data.iUserid.toString())){
            iv_friends_servicesign.visibility = View.VISIBLE
            iv_friends_servicesign.backgroundDrawable = ContextCompat.getDrawable(context,R.mipmap.official_iconnew)
        }else{
            iv_friends_servicesign.visibility = View.GONE
        }

        headView.setOnClickListener {
            isBaseActivity {
                it.startActivity<UserInfoActivity>("id" to data.iUserid.toString())
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

}