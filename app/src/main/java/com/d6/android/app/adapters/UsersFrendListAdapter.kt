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
import com.d6.android.app.extentions.request
import com.d6.android.app.models.GroupUserBean
import com.d6.android.app.models.LoveHeartFans
import com.d6.android.app.models.NewGroupBean
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity

/**
 * 好友
 */
class UsersFrendListAdapter(mData:ArrayList<LoveHeartFans>): HFRecyclerAdapter<LoveHeartFans>(mData, R.layout.item_usergoodfriends_list) ,View.OnClickListener{
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
                var status = -1
                if (IsNotNullGroupBean()) {
                    if (mGroupBean.iIsOwner == 1) {
                        status = 1
                    } else if (mGroupBean.iIsManager == 1) {
                        status = 2
                    }
                }
                var mGroupUserMoreDialog = GroupUserMoreDialog()
                mGroupUserMoreDialog.arguments = bundleOf("userId" to "${data.iUserid}","bean" to data,"status" to status)
                mGroupUserMoreDialog.setDialogListener { p, s ->
                    if(p==1){
                        if(status==1){

                        }
                    }else if(p==2){

                    }
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

    //设置管理员
    private fun setUserManager(groupId:String,iUserId:String,iIsManager:Int,position:Int){
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.updateUserGroupManager("${groupId}", "${iUserId}",iIsManager).request(it) { _, _ ->
                if(iIsManager==1){
                    it.showToast("设置成功")
                }else{
                    it.showToast("取消成功")
                }
            }
        }
    }

    private fun delUserManager(groupId:String,iUserId:String,position:Int){
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.kickGroup("${groupId}", "${iUserId}").request(it) { _, _ ->
                mData.removeAt(position)
                notifyDataSetChanged()
                it.showToast("剔除成功")
            }
        }
    }

    lateinit var mGroupBean: NewGroupBean
    fun IsNotNullGroupBean()=::mGroupBean.isInitialized

    fun setGroupOwner(group:NewGroupBean){
        mGroupBean = group
    }
}