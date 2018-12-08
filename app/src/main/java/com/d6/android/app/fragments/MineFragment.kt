package com.d6.android.app.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import cn.liaox.cachelib.CacheDbManager
import cn.liaox.cachelib.bean.UserBean
import com.d6.android.app.R
import com.d6.android.app.activities.*
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.base.BaseFragment
import com.d6.android.app.extentions.request
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.*
import com.umeng.message.PushAgent
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.UserInfo
import io.rong.message.TextMessage
import kotlinx.android.synthetic.main.fragment_mine.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult

/**
 * 我的
 */
class MineFragment : BaseFragment() {
    override fun contentViewId() = R.layout.fragment_mine
    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    private var mData: UserData? = null

    override fun onFirstVisibleToUser() {

        mSwipeRefreshLayout.setOnRefreshListener {
            getUserInfo()
        }
        rl_my_info.setOnClickListener {
            mData?.let {
                startActivityForResult<MyInfoActivity>(0, "data" to it)
            }
        }

        tv_my_square.setOnClickListener {
            startActivity<MySquareActivity>()
        }

        tv_contact_us.setOnClickListener {
            startActivity<ContactUsActivity>()
        }

        tv_online_service.setOnClickListener {
            //            val serviceId = "f67f360c9dde4b3c9eab01a0126f6684"
            val textMsg = TextMessage.obtain("欢迎使用D6社区APP\nD6社区官网：www-d6-zone.com\n微信公众号：D6社区CM\n可关注实时了解社区动向。")
            RongIMClient.getInstance().insertIncomingMessage(Conversation.ConversationType.PRIVATE
                    ,"5" ,"5",Message.ReceivedStatus(0)
                    , textMsg,object :RongIMClient.ResultCallback<Message>(){
                override fun onSuccess(p0: Message?) {

                }
                override fun onError(p0: RongIMClient.ErrorCode?) {

                }
            })

            val serviceId = "5"
            RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE, serviceId, "D6客服")
        }

        tv_messages.setOnClickListener {
            startActivity<MessagesActivity>()
        }

        tv_feedback.setOnClickListener {
            startActivity<FeedBackActivity>()
        }

        tv_aboutUs.setOnClickListener {
            startActivity<AboutUsMainActivity>()
        }

        tv_search_weChat.setOnClickListener {
            startActivity<WeChatSearchActivity>()
        }

        btn_sign_out.setOnClickListener {
            SPUtils.instance().remove(Const.User.USER_ID)
                    .remove(Const.User.IS_LOGIN)
                    .remove(Const.User.RONG_TOKEN)
                    .remove(Const.User.USER_TOKEN)
                    .apply()
            PushAgent.getInstance(activity.applicationContext).deleteAlias(userId, "D6", { _, _ ->

            })
            RongIM.getInstance().disconnect()
            startActivity<SignInActivity>()
            activity.finish()
        }
        showDialog()
        getUserInfo()
    }

    override fun onResume() {
        super.onResume()

        getUnReadCount()
    }

    private fun getUnReadCount() {
        RongIM.getInstance().getUnreadCount(object : RongIMClient.ResultCallback<Int>() {
            override fun onSuccess(p0: Int?) {
                p0?.let {
                    if (p0 > 0) {
                        tv_msg_count1.visible()
                    } else {
                        tv_msg_count1.gone()
                        getSysLastOne()
                    }
                }
            }

            override fun onError(p0: RongIMClient.ErrorCode?) {

            }

        }, Conversation.ConversationType.PRIVATE)
    }


    private fun getSysLastOne() {
        val time = SPUtils.instance().getLong(Const.LAST_TIME)
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getSystemMessages(userId, 1, time.toString(), pageSize = 1).request(this, false, success = { _, data ->
            if (data?.list?.results == null || data.list.results.isEmpty()) {
                //无数据
                tv_msg_count1.gone()
                getSquareMsg()
            } else {
                if ((data.count ?:0)> 0) {
                    tv_msg_count1.visible()
                } else {
                    getSquareMsg()
                }

            }

        }) { _, _ ->
            getSquareMsg()
        }
    }

    private fun getSquareMsg() {
        val time = SPUtils.instance().getLong(Const.LAST_TIME)
        val userId = SPUtils.instance().getString(Const.User.USER_ID)
        Request.getSquareMessages(userId, 1, time.toString(), pageSize = 1).request(this, false, success = { _, data ->
            if (data?.list?.results == null || data.list!!.results!!.isEmpty()) {
                //无数据
                tv_msg_count1.gone()
            } else {
                if ((data.count ?:0)> 0) {
                    tv_msg_count1.visible()
                }
            }
        }) { _, _ ->

        }
    }

    private fun getUserInfo() {

        Request.getUserInfo("",userId).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            mData = data
            activity?.saveUserInfo(data)
            data?.let {
                val info = UserInfo(data.accountId, data.name, Uri.parse("" + data.picUrl))
                RongIM.getInstance().refreshUserInfoCache(info)

                headView.setImageURI(it.picUrl)
                tv_nick.text = it.name
                tv_vip.text = String.format("%s", it.classesname)

                updateCache(data)
            }
        }) { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun updateCache(data: UserData) {
        val userBean = UserBean()
        userBean.userId = data.accountId
        val nick = data.name ?: ""
        userBean.nickName = nick
        val url = data.picUrl ?: ""
        userBean.headUrl = url
        userBean.status = 1//必须设置有效性>0的值
        CacheDbManager.getInstance().updateMemoryCache(data.accountId, userBean)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                getUserInfo()
            }
        }
    }
}