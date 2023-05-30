package com.d6.android.app.rong;

import android.content.Context;
import android.util.Log;

import io.rong.push.PushType;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * author : jinjiarui
 * time   : 2018/12/22
 * desc   :
 * version:
 */
public class D6NotificationReceiver extends PushMessageReceiver {
    @Override
    public boolean onNotificationMessageArrived(Context context, PushType pushType, PushNotificationMessage pushNotificationMessage) {
        Log.i("D6NotificationReceiver","pushType="+pushType+"Pushextra"+pushNotificationMessage.getExtra()+"pushTitle="
                +pushNotificationMessage.getPushTitle()+"pushcontent="+pushNotificationMessage.getPushContent());

        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushType pushType, PushNotificationMessage pushNotificationMessage) {
        return false;
    }

    @Override
    public void onThirdPartyPushState(PushType pushType, String action, long resultCode) {
        super.onThirdPartyPushState(pushType, action, resultCode);
        if(pushType== PushType.HUAWEI){
//            RongPushClient.resolveHWPushError();
        }
    }
}
