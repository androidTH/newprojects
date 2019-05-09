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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.d6.android.app.R;
import com.d6.android.app.adapters.CardManTagAdapter;
import com.d6.android.app.adapters.ChatDatelmageAdapter;
import com.d6.android.app.adapters.DatelmageAdapter;
import com.d6.android.app.models.UserData;
import com.d6.android.app.models.UserTag;
import com.d6.android.app.rong.bean.BusinessCardMMsgContent;
import com.d6.android.app.utils.GsonHelper;
import com.d6.android.app.widget.RxRecyclerViewDividerTool;
import com.d6.android.app.widget.badge.DisplayUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayoutManager;

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

@ProviderTag(messageContent = BusinessCardMMsgContent.class, showReadState = true)
public class BusinessCardMMsgProvider extends IContainerItemProvider.MessageProvider<BusinessCardMMsgContent> {
    private static final String TAG = BusinessCardMMsgProvider.class.getName();
    private List<String> mImages = new ArrayList<String>();

    private ArrayList<UserTag> mTags = new ArrayList<>();
    private CardManTagAdapter mUserTag = new CardManTagAdapter(mTags);

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_men_card, null);
        ViewHolder holder = new BusinessCardMMsgProvider.ViewHolder();
        holder.mllChatmenCard = view.findViewById(R.id.ll_chat_men_card);
        holder.chat_men_headView = view.findViewById(R.id.chat_men_headView);
        holder.noimg_chat_men_line = view.findViewById(R.id.noimg_chat_men_line);
        holder.chat_men_img_auther = view.findViewById(R.id.chat_men_img_auther);

        holder.tv_chat_men_name = view.findViewById(R.id.tv_chat_men_name);
        holder.tv_chat_men_age = view.findViewById(R.id.tv_chat_men_age);
        holder.tv_chat_men_vip = view.findViewById(R.id.tv_chat_men_vip);
        holder.tv_chat_men_content = view.findViewById(R.id.tv_chat_men_content);
        holder.rv_chat_men_images = view.findViewById(R.id.rv_chat_men_images);
        holder.rv_chat_men_tags = view.findViewById(R.id.rv_chat_men_tags);

        holder.rv_chat_men_images.setHasFixedSize(true);
        holder.rv_chat_men_images.setLayoutManager(new GridLayoutManager(context,3));
        holder.rv_chat_men_images.addItemDecoration(new RxRecyclerViewDividerTool(DisplayUtil.px2dp(context,8)));

        holder.rv_chat_men_tags.setHasFixedSize(true);
        holder.rv_chat_men_tags.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.rv_chat_men_tags.setNestedScrollingEnabled(false);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View v, int position, final BusinessCardMMsgContent content, final UIMessage data) {
        ViewHolder holder = (BusinessCardMMsgProvider.ViewHolder) v.getTag();
        if(data.getMessageDirection() == Message.MessageDirection.SEND){
            holder.mllChatmenCard.setBackgroundResource(R.drawable.ic_bubble_right);
        }else{
            holder.mllChatmenCard.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        }
        if (!TextUtils.isEmpty(content.getExtra())) {
            UserData mUserData = GsonHelper.getGson().fromJson(content.getExtra(), UserData.class);

            holder.chat_men_headView.setImageURI(mUserData.getPicUrl());
            holder.tv_chat_men_name.setText(mUserData.getName());
            holder.tv_chat_men_age.setText(mUserData.getAge());
            holder.tv_chat_men_age.setSelected(TextUtils.equals("0", mUserData.getSex()));

            if (TextUtils.equals("0", mUserData.getSex())) {
                if (TextUtils.equals("0", mUserData.getScreen()) || TextUtils.equals("3", mUserData.getScreen()) ||TextUtils.isEmpty(mUserData.getScreen())) {
                    holder.chat_men_img_auther.setVisibility(View.GONE);
                    holder.chat_men_img_auther.setBackground(ContextCompat.getDrawable(v.getContext(),R.mipmap.renzheng_small));
                } else if (TextUtils.equals("1", mUserData.getScreen())) {
                    holder.chat_men_img_auther.setVisibility(View.VISIBLE);
                    holder.chat_men_img_auther.setBackground(ContextCompat.getDrawable(v.getContext(),R.mipmap.video_small));
                }
            } else {
                holder.chat_men_img_auther.setVisibility(View.GONE);
            }

            if (TextUtils.equals("0", mUserData.getSex())) {//女性
                //27入门 28中级  29优质
                if (TextUtils.equals(mUserData.getUserclassesid(), "27")) {
                    holder.tv_chat_men_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.gril_cj));
                } else if (TextUtils.equals(mUserData.getUserclassesid(), "28")) {
                    holder.tv_chat_men_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.gril_zj));
                } else if (TextUtils.equals(mUserData.getUserclassesid(), "29")) {
                    holder.tv_chat_men_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.gril_gj));
                }
            } else {
                if (TextUtils.equals(mUserData.getUserclassesid(), "22")) {
                    holder.tv_chat_men_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_ordinary));
                } else if (TextUtils.equals(mUserData.getUserclassesid(), "23")) {
                    holder.tv_chat_men_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_silver));
                } else if (TextUtils.equals(mUserData.getUserclassesid(), "24")) {
                    holder.tv_chat_men_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_gold));
                } else if (TextUtils.equals(mUserData.getUserclassesid(), "25")) {
                    holder.tv_chat_men_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_zs));
                } else if (TextUtils.equals(mUserData.getUserclassesid(), "26")) {
                    holder.tv_chat_men_vip.setBackground(ContextCompat.getDrawable(v.getContext(), R.mipmap.vip_private));
                }
            }

            if (!TextUtils.isEmpty(mUserData.getEgagementtext())) {
                if (!TextUtils.equals("null", mUserData.getEgagementtext())) {
                    holder.tv_chat_men_content.setText(mUserData.getEgagementtext());
                } else {
                    holder.tv_chat_men_content.setText("");
                }
            } else if (!(TextUtils.isEmpty(mUserData.getSignature()))) {
                if (!TextUtils.equals("null", mUserData.getSignature())) {
                    holder.tv_chat_men_content.setText(mUserData.getSignature());
                } else {
                    holder.tv_chat_men_content.setText("");
                }
            } else if (!(TextUtils.isEmpty(mUserData.getIntro()))) {
                if (!TextUtils.equals("null", mUserData.getIntro())) {
                    holder.tv_chat_men_content.setText(mUserData.getIntro());
                } else {
                    holder.tv_chat_men_content.setText("");
                }
            } else {
                holder.tv_chat_men_content.setText("");
            }


            if (!TextUtils.equals(mUserData.getUserpics(), "null")) {
                if (TextUtils.isEmpty(mUserData.getUserpics())) {
                    mImages.clear();
                    holder.rv_chat_men_images.setVisibility(View.GONE);
                    holder.noimg_chat_men_line.setVisibility(View.VISIBLE);
                } else {
                    String[] imglist = mUserData.getUserpics().split(",");
                    if (imglist.length == 0) {
                        mImages.clear();
                        holder.rv_chat_men_images.setVisibility(View.GONE);
                        holder.noimg_chat_men_line.setVisibility(View.VISIBLE);
                    } else {
                        holder.noimg_chat_men_line.setVisibility(View.GONE);
                        holder.rv_chat_men_images.setVisibility(View.VISIBLE);
                        mImages.clear();
                        if (imglist.length >= 4) {
                            mImages.addAll(Arrays.asList(imglist).subList(0, 3));
                        } else {
                            mImages.addAll(Arrays.asList(imglist));
                        }

                        holder.rv_chat_men_images.setAdapter(new ChatDatelmageAdapter((ArrayList) mImages, 1));
                        ((DatelmageAdapter) holder.rv_chat_men_images.getAdapter()).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            }
                        });
                    }
                }
            } else {
                mImages.clear();
                holder.rv_chat_men_images.setVisibility(View.GONE);
                holder.noimg_chat_men_line.setVisibility(View.VISIBLE);
            }

            mTags.clear();
            if (!TextUtils.isEmpty(mUserData.getHeight())) {
                mTags.add(new UserTag("身高 " + mUserData.getHeight(), R.mipmap.boy_stature_icon));
            }

            if (!TextUtils.isEmpty(mUserData.getWeight())) {
                mTags.add(new UserTag("体重 " + mUserData.getWeight(), R.mipmap.boy_weight_grayicon));
            }

            if (!TextUtils.isEmpty(mUserData.getJob())) {
                mTags.add(new UserTag("职业 " + mUserData.getJob(), R.mipmap.boy_profession_icon));
            }

            if (!TextUtils.isEmpty(mUserData.getConstellation())) {
                mTags.add(new UserTag("星座 " + mUserData.getConstellation(), R.mipmap.boy_constellation_icon));
            }

            if (!TextUtils.isEmpty(mUserData.getCity())) {
                mTags.add(new UserTag("地区 " + mUserData.getCity(), R.mipmap.boy_area_icon));
            }

            if (!TextUtils.isEmpty(mUserData.getZuojia())) {
                mTags.add(new UserTag("座驾 " + mUserData.getZuojia(), R.mipmap.boy_car_icon));
            }

            if (!TextUtils.isEmpty(mUserData.getHobbit())) {
                String mHobbies = mUserData.getHobbit().replace("#", " ");
                StringBuilder sb = new StringBuilder();
                sb.append("爱好 ");
                sb.append(mHobbies);
                mTags.add(new UserTag(sb.toString(), R.mipmap.boy_hobby_icon));
            }

            if (mTags.size() == 0) {
                holder.rv_chat_men_tags.setVisibility(View.GONE);
            } else {
                holder.rv_chat_men_tags.setVisibility(View.VISIBLE);
                holder.rv_chat_men_tags.setAdapter(mUserTag);
            }
        }
    }

    @Override
    public Spannable getContentSummary(BusinessCardMMsgContent data) {
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
    public void onItemClick(View view, int position, BusinessCardMMsgContent content, UIMessage message) {
        UserData mUserData = GsonHelper.getGson().fromJson(content.getExtra(), UserData.class);
        if(mUserData!=null){
            Intent intent = new Intent();
            intent.setAction("com.d6.android.app.activities.UserInfoActivity");
            intent.putExtra("id",mUserData.getAccountId());
            view.getContext().startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(final View view, int position, final BusinessCardMMsgContent content, final UIMessage message) {

        BusinessCardMMsgProvider.ViewHolder holder = (BusinessCardMMsgProvider.ViewHolder) view.getTag();
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
                    clipboard.setText(((BusinessCardMMsgContent) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    private static class ViewHolder {
        LinearLayout mllChatmenCard;
        SimpleDraweeView chat_men_headView;
        ImageView chat_men_img_auther;
        TextView tv_chat_men_name;
        TextView tv_chat_men_age;
        TextView tv_chat_men_vip;
        TextView tv_chat_men_content;
        RecyclerView rv_chat_men_images;
        RecyclerView rv_chat_men_tags;
        View noimg_chat_men_line;
        boolean longClick;
    }
}
