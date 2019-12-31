package com.d6.android.app.activities

import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.text.TextUtils
import com.d6.android.app.R
import com.d6.android.app.base.TitleActivity
import com.d6.android.app.dialogs.SelectChatTypeDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.Const.User.USER_INVITEMESSAGESETTING
import com.d6.android.app.utils.Const.User.USER_MESSAGESETTING
import com.d6.android.app.utils.GsonHelper
import com.d6.android.app.utils.SPUtils
import com.d6.android.app.utils.saveUserInfo
import com.d6.android.app.widget.CustomToast
import kotlinx.android.synthetic.main.activity_message_setting.*
import org.jetbrains.anko.toast

class MessageSettingActivity : TitleActivity() {

    private val userId by lazy {
        SPUtils.instance().getString(Const.User.USER_ID)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_setting)
        immersionBar.init()
        setTitleBold("消息设置")
        val manager = NotificationManagerCompat.from(this)
        val isOpened = manager.areNotificationsEnabled()
        tv_state.text = if (isOpened) "已开启" else "已关闭"
        var messageSetting = SPUtils.instance().getString(USER_MESSAGESETTING)
        sw_friend_notfaction.isChecked = if(TextUtils.equals(messageSetting,"1")){
            true
        }else{
            false
        }
//        var inviateMessageSetting = SPUtils.instance().getString(USER_INVITEMESSAGESETTING,"1")

        sw_friend_notfaction.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                updateMessageSet(1)
            }else{
                updateMessageSet(2)
            }
        }

        sw_inviate_notfaction.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                updateInviteMessageSet(1)
            }else{
                updateInviteMessageSet(2)
            }
        }

        tv_private_chat_type.setOnClickListener {
            var mSelectChatTypeDialog = SelectChatTypeDialog()
            mSelectChatTypeDialog.show(supportFragmentManager,"SelectChatTypeDialog")
            mSelectChatTypeDialog.setDialogListener { p, s ->
                setPrivateChatType(p,s)
            }
        }

        getUserInfo()
    }

    private fun updateMessageSet(messageState:Int){
        Request.updateMessageSetting(userId,messageState).request(this,false,success={msg,nojson->
            SPUtils.instance().put(USER_MESSAGESETTING,"${messageState}").apply()
        })
    }

    private fun updateInviteMessageSet(messageState:Int){
        Request.updateInviteMessageSetting(messageState).request(this,false,success={msg,nojson->
            SPUtils.instance().put(USER_INVITEMESSAGESETTING,"${messageState}").apply()
        })
    }

    private fun getUserInfo() {
        Request.getUserInfo("",userId).request(this, success = { _, data ->
            SPUtils.instance().put(Const.USERINFO,GsonHelper.getGson().toJson(data)).apply()
            saveUserInfo(data)
            data?.let {
                if(it.iTalkSetting==1){
//                    tv_private_chat_type.text=resources.getString(R.string.string_linechat)
                    tv_private_chat_type.text=resources.getString(R.string.string_agree_openchat)
                }else if(it.iTalkSetting==2){
                    tv_private_chat_type.text=resources.getString(R.string.string_agree_openchat)
                }else{
                    sw_inviate_notfaction.isChecked = if (it.iShowInviteMsg==1) true else false
                }
            }
        })
    }

    private fun setPrivateChatType(status:Int,chatType:String?){
        Request.updateTalkSetting(userId,status).request(this,false,success={msg,data->
            tv_private_chat_type.text =chatType.toString()
        }){code,msg->
            showToast(msg)
        }
    }
}
