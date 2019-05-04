package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.FriendBean
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.textColor

/**
 * jinjiarui
 */
class DialogShareFriendsQuickAdapter(data: List<FriendBean>) : BaseQuickAdapter<FriendBean, BaseViewHolder>(R.layout.item_sharefriends, data) {

    override fun convert(helper: BaseViewHolder, data: FriendBean) {
        val mHeadView = helper.getView<SimpleDraweeView>(R.id.headView)
        mHeadView.setImageURI(data.sPicUrl)
        var mUserName = helper.getView<TextView>(R.id.tv_share_name)
        mUserName.text = data.sUserName
        if(getData().size > 9){
            mHeadView.setImageURI("res:///"+R.mipmap.share_more_icon)
            mUserName.text="更多"
            mUserName.textColor = ContextCompat.getColor(mContext,R.color.color_F7AB00)
            var paint = mUserName.paint
            paint.setFakeBoldText(true)
        }
    }
}
