package com.d6.android.app.rong.provider;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.d6.android.app.dialogs.RedMoneyDesDialog;
import com.d6.android.app.rong.bean.RedWalletMessage;

import org.json.JSONException;
import org.json.JSONObject;

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

@ProviderTag(messageContent = RedWalletMessage.class, showReadState = true)
public class RedMoenyMessageProvider extends IContainerItemProvider.MessageProvider<RedWalletMessage>{
    private static final String TAG = RedMoenyMessageProvider.class.getSimpleName();

    private Context mContext;
    private static class ViewHolder {
        TextView mTvReMoneyMsgContent;
        TextView mTvReMoneyState;
        boolean longClick;
//        SimpleDraweeView simpleDraweeView;
        LinearLayout mLl_RedMoney_Body;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.item_rong_redmoneymsg, null);

        RedMoenyMessageProvider.ViewHolder holder = new RedMoenyMessageProvider.ViewHolder();
        holder.mTvReMoneyMsgContent = view.findViewById(R.id.tv_redmoney_content);
        holder.mLl_RedMoney_Body = view.findViewById(R.id.ll_redmoney_body);
        holder.mTvReMoneyState = view.findViewById(R.id.tv_redmoney_state);
//        holder.simpleDraweeView = view.findViewById(R.id.iv_rong_custommsg_pic);
        view.setTag(holder);
        return view;
    }

    @Override
    public Spannable getContentSummary(RedWalletMessage data) {
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
    public void onItemClick(View view, int position, RedWalletMessage content, UIMessage message) {
//        Intent intent = new Intent();
//        intent.setAction("com.d6.android.app.activities.RedMoneyDesActivity");
//        view.getContext().startActivity(intent);

        if(view.getContext()!=null){
            RedMoneyDesDialog dialogRedMoenyDialog =new RedMoneyDesDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("ToFromType", 3);
            dialogRedMoenyDialog.setArguments(bundle);
            dialogRedMoenyDialog.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"RedMoneyDesc");
        }
    }

    @Override
    public void onItemLongClick(final View view, int position, final RedWalletMessage content, final UIMessage message) {

        RedMoenyMessageProvider.ViewHolder holder = (RedMoenyMessageProvider.ViewHolder) view.getTag();
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
                    clipboard.setText(((RedWalletMessage) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    @Override
    public void bindView(final View v, int position, final RedWalletMessage content, final UIMessage data) {
        RedMoenyMessageProvider.ViewHolder holder = (RedMoenyMessageProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLl_RedMoney_Body.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_redwallet_right);
            TextView textView = holder.mTvReMoneyMsgContent;
//            textView.setText(content.getContent());
            if(!TextUtils.isEmpty(content.getExtra())){
                int nums = 1;
                try {
                    JSONObject jsonObject = new JSONObject(content.getExtra());
                    nums = jsonObject.getInt("nums");
                    String receivename = jsonObject.getString("receiveusername");
//                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getTargetId());
//                  strDir=userInfo.getName()+"给你分享了一条动态";
                    textView.setText("你给"+receivename+"赠送了");
                } catch (JSONException e) {
                    e.printStackTrace();
                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getTargetId());
                    textView.setText("你给"+userInfo.getName()+"赠送了");//"+nums+"颗爱心"
                }
            }
        } else {
            holder.mLl_RedMoney_Body.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
            TextView textView = holder.mTvReMoneyMsgContent;
            try {
                if(!TextUtils.isEmpty(content.getExtra())){
                    JSONObject jsonObject =new JSONObject(content.getExtra());
                    String sendusername = jsonObject.getString("sendusername");
                    textView.setText(sendusername+"给你赠送了");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getTargetId());
                textView.setText(userInfo.getName()+"给你赠送了");//+num+"颗爱心"
            }
        }
    }
}
