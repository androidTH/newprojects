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
import com.d6.android.app.dialogs.GroupUserMoreDialog
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.models.UserData
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity

/**
 *粉丝
 */
class GroupUsersAdapter(mData:ArrayList<LoveHeartFans>): HFRecyclerAdapter<LoveHeartFans>(mData, R.layout.item_groupuser_list) ,View.OnClickListener{
    override fun onClick(v: View?) {

    }

    override fun onBind(holder: ViewHolder, position: Int, data: LoveHeartFans) {
        holder.setText(R.id.tv_name,data.sSendUserName)
        val headView = holder.bind<SimpleDraweeView>(R.id.user_headView)
        headView.setImageURI(data.sPicUrl)
        val tv_userinfo = holder.bind<TextView>(R.id.tv_userinfo)
        if(!data.gexingqianming.isNullOrEmpty()){
            tv_userinfo.text = data.gexingqianming
            tv_userinfo.visibility = View.VISIBLE
        }else if(!data.ziwojieshao.isNullOrEmpty()){
            tv_userinfo.text = data.ziwojieshao
            tv_userinfo.visibility = View.VISIBLE
        }else{
            tv_userinfo.visibility = View.GONE
        }

        val tv_sex = holder.bind<TextView>(R.id.tv_sex)
        tv_sex.isSelected = TextUtils.equals("0", "${data.sSex}")
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

        headView.setOnClickListener {
            isBaseActivity {
                it.startActivity<UserInfoActivity>("id" to "${data.iUserid}")
            }
        }

        val tv_vip = holder.bind<TextView>(R.id.tv_vip)
        tv_vip.backgroundDrawable =  getLevelDrawable("${data.userclassesid}",context)

        val tv_more = holder.bind<TextView>(R.id.tv_choose_friends)
        tv_more.setOnClickListener {
            isBaseActivity {
                var mGroupUserMoreDialog = GroupUserMoreDialog()
                mGroupUserMoreDialog.arguments = bundleOf("userId" to "${data.iUserid}")
                mGroupUserMoreDialog.setDialogListener { p, s ->

                }
                mGroupUserMoreDialog.show(it.supportFragmentManager,"cost")
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

}