package com.d6.android.app.activities

import android.os.Bundle
import android.service.carrier.CarrierMessagingService
import android.text.TextUtils
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.adapters.ConversationsAdapter
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
import io.rong.imlib.model.Message
import io.rong.message.TextMessage
import kotlinx.android.synthetic.main.header_messages.view.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


/**
 * 消息
 */
class MessagesActivity : RecyclerActivity() {
    private val mConversations = ArrayList<Conversation>()
    private val conversationsAdapter by lazy {
        ConversationsAdapter(mConversations)
    }

    private val mSquareMsg by lazy{
        QBadgeView(this);
    }

    private val mSysMsg by lazy{
        QBadgeView(this);
    }

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.header_messages,mSwipeRefreshLayout.mRecyclerView,false)
    }

    override fun mode(): SwipeRefreshRecyclerLayout.Mode {
        return SwipeRefreshRecyclerLayout.Mode.None
    }

    override fun adapter() = conversationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar.statusBarColor(R.color.trans_parent).statusBarDarkFont(true).init()
        title = "消息"
        titleBar.addRightButton(rightId = R.mipmap.ic_msg_setting,onClickListener = View.OnClickListener {
            startActivity<MessageSettingActivity>()
        })

        headerView.rl_sys.setOnClickListener{
            startActivity<SystemMessagesActivity>()
        }
        headerView.rl_square.setOnClickListener{
            startActivity<SquareMessagesActivity>()
        }
        mSwipeRefreshLayout.mRecyclerView.addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(this))
        conversationsAdapter.setHeaderView(headerView)

        conversationsAdapter.setOnItemClickListener{_,position->
            val conversation = mConversations[position]
            var s = "--"
            val info = RongUserInfoManager.getInstance().getUserInfo(conversation.targetId)
            if (info != null) {
                s = info.name
            }

            isAuthUser {
                if (TextUtils.equals("5", conversation.targetId)) {//客服
                    val textMsg = TextMessage.obtain("欢迎使用D6社区APP\nD6社区官网：www-d6-zone.com\n微信公众号：D6社区CM\n可关注实时了解社区动向。")
                    RongIMClient.getInstance().insertIncomingMessage(Conversation.ConversationType.PRIVATE
                            ,"5" ,"5",Message.ReceivedStatus(0)
                            , textMsg,object :RongIMClient.ResultCallback<Message>(){
                        override fun onSuccess(p0: Message?) {

                        }
                        override fun onError(p0: RongIMClient.ErrorCode?) {

                        }
                    })
                }
                checkChatCount(conversation.targetId){
//                    showDatePayPointDialog(s, conversation.targetId)
                    RongIM.getInstance().startConversation(this,conversation.conversationType,conversation.targetId,s)
                }
            }
        }
    }

    private fun showDatePayPointDialog(name:String,id:String){
        Request.getUnlockTalkPoint().request(this,false,success = {msg,data->
            val dateDialog = OpenDatePayPointDialog()
            var point = data!!.optInt("data")
            dateDialog.arguments= bundleOf("data" to point.toString(),"username" to name,"chatUserId" to id)
            dateDialog.show(supportFragmentManager, "d")
        }) { _, msg ->
            this.toast(msg)
        }
    }

    override fun onResume() {
        super.onResume()
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
        Request.getSystemMessages(userId, 1,time.toString(),pageSize = 1).request(this) { _, data ->
            SPUtils.instance().put(Const.LAST_TIME,D6Application.systemTime).apply()
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                //无数据
//                headerView.tv_msg_count1.gone()
            } else {
                val c = if ((data.count ?: 0) > 99) {
                    "99+"
                } else {
                    data.count.toString()
                }
//                headerView.tv_msg_count1.text = c
                if ((data.count ?: 0) > 0) {
                    mSysMsg.bindTarget(headerView.iv1).setBadgeText(c).setGravityOffset(0F,-2F, true).setOnDragStateChangedListener(Badge.OnDragStateChangedListener(){
                        dragState, badge, targetView ->

                    })
                } else {
                    mSysMsg.hide(false)
                }
                headerView.tv_content1.text = data.list.results[0].content
            }
        }
    }

    private fun getSquareMsg() {
        val time = SPUtils.instance().getLong(Const.LAST_TIME)
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getSquareMessages(userId, 1,time.toString(),pageSize = 1).request(this) { _, data ->
            SPUtils.instance().put(Const.LAST_TIME,D6Application.systemTime).apply()
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                //无数据
//                headerView.tv_msg_count2.gone()
            } else {
                val c = if ((data.count ?: 0) > 99) {
                    "99+"
                } else {
                    data.count.toString()
                }
                if ((data.count ?: 0) > 0) {
//                    headerView.tv_msg_count2.visible()
                    mSquareMsg.bindTarget(headerView.iv2).setBadgeText(c)
                            .setGravityOffset(-3F,-2F, true)
                            .setOnDragStateChangedListener(Badge.OnDragStateChangedListener(){
                                dragState, badge, targetView ->
                            })
                }else{
                    mSquareMsg.hide(false)
                }
                headerView.tv_content2.text = data.list.results[0].content
            }
        }
    }

    override fun pullDownRefresh() {
        super.pullDownRefresh()
        setRefresh(false)
    }
}
