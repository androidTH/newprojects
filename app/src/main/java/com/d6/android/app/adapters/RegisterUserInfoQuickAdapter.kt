package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.FriendBean
import com.d6.android.app.utils.Const.DIALOG_SHOW_MAX
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.textColor

/**
 * jinjiarui
 */
class RegisterUserInfoQuickAdapter(data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_registerinfo, data) {

    override fun convert(helper: BaseViewHolder, data: String) {
        var tv_content = helper.getView<TextView>(R.id.tv_content)
        tv_content.text = data
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
