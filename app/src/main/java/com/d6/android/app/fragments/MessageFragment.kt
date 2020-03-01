package com.d6.android.app.fragments

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.adapters.ConversationsAdapter
import com.d6.android.app.adapters.GroupListAdapter
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.dialogs.JoinGroupDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.*
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.CustomerServiceId
import com.d6.android.app.utils.Const.CustomerServiceWomenId
import com.d6.android.app.utils.Const.GROUPSPLIT_LEN
import com.d6.android.app.utils.Const.PUSH_ISNOTSHOW
import com.d6.android.app.widget.ScreenUtil
import com.d6.android.app.widget.SwipeItemLayout
import com.d6.android.app.widget.SwipeRefreshRecyclerLayout
import com.d6.android.app.widget.badge.DisplayUtil
import com.d6.android.app.widget.popup.EasyPopup
import com.d6.android.app.widget.popup.XGravity
import com.d6.android.app.widget.popup.YGravity
import io.rong.imkit.RongContext
import io.rong.imkit.RongIM
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.header_messages.view.*
import kotlinx.android.synthetic.main.item_group.view.*
import kotlinx.android.synthetic.main.message_fragment.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception

/**
 * 消息列表页
 */
class MessageFragment : BaseFragment(), SwipeRefreshRecyclerLayout.OnRefreshListener {



    private val iIsCreateGroup by lazy{
        SPUtils.instance().getInt(Const.User.USERISCREATE_GROUP,1)
    }
    fun mode(): SwipeRefreshRecyclerLayout.Mode {
        return SwipeRefreshRecyclerLayout.Mode.Top
    }

    private val mConversations = ArrayList<Conversation>()
    private val mUnConversations = ArrayList<Conversation>()
//    private val mISTopConversations = ArrayList<Conversation>()
    private var mNMUnReadTotal:Int = 0 //我匿名未读消息数
    private var showNums = 1
    private var pageNum=1

    private val conversationsAdapter by lazy {
        ConversationsAdapter(mConversations)
    }

    private val mGroupList = ArrayList<NewGroupBean>()
    private val mGroupListAdapter by lazy {
        GroupListAdapter(mGroupList)
    }

//    private val topConversationsAdapter by lazy{
//        TopConversationsAdapter(mISTopConversations)
//    }

    private var SquareMsg_time = SPUtils.instance().getLong(Const.SQUAREMSG_LAST_TIME)
    private var SysMsg_time = SPUtils.instance().getLong(Const.SYSMSG_LAST_TIME)

    private lateinit var headerView: View
    fun IsNotNullHeaderiew()=::headerView.isInitialized

    override fun contentViewId(): Int {
        return R.layout.message_fragment
    }

    override fun onFirstVisibleToUser() {
        swiprefreshRecyclerlayout_msg.setLayoutManager(LinearLayoutManager(context))
        headerView = layoutInflater.inflate(R.layout.header_messages, swiprefreshRecyclerlayout_msg.mRecyclerView, false)

        swiprefreshRecyclerlayout_msg.mRecyclerView.addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(activity))
        swiprefreshRecyclerlayout_msg.setMode(mode())
        headerView?.let {
            conversationsAdapter.setHeaderView(it)
        }
        swiprefreshRecyclerlayout_msg.setAdapter(conversationsAdapter)
        swiprefreshRecyclerlayout_msg.isRefreshing = false
        swiprefreshRecyclerlayout_msg.setOnRefreshListener(this)

        headerView.rl_sys.setOnClickListener {
            headerView.iv1_sys_num.visibility = View.GONE
            SPUtils.instance().put(Const.SYSMSG_LAST_TIME, D6Application.systemTime).apply()
            startActivity<SystemMessagesActivity>()
        }

        headerView.rl_square.setOnClickListener {
            headerView.iv2_square_num.visibility = View.GONE
            SPUtils.instance().put(Const.SQUAREMSG_LAST_TIME, D6Application.systemTime).apply()
            startActivity<SquareMessagesActivity>()
        }

        headerView.rl_unknowchat.setOnClickListener {
            startActivity<UnKnowChatActivity>()
        }

        headerView.iv_msgtip_close.setOnClickListener {
            headerView.rl_msg_tips.visibility = View.GONE
            SPUtils.instance().put(PUSH_ISNOTSHOW,System.currentTimeMillis()).apply()
        }

        headerView.tv_openmsg.setOnClickListener {
            requestNotify(context)
        }

        iv_msg_right.setOnClickListener {
//            startActivity<MessageSettingActivity>()
            var mView = it
            mCirclePop?.let {
                it.showAtAnchorView(mView, YGravity.BELOW, XGravity.ALIGN_RIGHT, -23, -15)
            }
        }

        tv_topsearch.setOnClickListener {
            startActivity<MessageSettingActivity>()
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

//        topConversationsAdapter.setOnItemClickListener { view, position ->
//            val conversation = mISTopConversations[position]
//            var s = "--"
//            val info = RongUserInfoManager.getInstance().getUserInfo(conversation.targetId)
//            if (info != null) {
//                s = info.name
//            }
//            if (TextUtils.equals(Const.CustomerServiceId, conversation.targetId)||TextUtils.equals(Const.CustomerServiceWomenId, conversation.targetId)) {
//                RongIM.getInstance().startConversation(context, conversation.conversationType, conversation.targetId, "D6客服")
//            } else if(conversation.conversationType ==Conversation.ConversationType.GROUP){
//                RongIM.getInstance().startConversation(context, Conversation.ConversationType.GROUP,conversation.targetId, "")
//            }else {
//                activity.isAuthUser{
//                    RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, conversation.targetId, s)
//                }
//            }
//            conversation.unreadMessageCount = 0
//        }
        getData()
        getSysLastOne(SysMsg_time.toString())
        getSquareMsg(SquareMsg_time.toString())

//        if(TextUtils.equals(CustomerServiceId, getLocalUserId())||TextUtils.equals(CustomerServiceWomenId,getLocalUserId())){
//            tv_topsearch.visibility = View.VISIBLE
//        }else{
//            tv_topsearch.visibility = View.GONE
//        }

        checkPushIsNotShow()

        initPopup()
    }

    override fun onResume() {
        super.onResume()
        Log.i("Message","ssssss");
    }

    private var mCirclePop: EasyPopup?=null
    private fun initPopup(){
        if (activity!= null) {
            var mContentView = LayoutInflater.from(activity).inflate(R.layout.popup_message_layout, null)
            if (TextUtils.equals(CustomerServiceId, getLocalUserId()) || TextUtils.equals(CustomerServiceWomenId, getLocalUserId())) {
                mContentView.findViewById<View>(R.id.line_searchusers)?.visibility = View.VISIBLE
                mContentView.findViewById<View>(R.id.tv_search_users)?.visibility = View.VISIBLE
                showNums = showNums + 1
            } else {
                mContentView.findViewById<View>(R.id.line_searchusers)?.visibility = View.GONE
                mContentView.findViewById<View>(R.id.tv_search_users)?.visibility = View.GONE
            }

            if (iIsCreateGroup == 1) {
                mContentView.findViewById<View>(R.id.tv_creategroup)?.visibility = View.GONE
                mContentView.findViewById<View>(R.id.line_creategroup)?.visibility = View.GONE
            } else {
                showNums = showNums + 1
                mContentView.findViewById<View>(R.id.tv_creategroup)?.visibility = View.VISIBLE
                mContentView.findViewById<View>(R.id.line_creategroup)?.visibility = View.VISIBLE
            }

            if(showNums == 1) {
                mContentView.findViewById<View>(R.id.rl_popup)?.backgroundDrawable = ContextCompat.getDrawable(activity,R.mipmap.topwindows01_bg)
            }
            mCirclePop = EasyPopup.create().setAnimationStyle(R.style.RightTop2PopAnim)
                    .setContentView(mContentView).setOnViewListener { view, popup ->
                        view.findViewById<TextView>(R.id.tv_creategroup).setOnClickListener {
                            startActivity<CreateGroupActivity>()
                            mCirclePop!!.dismiss()
                        }

                        view.findViewById<TextView>(R.id.tv_joingroup).setOnClickListener {
                            var mJoinGroupDialog = JoinGroupDialog()
                            mJoinGroupDialog.show(activity.supportFragmentManager,"joingroup")
                            mCirclePop!!.dismiss()
                        }
                        view.findViewById<TextView>(R.id.tv_search_users).setOnClickListener {
                            startActivity<SearchFriendsActivity>()
                            mCirclePop!!.dismiss()
                        }

                    }
                    //是否允许点击PopupWindow之外的地方消失
                    .setFocusAndOutsideEnable(true)
                    .apply()
        }

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
                mUnConversations.clear()
//                mISTopConversations.clear()
                if (conversations != null) {
                    mConversations.addAll(conversations)
                    for(c:Conversation in conversations){
                        if(c.conversationType == Conversation.ConversationType.GROUP){
                            var split = c.targetId.split("_")
                            if(split.size==GROUPSPLIT_LEN){
                                if(TextUtils.equals(split[1], getLocalUserId())){
                                    mConversations.remove(c)
                                    mUnConversations.add(c)
                                }
//                                else{
//                                    if(c.isTop){
//                                        mConversations.remove(c)
//                                        mISTopConversations.add(c)
//                                    }
//                                }
                            }
//                        }else{
//                            if(c.isTop){
//                                mConversations.remove(c)
//                                mISTopConversations.add(c)
//                            }
                        }
                    }
                    getNMChat()
//                    mConversations.addAll(0,mISTopConversations)
//                    setIsTopConversation()
                }
                conversationsAdapter.notifyDataSetChanged()
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {

            }
        }, Conversation.ConversationType.PRIVATE,Conversation.ConversationType.GROUP)
        updateGroupList()
    }

    /**
     * 密聊是否显示和未读消息数量
     */
    private fun getNMChat() {
        mNMUnReadTotal = 0
        Log.i("messagefragment", "ssssss${mUnConversations.size}")
        if (swiprefreshRecyclerlayout_msg.IsNotRecycler()&&swiprefreshRecyclerlayout_msg.mRecyclerView.hasPendingAdapterUpdates()) {
            swiprefreshRecyclerlayout_msg.setLayoutManager(LinearLayoutManager(context))
            headerView = layoutInflater.inflate(R.layout.header_messages, swiprefreshRecyclerlayout_msg.mRecyclerView, false)
        }
        try {
            if (mUnConversations != null && mUnConversations.size > 0) {
                if(IsNotNullHeaderiew()){
                    headerView.rl_unknowchat.visibility = View.VISIBLE
                    headerView.line_mchat.visibility = View.VISIBLE

                    for (c: Conversation in mUnConversations) {
                        if (c.unreadMessageCount > 0) {
                            mNMUnReadTotal = mNMUnReadTotal + c.unreadMessageCount
                        }
                    }

                    if (mNMUnReadTotal > 0) {
                        headerView.iv3_unreadnum.visibility = View.VISIBLE
                        headerView.iv3_unreadnum.text = "${mNMUnReadTotal}"
                    } else {
                        headerView.iv3_unreadnum.visibility = View.GONE
                    }
                    var mConv = mUnConversations.get(0)
                    val provider = RongContext.getInstance().getMessageTemplate(mConv.latestMessage.javaClass)
                    if (provider != null) {
                        headerView.tv_content3.text = provider.getContentSummary(context, mConv.latestMessage)
                    }
                }
            } else {
                if(IsNotNullHeaderiew()){
                    headerView.rl_unknowchat.visibility = View.GONE
                    headerView.line_mchat.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateGroupList(){
        if(IsNotNullHeaderiew()){
            headerView.rv_grouplist.setHasFixedSize(true)
            headerView.rv_grouplist.isNestedScrollingEnabled = true
            headerView.rv_grouplist.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
            headerView.rv_grouplist.adapter = mGroupListAdapter
            mGroupListAdapter.setOnItemClickListener { _, position ->
                val groupBean = mGroupList[position]
//                startActivity<GroupSettingActivity>("bean" to groupBean)
                if(groupBean.iType==1){
                    RongIM.getInstance().startConversation(context, Conversation.ConversationType.GROUP,"${groupBean.sId}","${groupBean.sGroupName}")
                }else if(groupBean.iType==2){
                    startActivity<UserInfoActivity>("id" to "${groupBean.sId}")
                }
            }
            if(Const.UPDATE_GROUPS_STATUS==-1){
                getGroupData()
                Const.UPDATE_GROUPS_STATUS = 0
            }
            headerView.tv_more_users.setOnClickListener {
                startActivity<GoodFriendsListActivity>()
            }
        }
    }

    private fun getGroupData() {
        Request.getMyGrouListAndFriendList(1).request(this,success = { _, data ->
            data?.let {
                mGroupList.clear()
                Log.i("onReceived","${Const.UPDATE_GROUPS_STATUS}-----${it.list?.results?.size}")
                if (it.list?.results == null || it.list.results.isEmpty()) {
                    if (pageNum > 1) {

                    } else {
                        headerView.ll_groups.visibility = View.GONE
                    }
                }else{
                    headerView.ll_groups.visibility = View.VISIBLE
                    mGroupList.addAll(it.list?.results)
                    mGroupListAdapter.notifyDataSetChanged()
                }
            }
        }) { code, msg ->
            toast(msg)
        }
    }

    private fun setIsTopConversation(){
//        headerView.rv_top_conversation.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
//        if(mISTopConversations.size>0){
//            headerView.rv_top_conversation.visibility = View.VISIBLE
//            headerView.rv_top_conversation.adapter = topConversationsAdapter
//        }else{
//            headerView.rv_top_conversation.visibility = View.GONE
//        }
    }

    /**
     * 系统消息
     */
    private fun getSysLastOne(lastTime:String) {
        Request.getSystemMessages(getLocalUserId(), 1, pageSize = 1).request(this) { _, data ->
             data?.let {
                 setSysMsg(data)
             }
        }
    }

    /**
     * 广场消息
     */
    private fun getSquareMsg(lastTime:String) {
        Request.getNewSquareMessages(getLocalUserId(), 1, pageSize = 1).request(this) { _, data ->
            setSquareMsg(data)
        }
    }

    override fun onRefresh() {
        SquareMsg_time = SPUtils.instance().getLong(Const.SQUAREMSG_LAST_TIME)
        SysMsg_time = SPUtils.instance().getLong(Const.SYSMSG_LAST_TIME)
        Const.UPDATE_GROUPS_STATUS = -1
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
                        if(IsNotNullHeaderiew()){
                            headerView.iv1_sys_num.visibility = View.VISIBLE
                            headerView.iv1_sys_num.text = c
                        }
                    } else {
                        if(IsNotNullHeaderiew()){
                            headerView.iv1_sys_num.visibility = View.GONE
                        }
                    }
                    var sysmsg = data.list.results[0]
                    if(IsNotNullHeaderiew()){
                        headerView.tv_content1.text = sysmsg.content
                        headerView.tv_systemmsg_time.text = DateToolUtils.getConversationFormatDate(sysmsg.createTime!!.toLong(),false, context)
                    }
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
                    if(IsNotNullHeaderiew()){
                        if ((data.count ?: 0) > 0) {
                            headerView.iv2_square_num.visibility = View.VISIBLE
                            headerView.iv2_square_num.text = c
                        } else {
                            headerView.iv2_square_num.visibility = View.GONE
                        }
                    }
                    var squaremsg = it.results[0];
                    if(squaremsg.content.isNullOrEmpty()){
                        if(IsNotNullHeaderiew()){
                            headerView.tv_content2.text = squaremsg.title
                        }
                    }else{
                        if(IsNotNullHeaderiew()){
                            headerView.tv_content2.text = squaremsg.content
                        }
                    }
                    if(IsNotNullHeaderiew()){
                        headerView.tv_squaremsg_time.text = DateToolUtils.getConversationFormatDate(squaremsg.createTime!!.toLong(),false, context)
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
