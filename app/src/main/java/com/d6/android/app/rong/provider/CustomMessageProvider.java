package com.d6.android.app.rong.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.ClipboardManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.rong.CustomMessage;
import com.d6.android.app.rong.bean.ImgTxtMessage;
import com.d6.android.app.utils.GsonHelper;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonObject;

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

@ProviderTag(messageContent = CustomMessage.class, showReadState = true)
public class CustomMessageProvider extends IContainerItemProvider.MessageProvider<CustomMessage>{
    private static final String TAG = "CustomMessageProvider";

    private static class ViewHolder {
        TextView mTvMsgContent;
        TextView mTvReceivedFlowerNums;
        boolean longClick;
        SimpleDraweeView simpleDraweeView;
        LinearLayout mLl_CustomMsg_Body;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rong_custommsg, null);

        CustomMessageProvider.ViewHolder holder = new CustomMessageProvider.ViewHolder();
        holder.mTvMsgContent = view.findViewById(R.id.tv_rongcustommsg_content);
        holder.mLl_CustomMsg_Body = view.findViewById(R.id.ll_custommsg_body);
        holder.mTvReceivedFlowerNums = view.findViewById(R.id.tv_receivedflower_nums);
//        holder.simpleDraweeView = view.findViewById(R.id.iv_rong_custommsg_pic);
        view.setTag(holder);
        return view;
    }

    @Override
    public Spannable getContentSummary(CustomMessage data) {
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
    public void onItemClick(View view, int position, CustomMessage content, UIMessage message) {

    }

    @Override
    public void onItemLongClick(final View view, int position, final CustomMessage content, final UIMessage message) {

        CustomMessageProvider.ViewHolder holder = (CustomMessageProvider.ViewHolder) view.getTag();
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
                    clipboard.setText(((CustomMessage) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    @Override
    public void bindView(final View v, int position, final CustomMessage content, final UIMessage data) {
        CustomMessageProvider.ViewHolder holder = (CustomMessageProvider.ViewHolder) v.getTag();

        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLl_CustomMsg_Body.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
            TextView textView = holder.mTvMsgContent;
            textView.setText(content.getContent());
            if(!TextUtils.isEmpty(content.getExtra())){
                ImgTxtMessage msg = GsonHelper.getGson().fromJson(content.getExtra(),ImgTxtMessage.class);
                holder.mTvReceivedFlowerNums.setText(msg.getNums());
            }
        } else {
            holder.mLl_CustomMsg_Body.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
            TextView textView = holder.mTvMsgContent;
            textView.setText(content.getContent());
            try {
                if(!TextUtils.isEmpty(content.getExtra())){
                    JSONObject jsonObject =new JSONObject(content.getExtra());
                    String num = jsonObject.getString("b");
                    holder.mTvReceivedFlowerNums.setText(num);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
