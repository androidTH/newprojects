package com.d6.android.app.rong.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.adapters.ChatImageAdapter;
import com.d6.android.app.adapters.SelfReleaselmageAdapter;
import com.d6.android.app.rong.bean.CustomMessage;
import com.d6.android.app.rong.bean.ImgTxtMessage;
import com.d6.android.app.rong.bean.SquareMessage;
import com.d6.android.app.rong.bean.SquareMsgContent;
import com.d6.android.app.utils.GsonHelper;
import com.d6.android.app.widget.RxRecyclerViewDividerTool;
import com.d6.android.app.widget.badge.DisplayUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

@ProviderTag(messageContent = SquareMsgContent.class, showReadState = true)
public class SquareMsgProvider extends IContainerItemProvider.MessageProvider<SquareMsgContent>{
    private static final String TAG = SquareMsgProvider.class.getName();

    ArrayList<String> mImages =new ArrayList<>();
    private ChatImageAdapter mImageAdapter = new ChatImageAdapter(mImages,1);
    private static class ViewHolder {
        LinearLayout mLlChatDynamicCard;
        SimpleDraweeView mHeaderView;
        ImageView mImgAuther;
        TextView mTvDynamicName;
        TextView mTvDynamicSex;
        TextView mTvDynamicVip;
        TextView mTvDynamicContent;
        RecyclerView mRvDynamicImages;
        TextView mTvDynamicRedflower;
        TextView mTvDynamicAppraise;
        TextView mTvDynamicComment;
        boolean longClick;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dynamic_card, null);
        SquareMsgProvider.ViewHolder holder = new SquareMsgProvider.ViewHolder();
        holder.mLlChatDynamicCard = view.findViewById(R.id.ll_chat_dynamic_card);
        holder.mHeaderView = view.findViewById(R.id.dynamic_headView);
        holder.mImgAuther = view.findViewById(R.id.img_auther);
        holder.mTvDynamicName = view.findViewById(R.id.tv_dynamic_name);
        holder.mTvDynamicSex = view.findViewById(R.id.tv_dynamic_sex);
        holder.mTvDynamicVip = view.findViewById(R.id.tv_dynamic_vip);
        holder.mTvDynamicContent = view.findViewById(R.id.tv_dynamic_content);
        holder.mRvDynamicImages = view.findViewById(R.id.rv_dynamic_images);
        holder.mTvDynamicRedflower = view.findViewById(R.id.tv_dynamic_redflower);
        holder.mTvDynamicAppraise = view.findViewById(R.id.tv_dynamic_appraise);
        holder.mTvDynamicComment = view.findViewById(R.id.tv_dynamic_comment);

        holder.mRvDynamicImages.setHasFixedSize(true);
//        holder.mRvDynamicImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        holder.mRvDynamicImages.setLayoutManager(new GridLayoutManager(context,3));
        holder.mRvDynamicImages.addItemDecoration(new RxRecyclerViewDividerTool(DisplayUtil.px2dp(context,8)));//SpacesItemDecoration(dip(4),3)
        view.setTag(holder);
        return view;
    }

    @Override
    public Spannable getContentSummary(SquareMsgContent data) {
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
    public void onItemClick(View view, int position, SquareMsgContent content, UIMessage message) {
        SquareMessage mSquareMsg = GsonHelper.getGson().fromJson(content.getExtra(), SquareMessage.class);
        if(mSquareMsg!=null){
            Intent intent = new Intent();
            intent.setAction("com.d6.android.app.activities.SquareTrendDetailActivity");
            intent.putExtra("id",mSquareMsg.getIds());
            view.getContext().startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(final View view, int position, final SquareMsgContent content, final UIMessage message) {

        SquareMsgProvider.ViewHolder holder = (SquareMsgProvider.ViewHolder) view.getTag();
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
                    clipboard.setText(((SquareMsgContent) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    @Override
    public void bindView(final View v, int position, final SquareMsgContent content, final UIMessage data) {
        SquareMsgProvider.ViewHolder holder = (SquareMsgProvider.ViewHolder) v.getTag();
        if(data.getMessageDirection() == Message.MessageDirection.SEND){
            holder.mLlChatDynamicCard.setBackgroundResource(R.drawable.ic_bubble_right);
        }else{
            holder.mLlChatDynamicCard.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        }
//        holder.mRlChatDynamicCard.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        if (!TextUtils.isEmpty(content.getExtra())) {
            Log.i(TAG,"内容="+content.getExtra());
            SquareMessage mSquareMsg = GsonHelper.getGson().fromJson(content.getExtra(), SquareMessage.class);
            holder.mHeaderView.setImageURI(mSquareMsg.getPicUrl());
            holder.mTvDynamicName.setText(mSquareMsg.getName());
            if(TextUtils.equals("0",mSquareMsg.getSex())){
                holder.mTvDynamicSex.setSelected(true);
                if(TextUtils.equals("0","0")){
                    holder.mImgAuther.setVisibility(View.GONE);
                }else if (TextUtils.equals("1", "1")) {
                    holder.mImgAuther.setVisibility(View.VISIBLE);
                    holder.mImgAuther.setImageResource(R.mipmap.video_small);
                } else if (TextUtils.equals("3", "3")) {
                    holder.mImgAuther.setVisibility(View.GONE);
                    holder.mImgAuther.setImageResource(R.mipmap.renzheng_small);
                }
            }else{
                holder.mTvDynamicSex.setSelected(false);
                holder.mImgAuther.setVisibility(View.GONE);
            }

            holder.mTvDynamicSex.setText(mSquareMsg.getAge());
            holder.mTvDynamicContent.setText(mSquareMsg.getContent());
            if (mSquareMsg.getUserclassesname().startsWith("入门")) {
                holder.mTvDynamicVip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.gril_cj));
            } else if (mSquareMsg.getUserclassesname().startsWith("中级")) {
                holder.mTvDynamicVip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.gril_zj));
            } else if (mSquareMsg.getUserclassesname().startsWith("优质")) {
                holder.mTvDynamicVip.setBackground(ContextCompat.getDrawable(v.getContext(),R.mipmap.gril_gj));
            } else if (mSquareMsg.getUserclassesname().startsWith("普通")) {
                holder.mTvDynamicVip.setBackground(ContextCompat.getDrawable(v.getContext(),R.mipmap.vip_ordinary));
            } else if (mSquareMsg.getUserclassesname().startsWith("白银")) {
                holder.mTvDynamicVip.setBackground(ContextCompat.getDrawable(v.getContext(),R.mipmap.vip_silver));
            } else if (mSquareMsg.getUserclassesname().startsWith("黄金")) {
                holder.mTvDynamicVip.setBackground(ContextCompat.getDrawable(v.getContext(),R.mipmap.vip_gold));
            } else if (mSquareMsg.getUserclassesname().startsWith( "钻石")) {
                holder.mTvDynamicVip.setBackground(ContextCompat.getDrawable(v.getContext(),R.mipmap.vip_zs));
            } else if (mSquareMsg.getUserclassesname().startsWith("私人")) {
                holder.mTvDynamicVip.setBackground(ContextCompat.getDrawable(v.getContext(),R.mipmap.vip_private));
            }else{
                holder.mTvDynamicVip.setVisibility(View.GONE);
            }

            holder.mTvDynamicRedflower.setText(String.valueOf(mSquareMsg.getiFlowerCount()));
            holder.mTvDynamicAppraise.setText(String.valueOf(mSquareMsg.getAppraiseCount()));
            holder.mTvDynamicComment.setText(String.valueOf(mSquareMsg.getCommentcount()));

            if (mSquareMsg.getCoverurl()==null||mSquareMsg.getCoverurl().length()==0) {
                holder.mRvDynamicImages.setVisibility(View.GONE);
            } else {
                holder.mRvDynamicImages.setVisibility(View.VISIBLE);
            }

            mImages.clear();
            String[] images = mSquareMsg.getCoverurl().split(",");
            if (images != null&&images.length>=1) {
                if(images.length>3){
                    mImages.addAll(Arrays.asList(images).subList(0,3));
                }else{
                    mImages.addAll(Arrays.asList(images));
                }
                holder.mRvDynamicImages.setAdapter(mImageAdapter);
            }
        }
    }
}
