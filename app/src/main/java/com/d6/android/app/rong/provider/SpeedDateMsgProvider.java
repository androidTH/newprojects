package com.d6.android.app.rong.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.models.MyDate;
import com.d6.android.app.rong.bean.SpeedDateMsgContent;
import com.d6.android.app.utils.GsonHelper;
import com.facebook.drawee.view.SimpleDraweeView;

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

@ProviderTag(messageContent = SpeedDateMsgContent.class, showReadState = true)
public class SpeedDateMsgProvider extends IContainerItemProvider.MessageProvider<SpeedDateMsgContent> {
    private static final String TAG = SpeedDateMsgProvider.class.getName();

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chatspeeddate_card, null);
        SpeedDateMsgProvider.ViewHolder holder = new SpeedDateMsgProvider.ViewHolder();
        holder.mLLChatSpeedDateCard = view.findViewById(R.id.ll_chat_speed_card);
        holder.chat_speeddate_imageView = view.findViewById(R.id.chat_speeddate_imageView);
        holder.tv_chat_speeddate_type = view.findViewById(R.id.tv_chat_speeddate_type);
        holder.tv_chat_speeddate_authlevel = view.findViewById(R.id.tv_chat_speeddate_authlevel);
        holder.tv_chat_speeddate_authstate = view.findViewById(R.id.tv_chat_speeddate_authstate);
        holder.tv_chat_speeddate_name = view.findViewById(R.id.tv_chat_speeddate_name);
        holder.tv_chat_speeddate_info = view.findViewById(R.id.tv_chat_speeddate_info);
        holder.tv_chat_speeddate_content = view.findViewById(R.id.tv_chat_speeddate_content);
        holder.tv_chat_speeddate_address = view.findViewById(R.id.tv_chat_speeddate_address);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View v, int position, final SpeedDateMsgContent content, final UIMessage data) {
        SpeedDateMsgProvider.ViewHolder holder = (SpeedDateMsgProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLLChatSpeedDateCard.setBackgroundResource(R.drawable.ic_bubble_date_right);
        } else {
            holder.mLLChatSpeedDateCard.setBackgroundResource(R.drawable.ic_bubble_date_left);
        }
        if (!TextUtils.isEmpty(content.getExtra())) {
            Log.i(TAG, "约会内容speed=" + content.getExtra());
            try {
                MyDate date = GsonHelper.getGson().fromJson(content.getExtra(), MyDate.class);
                holder.chat_speeddate_imageView.setImageURI(date.getSpeedpics());
                holder.tv_chat_speeddate_name.setText(String.valueOf(date.getLooknumber()));
                holder.tv_chat_speeddate_name.setSelected(TextUtils.equals("0", date.getSex()));

                if (TextUtils.equals("0", date.getSex())) {
                    holder.tv_chat_speeddate_info.setText(String.format("%s岁·%s·%s", date.getAge(), date.getHeight(), date.getWeight()));
                } else {
                    StringBuilder sb = new StringBuilder();
                    if (!TextUtils.isEmpty(date.getJob())) {
                        sb.append("职业:" + date.getJob());
                    }
                    if (!TextUtils.isEmpty(date.getZuojia())) {
                        sb.append("座驾:" + date.getZuojia());
                    }
                    if (TextUtils.isEmpty(sb.toString())) {
                        holder.tv_chat_speeddate_info.setVisibility(View.GONE);
                    } else {
                        holder.tv_chat_speeddate_info.setVisibility(View.VISIBLE);
                    }
                    holder.tv_chat_speeddate_info.setText(sb.toString());
                }

                holder.tv_chat_speeddate_type.setText(date.getSpeedStateStr());
                if (TextUtils.equals("0", date.getSex())) {
                    holder.tv_chat_speeddate_authstate.setVisibility(View.VISIBLE);
                    holder.tv_chat_speeddate_authlevel.setVisibility(View.GONE);
                    if (TextUtils.equals("1", date.getScreen())) {
                        holder.tv_chat_speeddate_authstate.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.video_big));
                    } else if (TextUtils.equals("0", date.getScreen())) {
                        holder.tv_chat_speeddate_authstate.setVisibility(View.GONE);
                    } else if (TextUtils.equals("3", date.getScreen())) {
                        holder.tv_chat_speeddate_authstate.setVisibility(View.GONE);
                        holder.tv_chat_speeddate_authstate.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.renzheng_big));
                    }
                } else {
                    holder.tv_chat_speeddate_authstate.setVisibility(View.GONE);
                    holder.tv_chat_speeddate_authlevel.setVisibility(View.VISIBLE);
                    if (date.getClassesname().startsWith("普通")) {
                        holder.tv_chat_speeddate_authlevel.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_ordinary));
                    } else if (date.getClassesname().startsWith("白银")) {
                        holder.tv_chat_speeddate_authlevel.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_silver));
                    } else if (date.getClassesname().startsWith("黄金")) {
                        holder.tv_chat_speeddate_authlevel.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_gold));
                    } else if (date.getClassesname().startsWith("钻石")) {
                        holder.tv_chat_speeddate_authlevel.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_zs));
                    } else if (date.getClassesname().startsWith("私人")) {
                        holder.tv_chat_speeddate_authlevel.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_private));
                    } else if (date.getClassesname().startsWith("游客")) {
                        holder.tv_chat_speeddate_authlevel.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.youke_icon));
                    }else if (date.getClassesname().startsWith("入群")) {
                        holder.tv_chat_speeddate_authlevel.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.ruqun_icon));
                    }
                }

                holder.tv_chat_speeddate_content.setText(date.getLookfriendstand());
                holder.tv_chat_speeddate_address.setText(date.getSpeedcity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Spannable getContentSummary(SpeedDateMsgContent data) {
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
    public void onItemClick(View view, int position, SpeedDateMsgContent content, UIMessage message) {
        MyDate date = GsonHelper.getGson().fromJson(content.getExtra(), MyDate.class);
        if (date != null) {
            Intent intent = new Intent();
            intent.setAction("com.d6.android.app.activities.SpeedDateDetailActivity");
            intent.putExtra("data", date);
            view.getContext().startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(final View view, int position, final SpeedDateMsgContent content, final UIMessage message) {

        SpeedDateMsgProvider.ViewHolder holder = (SpeedDateMsgProvider.ViewHolder) view.getTag();
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
                    clipboard.setText(((SpeedDateMsgContent) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    private static class ViewHolder {
        LinearLayout mLLChatSpeedDateCard;
        SimpleDraweeView chat_speeddate_imageView;
        TextView tv_chat_speeddate_type;
        TextView tv_chat_speeddate_authlevel;
        TextView tv_chat_speeddate_authstate;
        TextView tv_chat_speeddate_name;
        TextView tv_chat_speeddate_info;
        TextView tv_chat_speeddate_content;
        TextView tv_chat_speeddate_address;
        boolean longClick;
    }
}
