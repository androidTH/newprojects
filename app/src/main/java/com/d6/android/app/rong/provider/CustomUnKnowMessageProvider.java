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
import io.rong.imlib.model.Message;
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
       if(message.getMessageDirection() == Message.MessageDirection.SEND){
           viewHolder.tv_leftunknowmsg.setVisibility(View.GONE);
           viewHolder.mLeftHeaderView.setVisibility(View.GONE);
           viewHolder.mRightHeaderView.setVisibility(View.VISIBLE);
           viewHolder.tv_rightunknowmsg.setVisibility(View.VISIBLE);
           viewHolder.tv_rightunknowmsg.setText(R.string.string_unknowmessage);
           viewHolder.tv_rightunknowmsg.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
           UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
           if (userInfo != null) {
               viewHolder.mRightHeaderView.setAvatar(userInfo.getPortraitUri());
           }
       }else{
           viewHolder.mLeftHeaderView.setVisibility(View.VISIBLE);
           viewHolder.mRightHeaderView.setVisibility(View.GONE);
           viewHolder.tv_leftunknowmsg.setVisibility(View.VISIBLE);
           viewHolder.tv_rightunknowmsg.setVisibility(View.GONE);
           viewHolder.tv_leftunknowmsg.setText(R.string.string_unknowmessage);
           viewHolder.tv_leftunknowmsg.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
           UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getTargetId());
           if (userInfo != null) {
               viewHolder.mLeftHeaderView.setAvatar(userInfo.getPortraitUri());
           }
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
        viewHolder.tv_leftunknowmsg =view.findViewById(R.id.tv_leftunknowmsg);
        viewHolder.mLeftHeaderView = view.findViewById(R.id.leftheader);
        viewHolder.tv_rightunknowmsg =view.findViewById(R.id.tv_rightunknowmsg);
        viewHolder.mRightHeaderView = view.findViewById(R.id.rightheader);
//        viewHolder.tv_rightunknowmsg.setMovementMethod(LinkMovementMethod.getInstance());
        view.setTag(viewHolder);
        return view;
    }

    private static class ViewHolder {
        TextView tv_leftunknowmsg;
        AsyncImageView mLeftHeaderView;
        TextView tv_rightunknowmsg;
        AsyncImageView mRightHeaderView;

        private ViewHolder() {
        }
    }
}
