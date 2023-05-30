package com.d6.android.app.rong.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.adapters.CardTagAdapter;
import com.d6.android.app.models.UserData;
import com.d6.android.app.models.UserTag;
import com.d6.android.app.rong.bean.BusinessCardFMsgContent;
import com.d6.android.app.utils.GsonHelper;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;

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

@ProviderTag(messageContent = BusinessCardFMsgContent.class, showReadState = true)
public class BusinessCardFMsgProvider extends IContainerItemProvider.MessageProvider<BusinessCardFMsgContent> {
    private static final String TAG = BusinessCardFMsgProvider.class.getName();
    private ArrayList<UserTag> mTags = new ArrayList<UserTag>();

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_women_card, null);
        ViewHolder holder = new BusinessCardFMsgProvider.ViewHolder();
        holder.mllChatWomenCard = view.findViewById(R.id.ll_chat_women_card);
        holder.chat_imageView = view.findViewById(R.id.chat_imageView);
        holder.chat_headView = view.findViewById(R.id.chat_headView);
        holder.chat_img_auther = view.findViewById(R.id.chat_img_auther);
        holder.tv_chat_name = view.findViewById(R.id.tv_chat_name);
        holder.tv_chat_womang_age = view.findViewById(R.id.tv_chat_womang_age);
        holder.tv_chat_vip = view.findViewById(R.id.tv_chat_vip);
        holder.tv_chat_city = view.findViewById(R.id.tv_chat_city);
        holder.tv_chat_content = view.findViewById(R.id.tv_chat_content);
        holder.rv_chat_tags = view.findViewById(R.id.rv_chat_tags);

        holder.rv_chat_tags.setHasFixedSize(true);
        holder.rv_chat_tags.setLayoutManager(new FlexboxLayoutManager(context));
        holder.rv_chat_tags.setNestedScrollingEnabled(false);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View v, int position, final BusinessCardFMsgContent content, final UIMessage data) {
        ViewHolder holder = (BusinessCardFMsgProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mllChatWomenCard.setBackgroundResource(R.drawable.ic_bubble_right);
        } else {
            holder.mllChatWomenCard.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        }
        if (!TextUtils.isEmpty(content.getExtra())) {
            Log.i(TAG, "用户内容" + content.getExtra());
            try {
                UserData mUserData = GsonHelper.getGson().fromJson(content.getExtra(), UserData.class);
                if (!TextUtils.equals(mUserData.getUserpics(), "null")) {
                    if (TextUtils.isEmpty(mUserData.getUserpics())) {
                        holder.chat_imageView.setImageURI(mUserData.getPicUrl());
                    } else {
                        String[] images = mUserData.getUserpics().split(",");
                        if (images.length > 0) {
                            holder.chat_imageView.setImageURI(images[0]);
                        }
                    }
                } else {
                    holder.chat_imageView.setImageURI(mUserData.getPicUrl());
                }

                if (!TextUtils.isEmpty(mUserData.getEgagementtext())) {
                    if (!TextUtils.equals("null", mUserData.getEgagementtext())) {
                        holder.tv_chat_content.setText(mUserData.getEgagementtext());
                    } else {
                        holder.tv_chat_content.setVisibility(View.GONE);
                    }
                } else if (!(TextUtils.isEmpty(mUserData.getSignature()))) {
                    if (!TextUtils.equals("null", mUserData.getSignature())) {
                        holder.tv_chat_content.setText(mUserData.getSignature());
                    } else {
                        holder.tv_chat_content.setVisibility(View.GONE);
                    }
                } else if (!TextUtils.isEmpty(mUserData.getIntro())) {
                    if (!TextUtils.equals("null", mUserData.getIntro())) {
                        holder.tv_chat_content.setText(mUserData.getIntro());
                    } else {
                        holder.tv_chat_content.setVisibility(View.GONE);
                    }
                } else {
                    holder.tv_chat_content.setVisibility(View.GONE);
                }

                if (TextUtils.equals("0", mUserData.getSex())) {
                    if (TextUtils.equals("0", mUserData.getScreen()) || TextUtils.equals("3", mUserData.getScreen()) || TextUtils.isEmpty(mUserData.getScreen())) {
                        holder.chat_img_auther.setVisibility(View.GONE);
                        holder.chat_img_auther.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.renzheng_small));
                    } else if (TextUtils.equals("1", mUserData.getScreen())) {
                        holder.chat_img_auther.setVisibility(View.VISIBLE);
                        holder.chat_img_auther.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.video_small));
                    }
                } else {
                    holder.chat_img_auther.setVisibility(View.GONE);
                }

                holder.chat_headView.setImageURI(mUserData.getPicUrl());
                holder.tv_chat_name.setText(mUserData.getName());
                if (!TextUtils.isEmpty(mUserData.getAge())) {
                    holder.tv_chat_womang_age.setText(mUserData.getAge());
                } else {
                    holder.tv_chat_womang_age.setText("");
                }

                holder.tv_chat_womang_age.setSelected(TextUtils.equals("0", mUserData.getSex()));

                if (TextUtils.equals("0", mUserData.getSex())) {//女性
                    //27入门 28中级  29优质
                    if (TextUtils.equals(mUserData.getUserclassesid(), "27")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.gril_cj));
                    } else if (TextUtils.equals(mUserData.getUserclassesid(), "28")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.gril_zj));
                    } else if (TextUtils.equals(mUserData.getUserclassesid(), "29")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.gril_gj));
                    } else if (TextUtils.equals(mUserData.getUserclassesid(), "7")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.youke_icon));
                    }else if (TextUtils.equals(mUserData.getUserclassesid(), "31")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.app_vip));
                    }
                } else {
                    if (TextUtils.equals(mUserData.getUserclassesid(), "22")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_ordinary));
                    } else if (TextUtils.equals(mUserData.getUserclassesid(), "23")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_silver));
                    } else if (TextUtils.equals(mUserData.getUserclassesid(), "24")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_gold));
                    } else if (TextUtils.equals(mUserData.getUserclassesid(), "25")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_zs));
                    } else if (TextUtils.equals(mUserData.getUserclassesid(), "26")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_private));
                    } else if (TextUtils.equals(mUserData.getUserclassesid(), "7")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.youke_icon));
                    }else if (TextUtils.equals(mUserData.getUserclassesid(), "30")) {
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.ruqun_icon));
                    }else if(TextUtils.equals(mUserData.getUserclassesid(), "31")){
                        holder.tv_chat_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.app_vip));
                    }
                }

                mTags.clear();
                if (!TextUtils.isEmpty(mUserData.getHeight())) {
                    mTags.add(new UserTag("身高:" + mUserData.getHeight(), R.drawable.shape_tag_bg_1,2));
                }

                if (!TextUtils.isEmpty(mUserData.getWeight())) {
                    mTags.add(new UserTag("体重:" + mUserData.getWeight(), R.drawable.shape_tag_bg_2,2));
                }

                if (!TextUtils.isEmpty(mUserData.getJob())) {
                    mTags.add(new UserTag(mUserData.getJob(), R.drawable.shape_tag_bg_3,2));
                }

                if (!TextUtils.isEmpty(mUserData.getConstellation())) {
                    mTags.add(new UserTag(mUserData.getConstellation(), R.drawable.shape_tag_bg_5,2));
                }

                if (!TextUtils.isEmpty(mUserData.getHobbit())) {
                    String[] mHobbies = mUserData.getHobbit().replace("#", ",").split(",");
                    if (mHobbies != null) {
                        for (int i = 0; i < mHobbies.length; i++) {
                            if (!TextUtils.isEmpty(mHobbies[i])) {
                                mTags.add(new UserTag(mHobbies[i], R.drawable.shape_tag_bg_6,2));
                            }
                        }
                    }
                }
                if (mTags.size() >= 1) {
                    holder.rv_chat_tags.setAdapter(new CardTagAdapter(mTags));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Spannable getContentSummary(BusinessCardFMsgContent data) {
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
    public void onItemClick(View view, int position, BusinessCardFMsgContent content, UIMessage message) {
        UserData mUserData = GsonHelper.getGson().fromJson(content.getExtra(), UserData.class);
        if (mUserData != null) {
            Intent intent = new Intent();
            intent.setAction("com.d6.android.app.activities.UserInfoActivity");
            intent.putExtra("id", mUserData.getAccountId());
            view.getContext().startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(final View view, int position, final BusinessCardFMsgContent content, final UIMessage message) {

        BusinessCardFMsgProvider.ViewHolder holder = (BusinessCardFMsgProvider.ViewHolder) view.getTag();
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
                    clipboard.setText(((BusinessCardFMsgContent) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    private static class ViewHolder {
        LinearLayout mllChatWomenCard;
        SimpleDraweeView chat_imageView;
        SimpleDraweeView chat_headView;
        ImageView chat_img_auther;
        TextView tv_chat_name;
        TextView tv_chat_womang_age;
        TextView tv_chat_vip;
        TextView tv_chat_city;
        TextView tv_chat_content;
        RecyclerView rv_chat_tags;
        boolean longClick;
    }
}
