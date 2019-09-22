package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.MemberTeQuan
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.textColor

/**
 * jinjiarui
 */
class TeQuanQuickAdapter(data: List<MemberTeQuan>) : BaseQuickAdapter<MemberTeQuan, BaseViewHolder>(R.layout.item_men_auth, data) {

    override fun convert(helper: BaseViewHolder, data:MemberTeQuan) {
        val iv_tqicon = helper.getView<SimpleDraweeView>(R.id.iv_tqicon)
        val tv_tqname = helper.getView<TextView>(R.id.tv_tqname)
        val tv_tqtag = helper.getView<TextView>(R.id.tv_tqtag)
        val tv_tqdesc = helper.getView<TextView>(R.id.tv_tqdesc)
//        simpleDraweeView.setImageURI(data.picurl)
        iv_tqicon.setImageURI(data.sMemberPic)
        if(data.iMemberType==1){
            tv_tqtag.background = ContextCompat.getDrawable(mContext,R.drawable.shape_orange_2r_f7a)
            tv_tqtag.textColor = ContextCompat.getColor(mContext,R.color.color_F7AB00)
            tv_tqtag.text = "APP"
        }else if(data.iMemberType==2){
            tv_tqtag.background = ContextCompat.getDrawable(mContext,R.drawable.shape_64b_2r)
            tv_tqtag.textColor = ContextCompat.getColor(mContext,R.color.color_64BA0A)
            tv_tqtag.text = "微信"
        }else if(data.iMemberType==3){
            tv_tqtag.background = ContextCompat.getDrawable(mContext,R.drawable.shape_fe7_2r)
            tv_tqtag.textColor = ContextCompat.getColor(mContext,R.color.color_FE7254)
            tv_tqtag.text = "人工"
        }
        tv_tqname.text = data.sMemberTitle
        tv_tqdesc.text = data.sMemberDesc
    }
}
