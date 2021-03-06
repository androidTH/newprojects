package com.d6.android.app.rong.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.d6.android.app.R;
import com.d6.android.app.utils.SPUtils;

import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

import static com.d6.android.app.utils.Const.CustomerServiceId;
import static com.d6.android.app.utils.Const.CustomerServiceWomenId;
import static com.d6.android.app.utils.Const.WHO_ANONYMOUS;

/**
 * author : jinjiarui
 * time   : 2019/01/12
 * desc   :
 * version:
 */
public class MessageListAdapterEx extends MessageListAdapter {
    private static String TAG = MessageListAdapterEx.class.getSimpleName();

    private Context mContext;
    private String mWhoanonmouse = "1";

    public MessageListAdapterEx(Context context) {
        super(context);
        this.mContext = context;
        this.mWhoanonmouse = SPUtils.Companion.instance().getString(WHO_ANONYMOUS, "1");
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        View result = super.newView(context, position, group);
        ViewHolderEx holder = new ViewHolderEx();
        holder.mIvHeaderLeftSign = result.findViewById(R.id.iv_header_leftsign);
        holder.mIvHeaderRightSign = result.findViewById(R.id.iv_header_rightsign);
        result.setTag(R.id.viewholderex, holder);
        return result;
    }

    @Override
    protected void bindView(View v, int position, UIMessage data) {
        super.bindView(v, position, data);
        ViewHolderEx holderEx = (ViewHolderEx) v.getTag(R.id.viewholderex);
        if (TextUtils.equals(CustomerServiceId, data.getTargetId()) || TextUtils.equals(CustomerServiceWomenId, data.getTargetId())) {
            if (data.getMessageDirection() == Message.MessageDirection.RECEIVE) {
                holderEx.mIvHeaderLeftSign.setVisibility(View.VISIBLE);
                holderEx.mIvHeaderRightSign.setVisibility(View.GONE);
            } else {
                holderEx.mIvHeaderLeftSign.setVisibility(View.GONE);
                holderEx.mIvHeaderRightSign.setVisibility(View.GONE);
            }
        } else if (TextUtils.equals(CustomerServiceId, data.getSenderUserId()) || TextUtils.equals(CustomerServiceWomenId, data.getSenderUserId())) {
            if (data.getMessageDirection() == Message.MessageDirection.SEND) {
                holderEx.mIvHeaderLeftSign.setVisibility(View.GONE);
                holderEx.mIvHeaderRightSign.setVisibility(View.VISIBLE);
            } else {
                holderEx.mIvHeaderLeftSign.setVisibility(View.GONE);
                holderEx.mIvHeaderRightSign.setVisibility(View.GONE);
            }
        } else {
            holderEx.mIvHeaderLeftSign.setVisibility(View.GONE);
            holderEx.mIvHeaderRightSign.setVisibility(View.GONE);
        }

        if (data.getConversationType() == Conversation.ConversationType.GROUP) {
            if (data.getMessageDirection() == Message.MessageDirection.SEND) {
                MessageListAdapter.ViewHolder holder = (MessageListAdapter.ViewHolder) v.getTag();
                if (TextUtils.equals("1", mWhoanonmouse)) {
                    holder.rightIconView.setAvatar(null, R.mipmap.nimingtouxiang_small);
                    holder.nameView.setVisibility(View.GONE);
                }else{
                    holder.nameView.setVisibility(View.GONE);
                }
            } else {
                MessageListAdapter.ViewHolder holder = (MessageListAdapter.ViewHolder) v.getTag();
                if(TextUtils.equals("2", mWhoanonmouse)){
                    holder.leftIconView.setAvatar(null, R.mipmap.nimingtouxiang_small);
                    holder.nameView.setVisibility(View.GONE);
                }else{
                    holder.nameView.setVisibility(View.GONE);
                }
            }
        }
    }

    protected class ViewHolderEx extends MessageListAdapter.ViewHolder {
        ImageView mIvHeaderLeftSign;
        ImageView mIvHeaderRightSign;

        public ViewHolderEx() {
            super();
        }
    }
}
