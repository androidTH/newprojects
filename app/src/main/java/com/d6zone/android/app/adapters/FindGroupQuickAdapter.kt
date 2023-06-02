package com.d6zone.android.app.adapters

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.d6zone.android.app.R
import com.d6zone.android.app.models.FindGroupBean
import com.facebook.drawee.view.SimpleDraweeView

/**
 * jinjiarui
 */
class FindGroupQuickAdapter(data: List<FindGroupBean>) : BaseQuickAdapter<FindGroupBean, BaseViewHolder>(R.layout.item_findgroup, data) {

    override fun convert(helper: BaseViewHolder, data: FindGroupBean) {
        val group_headView = helper.getView<SimpleDraweeView>(R.id.group_headView)
        group_headView.setImageURI(data.sGroupPic)
        var tv_groupname = helper.getView<TextView>(R.id.tv_groupname)
        tv_groupname.text = "${data.sGroupName}"

        var tv_groupnum = helper.getView<TextView>(R.id.tv_groupnums)
        tv_groupnum.text = "${data.iMemberCount}人"
        var rl_headerview = helper.getView<RelativeLayout>(R.id.rl_headerview)
        var sv_list03 = helper.getView<SimpleDraweeView>(R.id.sv_list03)
        var sv_list02 = helper.getView<SimpleDraweeView>(R.id.sv_list02)
        var sv_list01 = helper.getView<SimpleDraweeView>(R.id.sv_list01)
        if(data.sLastJoinMember.isNullOrEmpty()){
            rl_headerview.visibility = View.GONE
        }else{
            rl_headerview.visibility = View.VISIBLE
        }
        Log.i("findgroupurl","群名字${data.sGroupName}：${data.sLastJoinMember}")
        data.sLastJoinMember?.let {
            var sLastJoinMember = it.split(",")
            if(sLastJoinMember.size>=3){
                sv_list01.setImageURI(sLastJoinMember[0])
                sv_list02.setImageURI(sLastJoinMember[1])
                sv_list03.setImageURI(Uri.parse("res:///" + R.mipmap.more_cycle_icon))
                sv_list02.visibility = View.VISIBLE
                sv_list03.visibility = View.VISIBLE
            }else if(sLastJoinMember.size==2){
                sv_list01.setImageURI(sLastJoinMember[0])
                sv_list02.setImageURI(sLastJoinMember[1])
                sv_list02.visibility = View.VISIBLE
                sv_list03.visibility = View.GONE
            }else{
                sv_list01.setImageURI(sLastJoinMember[0])
                sv_list02.visibility = View.GONE
                sv_list03.visibility = View.GONE
            }
        }
    }
}
