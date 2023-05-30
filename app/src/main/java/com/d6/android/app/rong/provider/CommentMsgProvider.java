package com.d6.android.app.rong.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.ClipboardManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.models.Comment;
import com.d6.android.app.rong.bean.CommentMsgContent;
import com.d6.android.app.utils.GsonHelper;
import com.facebook.drawee.view.SimpleDraweeView;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Beyond on 2016/12/5.
 */

@ProviderTag(messageContent = CommentMsgContent.class, showReadState = true)
public class CommentMsgProvider extends IContainerItemProvider.MessageProvider<CommentMsgContent> {
    private static final String TAG = CommentMsgProvider.class.getSimpleName();

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_card, null);
        CommentMsgProvider.ViewHolder holder = new CommentMsgProvider.ViewHolder();
        holder.mLlChatDynamicCommentCard = view.findViewById(R.id.rl_chat_comment_card);
        holder.tv_chat_comment_title = view.findViewById(R.id.tv_chat_comment_title);
        holder.tv_chat_comment_content = view.findViewById(R.id.tv_chat_comment_content);
        holder.mRightImagView = view.findViewById(R.id.chat_comment_imageView);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View v, int position, final CommentMsgContent content, final UIMessage data) {
        ViewHolder holder = (CommentMsgProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLlChatDynamicCommentCard.setBackgroundResource(R.drawable.ic_bubble_right);
        } else {
            holder.mLlChatDynamicCommentCard.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        }
        if (!TextUtils.isEmpty(content.getExtra())) {
            Log.i(TAG, "评论内容" + content.getExtra());
            try {
                Comment mCommentMsg = GsonHelper.getGson().fromJson(content.getExtra(), Comment.class);

//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(content.getExtra());
//                String  commentUserName= jsonObject.getString("commentUserName");
//                String commentContent = jsonObject.getString("content");
                if (data.getMessageDirection() == Message.MessageDirection.SEND) {
//                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getTargetId());
//                userInfo.getName()
                    if (TextUtils.isEmpty(mCommentMsg.getReplyUserName())) {
                        if (!TextUtils.isEmpty(mCommentMsg.getSuqareUserName())) {
                            holder.tv_chat_comment_title.setText("你评论了" + mCommentMsg.getSuqareUserName() + "的动态");
                        } else {
                            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getTargetId());
                            holder.tv_chat_comment_title.setText("你评论了" + userInfo.getName() + "的动态");
                        }
                    } else {
                        holder.tv_chat_comment_title.setText("你回复了" + mCommentMsg.getReplyUserName() + "的评论");
                    }
                } else {
                    holder.tv_chat_comment_title.setText(mCommentMsg.getCommentUserName() + " 评论了你的动态");
                }
                holder.tv_chat_comment_content.setText(mCommentMsg.getContent());
                if (TextUtils.isEmpty(mCommentMsg.getCoverUrl())) {
                    holder.mRightImagView.setVisibility(View.GONE);
                } else {
                    holder.mRightImagView.setVisibility(View.VISIBLE);
                    String[] imgs = mCommentMsg.getCoverUrl().split(",");
                    if (imgs != null && imgs.length > 0) {
                        holder.mRightImagView.setImageURI(imgs[0]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            holder.mRightImagView.setImageURI(mCommentMsg.getPicUrl());
        }
    }

    @Override
    public Spannable getContentSummary(CommentMsgContent data) {
        if (data == null)
            return null;

        String content = data.getContent();
        if (content != null) {
            if (content.length() > 100) {
                content = content.substring(0, 100);
            }
            return new SpannableString(AndroidEmoji.ensure(content));
        }
        return null;
    }

    @Override
    public void onItemClick(View view, int position, CommentMsgContent content, UIMessage message) {
        Comment mCommentMsg = GsonHelper.getGson().fromJson(content.getExtra(), Comment.class);
        if (mCommentMsg != null) {
            Intent intent = new Intent();
            intent.setAction("com.d6.android.app.activities.SquareTrendDetailActivity");
            intent.putExtra("id", mCommentMsg.getNewsId());
            view.getContext().startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(final View view, int position, final CommentMsgContent content, final UIMessage message) {

        CommentMsgProvider.ViewHolder holder = (CommentMsgProvider.ViewHolder) view.getTag();
        holder.longClick = true;
        if (view instanceof TextView) {
            CharSequence text = ((TextView) view).getText();
            if (text != null && text instanceof Spannable)
                Selection.removeSelection((Spannable) text);
        }

        String[] items;

        long deltaTime = RongIM.getInstance().getDeltaTime();
        long normalTime = System.currentTimeMillis() - deltaTime;
        boolean enableMessageRecall = false;
        int messageRecallInterval = -1;
        boolean hasSent = (!message.getSentStatus().equals(Message.SentStatus.SENDING)) && (!message.getSentStatus().equals(Message.SentStatus.FAILED));

        try {
            enableMessageRecall = RongContext.getInstance().getResources().getBoolean(io.rong.imkit.R.bool.rc_enable_message_recall);
            messageRecallInterval = RongContext.getInstance().getResources().getInteger(io.rong.imkit.R.integer.rc_message_recall_interval);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (hasSent
                && enableMessageRecall
                && (normalTime - message.getSentTime()) <= messageRecallInterval * 1000
                && message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())
                && !message.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.SYSTEM)
                && !message.getConversationType().equals(Conversation.ConversationType.CHATROOM)) {
            items = new String[]{view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_recall)};
        } else {
            items = new String[]{view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_delete)};
        }

        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    @SuppressWarnings("deprecation")
                    ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(((CommentMsgContent) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    private static class ViewHolder {
        LinearLayout mLlChatDynamicCommentCard;
        SimpleDraweeView mRightImagView;
        TextView tv_chat_comment_title;
        TextView tv_chat_comment_content;
        boolean longClick;
    }
}
