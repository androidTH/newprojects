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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.dialogs.RedMoneyDesDialog;
import com.d6.android.app.dialogs.SelectTagDialog;
import com.d6.android.app.models.EnvelopeStatus;
import com.d6.android.app.models.MyAppointment;
import com.d6.android.app.rong.bean.RedWalletMessage;
import com.d6.android.app.utils.GsonHelper;
import com.d6.android.app.utils.OnDialogListener;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/**
 * Created by Beyond on 2016/12/5.
 */

@ProviderTag(messageContent = RedWalletMessage.class, showReadState = true)
public class RedMoenyMessageProvider extends IContainerItemProvider.MessageProvider<RedWalletMessage>{
    private static final String TAG = RedMoenyMessageProvider.class.getSimpleName();

    private static class ViewHolder {
        TextView mTvReMoneyMsgContent;
        TextView mTvReMoneyState;
        boolean longClick;
        //        SimpleDraweeView simpleDraweeView;
        LinearLayout mLl_RedMoney_Body;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
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
    public void onItemClick(final View view, final int position, final RedWalletMessage content, final UIMessage message) {
//        Intent intent = new Intent();
//        intent.setAction("com.d6.android.app.activities.RedMoneyDesActivity");
//        view.getContext().startActivity(intent);

        if(view.getContext()!=null){
            RedMoneyDesDialog dialogRedMoenyDialog =new RedMoneyDesDialog();
            Bundle bundle = new Bundle();
            if(!TextUtils.isEmpty(content.getExtra())){
                try {
                    JSONObject jsonObject = new JSONObject(content.getExtra());
                    String sEnvelopeId = jsonObject.getString("sGuid");
                    String sendUserId = jsonObject.getString("iUserId");
                    String sEnvelopeDesc = jsonObject.getString("sEnvelopeDesc");
                    bundle.putString("sEnvelopeId",sEnvelopeId);
                    bundle.putString("iUserId",sendUserId);
                    bundle.putString("sEnvelopeDesc",sEnvelopeDesc);
                    bundle.putString("messageUId",message.getMessage().getUId());
                    bundle.putInt("messageId",message.getMessageId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialogRedMoenyDialog.setDialogListener(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
//                        Log.i("redMoneyMesssage","执行了");
                        message.getMessage().setExtra(s);
                        RongContext.getInstance().getEventBus().post(message.getMessage());
                        return null;
                    }
                });
                dialogRedMoenyDialog.setArguments(bundle);
                dialogRedMoenyDialog.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"RedMoneyDesc");
            }

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
        //内容:发了一个红包,extra{"iRemainPoint":10,"iUserId":103163,"iLovePoint":10,"sResourceId":"cf0549c2-a702-4992-ae69-623662dda9f3","sEnvelopeDesc":"测试红包","iStatus":1,"sGuid":"6b292df1-d6af-4318-ab77-add98c8fe738","iLoveCount":3,"iType":2,"iRemainCount":3,"dCreatetime":1612097293422}
        //sSendUserName
        Log.i(TAG,data.getMessageDirection()+"内容"+content.getContent()+",extra"+content.getExtra());
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLl_RedMoney_Body.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_redwallet_right);
            TextView textView = holder.mTvReMoneyMsgContent;
//            textView.setText(content.getContent());
            if(!TextUtils.isEmpty(content.getExtra())){
//                int nums = 1;
                try {
                    JSONObject jsonObject = new JSONObject(content.getExtra());
                    int iStatus = jsonObject.getInt("iStatus");
                    if(jsonObject.has("sSendUserName")){
                        String sSendUserName = jsonObject.getString("sSendUserName");
                        textView.setText("来自"+sSendUserName+"的小心心红包");
                    }else{
                        String userId = jsonObject.getString("iUserId");
                        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
                        if(userInfo!=null){
                            textView.setText("来自"+userInfo.getName()+"的小心心红包");
                        }
                    }
                    TextView textState = holder.mTvReMoneyState;
                    Message message = data.getMessage();
//                    if(message.isCanIncludeExpansion()){
//                        Map<String,String> has=message.getExpansion();
//                    }
                    Log.i("redMoney","send,extra:"+message.getExtra());

                    if(iStatus==1){
                        if(TextUtils.isEmpty(message.getExtra())){
                            textState.setText("领取红包");
                        }else{
                            String resCode =message.getExtra();
                            if(TextUtils.equals(resCode,"100")||TextUtils.equals(resCode,"200")){
                                textState.setText("已领取");
                            }else if(TextUtils.equals(resCode,"300")){
                                textState.setText("已被领完");
                            }else{
                                textState.setText("已过期");
                            }
                        }
                    }
//                    textView.setText("你"+content.getContent());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            holder.mLl_RedMoney_Body.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_redwallet_left);
            TextView textView = holder.mTvReMoneyMsgContent;
            try {
                if(!TextUtils.isEmpty(content.getExtra())){
                    JSONObject jsonObject =new JSONObject(content.getExtra());
                    int iStatus = jsonObject.getInt("iStatus");
                    if(jsonObject.has("sSendUserName")){
                        String sSendUserName = jsonObject.getString("sSendUserName");
                        textView.setText("来自"+sSendUserName+"的小心心红包");
                    }else{
                        String userId = jsonObject.getString("iUserId");
                        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
                        if(userInfo!=null){
                            textView.setText("来自"+userInfo.getName()+"的小心心红包");
                        }
                    }

                    Message message = data.getMessage();
                    Log.i("redMoney","receive,extra:"+message.getExtra());
                    TextView textState = holder.mTvReMoneyState;
                    if(iStatus==1){
                        if(TextUtils.isEmpty(message.getExtra())){
                            textState.setText("领取红包");
                        }else{
                            String resCode = message.getExtra();
                            if(TextUtils.equals(resCode,"100")||TextUtils.equals(resCode,"200")){
                                textState.setText("已领取");
                            }else if(TextUtils.equals(resCode,"300")){
                                textState.setText("已被领完");
                            }else{
                                textState.setText("已过期");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateExtra(int messageId,String code){
        RongIMClient.getInstance().setMessageExtra(messageId,code,new RongIMClient.ResultCallback<Boolean>(){
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }
}
