package com.d6zone.android.app.rong;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.d6zone.android.app.utils.Const;
import com.d6zone.android.app.utils.NToast;
import com.d6zone.android.app.utils.NetworkUtils;
import com.d6zone.android.app.utils.SPUtils;

import io.rong.callkit.RongCallKit;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * author : jinjiarui
 * time   : 2019/11/15
 * desc   : 发起连麦的工具类
 * version:
 */
public class RongD6Utils {

    private static String token = SPUtils.Companion.instance().getString(Const.User.RONG_TOKEN,"");

    public static void startSingleVoiceChat(Activity activity, String targetId, RongCallKit.CallMediaType CallMediaType,String extra){
        if (RongIM.getInstance().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
            startVoice(activity,targetId,CallMediaType,extra);
        } else {
            reconnect(activity,targetId,CallMediaType,extra);
        }
    }

    public static void startVoice(Activity activity,String targetId,RongCallKit.CallMediaType CallMediaType,String extra) {
        RongCallSession profile = RongCallClient.getInstance().getCallSession();
        if (profile != null && profile.getActiveTime() > 0) {
            if (profile.getMediaType() == RongCallCommon.CallMediaType.AUDIO)
                Toast.makeText(activity,activity.getString(io.rong.callkit.R.string.rc_voip_call_audio_start_fail),Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(activity,activity.getString(io.rong.callkit.R.string.rc_voip_call_video_start_fail),Toast.LENGTH_SHORT).show();
            return;
        }

        if(!NetworkUtils.hasInternet(activity)){
            Toast.makeText(activity,activity.getString(io.rong.callkit.R.string.rc_voip_call_network_error),Toast.LENGTH_SHORT).show();
            return;
        }

//        var intent = Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO)
//        intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase(Locale.US))
//        intent.putExtra("targetId", "103162")
//        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName())
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        intent.setPackage(packageName)
//        applicationContext.startActivity(intent)
         //103162
        RongCallKit.startSingleCall(activity,targetId, CallMediaType,extra);
    }

    private static void reconnect(final Activity activity, final String targetId, final RongCallKit.CallMediaType CallMediaType, final String extra) {
        RongIM.connect(token,new RongIMClient.ConnectCallback(){
            @Override
            public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {

            }

            @Override
            public void onSuccess(String s) {
                startVoice(activity,targetId,CallMediaType,extra);
            }

            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

            }
        });
    }

    public static void setConversationTop(final Context context, Conversation.ConversationType conversationType, String targetId, boolean state) {
        if (!TextUtils.isEmpty(targetId) && RongIM.getInstance() != null) {
            RongIM.getInstance().setConversationToTop(conversationType, targetId, state, new RongIMClient.ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    NToast.shortToast(context, "设置成功");
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    NToast.shortToast(context, "设置失败");
                }
            });
        }
    }

    public static void setConverstionNotif(final Context context, Conversation.ConversationType conversationType, String targetId, boolean state) {
        Conversation.ConversationNotificationStatus cns;
        if (state) {
            cns = Conversation.ConversationNotificationStatus.DO_NOT_DISTURB;
        } else {
            cns = Conversation.ConversationNotificationStatus.NOTIFY;
        }
        RongIM.getInstance().setConversationNotificationStatus(conversationType, targetId, cns, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
//                    NToast.shortToast(context, "设置免打扰成功");
                } else if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.NOTIFY) {
//                    NToast.shortToast(context, "取消免打扰成功");
                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                NToast.shortToast(context, "设置失败");
            }
        });
    }

    //移除会话
    public static void deleConverstion(Conversation.ConversationType conversationType,String targetId,RongIMClient.ResultCallback<Boolean> callback){
        RongIMClient.getInstance().removeConversation(conversationType, targetId,callback);
    }
}
