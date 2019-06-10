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
import com.d6.android.app.rong.bean.GroupUnKnowTipsMessage;
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
@ProviderTag(messageContent = GroupUnKnowTipsMessage.class, showReadState = true,showPortrait = false,centerInHorizontal = true)
public class GroupUnKnowMessageProvider extends IContainerItemProvider.MessageProvider<GroupUnKnowTipsMessage>{

    private static final String TAG = "GroupUnKnowMessageProvider";

    private static class ViewHolder {
        TextView tv_chat_group_unknowtips;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rong_groupunknowtipsmsg, null);
        GroupUnKnowMessageProvider.ViewHolder holder = new GroupUnKnowMessageProvider.ViewHolder();
        holder.tv_chat_group_unknowtips = view.findViewById(R.id.tv_chat_group_unknowtips);
        view.setTag(holder);
        return view;
    }

    @Override
    public Spannable getContentSummary(GroupUnKnowTipsMessage data) {
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
    public void onItemClick(View view, int position, GroupUnKnowTipsMessage content, UIMessage message) {

    }
    @Override
    public void onItemLongClick(final View view, int position, final GroupUnKnowTipsMessage content, final UIMessage message) {

    }

    @Override
    public void bindView(final View v, int position, final GroupUnKnowTipsMessage content, final UIMessage data) {
        GroupUnKnowMessageProvider.ViewHolder holder = (GroupUnKnowMessageProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            TextView textView = holder.tv_chat_group_unknowtips;
            textView.setText(content.getContent());
            if(!TextUtils.isEmpty(content.getExtra())){
                TipsTxtMessage msg = GsonHelper.getGson().fromJson(content.getExtra(),TipsTxtMessage.class);
                if(TextUtils.equals(msg.getType(),"1")){
                    holder.tv_chat_group_unknowtips.setVisibility(View.VISIBLE);
                }
            }
        } else {
            TextView textView = holder.tv_chat_group_unknowtips;
            textView.setText(content.getContent());
            try {
                if(!TextUtils.isEmpty(content.getExtra())){
                    JSONObject jsonObject =new JSONObject(content.getExtra());
                    String type = jsonObject.getString("status");
                    if(TextUtils.equals(type,"1")){
                        holder.tv_chat_group_unknowtips.setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
