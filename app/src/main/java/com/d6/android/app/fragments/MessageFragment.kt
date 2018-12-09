package com.d6.android.app.fragments

import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.activities.MessageSettingActivity
import com.d6.android.app.activities.SquareMessagesActivity
import com.d6.android.app.activities.SystemMessagesActivity
import com.d6.android.app.adapters.ConversationsAdapter
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.BaseFragment
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
import kotlinx.android.synthetic.main.header_messages.view.*
import kotlinx.android.synthetic.main.message_fragment.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * 消息列表页
 */
class MessageFragment : BaseFragment(), SwipeRefreshRecyclerLayout.OnRefreshListener {

    fun mode(): SwipeRefreshRecyclerLayout.Mode {
        return SwipeRefreshRecyclerLayout.Mode.Top
    }

    private val mConversations = ArrayList<Conversation>()

    private val conversationsAdapter by lazy {
        ConversationsAdapter(mConversations)
    }

    private val mSquareMsg by lazy {
        QBadgeView(activity);
    }

    private val mSysMsg by lazy {
        QBadgeView(activity);
    }

    private var mUserId = SPUtils.instance().getString(Const.User.USER_ID)
    private var mTime =  SPUtils.instance().getLong(Const.LAST_TIME)

    private val userId by lazy {
        mUserId
    }

    private val time by lazy {
        mTime
    }

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_messages, swiprefreshRecyclerlayout_msg.mRecyclerView, false)
    }

    override fun contentViewId(): Int {
        return R.layout.message_fragment
    }

    override fun onFirstVisibleToUser() {
        immersionBar.statusBarColor(R.color.colorPrimaryDark).init()
        swiprefreshRecyclerlayout_msg.setLayoutManager(LinearLayoutManager(context))
        swiprefreshRecyclerlayout_msg.mRecyclerView.addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(activity))
        swiprefreshRecyclerlayout_msg.setMode(mode())
        conversationsAdapter.setHeaderView(headerView)
        swiprefreshRecyclerlayout_msg.setAdapter(conversationsAdapter)
        swiprefreshRecyclerlayout_msg.isRefreshing = false
        swiprefreshRecyclerlayout_msg.setOnRefreshListener(this)

        headerView.rl_sys.setOnClickListener {
            startActivity<SystemMessagesActivity>()
        }

        headerView.rl_square.setOnClickListener {
            startActivity<SquareMessagesActivity>()
        }

        iv_msg_right.setOnClickListener {
            startActivity<MessageSettingActivity>()
        }

        conversationsAdapter.setOnItemClickListener { _, position ->
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
                RongIM.getInstance().startConversation(context, conversation.conversationType, conversation.targetId, "D6客服")
            } else {
//                activity.isAuthUser {
                RongIM.getInstance().startConversation(context, conversation.conversationType, conversation.targetId, s)
//                }
            }
        }
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

    /**
     * 系统消息
     */
    private fun getSysLastOne() {
        Request.getSystemMessages(userId, 1, time.toString(), pageSize = 1).request(this) { _, data ->
            if (data != null) {
                data.list?.let {
                    if (it.results != null) {
                        val c = if ((data.count ?: 0) > 99) {
                            "99+"
                        } else {
                            data.count.toString()
                        }
                        if ((data.count ?: 0) > 0) {
                            mSysMsg.bindTarget(headerView.iv1).setBadgeText(c).setGravityOffset(0F, -2F, true).setOnDragStateChangedListener(Badge.OnDragStateChangedListener() { dragState, badge, targetView ->

                            })
                        } else {
                            mSysMsg.hide(false)
                        }
                        headerView.tv_content1.text = it.results[0].content
                    }
                }
            }
        }
    }

    /**
     * 广场消息
     */
    private fun getSquareMsg() {
        Request.getSquareMessages(userId, 1, time.toString(), pageSize = 1).request(this) { _, data ->
            if (data != null) {
                data.list?.let {
                    if(it.results!=null){
                        val c = if ((data.count ?: 0) > 99) {
                            "99+"
                        } else {
                            data.count.toString()
                        }
                        if ((data.count ?: 0) > 0) {
                            mSquareMsg.bindTarget(headerView.iv2).setBadgeText(c)
                                    .setGravityOffset(-3F, -2F, true)
                                     .setOnDragStateChangedListener(Badge.OnDragStateChangedListener() { dragState, badge, targetView ->
                                    })
                        } else {
                            mSquareMsg.hide(false)
                        }
                        headerView.tv_content2.text = it.results[0].content
                    }
                }
            }
            SPUtils.instance().put(Const.LAST_TIME, D6Application.systemTime).apply()
        }
    }

    override fun onRefresh() {
        getSysLastOne()
        getSquareMsg()
        setRefresh(false)
    }

    override fun onLoadMore() {

    }

    private fun setRefresh(flag: Boolean) {
        swiprefreshRecyclerlayout_msg.isRefreshing = flag
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            immersionBar.init()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}
