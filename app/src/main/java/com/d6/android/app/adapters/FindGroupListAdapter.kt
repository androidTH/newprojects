package com.d6.android.app.adapters

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.BaseRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.models.FindGroupBean
import com.facebook.drawee.view.SimpleDraweeView

/**
 *
 */
class FindGroupListAdapter(mData: ArrayList<FindGroupBean>) : BaseRecyclerAdapter<FindGroupBean>(mData, R.layout.item_findgroup) {
    override fun onBind(holder: ViewHolder, position: Int, data: FindGroupBean) {
        val group_headView = holder.bind<SimpleDraweeView>(R.id.group_headView)
        group_headView.setImageURI(data.sGroupPic)
        var tv_groupname = holder.bind<TextView>(R.id.tv_groupname)
        tv_groupname.text = "${data.sGroupName}"

        var tv_groupnum = holder.bind<TextView>(R.id.tv_groupnums)
        tv_groupnum.text = "${data.iMemberCount}人"
        var rl_headerview = holder.bind<RelativeLayout>(R.id.rl_headerview)
        var sv_list03 = holder.bind<SimpleDraweeView>(R.id.sv_list03)
        var sv_list02 = holder.bind<SimpleDraweeView>(R.id.sv_list02)
        var sv_list01 = holder.bind<SimpleDraweeView>(R.id.sv_list01)
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

        var tv_more = holder.bind<TextView>(R.id.tv_more)
        tv_more.setOnClickListener {

        }
    }
}