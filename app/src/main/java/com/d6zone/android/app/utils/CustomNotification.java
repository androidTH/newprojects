package com.d6zone.android.app.utils;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

/**
 * author : jinjiarui
 * time   : 2019/01/16
 * desc   :
 * version:
 */
public class CustomNotification extends UmengMessageHandler {
    private static String TAG = CustomNotification.class.getSimpleName();

    @Override
    public void dealWithCustomMessage(Context context, UMessage uMessage) {
        super.dealWithCustomMessage(context, uMessage);
        Log.i(TAG,"dealWithCustomMessage");
    }

    @Override
    public Notification getNotification(Context context, UMessage uMessage) {
        Log.i(TAG,"success-"+uMessage.extra.get("type"));
        context.sendBroadcast(new Intent(Const.YOUMENG_MSG_NOTIFION));
        return super.getNotification(context, uMessage);
    }

}
