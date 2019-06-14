package com.d6.android.app.activities

import android.os.Bundle
import android.service.carrier.CarrierMessagingService
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.ConversationsAdapter
import com.d6.android.app.adapters.UnKnowChatAdapter
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.dialogs.OpenDatePayPointDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.SwipeItemLayout
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.d6.android.app.widget.badge.Badge
import com.d6.android.app.widget.badge.QBadgeView
import io.rong.imkit.RongIM
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Group
import io.rong.imlib.model.Message
import io.rong.message.TextMessage
import kotlinx.android.synthetic.main.header_messages.view.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


/**
 * 密聊
 */
class UnKnowChatActivity : RecyclerActivity() {

    private val mConversations = ArrayList<Conversation>()
    private val mUnKnowChatAdapter by lazy {
        UnKnowChatAdapter(mConversations)
    }

    override fun mode(): SwipeRefreshRecyclerLayout.Mode {
        return SwipeRefreshRecyclerLayout.Mode.None
    }

    override fun adapter() = mUnKnowChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "密聊"

        mSwipeRefreshLayout.mRecyclerView.addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(this))

        mUnKnowChatAdapter.setOnItemClickListener{_,position->
            val conversation = mConversations[position]

            if(conversation.conversationType ==Conversation.ConversationType.GROUP){
                RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP,conversation.targetId, "")
            }
        }
        getData()
    }

    private fun getData() {
        RongIM.getInstance().getConversationList(object : RongIMClient.ResultCallback<List<Conversation>>() {
            override fun onSuccess(conversations: List<Conversation>?) {
                mConversations.clear()
                if (conversations != null) {
                    mConversations.addAll(conversations)
                    for(c:Conversation in conversations){
                        if(c.conversationType == Conversation.ConversationType.GROUP){
                            var split = c.targetId.split("_")
                            if(split.size==3){
                                if(TextUtils.equals(split[2], getLocalUserId())){
                                    mConversations.remove(c)
                                }
                            }
                        }
                    }
                }
                mUnKnowChatAdapter.notifyDataSetChanged()
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {

            }
        }, Conversation.ConversationType.GROUP)
    }

    override fun pullDownRefresh() {
        super.pullDownRefresh()
        setRefresh(false)
    }
}
