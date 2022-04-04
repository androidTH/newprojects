package com.d6.android.app.rong.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;

import com.d6.android.app.activities.ChatActivity;
import com.d6.android.app.rong.adapter.MessageListAdapterEx;
import com.d6.android.app.rong.bean.TipsMessage;
import com.d6.android.app.utils.Const;
import com.d6.android.app.widget.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongExtension;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.model.Message;

/**
 * 会话 Fragment 继承自ConversationFragment
 * onResendItemClick: 重发按钮点击事件. 如果返回 false,走默认流程,如果返回 true,走自定义流程
 * onReadReceiptStateClick: 已读回执详情的点击事件.
 * 如果不需要重写 onResendItemClick 和 onReadReceiptStateClick ,可以不必定义此类,直接集成 ConversationFragment 就可以了
 */
public class ConversationFragmentEx extends ConversationFragment {

    private String TAG = ConversationFragmentEx.class.getSimpleName();
    private OnShowAnnounceListener onShowAnnounceListener;
    private String mTargetId = "";
    public RongExtension rongExtension;
    public EditText mMyEditText;
    public ImageView mMyEmoticonToggle;
    private ImageView mMyPluginToggle;
    public ImageView mMyVoiceToggle;
    public ImageView mLoveHeart;
    public ImageView mSendChatGift;
    private boolean IsNotInput = false;
    private String hitmsg ="";
    private boolean IsNotEditTextClick = false;
    private boolean IsHideInput = false;

    public ConversationFragmentEx(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            IsNotInput = getArguments().getBoolean("flag");
            IsHideInput = getArguments().getBoolean("hideinput");
            hitmsg = getArguments().getString("hitmsg");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        rongExtension = v.findViewById(io.rong.imkit.R.id.rc_extension);
        mMyEditText = rongExtension.findViewById(io.rong.imkit.R.id.rc_edit_text);
        mMyEmoticonToggle = rongExtension.findViewById(io.rong.imkit.R.id.rc_emoticon_toggle);
        mMyPluginToggle = rongExtension.findViewById(io.rong.imkit.R.id.rc_plugin_toggle);
        mMyVoiceToggle = rongExtension.findViewById(io.rong.imkit.R.id.rc_voice_toggle);
        mSendChatGift = rongExtension.findViewById(io.rong.imkit.R.id.comment_inputgift);
        mLoveHeart = rongExtension.findViewById(io.rong.imkit.R.id.chat_loveheart);
        doIsNotSendMsg(IsNotInput,hitmsg);
        hideChatInput(IsHideInput);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSendChatGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("loveheart","点击了");
                if(mOnExtensionExpandedListener!=null){
                    mOnExtensionExpandedListener.onSendLoveHeart();
                }
            }
        });

        mSendChatGift.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnExtensionExpandedListener!=null){
                    mOnExtensionExpandedListener.onSendLongLoveHeart();
                }
                return true;
            }
        });
    }

    @Override
    protected void initFragment(Uri uri) {
        super.initFragment(uri);
        if (uri != null) {
            mTargetId = uri.getQueryParameter("targetId");
        }
    }

    @Override
    public MessageListAdapter onResolveAdapter(Context context) {
        return new MessageListAdapterEx(context);
//        return super.onResolveAdapter(context);
    }

    @Override
    public void onEventMainThread(Message msg) {
        super.onEventMainThread(msg);
        if(msg.getContent() instanceof TipsMessage){
            if(msg.getMessageDirection() == Message.MessageDirection.RECEIVE){
                TipsMessage mTipsMessage = (TipsMessage) msg.getContent();
//                getActivity().sendBroadcast(new Intent("com.d6.app.privatechat_apply_msg").
//                        putExtra("extra", mTipsMessage.getExtra()));
                if (onShowAnnounceListener != null) {
                    onShowAnnounceListener.onShowAnnounceView(mTipsMessage.getExtra(), mTipsMessage.getExtra());
                }
            }
        }
    }

    @Override
    public void onReadReceiptStateClick(io.rong.imlib.model.Message message) {

    }

    public void onWarningDialog(String msg) {
        String typeStr = getUri().getLastPathSegment();
        if (!typeStr.equals("chatroom")) {
            super.onWarningDialog(msg);
        }
    }

    @Override
    public void onShowAnnounceView(String announceMsg, String announceUrl) {
        if (onShowAnnounceListener != null) {
            onShowAnnounceListener.onShowAnnounceView(announceMsg, announceUrl);
        }
    }

    /**
     * 显示通告栏的监听器
     */
    public interface OnShowAnnounceListener {

        /**
         * 展示通告栏的回调
         *
         * @param announceMsg 通告栏展示内容
         * @param annouceUrl  通告栏点击链接地址，若此参数为空，则表示不需要点击链接，否则点击进入链接页面
         * @return
         */
        void onShowAnnounceView(String announceMsg, String annouceUrl);
    }

    public void setOnShowAnnounceBarListener(OnShowAnnounceListener listener) {
        onShowAnnounceListener = listener;
    }

    public void showStartDialog(final String dialogId) {

    }

    @Override
    public void onShowStarAndTabletDialog(String dialogId) {
        showStartDialog(dialogId);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSwitchToggleClick(View v, ViewGroup inputBoard) {
        super.onSwitchToggleClick(v, inputBoard);
        Log.i(TAG,"onSwitchToggleClick");
        IsNotEditTextClick = true;
    }

    @Override
    public void onPluginToggleClick(View v, ViewGroup extensionBoard) {
        Log.i(TAG,"onPluginToggleClick");
        IsNotEditTextClick = false;
    }

    @Override
    public void onEmoticonToggleClick(View v, ViewGroup extensionBoard) {
        Log.i(TAG,"onEmoticonToggleClick");
        IsNotEditTextClick = false;
    }

    @Override
    public void onEditTextClick(EditText editText) {
        super.onEditTextClick(editText);
        Log.i(TAG,"onEditTextClick");
        IsNotEditTextClick = true;
    }

    @Override
    public void onExtensionCollapsed() {
        super.onExtensionCollapsed();
        Log.i(TAG,"onExtensionCollapsed");
        IsNotEditTextClick = false;
    }

    @Override
    public void onExtensionExpanded(int h) {
        super.onExtensionExpanded(h);
        Log.i(TAG,"onExtensionExpanded");
        if(!IsNotEditTextClick){
            if(mOnExtensionExpandedListener!=null){
                mOnExtensionExpandedListener.onExpandedListener(false);
            }
        }

//        rongExtension.isExtensionExpanded();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        if(!IsNotEditTextClick){
            if(mOnExtensionExpandedListener!=null){
                mOnExtensionExpandedListener.onExpandedListener(false);
            }
        }
    }

    public void doIsNotSendMsg(boolean flag, String hitmsg){
        if(flag){
            mMyEditText.setEnabled(false);
            mMyEmoticonToggle.setEnabled(false);
            mMyPluginToggle.setEnabled(false);
            mMyVoiceToggle.setEnabled(false);
            mMyEditText.setHint(hitmsg);
        }else{
            mMyEditText.setEnabled(true);
            mMyEmoticonToggle.setEnabled(true);
            mMyPluginToggle.setEnabled(true);
            mMyVoiceToggle.setEnabled(true);
            mMyEditText.setHint("");
        }
    }

    public void hideChatInput(boolean flag){
        if(flag){
            if(rongExtension!=null){
                rongExtension.setVisibility(View.GONE);
            }
        }else{
            if(rongExtension!=null){
                rongExtension.setVisibility(View.VISIBLE);
            }
        }
    }


    private OnExtensionExpandedListener mOnExtensionExpandedListener;

    public void setOnExtensionExpandedListener(OnExtensionExpandedListener expListener){
        mOnExtensionExpandedListener = expListener;
    }

    public interface OnExtensionExpandedListener{
        void onExpandedListener(Boolean flag);
        void onSendLoveHeart();
        void onSendLongLoveHeart();
    }
}
