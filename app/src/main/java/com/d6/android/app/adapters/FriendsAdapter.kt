package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.UserInfoActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.Fans
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.startActivity

/**
 *粉丝
 */
class FriendsAdapter(mData:ArrayList<Fans>): HFRecyclerAdapter<Fans>(mData, R.layout.item_list_friends) ,View.OnClickListener{
    override fun onClick(v: View?) {

    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: Fans) {
        holder.setText(R.id.tv_name,data.sUserName)
        val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
//        val tv_time =holder.bind<TextView>(R.id.tv_time)
//        tv_time.text = data.dJointime.toTime("MM.dd")
        headView.setImageURI(data.sPicUrl)
        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        tv_userinfo.text = data.gexingqianming
        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", data.sSex)
        tv_sex.text = data.nianling
        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        val sex = SPUtils.instance().getString(Const.User.USER_SEX)
        if (TextUtils.equals("1", sex)&& TextUtils.equals(data.sSex, "0")) {//0 女 1 男
            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility =View.GONE
        } else {
            tv_vip.text = String.format("%s", data.userclassesname)
            tv_vip.visibility = View.VISIBLE
        }
        var mTvFriends = holder.bind<TextView>(R.id.tv_choose_friends)
        if(data.iIsFollow == 0){
            mTvFriends.visibility = View.GONE
        }else{
            mTvFriends.visibility = View.VISIBLE
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