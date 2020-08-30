package com.d6.android.app.rong.provider;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.d6.android.app.R;
import com.d6.android.app.dialogs.CustomerServiceDialog;
import com.d6.android.app.rong.bean.TipsMessage;
import com.d6.android.app.rong.bean.TipsTxtMessage;
import com.d6.android.app.utils.GsonHelper;
import com.d6.android.app.utils.SpanBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * Created by Beyond on 2016/12/5.
 */
@ProviderTag(messageContent = TipsMessage.class, showReadState = true,showPortrait = false,centerInHorizontal = true)
public class TipsMessageProvider extends IContainerItemProvider.MessageProvider<TipsMessage>{

    private static final String TAG = "TipsMessageProvider";
    private ClickableSpan clickSpan;

    private Context mContext;
    private static class ViewHolder {
        TextView mTvMsgContent;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.item_rong_tipsmsg, null);
        TipsMessageProvider.ViewHolder holder = new TipsMessageProvider.ViewHolder();
        holder.mTvMsgContent = view.findViewById(R.id.tv_chat_tips);
        view.setTag(holder);
        clickSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(mContext, R.color.color_96FE6E17));
                ds.setUnderlineText(false);
            }
        };
        return view;
    }

    @Override
    public Spannable getContentSummary(TipsMessage data) {
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
    public void onItemClick(View view, int position, TipsMessage content, UIMessage message) {
        if(content.getContent().endsWith("求助客服联系对方")){
            CustomerServiceDialog customerServiceDialog = new CustomerServiceDialog();
            Bundle bundle = new Bundle();
            bundle.putString("resMsg", "对方可能暂时没看到你的申请，你可以求助你的专属微信客服联系对方");
            bundle.putString("dialog_title","求助客服联系对方");
            bundle.putString("service_type","1");
            customerServiceDialog.setArguments(bundle);
            customerServiceDialog.show(((FragmentActivity)mContext).getSupportFragmentManager(),"resMsg");
        }
    }
    @Override
    public void onItemLongClick(final View view, int position, final TipsMessage content, final UIMessage message) {

    }

    @Override
    public void bindView(final View v, int position, final TipsMessage content, final UIMessage data) {
        TipsMessageProvider.ViewHolder holder = (TipsMessageProvider.ViewHolder) v.getTag();
        Log.i(TAG,data.getMessageDirection()+"内容"+content.getContent());
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            TextView textView = holder.mTvMsgContent;
            String txt = content.getContent();
            if(txt.endsWith("求助客服联系对方")){
                SpanBuilder sb = new SpanBuilder(txt);
                sb.color(mContext,txt.length() - 8,txt.length(),R.color.color_96FE6E17);
                textView.setText(sb.build());
//                SpannableStringBuilder spanStringBuilder = new SpanBuilder(txt)
//                        .click(txt.length() - 8, txt.length(), clickSpan)
//                        .build();
            }else{
                textView.setText(txt);
            }

            if(!TextUtils.isEmpty(content.getExtra())){
                try{
                    TipsTxtMessage msg = GsonHelper.getGson().fromJson(content.getExtra(),TipsTxtMessage.class);
                    if(TextUtils.equals(msg.getType(),"1")){
                        holder.mTvMsgContent.setVisibility(View.VISIBLE);
                    }else if(TextUtils.equals(msg.getType(),"2")||TextUtils.equals(msg.getType(),"3")){
                        holder.mTvMsgContent.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
//               else if(TextUtils.equals(msg.getType(),"4")){
//                    holder.mTvMsgContent.setVisibility(View.VISIBLE);
//                }
            }
        } else {
            TextView textView = holder.mTvMsgContent;
            String txt = content.getContent();
            if(txt.endsWith("求助客服联系对方")){
//                SpannableStringBuilder spanStringBuilder = new SpanBuilder(txt)
//                        .click(txt.length() - 8, txt.length(), clickSpan)
//                        .build();
//                textView.setText(spanStringBuilder);
                SpanBuilder sb = new SpanBuilder(txt);
                sb.color(mContext,txt.length() - 8,txt.length(),R.color.color_96FE6E17);
                textView.setText(sb.build());
            }else{
                textView.setText(txt);
            }
            try {
                if(!TextUtils.isEmpty(content.getExtra())){
                    JSONObject jsonObject =new JSONObject(content.getExtra());
                    if(jsonObject.has("status")){
                        String type = jsonObject.getString("status");
                        if(TextUtils.equals(type,"1")){
                            holder.mTvMsgContent.setVisibility(View.VISIBLE);
                        }else if(TextUtils.equals(type,"2")||TextUtils.equals(type,"3")){
                            holder.mTvMsgContent.setVisibility(View.VISIBLE);
                        }
                    }
//                    else if(TextUtils.equals(type,"4")){
//                        holder.mTvMsgContent.setVisibility(View.VISIBLE);
//                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
