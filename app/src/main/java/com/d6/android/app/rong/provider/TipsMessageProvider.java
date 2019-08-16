package com.d6.android.app.rong.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.rong.bean.TipsMessage;
import com.d6.android.app.rong.bean.TipsTxtMessage;
import com.d6.android.app.utils.GsonHelper;

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

    private static class ViewHolder {
        TextView mTvMsgContent;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rong_tipsmsg, null);
        TipsMessageProvider.ViewHolder holder = new TipsMessageProvider.ViewHolder();
        holder.mTvMsgContent = view.findViewById(R.id.tv_chat_tips);
        view.setTag(holder);
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

    }
    @Override
    public void onItemLongClick(final View view, int position, final TipsMessage content, final UIMessage message) {

    }

    @Override
    public void bindView(final View v, int position, final TipsMessage content, final UIMessage data) {
        TipsMessageProvider.ViewHolder holder = (TipsMessageProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            TextView textView = holder.mTvMsgContent;
            textView.setText(content.getContent());
            if(!TextUtils.isEmpty(content.getExtra())){
                TipsTxtMessage msg = GsonHelper.getGson().fromJson(content.getExtra(),TipsTxtMessage.class);
                if(TextUtils.equals(msg.getType(),"1")){
                    holder.mTvMsgContent.setVisibility(View.VISIBLE);
                }else if(TextUtils.equals(msg.getType(),"2")||TextUtils.equals(msg.getType(),"3")){
                    holder.mTvMsgContent.setVisibility(View.VISIBLE);
                }
            }
        } else {
            TextView textView = holder.mTvMsgContent;
            textView.setText(content.getContent());
            try {
                if(!TextUtils.isEmpty(content.getExtra())){
                    JSONObject jsonObject =new JSONObject(content.getExtra());
                    String type = jsonObject.getString("status");
                    if(TextUtils.equals(type,"1")){
                        holder.mTvMsgContent.setVisibility(View.VISIBLE);
                    }else if(TextUtils.equals(type,"2")||TextUtils.equals(type,"3")){
                        holder.mTvMsgContent.setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
