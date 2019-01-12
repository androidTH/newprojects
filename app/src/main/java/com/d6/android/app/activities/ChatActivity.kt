package com.d6.android.app.activities

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.DialogPrivateChat
import com.d6.android.app.dialogs.OpenDatePayPointDialog
import com.d6.android.app.dialogs.OpenDatePointNoEnoughDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.net.Request
import com.d6.android.app.rong.fragment.ConversationFragmentEx
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.optInt
import com.d6.android.app.utils.optString
import io.rong.imkit.RongIM
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import java.util.*


//聊天
class ChatActivity : TitleActivity() ,RongIM.OnSendMessageListener{

    private var TAG = "ChatActivity"
    private var sendCount:Int=0
    private val mTargetId by lazy {
        intent.data.getQueryParameter("targetId")
    }
    private val mTitle by lazy {
        intent.data.getQueryParameter("title")
    }

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }

    private val sex by lazy {
        SPUtils.instance().getString(Const.User.USER_SEX)
    }

    private val mConversationType: Conversation.ConversationType by lazy {
        Conversation.ConversationType.valueOf(intent.data.lastPathSegment.toUpperCase(Locale.US))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        immersionBar.init()

        titleBar.addRightButton(rightId = R.mipmap.ic_more_orange, onClickListener = View.OnClickListener {
            startActivity<UserInfoActivity>("id" to mTargetId)
//            var mDialogPrivateChat=DialogPrivateChat()
//            mDialogPrivateChat.show(supportFragmentManager,"DialogPrivateChat")
        })

        val info = RongUserInfoManager.getInstance().getUserInfo(mTargetId)
        title = if (info == null || info.name.isNullOrEmpty()) {
            mTitle
        } else {
            info.name
        }

        tv_openchat_apply.setOnClickListener {
           tv_openchat_apply.text = resources.getText(R.string.string_already_applay)
        }

        tv_openchat_points.setOnClickListener {
             relative_tips.visibility = View.GONE
             showDatePayPointDialog(mTitle)//支付积分
             fragment?.doIsNotSendMsg(false,"")
        }

        linear_openchat_agree.visibility = View.GONE
        tv_chat_nopeople.visibility = View.GONE

        tv_openchat_no.setOnClickListener {

        }

        tv_openchat_agree.setOnClickListener {

        }

        enterActivity()

        RongIM.getInstance().setSendMessageListener(this)

        if(!TextUtils.equals(mTargetId,Const.CustomerServiceId)){
            if(TextUtils.equals("1",sex)){
                relative_tips.visibility = View.VISIBLE
                tv_openchat_points.visibility = View.VISIBLE
                tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg),sendCount)
                tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
            }else{
                relative_tips.visibility = View.VISIBLE
                tv_openchat_apply.visibility = View.VISIBLE
                tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                tv_openchat_tips.text = resources.getString(R.string.string_other_agreee_openchat)
            }
        }
    }

    private fun showDatePayPointDialog(name:String){
        Request.doTalkJustify(userId, mTargetId).request(this,false,success = {msg,data->
            if(data!=null){
                var code = data!!.optInt("code")
                if(code == 1){
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    if(point.toInt() > remainPoint.toInt()){
                        val dateDialog = OpenDatePointNoEnoughDialog()
                        var point = data!!.optString("iTalkPoint")
                        var remainPoint = data!!.optString("iRemainPoint")
                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
                        dateDialog.show(supportFragmentManager, "d")
                    }else{
                        val dateDialog = OpenDatePayPointDialog()
                        dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint,"username" to name,"chatUserId" to mTargetId)
                        dateDialog.show(supportFragmentManager, "d")
                    }
                } else if(code == 0){
                    showToast(msg.toString())
//                    RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, name)
                } else {
                    val dateDialog = OpenDatePointNoEnoughDialog()
                    var point = data!!.optString("iTalkPoint")
                    var remainPoint = data!!.optString("iRemainPoint")
                    dateDialog.arguments= bundleOf("point" to point,"remainPoint" to remainPoint)
                    dateDialog.show(supportFragmentManager, "d")
                }
            }else{
               showToast(msg.toString())
            }
        }) { code, msg ->
            if(code == 0){
                showToast(msg)
            }
        }
    }

    private fun enterActivity() {

        val token = SPUtils.instance().getString(Const.User.RONG_TOKEN)
        if (token == "default") {

        } else {
            if (RongIM.getInstance().currentConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                enterFragment(mConversationType, mTargetId)
            } else {
                reconnect(token)
            }
        }
    }

    private fun reconnect(token: String) {
        RongIM.connect(token, object : RongIMClient.ConnectCallback() {
            override fun onTokenIncorrect() {}

            override fun onSuccess(s: String) {
                enterFragment(mConversationType, mTargetId)
            }

            override fun onError(e: RongIMClient.ErrorCode) {
                enterFragment(mConversationType, mTargetId)
            }
        })
    }

    private var fragment: ConversationFragmentEx? = null
    private fun enterFragment(mConversationType: Conversation.ConversationType, mTargetId: String) {
        if (isDestroy) {
            return
        }
//        fragment = ConversationFragment()
        fragment = ConversationFragmentEx()
        if(!TextUtils.equals(mTargetId,Const.CustomerServiceId)){
            if(!TextUtils.equals("1",sex)){
                fragment?.arguments = bundleOf("flag" to true,"hitmsg" to resources.getString(R.string.string_other_agreee_openchat))
            }
        }

        val uri = Uri.parse("rong://" + applicationInfo.packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId)
                .appendQueryParameter("title", mTitle)
                .build()

        fragment!!.uri = uri

        val transaction = supportFragmentManager.beginTransaction()
        //xxx 涓轰綘瑕佸姞杞界殑 id
        transaction.add(R.id.rong_content, fragment)
        transaction.commitAllowingStateLoss()
    }

    override fun onSend(p0: Message?): Message? {
        return p0
    }

    override fun onSent(p0: Message?, p1: RongIM.SentMessageErrorCode?): Boolean {
        p0?.let {
            if(!TextUtils.equals(mTargetId,Const.CustomerServiceId)){
                if(TextUtils.equals("1",sex)){
                    Log.i(TAG,"用户Id${it.senderUserId}")
                    sendCount=sendCount + 1
                    tv_openchat_tips_title.text = String.format(getString(R.string.string_openchat_sendcount_msg),sendCount)
                    tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_nopoints)
                    if(sendCount==3){
                        tv_openchat_tips_title.text = resources.getString(R.string.string_openchat)
                        tv_openchat_tips.text = resources.getString(R.string.string_openchat_pay_points)
                    }
                    fragment?.doIsNotSendMsg(sendCount==3,resources.getString(R.string.string_pay_points_openchat))
                }
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        RongIM.getInstance().setSendMessageListener(null)
    }
}
