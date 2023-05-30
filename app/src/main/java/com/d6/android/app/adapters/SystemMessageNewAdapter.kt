package com.d6.android.app.adapters

import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.SysMessage
import com.facebook.drawee.view.SimpleDraweeView

/**
 *
 */
class SystemMessageNewAdapter(mData: ArrayList<SysMessage>) : HFRecyclerAdapter<SysMessage>(mData, R.layout.layout_systemmsg_item) {

    override fun onBind(holder: ViewHolder, position: Int, data: SysMessage) {

        var group_headView = holder.bind<SimpleDraweeView>(R.id.group_headView)
        group_headView.setImageURI("res:///"+R.mipmap.noticesys_sysinfo)
        var tv_msg_content = holder.bind<TextView>(R.id.tv_msg_content)
        tv_msg_content.text = data.content
        var tv_groupname = holder.bind<TextView>(R.id.tv_groupname)
        tv_groupname.text = "${data.title}"

        var ll_group_info = holder.bind<LinearLayout>(R.id.ll_group_info)

        var linear_group_agree = holder.bind<LinearLayout>(R.id.linear_group_agree)
        var tv_group_no = holder.bind<TextView>(R.id.tv_group_no)
        var tv_group_agree = holder.bind<TextView>(R.id.tv_group_agree)

        var line_group = holder.bind<View>(R.id.line_group)
        var tv_checkmore = holder.bind<TextView>(R.id.tv_checkmore)

        if (TextUtils.equals("1",data.urltype)){
            ll_group_info.visibility = View.VISIBLE
            linear_group_agree.visibility = View.GONE
            line_group.visibility = View.GONE
            tv_checkmore.visibility = View.GONE
        }else if(TextUtils.equals("2",data.urltype)){
            ll_group_info.visibility = View.VISIBLE
            linear_group_agree.visibility = View.VISIBLE
            line_group.visibility = View.GONE
            tv_checkmore.visibility = View.GONE
        }else if(TextUtils.equals("3",data.urltype)){
            ll_group_info.visibility = View.GONE
            linear_group_agree.visibility = View.GONE
            line_group.visibility = View.VISIBLE
            tv_checkmore.visibility = View.VISIBLE
        }else{
            ll_group_info.visibility = View.GONE
            linear_group_agree.visibility = View.GONE
            line_group.visibility = View.VISIBLE
            tv_checkmore.visibility = View.VISIBLE
        }

        tv_group_no.setOnClickListener {
            mOnItemClickListener?.onItemClick(it,position)
        }

        tv_group_agree.setOnClickListener {
            mOnItemClickListener?.onItemClick(it,position)
        }

        tv_checkmore.setOnClickListener {
            mOnItemClickListener?.onItemClick(it,position)
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

}