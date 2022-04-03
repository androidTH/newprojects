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
import com.d6.android.app.rong.bean.GiftRongIMMessage;
import com.d6.android.app.rong.bean.LoveHeartMessage;
import com.facebook.drawee.view.SimpleDraweeView;

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

@ProviderTag(messageContent = GiftRongIMMessage.class, showReadState = true)
public class GiftMessageProvider extends IContainerItemProvider.MessageProvider<GiftRongIMMessage>{
    private static final String TAG = "LoveHeartMessageProvider";

    private static class ViewHolder {
        TextView mTvMsgContent;
        TextView mTvReceivedLoveHeartNums;
        TextView mTvGiftName;
        boolean longClick;
        SimpleDraweeView simpleDraweeView;
        LinearLayout mLl_LoveHeart_Body;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rong_giftmsg, null);

        GiftMessageProvider.ViewHolder holder = new GiftMessageProvider.ViewHolder();
        holder.mTvMsgContent = view.findViewById(R.id.tv_ronggift_content);
        holder.mLl_LoveHeart_Body = view.findViewById(R.id.ll_loveheart_body);
        holder.mTvReceivedLoveHeartNums = view.findViewById(R.id.tv_receivedloveheart_nums);
        holder.mTvGiftName = view.findViewById(R.id.tv_ronggiftname);
        holder.simpleDraweeView = view.findViewById(R.id.sv_gifticon);
        view.setTag(holder);
        return view;
    }

    @Override
    public Spannable getContentSummary(GiftRongIMMessage data) {
        if (data == null)
            return null;
        String content = data.getContent();
//        Log.i("loveHeartMessage",getLocalUserId()+"sendUserId=");
        if (content != null) {
            if (content.length() > 100) {
                content = content.substring(0, 100);
            }
            return new SpannableString(AndroidEmoji.ensure(content));
        }
        return null;
    }

    @Override
    public Spannable getSummary(UIMessage data) {
        return super.getSummary(data);
    }

    @Override
    public void onItemClick(View view, int position, GiftRongIMMessage content, UIMessage message) {
        Intent intent = new Intent();
        intent.setAction("com.d6.android.app.activities.MyPointsActivity");
        view.getContext().startActivity(intent);
    }

    @Override
    public void onItemLongClick(final View view, int position, final GiftRongIMMessage content, final UIMessage message) {

        GiftMessageProvider.ViewHolder holder = (GiftMessageProvider.ViewHolder) view.getTag();
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
                    clipboard.setText(((GiftRongIMMessage) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    @Override
    public void bindView(final View v, int position, final GiftRongIMMessage content, final UIMessage data) {
        GiftMessageProvider.ViewHolder holder = (GiftMessageProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLl_LoveHeart_Body.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
            TextView textView = holder.mTvGiftName;
            textView.setText("四叶草");
            if(!TextUtils.isEmpty(content.getExtra())){
                int nums = 1;
                try {
                    JSONObject jsonObject = new JSONObject(content.getExtra());
                    nums = jsonObject.getInt("nums");
//                    String receivename = jsonObject.getString("receiveusername");
//                    String desc = jsonObject.getString("sDesc");
                    holder.mTvReceivedLoveHeartNums.setText(nums+" [img src=redheart_small/]");
                    holder.simpleDraweeView.setImageURI("https://img-local.d6-zone.com/syc.png");
                } catch (JSONException e) {
                    e.printStackTrace();
                    holder.mTvReceivedLoveHeartNums.setText(String.valueOf(nums));
                }
            }
        } else {
            holder.mLl_LoveHeart_Body.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
            TextView textView = holder.mTvGiftName;
            String num = "1";
            try {
                if(!TextUtils.isEmpty(content.getExtra())){
                    JSONObject jsonObject =new JSONObject(content.getExtra());
                    num = jsonObject.optString("nums");
                    String sendusername = jsonObject.optString("sendusername");
                    String desc = jsonObject.optString("sDesc");
                    textView.setText("四叶草");
                    holder.mTvReceivedLoveHeartNums.setText(num+" [img src=redheart_small/]");
                    holder.simpleDraweeView.setImageURI("https://img-local.d6-zone.com/syc.png");
                }
            } catch (JSONException e) {
                e.printStackTrace();
//                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getTargetId());
//                textView.setText(userInfo.getName()+"给你赠送了");//+num+"颗爱心"
                holder.mTvReceivedLoveHeartNums.setText(num);
            }
        }
    }
}
