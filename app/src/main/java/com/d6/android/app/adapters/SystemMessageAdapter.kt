package com.d6.android.app.adapters

import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.extentions.request
import com.d6.android.app.models.SysMessage
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.toDefaultTime

/**
 *
 */
class SystemMessageAdapter(mData: ArrayList<SysMessage>) : HFRecyclerAdapter<SysMessage>(mData, R.layout.item_list_system_message) {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    override fun onBind(holder: ViewHolder, position: Int, data: SysMessage) {
        holder.setText(R.id.tv_title, data.title)
        holder.setText(R.id.tv_content, data.content)
        holder.setText(R.id.tv_time, data.createTime?.toDefaultTime())
        holder.bind<View>(R.id.ll_main).setOnClickListener {
            mOnItemClickListener?.onItemClick(it,position)
        }
        holder.bind<View>(R.id.tv_delete).setOnClickListener {
            delete(data)
        }
    }


    private fun delete(sysMessage: SysMessage) {
        isBaseActivity {
            it.dialog(canCancel = false)
            Request.deleteSysMsg(userId, sysMessage.ids.toString()).request(it) { _, _ ->
                it.showToast("删除成功")
                mData.remove(sysMessage)
                notifyDataSetChanged()
            }
        }
    }

    private inline fun isBaseActivity(next: (a: BaseActivity) -> Unit) {
        if (context != null && context is BaseActivity) {
            next(context as BaseActivity)
        }
    }

}