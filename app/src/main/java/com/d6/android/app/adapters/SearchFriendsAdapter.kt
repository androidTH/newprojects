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
import com.d6.android.app.models.Fans
import com.d6.android.app.models.FriendBean
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.view_trend_view.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivity

/**
 *粉丝
 */
class SearchFriendsAdapter(mData:ArrayList<FriendBean>): HFRecyclerAdapter<FriendBean>(mData, R.layout.item_list_friends) ,View.OnClickListener{
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
        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        var img_friends_auther = holder.bind<ImageView>(R.id.img_friends_auther)
        if(TextUtils.equals("3",data.screen)){
            img_friends_auther.visibility=View.GONE
            img_friends_auther.setImageResource(R.mipmap.renzheng_small)
        }else if(TextUtils.equals("1",data.screen)){
            img_friends_auther.visibility=View.VISIBLE
            img_friends_auther.setImageResource(R.mipmap.video_small)
        }else{
            img_friends_auther.visibility=View.GONE
        }

        headView.setOnClickListener {
            isBaseActivity {
                it.startActivity<UserInfoActivity>("id" to data.iUserid.toString())
            }
        }

        if (TextUtils.equals(data.userclassesid, "27")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_cj)
        } else if (TextUtils.equals(data.userclassesid, "28")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_zj)
        } else if (TextUtils.equals(data.userclassesid, "29")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.gril_gj)
        } else if (TextUtils.equals(data.userclassesid.toString(), "22")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_ordinary)
        } else if (TextUtils.equals(data.userclassesid, "23")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_silver)
        } else if (TextUtils.equals(data.userclassesid, "24")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_gold)
        } else if (TextUtils.equals(data.userclassesid, "25")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_zs)
        } else if (TextUtils.equals(data.userclassesid, "26")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.vip_private)
        }else if (TextUtils.equals(data.userclassesid, "7")) {
            tv_vip.backgroundDrawable = ContextCompat.getDrawable(context, R.mipmap.youke_icon)
        }else{
            tv_vip.visibility = View.GONE
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

}