package com.d6.android.app.activities

import android.os.Bundle
import android.service.carrier.CarrierMessagingService
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.UnKnowChatAdapter
import com.d6.android.app.base.RecyclerActivity
import com.d6.android.app.utils.*
import com.d6.android.app.widget.SwipeItemLayout
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import org.jetbrains.anko.*


/**
 * 密聊
 */
class UnKnowChatActivity : RecyclerActivity() {

    private val mConversations = ArrayList<Conversation>()
    private val mUnKnowChatAdapter by lazy {
        UnKnowChatAdapter(mConversations)
    }

    override fun mode(): SwipeRefreshRecyclerLayout.Mode {
        return SwipeRefreshRecyclerLayout.Mode.Top
    }

    override fun adapter() = mUnKnowChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar.statusBarColor(R.color.color_8F5A5A).statusBarDarkFont(true).init()
        titleBar.backgroundColor = ContextCompat.getColor(this,R.color.color_8F5A5A)
        titleBar.titleView.textColor = ContextCompat.getColor(this,R.color.white)
        title = "密聊"

        mSwipeRefreshLayout.mRecyclerView.addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(this))
        mUnKnowChatAdapter.setOnItemClickListener{_,position->
            val conversation = mConversations[position]
            if(conversation.conversationType ==Conversation.ConversationType.GROUP){
                RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP,conversation.targetId, "")
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
        getData()
        setRefresh(false)
    }
}
