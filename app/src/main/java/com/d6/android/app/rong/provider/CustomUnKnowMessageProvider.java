package com.d6.android.app.rong.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.d6.android.app.R;

import io.rong.imkit.RongContext;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.provider.UnknownMessageItemProvider;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UnknownMessage;
import io.rong.imlib.model.UserInfo;

/**
 * author : jinjiarui
 * time   : 2019/04/28
 * desc   :
 * version:
 */
@ProviderTag(
        messageContent = UnknownMessage.class,
        showPortrait = false,
        showWarning = false,
        showSummaryWithName = false
)
public class CustomUnKnowMessageProvider extends UnknownMessageItemProvider {

    public void bindView(View v, int position, MessageContent content, UIMessage message) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        viewHolder.contentTextView.setText(R.string.string_unknowmessage);
        viewHolder.contentTextView.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getTargetId());
        if (userInfo != null) {
            viewHolder.mHeaderView.setAvatar(userInfo.getPortraitUri());
        }
    }

    public Spannable getContentSummary(MessageContent data) {
        return new SpannableString(RongContext.getInstance().getResources().getString(R.string.string_unknowmessage));
    }

    public void onItemClick(View view, int position, MessageContent content, UIMessage message) {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rongunknowmsg, (ViewGroup) null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.contentTextView =view.findViewById(R.id.tv_unknowmsg);
        viewHolder.mHeaderView = view.findViewById(R.id.leftheader);
        viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        view.setTag(viewHolder);
        return view;
    }

    private static class ViewHolder {
        TextView contentTextView;
        AsyncImageView mHeaderView;

        private ViewHolder() {
        }
    }
}
