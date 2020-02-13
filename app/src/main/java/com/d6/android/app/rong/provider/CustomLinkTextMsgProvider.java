package com.d6.android.app.rong.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.rong.bean.CustomLinkTextMsg;
import com.facebook.drawee.view.SimpleDraweeView;

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
@ProviderTag(messageContent = CustomLinkTextMsg.class, showReadState = true)
public class CustomLinkTextMsgProvider extends IContainerItemProvider.MessageProvider<CustomLinkTextMsg>{

    private static final String TAG = "CustomGroupMsgProvider";

    private static class ViewHolder {
        TextView tv_msg_content;
        TextView tv_groupname;
        SimpleDraweeView group_headview;
        LinearLayout ll_group_info;
        LinearLayout linear_group_agree;
        TextView tv_group_no;
        TextView tv_group_agree;
        View line_group;
        TextView tv_checkmore;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rong_systemmsg_item, null);
        CustomLinkTextMsgProvider.ViewHolder holder = new CustomLinkTextMsgProvider.ViewHolder();
        holder.tv_msg_content = view.findViewById(R.id.tv_msg_content);
        holder.group_headview = view.findViewById(R.id.group_headview);
        holder.tv_groupname = view.findViewById(R.id.tv_groupname);
        holder.ll_group_info = view.findViewById(R.id.ll_group_info);
        holder.linear_group_agree = view.findViewById(R.id.linear_group_agree);
        holder.tv_group_no = view.findViewById(R.id.tv_group_no);
        holder.tv_group_agree = view.findViewById(R.id.tv_group_agree);
        holder.line_group = view.findViewById(R.id.line_group);
        holder.tv_checkmore = view.findViewById(R.id.tv_checkmore);

        holder.ll_group_info.setVisibility(View.GONE);
        holder.linear_group_agree.setVisibility(View.GONE);
        holder.line_group.setVisibility(View.VISIBLE);
        holder.tv_checkmore.setVisibility(View.VISIBLE);
        view.setTag(holder);
        return view;
    }

    @Override
    public Spannable getContentSummary(CustomLinkTextMsg data) {
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
    public void onItemClick(View view, int position, CustomLinkTextMsg content, UIMessage message) {
        if(!TextUtils.isEmpty(content.getExtra())){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(content.getExtra());
                if(jsonObject.has("type")){
                    String type = jsonObject.getString("type");
                    String id = jsonObject.getString("id");
                    Intent intent = new Intent();
                    if(TextUtils.equals(type,"1")){

                    }else if(TextUtils.equals(type,"2")){
                        intent.setAction("com.d6.android.app.activities.SquareTrendDetailActivity");
                        intent.putExtra("id",id+"");
                    }else if(TextUtils.equals(type,"3")){
                        intent.setAction("com.d6.android.app.activities.OldFansActivity");
                    }else if(TextUtils.equals(type,"4")){
                        intent.setAction("com.d6.android.app.activities.UserInfoActivity");
                        intent.putExtra("id",id+"");
                    }else if(TextUtils.equals(type,"5")){
                        intent.setAction("com.d6.android.app.activities.SquareTrendDetailActivity");

                    }else if(TextUtils.equals(type,"6")){
                        intent.setAction("com.d6.android.app.activities.RecommendDateActivity");
                    }else if(TextUtils.equals(type,"7")){
                        intent.setAction("com.d6.android.app.activities.SquareTrendDetailActivity");
                    }else if(TextUtils.equals(type,"8")){
                        intent.setAction("com.d6.android.app.activities.FindDateDetailActivity");
                        intent.putExtra("id",id+"");
                    }else if(TextUtils.equals(type,"9")){
                        intent.setAction("com.d6.android.app.activities.SpeedDateDetailActivity");
                        intent.putExtra("id",id+"");
                    }else if(TextUtils.equals(type,"10")){
                        intent.setAction("com.d6.android.app.activities.MyPointsActivity");
                    }else if(TextUtils.equals(type,"11")){
                        intent.setAction("com.d6.android.app.activities.FansActivity");
                    }else if(TextUtils.equals(type,"12")){
                        intent.setAction("com.d6.android.app.activities.InviteGoodFriendsActivity");
                    }
                    view.getContext().startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//                    else if(TextUtils.equals(type,"4")){
//                        holder.mTvMsgContent.setVisibility(View.VISIBLE);
//                    }
        }
    }
    @Override
    public void onItemLongClick(final View view, int position, final CustomLinkTextMsg content, final UIMessage message) {

    }

    @Override
    public void bindView(final View v, int position, final CustomLinkTextMsg content, final UIMessage data) {
        CustomLinkTextMsgProvider.ViewHolder holder = (CustomLinkTextMsgProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.RECEIVE) {
            TextView tvcontent = holder.tv_msg_content;
            tvcontent.setText(content.getContent());
        }
    }
}
