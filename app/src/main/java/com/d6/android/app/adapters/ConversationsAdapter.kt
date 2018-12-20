package com.d6.android.app.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.utils.RongUtils
import com.d6.android.app.utils.showTips
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.badge.Badge
import com.d6.android.app.widget.badge.QBadgeView
import com.facebook.drawee.view.SimpleDraweeView
import io.rong.imkit.RongContext
import io.rong.imkit.utils.RongDateUtils
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.header_messages.view.*
import org.jetbrains.anko.toast


/**
 * Created on 2017/12/25.
 */
class ConversationsAdapter(mData: ArrayList<Conversation>) : HFRecyclerAdapter<Conversation>(mData, R.layout.item_list_conversations) {
    @SuppressLint("SetTextI18n")
    override fun onBind(holder: ViewHolder, position: Int, data: Conversation) {
        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        val tv_name = holder.bind<TextView>(R.id.tv_name)
        val tv_time = holder.bind<TextView>(R.id.tv_time)

        if (data.conversationType === Conversation.ConversationType.PRIVATE) {
            RongUtils.setUserInfo(data.targetId, tv_name, headView)
        }
        tv_time.text = RongDateUtils.getConversationListFormatDate(data.sentTime, context)
        val tv_content = holder.bind<TextView>(R.id.tv_content)
        if (data.latestMessage != null) {
            val provider = RongContext.getInstance().getMessageTemplate(data.latestMessage.javaClass)
            if (provider != null) {
                tv_content.text = provider.getContentSummary(data.latestMessage)
            }
        } else {
            tv_content.text = ""
        }

//        val tv_unread = holder.bind<TextView>(R.id.tv_unread)
        var count = data.unreadMessageCount
        if (count > 99) {
            count = 99
        }
//        tv_unread.visibility = if (count > 0) View.VISIBLE else View.GONE
//        tv_unread.text = count.toString() + ""
        if(count > 0){
            if(mBadegeUser == null){
                mBadegeUser = QBadgeView(context).bindTarget(headView)
            }
            mBadegeUser?.let {
                it.setBadgeText(count.toString())
                it.setGravityOffset(0F,-2F, true)
                it.setOnDragStateChangedListener { dragState, badge, targetView ->  }
            }
        }

        holder.bind<View>(R.id.rl_main).setOnClickListener {
            if (mOnItemClickListener!=null){
                mOnItemClickListener?.onItemClick(it,position)
            }
        }

        holder.bind<View>(R.id.tv_delete).setOnClickListener {
            RongIMClient.getInstance().removeConversation(Conversation.ConversationType.PRIVATE,data.targetId,object :RongIMClient.ResultCallback<Boolean>(){
                override fun onSuccess(p0: Boolean?) {
                    context.toast("删除成功！")
                    mData.remove(data)
                    notifyDataSetChanged()
                }

                override fun onError(p0: RongIMClient.ErrorCode?) {
                    context.toast("删除失败！")
                }
            })
        }
    }

    var mBadegeUser:Badge?=null
}