package com.d6.android.app.rong.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.nfc.Tag;
import android.text.ClipboardManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.rong.bean.VoiceChatMsgContent;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by Beyond on 2016/12/5.
 */

@ProviderTag(messageContent = VoiceChatMsgContent.class, showReadState = true)
public class VoiceChatMessageProvider extends IContainerItemProvider.MessageProvider<VoiceChatMsgContent>{
    private static final String TAG = VoiceChatMessageProvider.class.getSimpleName();

    private static class ViewHolder {
        TextView mTvVoiceChatTitle;
        TextView mTvVoiceChatContent;
        boolean longClick;
        ImageView mIvLeft;
        ImageView mIvRight;
        LinearLayout mLL_VoiceChatMsg_Body;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rong_voicechatmsg, null);

        VoiceChatMessageProvider.ViewHolder holder = new VoiceChatMessageProvider.ViewHolder();
        holder.mTvVoiceChatTitle = view.findViewById(R.id.tv_voicechat_title);
        holder.mTvVoiceChatContent = view.findViewById(R.id.tv_voicechat_content);
        holder.mLL_VoiceChatMsg_Body = view.findViewById(R.id.ll_voicechat_body);
        holder.mIvLeft = view.findViewById(R.id.iv_left);
        holder.mIvRight = view.findViewById(R.id.iv_right);
        view.setTag(holder);
        return view;
    }

    @Override
    public Spannable getContentSummary(VoiceChatMsgContent data) {
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
    public void onItemClick(View view, int position, VoiceChatMsgContent content, UIMessage message) {
//        Intent intent = new Intent();
//        intent.setAction("com.d6.android.app.activities.MyPointsActivity");
//        view.getContext().startActivity(intent);
    }

    @Override
    public void onItemLongClick(final View view, int position, final VoiceChatMsgContent content, final UIMessage message) {

        VoiceChatMessageProvider.ViewHolder holder = (VoiceChatMessageProvider.ViewHolder) view.getTag();
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
            items = new String[] {view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_recall)};
        } else {
            items = new String[] {view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_delete)};
        }

        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    @SuppressWarnings("deprecation")
                    ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(((VoiceChatMsgContent) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    @Override
    public void bindView(final View v, int position, final VoiceChatMsgContent content, final UIMessage data) {
        VoiceChatMessageProvider.ViewHolder holder = (VoiceChatMessageProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLL_VoiceChatMsg_Body.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
            TextView textView = holder.mTvVoiceChatContent;
            textView.setText("请求连麦...");
            Log.i("VoiceChat",content.getExtra()+"------");
            if(!TextUtils.isEmpty(content.getExtra())){
//                int nums = 1;
                try {
                    JSONObject jsonObject = new JSONObject(content.getExtra());
                    String sVoiceMsg = jsonObject.getString("sVoiceMsg");
                    String sCreateUserName = jsonObject.getString("sVoiceUserName");
                    //sSignUserName
                    Log.i(TAG,"extra="+content.getExtra());
                    holder.mTvVoiceChatContent.setText(sCreateUserName+":"+sVoiceMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
//                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getTargetId());
                    holder.mTvVoiceChatContent.setText("");
                }
            }
        } else {
            holder.mLL_VoiceChatMsg_Body.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
            holder.mTvVoiceChatContent.setText(content.getContent());
            try {
                if(!TextUtils.isEmpty(content.getExtra())){
                    JSONObject jsonObject =new JSONObject(content.getExtra());
                    String sVoiceMsg = jsonObject.getString("sVoiceMsg");
                    String sCreateUserName = jsonObject.getString("sVoiceUserName");
                    //sSignUserName
                    Log.i(TAG,"extra="+content.getExtra());
                    holder.mTvVoiceChatContent.setText(sCreateUserName+":"+sVoiceMsg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
