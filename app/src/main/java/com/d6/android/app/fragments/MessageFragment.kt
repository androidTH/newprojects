package com.d6.android.app.fragments

import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.ConversationsAdapter
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.Page
import com.d6.android.app.models.SquareMessage
import com.d6.android.app.models.SysMessage
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.PUSH_ISNOTSHOW
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

    private var mSquareMsg:Badge? = null

    private var mBadgeSys:Badge? = null

    private var mUserId = SPUtils.instance().getString(Const.User.USER_ID)
    private var SquareMsg_time = SPUtils.instance().getLong(Const.SQUAREMSG_LAST_TIME)
    private var SysMsg_time = SPUtils.instance().getLong(Const.SYSMSG_LAST_TIME)

    private val userId by lazy {
        mUserId
    }

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_messages, swiprefreshRecyclerlayout_msg.mRecyclerView, false)
    }

    override fun contentViewId(): Int {
        return R.layout.message_fragment
    }

    override fun onFirstVisibleToUser() {
        swiprefreshRecyclerlayout_msg.setLayoutManager(LinearLayoutManager(context))
        swiprefreshRecyclerlayout_msg.mRecyclerView.addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(activity))
        swiprefreshRecyclerlayout_msg.setMode(mode())
        conversationsAdapter.setHeaderView(headerView)
        swiprefreshRecyclerlayout_msg.setAdapter(conversationsAdapter)
        swiprefreshRecyclerlayout_msg.isRefreshing = false
        swiprefreshRecyclerlayout_msg.setOnRefreshListener(this)

        headerView.rl_sys.setOnClickListener {
            mBadgeSys?.let {
                it.hide(false)
            }
            SPUtils.instance().put(Const.SYSMSG_LAST_TIME, D6Application.systemTime).apply()
            startActivity<SystemMessagesActivity>()
        }

        headerView.rl_square.setOnClickListener {
            mSquareMsg?.let {
                it.hide(false)
            }
            SPUtils.instance().put(Const.SQUAREMSG_LAST_TIME, D6Application.systemTime).apply()
            startActivity<SquareMessagesActivity>()
        }

        iv_msg_right.setOnClickListener {
            startActivity<MessageSettingActivity>()
        }

        tv_topsearch.setOnClickListener {
            startActivity<SearchFriendsActivity>()
        }

        headerView.iv_msgtip_close.setOnClickListener {
            headerView.rl_msg_tips.visibility = View.GONE
            SPUtils.instance().put(PUSH_ISNOTSHOW,System.currentTimeMillis()).apply()
        }

        headerView.tv_openmsg.setOnClickListener {
            requestNotify(context)
        }

        conversationsAdapter.setOnItemClickListener { _, position ->
            val conversation = mConversations[position]
            var s = "--"
            val info = RongUserInfoManager.getInstance().getUserInfo(conversation.targetId)
            if (info != null) {
                s = info.name
            }

            if (TextUtils.equals(Const.CustomerServiceId, conversation.targetId)||TextUtils.equals(Const.CustomerServiceWomenId, conversation.targetId)) {
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
//                val builder = CSCustomServiceInfo.Builder()
//                builder.province("D6客服")
//                builder.loginName("D6客服")
//                builder.city("北京")
//                RongIM.getInstance().startCustomerServiceChat(activity, "KEFU146001495753714", "在线客服", builder.build())
                RongIM.getInstance().startConversation(context, conversation.conversationType, conversation.targetId, "D6客服")
            } else if(conversation.conversationType ==Conversation.ConversationType.GROUP){
               // Log.i("messageFragment","${conversation.targetId}") //anoy_100486_100541 anoy_100486_21881  anoy_100491_100486
                RongIM.getInstance().startConversation(context, Conversation.ConversationType.GROUP,conversation.targetId, "")
            }else {
                activity.isAuthUser{
                    RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, conversation.targetId, s)
//                    Request.getApplyStatus(userId, conversation.targetId).request(this, false, success = { msg, jsonObjetct ->
//                        jsonObjetct?.let {
//                            var code = it.optInt("code")
//                            if (code != 7) {
//                                RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, conversation.targetId, s)
//                            }
//                        }
//                    })
                }
            }
            conversation.unreadMessageCount = 0
//            conversationsAdapter.notifyDataSetChanged()
        }
        getData()
        getSysLastOne(SysMsg_time.toString())
        getSquareMsg(SquareMsg_time.toString())

        if(TextUtils.equals(Const.CustomerServiceId,userId)||TextUtils.equals(Const.CustomerServiceWomenId,userId)){
            tv_topsearch.visibility = View.VISIBLE
        }else{
            tv_topsearch.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        checkPushIsNotShow()
    }

    private fun checkPushIsNotShow() {
        if (isNotificationEnabled(context)) {
            headerView.rl_msg_tips.visibility = View.GONE
        } else {
            if(SPUtils.instance().getLong(PUSH_ISNOTSHOW,System.currentTimeMillis())!=System.currentTimeMillis()){
                if (getSevenDays(SPUtils.instance().getLong(PUSH_ISNOTSHOW, System.currentTimeMillis()))) {
                    if (isNotificationEnabled(context)) {
                        headerView.rl_msg_tips.visibility = View.GONE
                    } else {
                        headerView.rl_msg_tips.visibility = View.VISIBLE
                    }
                } else {
                    headerView.rl_msg_tips.visibility = View.GONE
                }
            }else{
                headerView.rl_msg_tips.visibility = View.VISIBLE
            }
        }
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
        }, Conversation.ConversationType.PRIVATE,Conversation.ConversationType.GROUP)
    }

    /**
     * 系统消息
     */
    private fun getSysLastOne(lastTime:String) {
        Request.getSystemMessages(userId, 1, pageSize = 1).request(this) { _, data ->
             data?.let {
                 setSysMsg(data)
             }
        }
    }

    /**
     * 广场消息
     */
    private fun getSquareMsg(lastTime:String) {
        Request.getNewSquareMessages(userId, 1, pageSize = 1).request(this) { _, data ->
            setSquareMsg(data)
        }
    }

    override fun onRefresh() {
        SquareMsg_time = SPUtils.instance().getLong(Const.SQUAREMSG_LAST_TIME)
        SysMsg_time = SPUtils.instance().getLong(Const.SYSMSG_LAST_TIME)
        getData()
        getSysLastOne(SysMsg_time.toString())
        getSquareMsg(SquareMsg_time.toString())
        setRefresh(false)
    }

    //获得聊天消息
    fun getChatMsg(){
        getData()
    }

    fun setSysMsg(data:Page<SysMessage>){
        if (data != null) {
            if(data.list != null){
                if (data.list.results != null) {
                    var c = if ((data.count ?: 0) > 99) {
                        "99+"
                    } else {
                        data.count.toString()
                    }
                    if ((data.count ?: 0) > 0) {
                        if(mBadgeSys==null){
                            mBadgeSys = QBadgeView(activity).bindTarget(headerView.iv1)
                        }
                        mBadgeSys?.let {
                            it.badgeText = c
                            it.setOnDragStateChangedListener { dragState, badge, targetView ->  }
                        }
                    } else {
                        mBadgeSys?.let {
                            it.hide(false)
                        }
                    }
                    headerView.tv_content1.text = data.list.results[0].content
                }
            }
        }
    }

    //获得广场消息
    fun setSquareMsg(data:Page<SquareMessage>?){
        if (data != null) {
            data.list?.let {
                if(it.results!=null){
                    var c = if ((data.count ?: 0) > 99) {
                        "99+"
                    } else {
                        data.count.toString()
                    }
                    if ((data.count ?: 0) > 0) {
                        if(mSquareMsg == null){
                            mSquareMsg = QBadgeView(activity).bindTarget(headerView.iv2)
                        }
                        mSquareMsg?.let {
                            it.badgeText = c
                            it.setGravityOffset(-3F, -2F, true)
                                    .setOnDragStateChangedListener(Badge.OnDragStateChangedListener() { dragState, badge, targetView ->
                                    })
                        }
                    } else {
                        mSquareMsg?.let {
                            it.hide(false)
                        }
                    }
                    var squaremsg = it.results[0];
                    if(squaremsg.content.isNullOrEmpty()){
                        headerView.tv_content2.text = squaremsg.title
                    }else{
                        headerView.tv_content2.text = squaremsg.content
                    }
                }
            }
        }
    }

    /**
     * 刷新
     */
    private fun setRefresh(flag: Boolean) {
        swiprefreshRecyclerlayout_msg.isRefreshing = flag
        SPUtils.instance().put(Const.SQUAREMSG_LAST_TIME, D6Application.systemTime).apply()
        SPUtils.instance().put(Const.SYSMSG_LAST_TIME, D6Application.systemTime).apply()
    }

    override fun onLoadMore() {

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
