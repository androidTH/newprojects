package com.d6zone.android.app.rong.provider;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d6zone.android.app.R;
import com.d6zone.android.app.rong.bean.CustomGroupApplyMsg;
import com.d6zone.android.app.rong.bean.GroupApplyMessage;
import com.d6zone.android.app.utils.GsonHelper;
import com.facebook.drawee.view.SimpleDraweeView;

import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import static com.d6zone.android.app.utils.UtilKt.confirmToGroup;

/**
 * Created by Beyond on 2016/12/5.
 */
@ProviderTag(messageContent = CustomGroupApplyMsg.class, showReadState = true)
public class CustomGroupApplyMsgProvider extends IContainerItemProvider.MessageProvider<CustomGroupApplyMsg>{

    private static final String TAG = "CustomGroupApplyMsgProvider";

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
        CustomGroupApplyMsgProvider.ViewHolder holder = new CustomGroupApplyMsgProvider.ViewHolder();
        holder.tv_msg_content = view.findViewById(R.id.tv_msg_content);
        holder.group_headview = view.findViewById(R.id.group_headview);
        holder.tv_groupname = view.findViewById(R.id.tv_groupname);
        holder.ll_group_info = view.findViewById(R.id.ll_group_info);
        holder.linear_group_agree = view.findViewById(R.id.linear_group_agree);
        holder.tv_group_no = view.findViewById(R.id.tv_group_no);
        holder.tv_group_agree = view.findViewById(R.id.tv_group_agree);
        holder.line_group = view.findViewById(R.id.line_group);
        holder.tv_checkmore = view.findViewById(R.id.tv_checkmore);

        holder.ll_group_info.setVisibility(View.VISIBLE);
        holder.linear_group_agree.setVisibility(View.VISIBLE);
        holder.line_group.setVisibility(View.GONE);
        holder.tv_checkmore.setVisibility(View.GONE);
        view.setTag(holder);
        return view;
    }

    @Override
    public Spannable getContentSummary(CustomGroupApplyMsg data) {
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
    public void onItemClick(View view, int position, CustomGroupApplyMsg content, UIMessage message) {
//        CustomGroupApplyMsgProvider.ViewHolder holder = (CustomGroupApplyMsgProvider.ViewHolder) view.getTag();
//        if(holder!=null){
//            holder.tv_group_agree.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    confirmToGroup((Activity) v.getContext(),msg.getsApplyId()+"","");
//                }
//            });
//        }
    }
    @Override
    public void onItemLongClick(final View view, int position, final CustomGroupApplyMsg content, final UIMessage message) {

    }

    @Override
    public void bindView(final View v, int position, final CustomGroupApplyMsg content, final UIMessage data) {
        final CustomGroupApplyMsgProvider.ViewHolder holder = (CustomGroupApplyMsgProvider.ViewHolder) v.getTag();

        if (data.getMessageDirection() == Message.MessageDirection.RECEIVE) {
            if(!TextUtils.isEmpty(content.getExtra())){
                try{
//                    JSONObject jsonObject =new JSONObject(content.getExtra());
//                    String sGroupName = jsonObject.getString("sGroupName");
//                    String sGroupPic = jsonObject.getString("sGroupPic");
//                    String sGroupName = jsonObject.getString("sGroupName");
//                    String sGroupName = jsonObject.getString("sGroupName");
                    final GroupApplyMessage msg = GsonHelper.getGson().fromJson(content.getExtra(), GroupApplyMessage.class);
                    holder.tv_msg_content.setText(msg.getContent());
                    holder.tv_groupname.setText(msg.getsGroupName());
                    holder.group_headview.setImageURI(msg.getsGroupPic());
                    Message message = data.getMessage();
                    if(TextUtils.isEmpty(message.getExtra())){
                        holder.tv_group_no.setText("拒绝");
                        holder.tv_group_agree.setText("同意");
                    }else{
                        if(TextUtils.equals("3",message.getExtra())){
                            holder.tv_group_no.setText("已拒绝");
                        }else if(TextUtils.equals("2",message.getExtra())){
                            holder.tv_group_agree.setText("已同意");
                        }
                    }


                    holder.tv_group_agree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmToGroup((Activity) v.getContext(),msg.getsApplyId()+"","2");
//                            updateExtra(data.getMessageId(),"2");
//                            holder.tv_group_agree.setText("已同意");
//                            holder.tv_group_no.setText("拒绝");
                        }
                    });

                    holder.tv_group_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmToGroup((Activity) v.getContext(),msg.getsApplyId()+"","3");
//                            updateExtra(data.getMessageId(),"3");
//                            holder.tv_group_agree.setText("同意");
//                            holder.tv_group_no.setText("已拒绝");
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateExtra(int messageId,String code){
        RongIMClient.getInstance().setMessageExtra(messageId,code,new RongIMClient.ResultCallback<Boolean>(){
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }

            @Override
            public void onSuccess(Boolean aBoolean) {

            }
        });
    }
}
