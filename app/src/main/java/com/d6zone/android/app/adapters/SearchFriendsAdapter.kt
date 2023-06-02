package com.d6zone.android.app.adapters

import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.d6zone.android.app.R
import com.d6zone.android.app.activities.UserInfoActivity
import com.d6zone.android.app.base.BaseActivity
import com.d6zone.android.app.base.adapters.HFRecyclerAdapter
import com.d6zone.android.app.base.adapters.util.ViewHolder
import com.d6zone.android.app.models.UserData
import com.d6zone.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.startActivity

/**
 *粉丝
 */
class SearchFriendsAdapter(mData:ArrayList<UserData>): HFRecyclerAdapter<UserData>(mData, R.layout.item_list_friends) ,View.OnClickListener{
    override fun onClick(v: View?) {

    }

    override fun onBind(holder: ViewHolder, position: Int, data: UserData) {
        holder.setText(R.id.tv_name,data.name)
        val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
        headView.setImageURI(data.picUrl)
        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        if(!data.signature.isNullOrEmpty()){
            tv_userinfo.text = data.signature
            tv_userinfo.visibility = View.VISIBLE
        }else{
            tv_userinfo.visibility = View.GONE
        }

        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", "${data.sex}")
        tv_sex.text = data.age

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

        headView.setOnClickListener {
            isBaseActivity {
                it.startActivity<UserInfoActivity>("id" to data.accountId.toString())
            }
        }

        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        tv_vip.backgroundDrawable =  getLevelDrawable("${data.userclassesid}",context)
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

}