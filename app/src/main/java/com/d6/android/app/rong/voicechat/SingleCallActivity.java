package com.d6.android.app.rong.voicechat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d6.android.app.R;
import com.d6.android.app.activities.UserInfoActivity;
import com.d6.android.app.widget.blurry.internal.Helper;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.rongcloud.rtc.utils.FinLog;
import io.rong.callkit.BaseCallActivity;
import io.rong.callkit.CallFloatBoxView;
import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;
import io.rong.callkit.util.BluetoothUtil;
import io.rong.callkit.util.CallKitUtils;
import io.rong.callkit.util.GlideUtils;
import io.rong.callkit.util.HeadsetInfo;
import io.rong.calllib.CallUserProfile;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 *
 * voice_voip_call_minimize 之前全是Gone 现在全是visible
 */
public class SingleCallActivity extends BaseCallActivity implements Handler.Callback, View.OnClickListener {
    private static final String TAG = "VoIPSingleActivity";
    private LayoutInflater inflater;
    private RongCallSession callSession;
    private FrameLayout mLPreviewContainer;
    private FrameLayout mSPreviewContainer;
    private FrameLayout mButtonContainer;
    private LinearLayout mUserInfoContainer;
    private TextView mConnectionStateTextView;
    private Boolean isInformationShow = false;
    private SurfaceView mLocalVideo = null;
    private boolean muted = false;
    private boolean handFree = false;
    private boolean startForCheckPermissions = false;
    private boolean isReceiveLost = false;
    private boolean isSendLost = false;
    private SoundPool mSoundPool = null;

    private int EVENT_FULL_SCREEN = 1;

    private String targetId = null;
    private RongCallCommon.CallMediaType mediaType;

    @Override
    final public boolean handleMessage(Message msg) {
        if (msg.what == EVENT_FULL_SCREEN) {
            hideVideoCallInformation();
            return true;
        }
        return false;
    }

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.rong.callkit.R.layout.rc_voip_activity_single_call);
		Log.i("AudioPlugin","savedInstanceState != null="+(savedInstanceState != null)+",,,RongCallClient.getInstance() == null"+(RongCallClient.getInstance() == null));
        if (savedInstanceState != null && RongCallClient.getInstance() == null) {
            // 音视频请求权限时，用户在设置页面取消权限，导致应用重启，退出当前activity.
			Log.i("AudioPlugin","音视频请求权限时，用户在设置页面取消权限，导致应用重启，退出当前activity");
            finish();
            return;
        }
        Intent intent = getIntent();
        mLPreviewContainer = (FrameLayout) findViewById(R.id.rc_voip_call_large_preview);
        mSPreviewContainer = (FrameLayout) findViewById(R.id.rc_voip_call_small_preview);
        mButtonContainer = (FrameLayout) findViewById(R.id.rc_voip_btn);
        mUserInfoContainer = (LinearLayout) findViewById(R.id.rc_voip_user_info);
        mConnectionStateTextView = findViewById(R.id.rc_tv_connection_state);

        startForCheckPermissions = intent.getBooleanExtra("checkPermissions", false);
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));

        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
        } else if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");
            mediaType = callSession.getMediaType();
        } else {
            callSession = RongCallClient.getInstance().getCallSession();
            if (callSession != null) {
                mediaType = callSession.getMediaType();
            }
        }
        if (mediaType != null) {
            inflater = LayoutInflater.from(this);
            initView(mediaType, callAction);

            if (requestCallPermissions(mediaType, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
                setupIntent();
            }
        } else {
            RLog.w(TAG, "恢复的瞬间，对方已挂断");
            setShouldShowFloat(false);
            CallFloatBoxView.hideFloatBox();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        startForCheckPermissions = intent.getBooleanExtra("checkPermissions", false);
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));
        if (callAction == null) {
            return;
        }
        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
        } else if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");
            mediaType = callSession.getMediaType();
        } else {
            callSession = RongCallClient.getInstance().getCallSession();
            mediaType = callSession.getMediaType();
        }
        super.onNewIntent(intent);

        if (requestCallPermissions(mediaType, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
            setupIntent();
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                boolean permissionGranted;
                if (mediaType == RongCallCommon.CallMediaType.AUDIO) {
                    permissionGranted = PermissionCheckUtil.checkPermissions(this, AUDIO_CALL_PERMISSIONS);
                } else {
                    permissionGranted = PermissionCheckUtil.checkPermissions(this, VIDEO_CALL_PERMISSIONS);

                }
                if (permissionGranted) {
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                        RongCallClient.getInstance().onPermissionGranted();
                    } else {
                        setupIntent();
                    }
                } else {
                    Toast.makeText(this, getString(io.rong.callkit.R.string.rc_permission_grant_needed), Toast.LENGTH_SHORT).show();
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                        RongCallClient.getInstance().onPermissionDenied();
                    } else {
						Log.i("AudioPlugin","--onRequestPermissionsResult--finish");
                        finish();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {

            String[] permissions;
            if (mediaType == RongCallCommon.CallMediaType.AUDIO) {
                permissions = AUDIO_CALL_PERMISSIONS;
            } else {
                permissions = VIDEO_CALL_PERMISSIONS;
            }
            if (PermissionCheckUtil.checkPermissions(this, permissions)) {
                if (startForCheckPermissions) {
                    RongCallClient.getInstance().onPermissionGranted();
                } else {
                    setupIntent();
                }
            } else {
                if (startForCheckPermissions) {
                    RongCallClient.getInstance().onPermissionDenied();
                } else {
					Log.i("AudioPlugin","onActivityResult finish");
                    finish();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupIntent() {
        RongCallCommon.CallMediaType mediaType;
        Intent intent = getIntent();
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));
//        if (callAction.equals(RongCallAction.ACTION_RESUME_CALL)) {
//            return;
//        }
        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");
            mediaType = callSession.getMediaType();
            targetId = callSession.getInviterUserId();
        } else if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
            Conversation.ConversationType conversationType = Conversation.ConversationType.valueOf(intent.getStringExtra("conversationType").toUpperCase(Locale.US));
            targetId = intent.getStringExtra("targetId");

            List<String> userIds = new ArrayList<>();
            userIds.add(targetId);
            RongCallClient.getInstance().startCall(conversationType, targetId, userIds, null, mediaType, null);
        } else { // resume call
            callSession = RongCallClient.getInstance().getCallSession();
            mediaType = callSession.getMediaType();
        }

        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            handFree = false;
        } else if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
            handFree = true;
        }

        UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
        if (userInfo != null) {
            if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO) || callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
//                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.voice_voip_user_portrait);
                SimpleDraweeView userPortrait = (SimpleDraweeView) mUserInfoContainer.findViewById(R.id.voice_voip_user_portrait);
                if (userPortrait != null && userInfo.getPortraitUri() != null) {
                    userPortrait.setImageURI(userInfo.getPortraitUri().toString());
                    userPortrait.setOnClickListener(this);
//                    userPortrait.setResource(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
                }
                userPortrait.setOnClickListener(this);
                TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.voice_voip_user_name);
                userName.setText(userInfo.getName());

                SimpleDraweeView iv_icoming_backgroud=(SimpleDraweeView)mUserInfoContainer.findViewById(R.id.iv_icoming_backgroud);
                iv_icoming_backgroud.setVisibility(View.VISIBLE);
                Log.i("SinleCallActivity","头像"+userInfo.getPortraitUri());
                Helper.showUrlBlur(iv_icoming_backgroud,userInfo.getPortraitUri().toString(),8,30);
//                GlideUtils.showBlurTransformation(SingleCallActivity.this, iv_icoming_backgroud, null != userInfo ? userInfo.getPortraitUri() : null);
            }
        }
//        if(callAction.equals(RongCallAction.ACTION_INCOMING_CALL) && userInfo!=null){
//            ImageView iv_icoming_backgroud=(ImageView)mUserInfoContainer.findViewById(R.id.iv_icoming_backgroud);
//            iv_icoming_backgroud.setVisibility(View.VISIBLE);
//            Log.i("SinleCallActivity","头像"+userInfo.getPortraitUri());
//            GlideUtils.showBlurTransformation(SingleCallActivity.this, iv_icoming_backgroud, null != userInfo ? userInfo.getPortraitUri() : null);
//        }

        Log.i("SinleCallActivity","------");
        createPowerManager();
        createPickupDetector();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FinLog.v("AudioPlugin","---single activity onResume---");
        if (pickupDetector != null && mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            pickupDetector.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pickupDetector != null) {
            pickupDetector.unRegister();
        }
    }

    private void initView(RongCallCommon.CallMediaType mediaType, RongCallAction callAction) {
        RelativeLayout buttonLayout = (RelativeLayout) inflater.inflate(io.rong.callkit.R.layout.rc_voip_call_bottom_connected_button_layout, null);
        RelativeLayout userInfoLayout = null;
        Log.i("SinleCallActivity","mediaType="+mediaType);
        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)|| callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            userInfoLayout = (RelativeLayout) inflater.inflate(R.layout.voicechat_voip_audio_call_user_info_incoming, null);
            userInfoLayout.findViewById(R.id.iv_large_preview_Mask).setVisibility(View.VISIBLE);
            userInfoLayout.findViewById(R.id.voice_voip_call_minimize).setVisibility(View.GONE);
            Log.i("SinleCallActivity","iv_large_preview_Mask");
        } else {
            //单人视频 or 拨打 界面
            userInfoLayout = (RelativeLayout) inflater.inflate(io.rong.callkit.R.layout.rc_voip_audio_call_user_info, null);
            TextView callInfo = (TextView) userInfoLayout.findViewById(io.rong.callkit.R.id.rc_voip_call_remind_info);
            CallKitUtils.textViewShadowLayer(callInfo, SingleCallActivity.this);
            userInfoLayout.findViewById(R.id.rc_voip_call_minimize).setVisibility(View.GONE);
        }

        if (callAction.equals(RongCallAction.ACTION_RESUME_CALL) && CallKitUtils.isDial) {
            try {
                ImageView button = buttonLayout.findViewById(io.rong.callkit.R.id.rc_voip_call_mute_btn);
                button.setEnabled(false);
                userInfoLayout.findViewById(R.id.voice_voip_call_minimize).setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            RelativeLayout layout = buttonLayout.findViewById(io.rong.callkit.R.id.rc_voip_call_mute);
            layout.setVisibility(View.INVISIBLE);
            ImageView button = buttonLayout.findViewById(io.rong.callkit.R.id.rc_voip_call_mute_btn);
            button.setEnabled(false);
            buttonLayout.findViewById(io.rong.callkit.R.id.rc_voip_handfree).setVisibility(View.INVISIBLE);
        }

        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            findViewById(io.rong.callkit.R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(io.rong.callkit.R.color.rc_voip_background_color));
            mLPreviewContainer.setVisibility(View.GONE);
            mSPreviewContainer.setVisibility(View.GONE);

            //别人发起语音过来
            if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
                buttonLayout = (RelativeLayout) inflater.inflate(io.rong.callkit.R.layout.rc_voip_call_bottom_incoming_button_layout, null);
                ImageView iv_answerBtn = (ImageView) buttonLayout.findViewById(io.rong.callkit.R.id.rc_voip_call_answer_btn);
                iv_answerBtn.setBackground(CallKitUtils.BackgroundDrawable(io.rong.callkit.R.drawable.rc_voip_audio_answer_selector, SingleCallActivity.this));

                TextView callInfo = (TextView) userInfoLayout.findViewById(R.id.voice_voip_call_remind_info);
//                CallKitUtils.textViewShadowLayer(callInfo, SingleCallActivity.this);
                callInfo.setText(io.rong.callkit.R.string.rc_voip_audio_call_inviting);
                onIncomingCallRinging();
                Log.i("SinleCallActivity","incomingcall");
            }
        } else if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)){
            if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
                findViewById(io.rong.callkit.R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(io.rong.callkit.R.color.rc_voip_background_color));
                buttonLayout = (RelativeLayout) inflater.inflate(io.rong.callkit.R.layout.rc_voip_call_bottom_incoming_button_layout, null);
                ImageView iv_answerBtn = (ImageView) buttonLayout.findViewById(io.rong.callkit.R.id.rc_voip_call_answer_btn);
                iv_answerBtn.setBackground(CallKitUtils.BackgroundDrawable(io.rong.callkit.R.drawable.rc_voip_vedio_answer_selector_new, SingleCallActivity.this));

                TextView callInfo = (TextView) userInfoLayout.findViewById(R.id.voice_voip_call_remind_info);
                CallKitUtils.textViewShadowLayer(callInfo, SingleCallActivity.this);
                callInfo.setText(io.rong.callkit.R.string.rc_voip_video_call_inviting);
                onIncomingCallRinging();
            }
        }
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(buttonLayout);
        mUserInfoContainer.removeAllViews();
        mUserInfoContainer.addView(userInfoLayout);

        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            regisHeadsetPlugReceiver();
            if(BluetoothUtil.hasBluetoothA2dpConnected() || BluetoothUtil.isWiredHeadsetOn(SingleCallActivity.this)){
                HeadsetInfo headsetInfo=new HeadsetInfo(true,HeadsetInfo.HeadsetType.BluetoothA2dp);
                onEventMainThread(headsetInfo);
            }
        }
    }


    @Override
    public void onCallOutgoing(RongCallSession callSession, SurfaceView localVideo) {
        super.onCallOutgoing(callSession, localVideo);
        this.callSession = callSession;
        try {
            UserInfo InviterUserIdInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
            UserInfo SelfUserInfo = RongContext.getInstance().getUserInfoFromCache(callSession.getSelfUserId());
            if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)) {
                mLPreviewContainer.setVisibility(View.VISIBLE);
                localVideo.setTag(callSession.getSelfUserId());
                mLPreviewContainer.addView(localVideo);
                if (null!=SelfUserInfo && null!=SelfUserInfo.getName()) {
                    //单人视频
                    TextView callkit_voip_user_name_signleVideo = (TextView) mUserInfoContainer.findViewById(io.rong.callkit.R.id.callkit_voip_user_name_signleVideo);
                    callkit_voip_user_name_signleVideo.setText(SelfUserInfo.getName());
                }
            }else if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){
                if (null!=InviterUserIdInfo && null!=InviterUserIdInfo.getPortraitUri()) {
                    SimpleDraweeView iv_icoming_backgroud = mUserInfoContainer.findViewById(R.id.iv_icoming_backgroud);
                    iv_icoming_backgroud.setVisibility(View.VISIBLE);
//                    GlideUtils.showBlurTransformation(SingleCallActivity.this, iv_icoming_backgroud, null != InviterUserIdInfo ? InviterUserIdInfo.getPortraitUri() : null);
                    Helper.showUrlBlur(iv_icoming_backgroud,InviterUserIdInfo.getPortraitUri().toString(),8,30);
                    mUserInfoContainer.findViewById(R.id.iv_large_preview_Mask).setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onOutgoingCallRinging();

        regisHeadsetPlugReceiver();
        if(BluetoothUtil.hasBluetoothA2dpConnected() || BluetoothUtil.isWiredHeadsetOn(this)){
            HeadsetInfo headsetInfo=new HeadsetInfo(true,HeadsetInfo.HeadsetType.BluetoothA2dp);
            onEventMainThread(headsetInfo);
        }
        Log.i("SinleCallActivity","onCallOutgoing");
    }

    @Override
    public void onCallConnected(RongCallSession callSession, SurfaceView localVideo) {
        super.onCallConnected(callSession, localVideo);
        this.callSession = callSession;
        FinLog.v(TAG,"onCallConnected----mediaType="+callSession.getMediaType().getValue());
        TextView tv_rc_voip_call_remind_info=null;
        if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
            findViewById(R.id.voice_voip_call_minimize).setVisibility(View.GONE);
            tv_rc_voip_call_remind_info = (TextView) mUserInfoContainer.findViewById(R.id.voice_voip_call_remind_info);
            ImageButton ib_voicechat_loveheart= mUserInfoContainer.findViewById(R.id.ib_voicechat_loveheart);
            ib_voicechat_loveheart.setVisibility(View.VISIBLE);
            ib_voicechat_loveheart.setOnClickListener(this);
            //底部按钮
            RelativeLayout btnLayout = (RelativeLayout) inflater.inflate(io.rong.callkit.R.layout.rc_voip_call_bottom_connected_button_layout, null);
            ImageView button = btnLayout.findViewById(io.rong.callkit.R.id.rc_voip_call_mute_btn);
            button.setEnabled(true);
            btnLayout.findViewById(R.id.rc_voip_call_mute).setVisibility(View.INVISIBLE);
            btnLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.INVISIBLE);
            mButtonContainer.removeAllViews();
            mButtonContainer.addView(btnLayout);
        } else {
            // 二人视频通话接通后 mUserInfoContainer 中更换为无头像的布局
            mUserInfoContainer.removeAllViews();
            inflater.inflate(io.rong.callkit.R.layout.rc_voip_video_call_user_info, mUserInfoContainer);
            UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
            if (userInfo != null) {
                TextView userName = mUserInfoContainer.findViewById(io.rong.callkit.R.id.rc_voip_user_name);
                userName.setText(userInfo.getName());
//                userName.setShadowLayer(16F, 0F, 2F, getResources().getColor(R.color.rc_voip_reminder_shadow));//callkit_shadowcolor
                CallKitUtils.textViewShadowLayer(userName, SingleCallActivity.this);
            }
            mLocalVideo = localVideo;
            mLocalVideo.setTag(callSession.getSelfUserId());
            tv_rc_voip_call_remind_info = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_call_remind_info);
        }

        CallKitUtils.textViewShadowLayer(tv_rc_voip_call_remind_info, SingleCallActivity.this);
        tv_rc_voip_call_remind_info.setVisibility(View.GONE);

        TextView remindInfo = null;
        if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
            remindInfo = mUserInfoContainer.findViewById(R.id.tv_setupTime);
        } else {
            remindInfo = mUserInfoContainer.findViewById(io.rong.callkit.R.id.tv_setupTime_video);
        }
        if(remindInfo==null){
            remindInfo=tv_rc_voip_call_remind_info;
        }
        setupTime(remindInfo);

        RongCallClient.getInstance().setEnableLocalAudio(!muted);
        View muteV = mButtonContainer.findViewById(io.rong.callkit.R.id.rc_voip_call_mute);
        if (muteV != null) {
            muteV.setSelected(muted);
        }

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn() || BluetoothUtil.hasBluetoothA2dpConnected()) {
            handFree=false;
            RongCallClient.getInstance().setEnableSpeakerphone(false);
            ImageView handFreeV=null;
            if(null!=mButtonContainer){
                handFreeV = mButtonContainer.findViewById(io.rong.callkit.R.id.rc_voip_handfree_btn);
            }
            if (handFreeV != null) {
                handFreeV.setSelected(false);
                handFreeV.setEnabled(false);
                handFreeV.setClickable(false);
            }
        } else {
            RongCallClient.getInstance().setEnableSpeakerphone(handFree);
            View handFreeV = mButtonContainer.findViewById(io.rong.callkit.R.id.rc_voip_handfree);
            if (handFreeV != null) {
                handFreeV.setSelected(handFree);
            }
        }
        stopRing();
    }

    @Override
    protected void onDestroy() {
        stopRing();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.setReferenceCounted(false);
            wakeLock.release();
        }
        super.onDestroy();
    }

    @Override
    public void onRemoteUserJoined(final String userId, RongCallCommon.CallMediaType mediaType, int userType, SurfaceView remoteVideo) {
        super.onRemoteUserJoined(userId, mediaType, userType, remoteVideo);
        FinLog.v(TAG,"onRemoteUserJoined userID="+userId+",mediaType="+mediaType.getValue()+",userType="+userId);
        if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
            findViewById(io.rong.callkit.R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mLPreviewContainer.setVisibility(View.VISIBLE);
            mLPreviewContainer.removeAllViews();
            remoteVideo.setTag(userId);

            FinLog.v(TAG,"onRemoteUserJoined mLPreviewContainer.addView(remoteVideo)");
            mLPreviewContainer.addView(remoteVideo);
            mLPreviewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInformationShow) {
                        hideVideoCallInformation();
                    } else {
                        showVideoCallInformation();
                        handler.sendEmptyMessageDelayed(EVENT_FULL_SCREEN, 5 * 1000);
                    }
                }
            });
            mSPreviewContainer.setVisibility(View.VISIBLE);
            mSPreviewContainer.removeAllViews();
            FinLog.v(TAG,"onRemoteUserJoined mLocalVideo != null="+(mLocalVideo != null));
            if (mLocalVideo != null) {
                mLocalVideo.setZOrderMediaOverlay(true);
                mLocalVideo.setZOrderOnTop(true);
                mSPreviewContainer.addView(mLocalVideo);
            }
            /** 小窗口点击事件 **/
            mSPreviewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SurfaceView fromView = (SurfaceView) mSPreviewContainer.getChildAt(0);
                        SurfaceView toView = (SurfaceView) mLPreviewContainer.getChildAt(0);

                        mLPreviewContainer.removeAllViews();
                        mSPreviewContainer.removeAllViews();
                        fromView.setZOrderOnTop(false);
                        fromView.setZOrderMediaOverlay(false);
                        mLPreviewContainer.addView(fromView);
                        toView.setZOrderOnTop(true);
                        toView.setZOrderMediaOverlay(true);
                        mSPreviewContainer.addView(toView);
                        if(null!= fromView.getTag() && !TextUtils.isEmpty(fromView.getTag().toString())){
                            UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(fromView.getTag().toString());
                            TextView userName = (TextView) mUserInfoContainer.findViewById(io.rong.callkit.R.id.rc_voip_user_name);
                            userName.setText(userInfo.getName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mButtonContainer.setVisibility(View.GONE);
            mUserInfoContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 当通话中的某一个参与者切换通话类型，例如由 audio 切换至 video，回调 onMediaTypeChanged。
     *
     * @param userId    切换者的 userId。
     * @param mediaType 切换者，切换后的媒体类型。
     * @param video     切换着，切换后的 camera 信息，如果由 video 切换至 audio，则为 null。
     */
    @Override
    public void onMediaTypeChanged(String userId, RongCallCommon.CallMediaType mediaType, SurfaceView video) {
        if (callSession.getSelfUserId().equals(userId)) {
            showShortToast(getString(io.rong.callkit.R.string.rc_voip_switched_to_audio));
        } else {
            if (callSession.getMediaType() != RongCallCommon.CallMediaType.AUDIO) {
                RongCallClient.getInstance().changeCallMediaType(RongCallCommon.CallMediaType.AUDIO);
                callSession.setMediaType(RongCallCommon.CallMediaType.AUDIO);
                showShortToast(getString(io.rong.callkit.R.string.rc_voip_remote_switched_to_audio));
            }
        }
        initAudioCallView();
        handler.removeMessages(EVENT_FULL_SCREEN);
        mButtonContainer.findViewById(io.rong.callkit.R.id.rc_voip_call_mute).setSelected(muted);
    }

    /** 视频转语音 **/
    private void initAudioCallView() {
        mLPreviewContainer.removeAllViews();
        mLPreviewContainer.setVisibility(View.GONE);
        mSPreviewContainer.removeAllViews();
        mSPreviewContainer.setVisibility(View.GONE);
        //显示全屏底色
        findViewById(io.rong.callkit.R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(io.rong.callkit.R.color.rc_voip_background_color));
        findViewById(io.rong.callkit.R.id.rc_voip_audio_chat).setVisibility(View.GONE);//隐藏语音聊天按钮

        View userInfoView = inflater.inflate(R.layout.voicechat_voip_audio_call_user_info_incoming, null);
        TextView tv_rc_voip_call_remind_info = (TextView) userInfoView.findViewById(R.id.voice_voip_call_remind_info);
        tv_rc_voip_call_remind_info.setVisibility(View.GONE);

        TextView timeView = userInfoView.findViewById(R.id.tv_setupTime);
        setupTime(timeView);

        mUserInfoContainer.removeAllViews();
        mUserInfoContainer.addView(userInfoView);
        UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
        if (userInfo != null) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.voice_voip_user_name);
            userName.setText(userInfo.getName());
            if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
//                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.voice_voip_user_portrait);
                SimpleDraweeView userPortrait = (SimpleDraweeView) mUserInfoContainer.findViewById(R.id.voice_voip_user_portrait);
                if (userPortrait != null) {
                    userPortrait.setImageURI(userInfo.getPortraitUri().toString());
                    userPortrait.setOnClickListener(this);
//                    userPortrait.setAvatar(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
                }
            } else {//单人视频接听layout
                ImageView iv_large_preview = mUserInfoContainer.findViewById(R.id.iv_large_preview);
                iv_large_preview.setVisibility(View.VISIBLE);
                GlideUtils.showBlurTransformation(SingleCallActivity.this, iv_large_preview, null != userInfo ? userInfo.getPortraitUri() : null);
            }
        }
        mUserInfoContainer.setVisibility(View.VISIBLE);
        mUserInfoContainer.findViewById(R.id.voice_voip_call_minimize).setVisibility(View.GONE);

        View button = inflater.inflate(io.rong.callkit.R.layout.rc_voip_call_bottom_connected_button_layout, null);
        button.findViewById(R.id.rc_voip_call_mute).setVisibility(View.INVISIBLE);
        button.findViewById(R.id.rc_voip_handfree).setVisibility(View.INVISIBLE);
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(button);
        mButtonContainer.setVisibility(View.VISIBLE);
        // 视频转音频时默认不开启免提
        handFree = false;
        RongCallClient.getInstance().setEnableSpeakerphone(false);
        View handFreeV = mButtonContainer.findViewById(io.rong.callkit.R.id.rc_voip_handfree);
        handFreeV.setSelected(handFree);

        ImageView iv_large_preview_Mask=(ImageView)userInfoView.findViewById(io.rong.callkit.R.id.iv_large_preview_Mask);
        iv_large_preview_Mask.setVisibility(View.VISIBLE);

        /**视频切换成语音 全是语音界面的ui**/
        if(null!=userInfo && callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){
            SimpleDraweeView iv_icoming_backgroud = mUserInfoContainer.findViewById(R.id.iv_icoming_backgroud);
//            GlideUtils.showBlurTransformation(SingleCallActivity.this, iv_large_preview, null != userInfo ? userInfo.getPortraitUri() : null);
            Helper.showUrlBlur(iv_icoming_backgroud,userInfo.getPortraitUri().toString(),8,30);
            iv_icoming_backgroud.setVisibility(View.VISIBLE);

            ImageButton ib_voicechat_loveheart= mUserInfoContainer.findViewById(R.id.ib_voicechat_loveheart);
            ib_voicechat_loveheart.setVisibility(View.VISIBLE);
            ib_voicechat_loveheart.setOnClickListener(this);
        }

        if (pickupDetector != null) {
            pickupDetector.register(this);
        }
    }

    public void onHangupBtnClick(View view) {
        onHangupVoiceChat();
        FinLog.e(TAG, "_挂断单人视频出错 callSession="+(callSession == null)+",isFinishing="+isFinishing);
    }

    public void onReceiveBtnClick(View view) {
        RongCallSession session = RongCallClient.getInstance().getCallSession();
        if (session == null || isFinishing) {
            FinLog.e(TAG, "_接听单人视频出错 callSession="+(callSession == null)+",isFinishing="+isFinishing);
            finish();
            return;
        }
        RongCallClient.getInstance().acceptCall(session.getCallId());
    }

    public void hideVideoCallInformation() {
        isInformationShow = false;
        mUserInfoContainer.setVisibility(View.GONE);
        mButtonContainer.setVisibility(View.GONE);
        findViewById(io.rong.callkit.R.id.rc_voip_audio_chat).setVisibility(View.GONE);
    }

    public void showVideoCallInformation() {
        isInformationShow = true;
        mUserInfoContainer.setVisibility(View.VISIBLE);

        mUserInfoContainer.findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);
        mButtonContainer.setVisibility(View.VISIBLE);
        RelativeLayout btnLayout = (RelativeLayout) inflater.inflate(io.rong.callkit.R.layout.rc_voip_call_bottom_connected_button_layout, null);
        btnLayout.findViewById(R.id.rc_voip_call_mute).setSelected(muted);
        btnLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.GONE);
        btnLayout.findViewById(R.id.rc_voip_camera).setVisibility(View.VISIBLE);
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(btnLayout);
        View view = findViewById(io.rong.callkit.R.id.rc_voip_audio_chat);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongCallClient.getInstance().changeCallMediaType(RongCallCommon.CallMediaType.AUDIO);
                callSession.setMediaType(RongCallCommon.CallMediaType.AUDIO);
                initAudioCallView();
            }
        });
    }

    public void onHandFreeButtonClick(View view) {
        CallKitUtils.speakerphoneState=!view.isSelected();
        RongCallClient.getInstance().setEnableSpeakerphone(!view.isSelected());//true:打开免提 false:关闭免提
        view.setSelected(!view.isSelected());
        handFree = view.isSelected();
    }

    public void onMuteButtonClick(View view) {
        RongCallClient.getInstance().setEnableLocalAudio(view.isSelected());
        view.setSelected(!view.isSelected());
        muted = view.isSelected();
    }

    @Override
    public void onCallDisconnected(RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason) {
        super.onCallDisconnected(callSession, reason);

        String senderId;
        String extra = "";

        isFinishing = true;
        if (callSession == null) {
            RLog.e(TAG, "onCallDisconnected. callSession is null!");
            postRunnableDelay(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
            return;
        }
        senderId = callSession.getInviterUserId();
        switch (reason) {
            case HANGUP:
            case REMOTE_HANGUP:
                long time = getTime();
                if (time >= 3600) {
                    extra = String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60));
                } else {
                    extra = String.format("%02d:%02d", (time % 3600) / 60, (time % 60));
                }
                break;
        }
        cancelTime();

//        if (!TextUtils.isEmpty(senderId)) {
//            CallSTerminateMessage message = new CallSTerminateMessage();
//            message.setReason(reason);
//            message.setMediaType(callSession.getMediaType());
//            message.setExtra(extra);
//            long serverTime = System.currentTimeMillis() - RongIMClient.getInstance().getDeltaTime();
//            if (senderId.equals(callSession.getSelfUserId())) {
//                message.setDirection("MO");
//                RongIM.getInstance().insertOutgoingMessage(Conversation.ConversationType.PRIVATE, callSession.getTargetId(), io.rong.imlib.model.Message.SentStatus.SENT, message, serverTime, null);
//            } else {
//                message.setDirection("MT");
//                io.rong.imlib.model.Message.ReceivedStatus receivedStatus = new io.rong.imlib.model.Message.ReceivedStatus(0);
//                receivedStatus.setRead();
//                RongIM.getInstance().insertIncomingMessage(Conversation.ConversationType.PRIVATE, callSession.getTargetId(), senderId, receivedStatus, message, serverTime, null);
//            }
//        }
        postRunnableDelay(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    public void onNetworkSendLost(int lossRate) {
        isSendLost = lossRate > 30;
        refreshConnectionState();
    }

    @Override
    public void onNetworkReceiveLost(int lossRate) {
        isReceiveLost = lossRate > 30;
        refreshConnectionState();
    }

    private Runnable mCheckConnectionStableTask = new Runnable() {
        @Override
        public void run() {
            boolean isConnectionStable = !isSendLost && !isReceiveLost;
            if (isConnectionStable) {
                mConnectionStateTextView.setVisibility(View.GONE);
            }
        }
    };

    private void refreshConnectionState() {
        if (isSendLost || isReceiveLost) {
            if (mConnectionStateTextView.getVisibility() == View.GONE) {
                mConnectionStateTextView.setVisibility(View.VISIBLE);
                if (mSoundPool != null) {
                    mSoundPool.release();
                }
                mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                mSoundPool.load(this, io.rong.callkit.R.raw.voip_network_error_sound, 0);
                mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        soundPool.play(sampleId, 1F, 1F, 0, 0, 1F);
                    }
                });
            }
            mConnectionStateTextView.removeCallbacks(mCheckConnectionStableTask);
            mConnectionStateTextView.postDelayed(mCheckConnectionStableTask, 3000);
        }
    }

    @Override
    public void onRestoreFloatBox(Bundle bundle) {
        super.onRestoreFloatBox(bundle);
        if (bundle == null)
            return;
        muted = bundle.getBoolean("muted");
        handFree = bundle.getBoolean("handFree");

        setShouldShowFloat(true);
        callSession = RongCallClient.getInstance().getCallSession();
        if (callSession == null) {
            setShouldShowFloat(false);
            finish();
            return;
        }
        RongCallCommon.CallMediaType mediaType = callSession.getMediaType();
        RongCallAction callAction = RongCallAction.valueOf(getIntent().getStringExtra("callAction"));
        inflater = LayoutInflater.from(this);
        initView(mediaType, callAction);
        targetId = callSession.getTargetId();
        UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
        if (userInfo != null) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.voice_voip_user_name);
            userName.setText(userInfo.getName());
            if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
//                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.voice_voip_user_portrait);
                SimpleDraweeView userPortrait = (SimpleDraweeView) mUserInfoContainer.findViewById(R.id.voice_voip_user_portrait);
                if (userPortrait != null) {
                    userPortrait.setImageURI(userInfo.getPortraitUri().toString());
                    userPortrait.setOnClickListener(this);
//                    userPortrait.setAvatar(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
                }
            } else if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)){
                if(null!=callAction && callAction.equals(RongCallAction.ACTION_INCOMING_CALL)){
                    ImageView iv_large_preview = mUserInfoContainer.findViewById(R.id.iv_large_preview);
                    iv_large_preview.setVisibility(View.VISIBLE);
                    GlideUtils.showBlurTransformation(SingleCallActivity.this, iv_large_preview, null != userInfo ? userInfo.getPortraitUri() : null);
                }
            }
        }
        SurfaceView localVideo = null;
        SurfaceView remoteVideo = null;
        String remoteUserId = null;
        for (CallUserProfile profile : callSession.getParticipantProfileList()) {
            if (profile.getUserId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                localVideo = profile.getVideoView();
            } else {
                remoteVideo = profile.getVideoView();
                remoteUserId = profile.getUserId();
            }
        }
        if (localVideo != null && localVideo.getParent() != null) {
            ((ViewGroup) localVideo.getParent()).removeView(localVideo);
        }
        onCallOutgoing(callSession, localVideo);
        if(!(boolean)bundle.get("isDial")){
            onCallConnected(callSession, localVideo);
        }
        if (remoteVideo != null && remoteVideo.getParent() != null) {
            ((ViewGroup) remoteVideo.getParent()).removeView(remoteVideo);
            onRemoteUserJoined(remoteUserId, mediaType, 1, remoteVideo);
        }
    }

    @Override
    public String onSaveFloatBoxState(Bundle bundle) {
        super.onSaveFloatBoxState(bundle);
        callSession = RongCallClient.getInstance().getCallSession();
        if (callSession == null) {
            return null;
        }
        bundle.putBoolean("muted", muted);
        bundle.putBoolean("handFree", handFree);
        bundle.putInt("mediaType", callSession.getMediaType().getValue());

        return getIntent().getAction();
    }

    public void onMinimizeClick(View view) {
        super.onMinimizeClick(view);
    }

    public void onSwitchCameraClick(View view) {
        RongCallClient.getInstance().switchCamera();
    }

    @Override
    public void onBackPressed() {
        return;
//        List<CallUserProfile> participantProfiles = callSession.getParticipantProfileList();
//        RongCallCommon.CallStatus callStatus = null;
//        for (CallUserProfile item : participantProfiles) {
//            if (item.getUserId().equals(callSession.getSelfUserId())) {
//                callStatus = item.getCallStatus();
//                break;
//            }
//        }
//        if (callStatus != null && callStatus.equals(RongCallCommon.CallStatus.CONNECTED)) {
//            super.onBackPressed();
//        } else {
//            RongCallClient.getInstance().hangUpCall(callSession.getCallId());
//        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        if (isFinishing()) {
            return;
        }
        if (targetId != null && targetId.equals(userInfo.getUserId())) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.voice_voip_user_name);
            if (userInfo.getName() != null)
                userName.setText(userInfo.getName());
                SimpleDraweeView userPortrait =  mUserInfoContainer.findViewById(R.id.voice_voip_user_portrait);
                if (userPortrait != null && userInfo.getPortraitUri() != null) {
                    userPortrait.setImageURI(userInfo.getPortraitUri().toString());
                    userPortrait.setOnClickListener(this);
                }
//            AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
//            if (userPortrait != null && userInfo.getPortraitUri() != null) {
//                userPortrait.setResource(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
//            }
//            ImageView iv_large_preview=mUserInfoContainer.findViewById(R.id.iv_large_preview);
//            GlideUtils.blurTransformation(SingleCallActivity.this,iv_large_preview,null!=userInfo?userInfo.getPortraitUri():null);
        }
    }

//    @Override
//    public void showOnGoingNotification() {
//        Intent intent = new Intent(getIntent().getAction());
//        Bundle bundle = new Bundle();
//        onSaveFloatBoxState(bundle);
//        intent.putExtra("floatbox", bundle);
//        intent.putExtra("callAction", RongCallAction.ACTION_RESUME_CALL.getName());
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationUtil.showNotification(this, "todo", "coontent", pendingIntent, CALL_NOTIFICATION_ID);
//    }

    public void onEventMainThread(HeadsetInfo headsetInfo) {
        if(headsetInfo==null || !BluetoothUtil.isForground(SingleCallActivity.this)){
            FinLog.v("bugtags","SingleCallActivity 不在前台！");
            return;
        }
        FinLog.v("bugtags","Insert="+headsetInfo.isInsert()+",headsetInfo.getType="+headsetInfo.getType().getValue());
        try {
            if(headsetInfo.isInsert()){
                RongCallClient.getInstance().setEnableSpeakerphone(false);
                ImageView handFreeV=null;
                if(null!=mButtonContainer){
                    handFreeV = mButtonContainer.findViewById(io.rong.callkit.R.id.rc_voip_handfree_btn);
                }
                if (handFreeV != null) {
                    handFreeV.setSelected(false);
                    handFreeV.setEnabled(false);
                    handFreeV.setClickable(false);
                }
                if(headsetInfo.getType()==HeadsetInfo.HeadsetType.BluetoothA2dp){
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    am.startBluetoothSco();
                    am.setBluetoothScoOn(true);
                    am.setSpeakerphoneOn(false);
                }
            }else{
                if(headsetInfo.getType()==HeadsetInfo.HeadsetType.WiredHeadset &&
                        BluetoothUtil.hasBluetoothA2dpConnected()){
                    return;
                }
                RongCallClient.getInstance().setEnableSpeakerphone(false);
                ImageView handFreeV = mButtonContainer.findViewById(io.rong.callkit.R.id.rc_voip_handfree_btn);
                if (handFreeV != null) {
                    handFreeV.setSelected(false);
                    handFreeV.setEnabled(true);
                    handFreeV.setClickable(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FinLog.v("bugtags","SingleCallActivity->onEventMainThread Error="+e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.voice_voip_user_portrait){
            Intent intent = new Intent(SingleCallActivity.this, UserInfoActivity.class);
            intent.putExtra("id",targetId);
            startActivity(intent);
        }else if(v.getId()==R.id.ib_voicechat_loveheart){

        }
    }
}
