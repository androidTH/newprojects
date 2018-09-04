package com.d6.android.app.adapters

import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.MyDateActivity
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.dialogs.DateContactDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.NewDate
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.toast

class DateMeAdapter(mData: ArrayList<NewDate>, val type: Int = 0) : HFRecyclerAdapter<NewDate>(mData, R.layout.item_grid_new_date) {
    override fun onBind(holder: ViewHolder, position: Int, data: NewDate) {
        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        headView.setImageURI(data.picUrl)
        val nameView = holder.bind<TextView>(R.id.tv_name)
        var tv_content = holder.bind<TextView>(R.id.tv_content)
        nameView.text = data.name
        if(!data.egagementtext.isNullOrEmpty()){
           tv_content.visibility = View.VISIBLE
           tv_content.text = data.egagementtext
        }else{
           tv_content.visibility = View.GONE
        }
        val tv_action0 = holder.bind<TextView>(R.id.tv_action0)
        val tv_action1 = holder.bind<TextView>(R.id.tv_action1)
        val tv_action2 = holder.bind<TextView>(R.id.tv_action2)
        val tv_action3 = holder.bind<TextView>(R.id.tv_action3)
        val tv_action4 = holder.bind<TextView>(R.id.tv_action4)
        val tv_action5 = holder.bind<TextView>(R.id.tv_action5)
        tv_action0.visible()
        tv_action1.gone()
        tv_action2.gone()
        tv_action3.gone()
        tv_action4.gone()
        tv_action5.gone()
        //老  0发起,1赴约,2约会成功,3取消约会,4对方拒绝

        //新  1同意，2发起，3拒绝，4放弃
        when (data.state) {
            2 -> {     //老  0   发起
                if (type == 0) {
                    tv_action0.gone()
                    tv_action2.visible()
                    tv_action3.visible()
                } else {//我约的人
                    tv_action0.gone()
                    tv_action4.visible()
                    tv_action5.visible()
                }
            }
            1 -> { //老  1   赴约
                tv_action0.visible()
                tv_action1.visible()
                tv_action0.text = "赴约"
                tv_action1.text = "联系方式"
            }
//            2->{ //老 2约会成功
//                tv_action0.visible()
//                tv_action0.text = "已约会"
//            }
            4 -> { //老  3取消约会
               // tv_action0.text = "对方已关闭约会"
                tv_action0.text = "取消约会"
                tv_action0.visible()
            }
            3 -> { //老  4对方拒绝
                tv_action0.text = "已拒绝"
                tv_action0.visible()
            }
        }
        //拒绝
        tv_action2.setOnClickListener {
            updateState(data, 3)
        }
        //同意
        tv_action3.setOnClickListener {
            updateState(data, 1)
        }
        //联系方式
        tv_action1.setOnClickListener {
            val dateContactDialog = DateContactDialog()
            dateContactDialog.arguments = bundleOf("phone" to (data.phone
                    ?: ""), "wx" to (data.egagementwx ?: ""), "ids" to data.ids,"name" to if(data.name !=null) data.name else "")
            if (context is MyDateActivity) {
                dateContactDialog.show((context as MyDateActivity).supportFragmentManager, "dcd")
            }
        }
        //放弃
        tv_action5.setOnClickListener {
            updateState(data, 4)
        }
    }

    private fun updateState(date: NewDate, state: Int) {
        if (context is BaseActivity) {
            val c = context as BaseActivity
            c.dialog()//612
//            val userId = if(state == 1)date.accountId else SPUtils.instance().getString(Const.User.USER_ID)
//            val userId = SPUtils.instance().getString(Const.User.USER_ID)
            Request.updateDateState(date.ids, state.toString()).request(c) { _, data ->
                c.toast("状态更新成功")
                date.state = state
                notifyDataSetChanged()
            }
        }
    }
}