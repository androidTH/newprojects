package com.d6.android.app.rong;

import android.app.Activity;
import android.widget.Toast;

import com.d6.android.app.utils.NetworkUtils;
import com.d6.android.app.utils.SPUtils;

import io.rong.callkit.RongCallKit;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import static com.d6.android.app.utils.Const.User.RONG_TOKEN;

/**
 * author : jinjiarui
 * time   : 2019/11/15
 * desc   : 发起连麦的工具类
 * version:
 */
public class RongD6Utils {

    private static String token = SPUtils.Companion.instance().getString(RONG_TOKEN,"");

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
            public void onError(RongIMClient.ErrorCode errorCode) {

            }

            @Override
            public void onSuccess(String s) {
                startVoice(activity,targetId,CallMediaType,extra);
            }

            @Override
            public void onTokenIncorrect() {

            }
        });
    }
}
