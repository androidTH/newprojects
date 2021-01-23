package com.d6.android.app.rong.provider;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.dialogs.CustomerServiceDialog;
import com.d6.android.app.rong.bean.RedWalletTipsMessage;
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
@ProviderTag(messageContent = RedWalletTipsMessage.class, showReadState = true,showPortrait = false,centerInHorizontal = true)
public class RedWalletTipsMessageProvider extends IContainerItemProvider.MessageProvider<RedWalletTipsMessage>{

    private static final String TAG = "TipsMessageProvider";
    private ClickableSpan clickSpan;

    private Context mContext;
    private static class ViewHolder {
        TextView mTvMsgContent;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.item_rong_redwallettipsmsg, null);
        RedWalletTipsMessageProvider.ViewHolder holder = new RedWalletTipsMessageProvider.ViewHolder();
        holder.mTvMsgContent = view.findViewById(R.id.tv_chat_redwallet_tips);
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
    public Spannable getContentSummary(RedWalletTipsMessage data) {
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
    public void onItemClick(View view, int position, RedWalletTipsMessage content, UIMessage message) {

    }
    @Override
    public void onItemLongClick(final View view, int position, final RedWalletTipsMessage content, final UIMessage message) {

    }

    @Override
    public void bindView(final View v, int position, final RedWalletTipsMessage content, final UIMessage data) {
        RedWalletTipsMessageProvider.ViewHolder holder = (RedWalletTipsMessageProvider.ViewHolder) v.getTag();
        Log.i(TAG,data.getMessageDirection()+"内容"+content.getContent());
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            TextView textView = holder.mTvMsgContent;
            String txt = content.getContent();
            textView.setText(txt);
        } else {
            TextView textView = holder.mTvMsgContent;
            String txt = content.getContent();
            textView.setText(txt);
        }
    }
}
