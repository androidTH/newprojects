package com.d6.android.app.adapters

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.base.adapters.HFRecyclerAdapter
import com.d6.android.app.base.adapters.util.ViewHolder
import com.d6.android.app.rong.provider.SquareMsgProvider
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.CONVERSATION_APPLAY_DATE_TYPE
import com.d6.android.app.utils.Const.CONVERSATION_APPLAY_PRIVATE_TYPE
import com.d6.android.app.utils.Const.GROUPSPLIT_LEN
import com.d6.android.app.utils.RongUtils
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.getLocalUserId
import com.d6.android.app.widget.SwipeItemLayout
import com.facebook.drawee.view.SimpleDraweeView
import io.rong.imkit.RongContext
import io.rong.imkit.RongIM
import io.rong.imkit.model.ConversationProviderTag
import io.rong.imkit.model.UIConversation
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imkit.utils.RongDateUtils
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import org.jetbrains.anko.toast


/**
 * Created on 2017/12/25.
 */
class UnKnowChatAdapter(mData: ArrayList<Conversation>) : HFRecyclerAdapter<Conversation>(mData, R.layout.item_list_conversations) {
    @SuppressLint("SetTextI18n")
    override fun onBind(holder: ViewHolder, position: Int, data: Conversation) {
        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        val tv_name = holder.bind<TextView>(R.id.tv_name)
        val tv_time = holder.bind<TextView>(R.id.tv_time)

        Log.i("UnKnowChatAdapter","UnKnowChatAdapter-${data.targetId}")
        val split = data.targetId.split("_")
        if(split.size==GROUPSPLIT_LEN){
            RongUtils.setUserInfo(split[2], tv_name, headView)
        }
//        var groupbean = RongUserInfoManager.getInstance().getGroupInfo(data.targetId)

//        if(groupbean!=null){
//            tv_name.text = groupbean.name
//            headView.setImageURI(groupbean.portraitUri)
//        }


        tv_time.text = RongDateUtils.getConversationListFormatDate(data.sentTime, context)
        val tv_content = holder.bind<TextView>(R.id.tv_content)
        val tv_conversation_type = holder.bind<TextView>(R.id.tv_conversation_type)
        var applay_private_type =  SPUtils.instance().getBoolean(CONVERSATION_APPLAY_PRIVATE_TYPE+ getLocalUserId()+"-"+data.targetId,false)
        if(applay_private_type){
            tv_conversation_type.visibility = View.VISIBLE
            tv_conversation_type.text=context.getString(R.string.string_conversation_type)
            var drawable = ContextCompat.getDrawable(context, R.mipmap.chatlist_chat)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
            tv_conversation_type.setCompoundDrawables(drawable, null, null, null)
        }else{
            var applay_date_type =  SPUtils.instance().getBoolean(CONVERSATION_APPLAY_DATE_TYPE+ getLocalUserId()+"-"+data.targetId,false)
            if(applay_date_type){
                tv_conversation_type.visibility = View.VISIBLE
                tv_conversation_type.text="申请约会"
                var drawable = ContextCompat.getDrawable(context, R.mipmap.chatlist_date)
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                tv_conversation_type.setCompoundDrawables(drawable, null, null, null)
            }else{
                tv_conversation_type.visibility = View.GONE
            }
        }

        if (data.latestMessage != null) {
            val provider = RongContext.getInstance().getMessageTemplate(data.latestMessage.javaClass)
            if (provider != null) {
                tv_content.text = provider.getContentSummary(context,data.latestMessage)
            }
        } else {
            tv_content.text = ""
        }

        var img_servicesign = holder.bind<ImageView>(R.id.iv_header_servicesign)
        if(TextUtils.equals(Const.CustomerServiceId, data.targetId)|| TextUtils.equals(Const.CustomerServiceWomenId, data.targetId)){
            img_servicesign.visibility = View.VISIBLE
        }else{
            img_servicesign.visibility = View.GONE
        }

        val tv_unread = holder.bind<TextView>(R.id.tv_unreadnum)
        var count = data.unreadMessageCount
        if (count > 99) {
            count = 99
        }
        tv_unread.visibility = if (count > 0) View.VISIBLE else View.GONE
        tv_unread.text = count.toString() + ""

        holder.bind<View>(R.id.rl_main).setOnClickListener {
            if (mOnItemClickListener!=null){
                mOnItemClickListener?.onItemClick(it,position)
            }
        }

        holder.bind<View>(R.id.tv_delete).setOnClickListener {
            RongIMClient.getInstance().removeConversation(data.conversationType,data.targetId,object :RongIMClient.ResultCallback<Boolean>(){
                override fun onSuccess(p0: Boolean?) {
                    context.toast("删除成功！")
                    mData.remove(data)
                    notifyDataSetChanged()
                    RongIM.getInstance().clearMessages(data.conversationType,
                            data.targetId, null)
                    RongIMClient.getInstance().cleanRemoteHistoryMessages(
                            data.conversationType,
                       data.targetId, System.currentTimeMillis(),
                    null)
                }

                override fun onError(p0: RongIMClient.ErrorCode?) {
                    context.toast("删除失败！")
                }
            })
        }
    }
}