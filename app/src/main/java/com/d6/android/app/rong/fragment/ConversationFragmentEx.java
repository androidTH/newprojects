package com.d6.android.app.rong.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import io.rong.imkit.RongExtension;
import io.rong.imkit.fragment.ConversationFragment;

/**
 * 会话 Fragment 继承自ConversationFragment
 * onResendItemClick: 重发按钮点击事件. 如果返回 false,走默认流程,如果返回 true,走自定义流程
 * onReadReceiptStateClick: 已读回执详情的点击事件.
 * 如果不需要重写 onResendItemClick 和 onReadReceiptStateClick ,可以不必定义此类,直接集成 ConversationFragment 就可以了
 */
public class ConversationFragmentEx extends ConversationFragment {
    private OnShowAnnounceListener onShowAnnounceListener;
    private String mTargetId = "";
    private RongExtension rongExtension;
    private EditText mMyEditText;
    private ImageView mMyEmoticonToggle;
    private ImageView mMyPluginToggle;
    private ImageView mMyVoiceToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        rongExtension = v.findViewById(io.rong.imkit.R.id.rc_extension);
        mMyEditText = rongExtension.findViewById(io.rong.imkit.R.id.rc_edit_text);
        mMyEmoticonToggle = rongExtension.findViewById(io.rong.imkit.R.id.rc_emoticon_toggle);
        mMyPluginToggle = rongExtension.findViewById(io.rong.imkit.R.id.rc_plugin_toggle);
        mMyVoiceToggle = rongExtension.findViewById(io.rong.imkit.R.id.rc_voice_toggle);

//        mMyEditText.setEnabled(false);
//        mMyEmoticonToggle.setEnabled(false);
//        mMyPluginToggle.setEnabled(false);
//        mMyVoiceToggle.setEnabled(false);
//        mMyEditText.setHint("对方同意后可开启私聊");
        return v;
    }

    @Override
    protected void initFragment(Uri uri) {
        super.initFragment(uri);
        if (uri != null) {
            mTargetId = uri.getQueryParameter("targetId");
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
    public void onPluginToggleClick(View v, ViewGroup extensionBoard) {

    }

    @Override
    public void onEmoticonToggleClick(View v, ViewGroup extensionBoard) {
    }
}
