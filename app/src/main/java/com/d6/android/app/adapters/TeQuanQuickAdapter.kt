package com.d6.android.app.adapters

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6.android.app.R
import com.d6.android.app.models.MemberTeQuan
import com.d6.android.app.utils.Const.CONST_RES_MIPMAP
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.textColor

/**
 * jinjiarui
 */
class TeQuanQuickAdapter(data: List<MemberTeQuan>,IsNotShow:Boolean=true) : BaseQuickAdapter<MemberTeQuan, BaseViewHolder>(R.layout.item_men_auth, data) {

    private var mIsNotShow = IsNotShow
    override fun convert(helper: BaseViewHolder, data:MemberTeQuan) {
        val iv_tqicon = helper.getView<SimpleDraweeView>(R.id.iv_tqicon)
        val tv_tqname = helper.getView<TextView>(R.id.tv_tqname)
        val iv_tqtag = helper.getView<SimpleDraweeView>(R.id.iv_tqtag)
        val tv_tqdesc = helper.getView<TextView>(R.id.tv_tqdesc)
//      simpleDraweeView.setImageURI(data.picurl)
        if(data.sMemberPic.isNotEmpty()){
            iv_tqicon.setImageURI(data.sMemberPic)
        }

        if(data.iMemberType==1){
            iv_tqtag.setImageURI(CONST_RES_MIPMAP+R.mipmap.jiaobiao_app)
        }else if(data.iMemberType==2){
            iv_tqtag.setImageURI(CONST_RES_MIPMAP+R.mipmap.jiaobiao_wechat)
        }else if(data.iMemberType==3){
            iv_tqtag.setImageURI(CONST_RES_MIPMAP+R.mipmap.jiaobiao_rengong)
        }

        if(mIsNotShow){
            iv_tqtag.visibility= View.VISIBLE
        }else{
            iv_tqtag.visibility= View.GONE
        }

        if(data.iStatus!=1){
            tv_tqname.textColor = ContextCompat.getColor(mContext,R.color.color_C1C0C0)
            tv_tqdesc.textColor = ContextCompat.getColor(mContext,R.color.color_C1C0C0)
        }else{
            tv_tqname.textColor = ContextCompat.getColor(mContext,R.color.color_black)
            tv_tqdesc.textColor = ContextCompat.getColor(mContext,R.color.color_888888)
        }
        tv_tqname.text = data.sMemberTitle
        tv_tqdesc.text = data.sMemberDesc
    }
}
