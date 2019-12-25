package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.FriendBean
import com.d6.android.app.models.InviteUserBean
import com.d6.android.app.utils.Const.DIALOG_SHOW_MAX
import com.d6.android.app.utils.getLevelName
import com.d6.android.app.utils.setLeftDrawable
import com.d6.android.app.utils.toTime
import com.facebook.drawee.view.SimpleDraweeView
import me.nereo.multi_image_selector.utils.TimeUtils
import org.jetbrains.anko.textColor

/**
 * jinjiarui
 */
class RegisterUserInfoQuickAdapter(data: List<InviteUserBean>) : BaseQuickAdapter<InviteUserBean, BaseViewHolder>(R.layout.item_registerinfo, data) {

    override fun convert(helper: BaseViewHolder, data: InviteUserBean) {
        var tv_content = helper.getView<TextView>(R.id.tv_content)
        var tv_content1 = helper.getView<TextView>(R.id.tv_content1)
        var tv_arrow = helper.getView<TextView>(R.id.tv_arrow)
        var tv_time = helper.getView<TextView>(R.id.tv_time)
        var tv_dot = helper.getView<TextView>(R.id.tv_dot)
        tv_time.text = data.dCreatetime.toTime("yyyy.MM.dd HH:mm")
        tv_content1.visibility = View.GONE
        tv_arrow.visibility = View.GONE
        if(helper.layoutPosition==0){
            setLeftDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_dot_selected_10r),tv_dot)
        }else{
            setLeftDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_dot_noselected_10r),tv_dot)
        }
        if(data.iEventType==1){
            tv_content.text = "注册D6"
        }else if(data.iEventType==2){
            if(data.iEnableMonth!! >0){
                tv_content.text = "成为会员：${data.userclassesname}(1个月)"
            }else{
                tv_content.text = "成为会员：${data.userclassesname}"
            }
        }else if(data.iEventType==3){
            tv_content1.visibility = View.VISIBLE
            tv_arrow.visibility = View.VISIBLE
            tv_content.text = "升级会员：${getLevelName("${data.sEventBefor}",mContext)}"
            tv_content1.text = "${getLevelName("${data.sEventAfter}",mContext)}"
        }else if(data.iEventType==4){
            tv_content.text = "续费会员：${data.userclassesname}"
        }
//        var position = (helper.layoutPosition - getHeaderLayoutCount())
//        if(position == DIALOG_SHOW_MAX){
//            mHeadView.setImageURI("res:///"+R.mipmap.share_more_icon)
//            mUserName.text="更多"
//            mUserName.textColor = ContextCompat.getColor(mContext,R.color.color_F7AB00)
//            var paint = mUserName.paint
//            paint.setFakeBoldText(true)
//        }else{
//            mHeadView.setImageURI(data.sPicUrl)
//        }
    }
}
