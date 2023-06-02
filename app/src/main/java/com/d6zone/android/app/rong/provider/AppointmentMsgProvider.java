package com.d6zone.android.app.rong.provider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
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

import com.d6zone.android.app.R;
import com.d6zone.android.app.adapters.ChatImageAdapter;
import com.d6zone.android.app.models.MyAppointment;
import com.d6zone.android.app.rong.bean.AppointmentMsgContent;
import com.d6zone.android.app.utils.Const;
import com.d6zone.android.app.utils.GsonHelper;
import com.d6zone.android.app.widget.RxRecyclerViewDividerTool;
import com.d6zone.android.app.widget.badge.DisplayUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Arrays;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

import static com.d6zone.android.app.utils.UtilKt.getLevelDrawable;

/**
 * Created by Beyond on 2016/12/5.
 */

@ProviderTag(messageContent = AppointmentMsgContent.class, showReadState = true)
public class AppointmentMsgProvider extends IContainerItemProvider.MessageProvider<AppointmentMsgContent> {
    private static final String TAG = AppointmentMsgProvider.class.getName();

    ArrayList<String> mImages = new ArrayList<String>();

    private ChatImageAdapter mImageAdapter = new ChatImageAdapter(mImages, 1);

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chatdate_card, null);
        ViewHolder holder = new ViewHolder();
        holder.mLlChatDateCard = view.findViewById(R.id.ll_chat_date_card);
        holder.mHeaderView = view.findViewById(R.id.chat_date_headView);
        holder.img_chat_date_auther = view.findViewById(R.id.img_chat_date_auther);
        holder.tv_chat_date_name = view.findViewById(R.id.tv_chat_date_name);
        holder.tv_chat_date_sex = view.findViewById(R.id.tv_chat_date_sex);
        holder.tv_chat_date_vip = view.findViewById(R.id.tv_chat_date_vip);
        holder.tv_chat_date_content = view.findViewById(R.id.tv_chat_date_content);
        holder.rv_chat_date_images = view.findViewById(R.id.rv_chat_date_images);
        holder.tv_chat_datetype = view.findViewById(R.id.tv_chat_datetype);
//        holder.tv_chat_date_time = view.findViewById(R.id.tv_chat_date_time);
        holder.tv_chat_date_address = view.findViewById(R.id.tv_chat_date_address);

        holder.rv_chat_date_images.setHasFixedSize(true);
        holder.rv_chat_date_images.setLayoutManager(new GridLayoutManager(context, 3));
        holder.rv_chat_date_images.addItemDecoration(new RxRecyclerViewDividerTool(DisplayUtil.px2dp(context, 8)));//SpacesItemDecoration(dip(4),3)

//        holder.rv_chat_date_images.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View v, int position, final AppointmentMsgContent content, final UIMessage data) {
        ViewHolder holder = (AppointmentMsgProvider.ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLlChatDateCard.setBackgroundResource(R.drawable.ic_bubble_right);
        } else {
            holder.mLlChatDateCard.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        }

        if (!TextUtils.isEmpty(content.getExtra())) {
            Log.i(TAG, "内容=" + content.getExtra());
            try {
                final MyAppointment appointmentMsg = GsonHelper.getGson().fromJson(content.getExtra(), MyAppointment.class);
                holder.mHeaderView.setImageURI(appointmentMsg.getSAppointmentPicUrl());
                holder.tv_chat_date_name.setText(appointmentMsg.getSAppointUserName());
                holder.tv_chat_date_sex.setSelected(appointmentMsg.getISex() == 0);

                if (appointmentMsg.getIAge() <= 0) {
                    holder.tv_chat_date_sex.setText("");
                } else {
                    holder.tv_chat_date_sex.setText(String.valueOf(appointmentMsg.getIAge()));
                }

                holder.tv_chat_date_content.setText(appointmentMsg.getSDesc());

                holder.tv_chat_date_vip.setBackground(getLevelDrawable(""+appointmentMsg.getUserclassesid(),v.getContext()));
                if (0 == appointmentMsg.getISex()) {
                    holder.img_chat_date_auther.setVisibility(View.VISIBLE);
                    if (TextUtils.equals("0", "0")) {
                        holder.img_chat_date_auther.setVisibility(View.GONE);
                    } else if (TextUtils.equals("1", "1")) {
                        holder.img_chat_date_auther.setVisibility(View.VISIBLE);
                        holder.img_chat_date_auther.setImageResource(R.mipmap.video_small);
                    } else if (TextUtils.equals("3", "3")) {
                        holder.img_chat_date_auther.setVisibility(View.GONE);
                        holder.img_chat_date_auther.setImageResource(R.mipmap.renzheng_small);
                    }
                } else {
                    holder.img_chat_date_auther.setVisibility(View.GONE);
                }

                holder.tv_chat_datetype.setText(Const.dateTypes[appointmentMsg.getIAppointType() - 1]);

                if (appointmentMsg.getIAppointType() == 5) {
                    Drawable drawable = ContextCompat.getDrawable(v.getContext(), R.mipmap.invitation_nolimit_default);
                    holder.tv_chat_datetype.setCompoundDrawables(drawable, null, null, null);
                } else {
                    Drawable drawable = ContextCompat.getDrawable(v.getContext(), Const.dateTypesBig[appointmentMsg.getIAppointType() - 1]);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
                    holder.tv_chat_datetype.setCompoundDrawables(drawable, null, null, null);
                }

//                holder.tv_chat_date_time.setText("倒计时·" + converTime(appointmentMsg.getDEndtime()));
                holder.tv_chat_date_address.setText(appointmentMsg.getSPlace());
                if (appointmentMsg.getSAppointPic() == null || appointmentMsg.getSAppointPic().length() == 0) {
                    holder.rv_chat_date_images.setVisibility(View.GONE);
                } else {
                    holder.rv_chat_date_images.setVisibility(View.VISIBLE);
                }

                mImages.clear();
                if (!TextUtils.isEmpty(appointmentMsg.getSAppointPic())) {
                    String[] images = appointmentMsg.getSAppointPic().split(",");
                    mImages.addAll(Arrays.asList(images));
                    holder.rv_chat_date_images.setAdapter(mImageAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Spannable getContentSummary(AppointmentMsgContent data) {
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
    public void onItemClick(View view, int position, AppointmentMsgContent content, UIMessage message) {
//        MyAppointment appointment = GsonHelper.getGson().fromJson(content.getExtra(), MyAppointment.class);
//        if (appointment != null) {
//            Intent intent = new Intent();
//            intent.setAction("com.d6.android.app.activities.MyDateDetailActivity");
//            intent.putExtra("from", FROM_MY_CHATDATE);
//            if(message.getMessageDirection() == Message.MessageDirection.SEND){
//            intent.putExtra("iShareUserId", message.getTargetId());
//            }
//            intent.putExtra("data", appointment);
//            view.getContext().startActivity(intent);
//        }
    }

    @Override
    public void onItemLongClick(final View view, int position, final AppointmentMsgContent content, final UIMessage message) {

        AppointmentMsgProvider.ViewHolder holder = (AppointmentMsgProvider.ViewHolder) view.getTag();
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
                    clipboard.setText(((AppointmentMsgContent) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, null);
                } else if (which == 2) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    private static class ViewHolder {
        LinearLayout mLlChatDateCard;
        SimpleDraweeView mHeaderView;
        ImageView img_chat_date_auther;
        TextView tv_chat_date_name;
        TextView tv_chat_date_sex;
        TextView tv_chat_date_vip;
        TextView tv_chat_date_content;
        RecyclerView rv_chat_date_images;
        TextView tv_chat_datetype;
//        TextView tv_chat_date_time;
        TextView tv_chat_date_address;

        boolean longClick;
    }
}
