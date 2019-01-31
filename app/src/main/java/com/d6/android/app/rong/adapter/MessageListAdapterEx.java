package com.d6.android.app.rong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.d6.android.app.rong.bean.CustomMessage;
import com.d6.android.app.rong.bean.TipsMessage;
import com.d6.android.app.widget.CustomToast;

import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.model.Conversation;

/**
 * author : jinjiarui
 * time   : 2019/01/12
 * desc   :
 * version:
 */
public class MessageListAdapterEx extends MessageListAdapter {

    private Context mContext;
    public MessageListAdapterEx(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        return super.newView(context, position, group);
    }

    @Override
    protected void bindView(View v, int position, UIMessage data) {
        super.bindView(v, position, data);
//      TextView mTvTips = v.findViewById(io.rong.imkit.R.id.rc_apply_chat_tips);
        if(data.getContent() instanceof TipsMessage){

        }
    }
}
