package com.d6.android.app.fragments

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.activities.SquareMessagesActivity
import com.d6.android.app.activities.SystemMessagesActivity
import com.d6.android.app.adapters.ConversationsAdapter
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.RecyclerFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.widget.SwipeItemLayout
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import io.rong.imkit.RongIM
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.message.TextMessage
import kotlinx.android.synthetic.main.header_messages.view.*
import org.jetbrains.anko.support.v4.startActivity

/**
 *
 */
class MessagesFragment: RecyclerFragment() {
    private val mConversations = ArrayList<Conversation>()
    private val conversationsAdapter by lazy {
        ConversationsAdapter(mConversations)
    }

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_messages,mSwipeRefreshLayout.mRecyclerView,false)
    }

    override fun getMode(): SwipeRefreshRecyclerLayout.Mode {
        return SwipeRefreshRecyclerLayout.Mode.Top
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun setAdapter() = conversationsAdapter

    override fun onFirstVisibleToUser() {

        headerView.rl_sys.setOnClickListener{
            startActivity<SystemMessagesActivity>()
        }
        headerView.rl_square.setOnClickListener{
            startActivity<SquareMessagesActivity>()
        }
        mSwipeRefreshLayout.mRecyclerView.addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(context))
        conversationsAdapter.setHeaderView(headerView)

        conversationsAdapter.setOnItemClickListener{_,position->
            val conversation = mConversations[position]
            var s = "--"
            val info = RongUserInfoManager.getInstance().getUserInfo(conversation.targetId)
            if (info != null) {
                s = info.name
            }


            if (TextUtils.equals("5", conversation.targetId)) {
                //客服
//                    val textMsg = TextMessage.obtain("欢迎使用D6社区APP\nD6社区官网：www-d6-zone.com\n微信公众号：D6社区CM\n可关注实时了解社区动向。")
//                    RongIMClient.getInstance().insertIncomingMessage(Conversation.ConversationType.PRIVATE
//                            ,"5" ,"5", Message.ReceivedStatus(0)
//                            , textMsg,object : RongIMClient.ResultCallback<Message>(){
//                        override fun onSuccess(p0: Message?) {
//
//                        }
//                        override fun onError(p0: RongIMClient.ErrorCode?) {
//
//                        }
//                    })
                RongIM.getInstance().startConversation(context, conversation.conversationType, conversation.targetId, s)
            } else {
                activity.isAuthUser {
                    RongIM.getInstance().startConversation(context, conversation.conversationType, conversation.targetId, s)
                }
            }
        }
        getData()
        getSysLastOne()
        getSquareMsg()
    }

    override fun onVisibleToUser() {
        super.onVisibleToUser()
        getData()
        getSysLastOne()
        getSquareMsg()
    }

    private fun getData() {
        RongIM.getInstance().getConversationList(object : RongIMClient.ResultCallback<List<Conversation>>() {
            override fun onSuccess(conversations: List<Conversation>?) {
                mConversations.clear()
                if (conversations != null) {
                    mConversations.addAll(conversations)
                }
                conversationsAdapter.notifyDataSetChanged()
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {

            }
        }, Conversation.ConversationType.PRIVATE)
    }

    private fun getSysLastOne() {
        val time = SPUtils.instance().getLong(Const.LAST_TIME)
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getSystemMessages(userId, 1,time.toString(),pageSize = 1).request(this,false) { _, data ->
            SPUtils.instance().put(Const.LAST_TIME, D6Application.systemTime).apply()
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                //无数据
                headerView.tv_msg_count1.gone()
            } else {
                headerView.tv_msg_count1.visible()
                val c = if ((data.count ?: 0) > 99) {
                    "99+"
                } else {
                    data.count.toString()
                }
                headerView.tv_msg_count1.text = c
                headerView.tv_content1.text = data.list.results[0].content
            }
        }
    }

    private fun getSquareMsg() {
        val time = SPUtils.instance().getLong(Const.LAST_TIME)
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getSquareMessages(userId, 1,time.toString(),pageSize = 1).request(this,false) { _, data ->
            SPUtils.instance().put(Const.LAST_TIME, D6Application.systemTime).apply()
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                //无数据
                headerView.tv_msg_count2.gone()
            } else {
                headerView.tv_msg_count2.visible()

                val c = if ((data.count ?: 0) > 99) {
                    "99+"
                } else {
                    data.count.toString()
                }
                headerView.tv_msg_count2.text = c
                headerView.tv_content2.text = data.list.results[0].content
            }
        }
    }

    override fun pullDownRefresh() {
        super.pullDownRefresh()
        mSwipeRefreshLayout.isRefreshing = false
    }
}