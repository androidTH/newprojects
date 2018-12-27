package com.d6.android.app.rong.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.d6.android.app.R;
import com.d6.android.app.rong.CustomMessage;
import com.d6.android.app.widget.CustomToast;

import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.widget.provider.TextMessageItemProvider;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;

/**
 * author : jinjiarui
 * time   : 2018/12/22
 * desc   :
 * version:
 */
public class FlowerPluginModule implements IPluginModule {

    Conversation.ConversationType conversationType;
    String targetId;

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.mipmap.chat_flowers_big);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.string_red_flower);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        this.conversationType = rongExtension.getConversationType();
        this.targetId = rongExtension.getTargetId();
//        RichContentMessage richContentMessage = RichContentMessage.obtain("送花", "xxx送你3朵小红花", "https://www.rongcloud.cn/images/logo.png");
        CustomMessage richContentMessage = CustomMessage.obtain("xxx送你3朵小红花","https://www.rongcloud.cn/images/logo.png");
        //Conversation.ConversationType.PRIVATE 为会话类型。
        Message myMessage = Message.obtain(targetId, conversationType, richContentMessage);
        RongIM.getInstance().sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
                //消息本地数据库存储成功的回调
            }

            @Override
            public void onSuccess(Message message) {
                //消息通过网络发送成功的回调
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                //消息发送失败的回调
            }
        });
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
