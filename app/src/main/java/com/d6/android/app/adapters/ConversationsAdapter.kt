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
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CONVERSATION_APPLAY_DATE_TYPE
import com.d6.android.app.utils.Const.CONVERSATION_APPLAY_PRIVATE_TYPE
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
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.toast


/**
 * Created on 2017/12/25.
 */
class ConversationsAdapter(mData: ArrayList<Conversation>) : HFRecyclerAdapter<Conversation>(mData, R.layout.item_list_conversations) {

    @SuppressLint("SetTextI18n")
    override fun onBind(holder: ViewHolder, position: Int, data: Conversation) {
        var swipeItemLayout = holder.bind<SwipeItemLayout>(R.id.root_swipitem);
        val headView = holder.bind<SimpleDraweeView>(R.id.headView)
        val tv_name = holder.bind<TextView>(R.id.tv_name)
        val tv_time = holder.bind<TextView>(R.id.tv_time)
        val tv_conversation_type = holder.bind<TextView>(R.id.tv_conversation_type)

        if(data.isTop){
            tv_conversation_type.visibility = View.VISIBLE
            swipeItemLayout.backgroundColor = ContextCompat.getColor(context,R.color.color_05000000)
            tv_conversation_type.text= context.getString(R.string.string_conversation_type)
//            var drawable = ContextCompat.getDrawable(context, R.mipmap.chatlist_chat)
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
//            tv_conversation_type.setCompoundDrawables(drawable, null, null, null)
        }else{
            swipeItemLayout.backgroundColor = ContextCompat.getColor(context,R.color.white)
            var applay_private_type =  SPUtils.instance().getBoolean(CONVERSATION_APPLAY_PRIVATE_TYPE+ getLocalUserId()+"-"+data.targetId,false)
            Log.i("sssssss","${applay_private_type}")
            if(applay_private_type){
                tv_conversation_type.visibility = View.VISIBLE
                tv_conversation_type.text=context.getString(R.string.string_conversation_type)
//                var drawable = ContextCompat.getDrawable(context, R.mipmap.chatlist_chat)
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
//                tv_conversation_type.setCompoundDrawables(drawable, null, null, null)
            }else{
                var applay_date_type =  SPUtils.instance().getBoolean(CONVERSATION_APPLAY_DATE_TYPE+ getLocalUserId()+"-"+data.targetId,false)
                Log.i("sssssss","${CONVERSATION_APPLAY_DATE_TYPE+ getLocalUserId()+"-"+data.targetId}申请约会${applay_date_type}")
                if(applay_date_type){
                    tv_conversation_type.visibility = View.VISIBLE
                    tv_conversation_type.text="邀约"
//                    var drawable = ContextCompat.getDrawable(context, R.mipmap.chatlist_date)
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
//                    tv_conversation_type.setCompoundDrawables(drawable, null, null, null)
                }else{
                    tv_conversation_type.visibility = View.GONE
                }
            }
        }

        if (data.conversationType == Conversation.ConversationType.PRIVATE) {
            RongUtils.setUserInfo(data.targetId, tv_name, headView)
        }else if(data.conversationType==Conversation.ConversationType.GROUP){
            var groupbean = RongUserInfoManager.getInstance().getGroupInfo(data.targetId)
            if(data.targetId.contains("_")){
                if (groupbean != null) {
                    tv_name.text = "${groupbean.name}"
                    headView.setImageURI("res:///"+R.mipmap.nimingtouxiang_small)//quanguo_icon //nimingtouxiang_small
                }else{
                    tv_name.text = "匿名"
                    headView.setImageURI("res:///"+R.mipmap.nimingtouxiang_small)
                }
            }else{
//                tv_name.text = "${groupbean.name}"
                RongUtils.setGroupInfo(data.targetId,tv_name,headView)
//                headView.setImageURI(groupbean.portraitUri)
            }
        }

        tv_time.text = DateToolUtils.getConversationFormatDate(data.sentTime,false, context)
        val tv_content = holder.bind<TextView>(R.id.tv_content)
        if (data.latestMessage != null) {
            val provider = RongContext.getInstance().getMessageTemplate(data.latestMessage.javaClass)
            if (provider != null) {
                var content = provider.getContentSummary(context,data.latestMessage)
                if(content.contains("你给${getLocalUserName()}赠送了")){
                    var startsub = "你给${getLocalUserName()}赠送了"
                    var end = content.subSequence(startsub.length,content.length)
                    tv_content.text = "对方给你赠送了${end}"
                }else{
                    tv_content.text = content;
                }
                tv_time.visibility = View.VISIBLE
            }
        } else {
            tv_content.text = ""
            tv_time.visibility = View.GONE
        }

//        var tag = RongContext.getInstance().getConversationProviderTag(data.conversationType.getName())

//        if(tag.portraitPosition==1){
//          Log.i("ddddd","发送方")
//        }else if(tag.portraitPosition==2){
//            Log.i("ddddd","接收方")
//        }else{
//            Log.i("ddddd","接收方")
//        }

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