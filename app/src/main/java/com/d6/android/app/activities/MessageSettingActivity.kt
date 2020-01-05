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
import com.d6.android.app.utils.*
import com.d6.android.app.utils.Const.User.USER_INVITEMESSAGESETTING
import com.d6.android.app.utils.Const.User.USER_LOOKABOUTMESSAGESETTING
import com.d6.android.app.utils.Const.User.USER_MESSAGESETTING
import com.d6.android.app.utils.Const.User.USER_PHONEMESSAGESETTING
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
        var importantmessageSetting = SPUtils.instance().getString("${USER_PHONEMESSAGESETTING}${getLocalUserId()}")
        sw_important_msgimportant.isChecked = if(TextUtils.equals(importantmessageSetting,"1")){
            true
        }else{
            false
        }

        var LookaboutmessageSetting = SPUtils.instance().getString("${USER_LOOKABOUTMESSAGESETTING}${getLocalUserId()}")
        sw_friend_notfaction.isChecked = if(TextUtils.equals(LookaboutmessageSetting,"1")){
            true
        }else{
            false
        }

        sw_friend_notfaction.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                setUpdateLookaboutSetting(1)
            }else{
                setUpdateLookaboutSetting(2)
            }
        }

        sw_important_msgimportant.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                setUpdatePhoneSetting(1)
            }else{
                setUpdatePhoneSetting(2)
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
//                if(it.iTalkSetting==1){
//                    tv_private_chat_type.text=resources.getString(R.string.string_agree_openchat)
//                }else if(it.iTalkSetting==2){
//                    tv_private_chat_type.text=resources.getString(R.string.string_agree_openchat)
//                }
                sw_inviate_notfaction.isChecked = if (it.iShowInviteMessage==1) true else false
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
    //1、推送 2、不推送
    private fun setUpdatePhoneSetting(iPhoneSetting:Int){
        Request.updatePhoneSetting(iPhoneSetting).request(this,false,success={msg,data->
            SPUtils.instance().put("${USER_PHONEMESSAGESETTING}${getLocalUserId()}","${iPhoneSetting}").apply()
        }){code,msg->
            showToast(msg)
        }
    }

    //1、推送 2、不推送 优质嘉宾推荐通知
    private fun setUpdateLookaboutSetting(iLookaboutSetting:Int){
        Request.updateLookaboutSetting(iLookaboutSetting).request(this,false,success={msg,data->
            SPUtils.instance().put("${USER_LOOKABOUTMESSAGESETTING}${getLocalUserId()}","${iLookaboutSetting}").apply()
        }){code,msg->
            showToast(msg)
        }
    }
}
