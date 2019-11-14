package io.rong.callkit;

import android.view.SurfaceView;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import io.rong.calllib.IRongCallListener;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.common.RLog;

/**
 * Created by jiangecho on 2016/10/27.
 */

public class RongCallProxy implements IRongCallListener {

    private static final String TAG = "RongCallProxy";
    private IRongCallListener mCallListener;
    private Queue<CallDisconnectedInfo> mCachedCallQueue;
    private static RongCallProxy mInstance;

    private RongCallProxy() {
        mCachedCallQueue = new LinkedBlockingQueue<>();
    }

    public static synchronized RongCallProxy getInstance() {
        if (mInstance == null) {
            mInstance = new RongCallProxy();
        }
        return mInstance;
    }

    public void setCallListener(IRongCallListener listener) {
        RLog.d(TAG, "setCallListener listener = " + listener);
        this.mCallListener = listener;
//        if (listener != null) {
//            CallDisconnectedInfo callDisconnectedInfo = mCachedCallQueue.poll();
//            if (callDisconnectedInfo != null) {
//                listener.onCallDisconnected(callDisconnectedInfo.mCallSession, callDisconnectedInfo.mReason);
//            }
//        }
    }

    //电话已拨出。
    @Override
    public void onCallOutgoing(RongCallSession callSession, SurfaceView localVideo) {
        if (mCallListener != null) {
            mCallListener.onCallOutgoing(callSession, localVideo);
        }
    }

    //已建立通话。
    @Override
    public void onCallConnected(RongCallSession callSession, SurfaceView localVideo) {
        if (mCallListener != null) {
            mCallListener.onCallConnected(callSession, localVideo);
        }
    }

    //通话结束。
    @Override
    public void onCallDisconnected(RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason) {
        RLog.d(TAG, "RongCallProxy onCallDisconnected mCallListener = " + mCallListener);
        if (mCallListener != null) {
            mCallListener.onCallDisconnected(callSession, reason);
        } else {
            mCachedCallQueue.offer(new CallDisconnectedInfo(callSession, reason));
        }
    }

    //被叫端正在振铃。
    @Override
    public void onRemoteUserRinging(String userId) {
        if (mCallListener != null) {
            mCallListener.onRemoteUserRinging(userId);
        }
    }

    //被叫端加入通话。
    @Override
    public void onRemoteUserJoined(String userId, RongCallCommon.CallMediaType mediaType, int userType, SurfaceView remoteVideo) {
        if (mCallListener != null) {
            mCallListener.onRemoteUserJoined(userId, mediaType, userType, remoteVideo);
        }
    }

    //通话中的某一个参与者，邀请好友加入通话，发出邀请请求后，回调 onRemoteUserInvited。
    @Override
    public void onRemoteUserInvited(String userId, RongCallCommon.CallMediaType mediaType) {
        if (mCallListener != null) {
            mCallListener.onRemoteUserInvited(userId, mediaType);
        }
    }

    //通话中的远端参与者离开。
    @Override
    public void onRemoteUserLeft(String userId, RongCallCommon.CallDisconnectedReason reason) {
        if (mCallListener != null) {
            mCallListener.onRemoteUserLeft(userId, reason);
        }
    }

    //当通话中的某一个参与者切换通话类型，例如由 audio 切换至 video，回调 onMediaTypeChanged。
    @Override
    public void onMediaTypeChanged(String userId, RongCallCommon.CallMediaType mediaType, SurfaceView video) {
        if (mCallListener != null) {
            mCallListener.onMediaTypeChanged(userId, mediaType, video);
        }
    }

    //通话过程中，发生异常。
    @Override
    public void onError(RongCallCommon.CallErrorCode errorCode) {
        if (mCallListener != null) {
            mCallListener.onError(errorCode);
        }
    }

    //远端参与者 camera 状态发生变化时，回调 onRemoteCameraDisabled 通知状态变化。
    @Override
    public void onRemoteCameraDisabled(String userId, boolean disabled) {
        if (mCallListener != null) {
            mCallListener.onRemoteCameraDisabled(userId, disabled);
        }
    }

    @Override
    public void onWhiteBoardURL(String url) {
        if (mCallListener != null) {
            mCallListener.onWhiteBoardURL(url);
        }
    }

    @Override
    public void onNetworkSendLost(int lossRate) {
        if (mCallListener != null) {
            mCallListener.onNetworkSendLost(lossRate);
        }
    }

    @Override
    public void onNetworkReceiveLost(int lossRate) {
        if (mCallListener != null) {
            mCallListener.onNetworkReceiveLost(lossRate);
        }
    }

    @Override
    public void onNotifySharingScreen(String userId, boolean isSharing) {
        if (mCallListener != null) {
            mCallListener.onNotifySharingScreen(userId, isSharing);
        }
    }

    @Override
    public void onNotifyDegradeNormalUserToObserver(String userId) {
        if (mCallListener != null) {
            mCallListener.onNotifyDegradeNormalUserToObserver(userId);
        }
    }

    @Override
    public void onNotifyAnswerObserverRequestBecomeNormalUser(String userId, long status) {
        if (mCallListener != null) {
            mCallListener.onNotifyAnswerObserverRequestBecomeNormalUser(userId, status);
        }
    }

    @Override
    public void onNotifyUpgradeObserverToNormalUser() {
        if (mCallListener != null) {
            mCallListener.onNotifyUpgradeObserverToNormalUser();
        }
    }

    @Override
    public void onNotifyHostControlUserDevice(String userId, int dType, int isOpen) {
        if (mCallListener != null) {
            mCallListener.onNotifyHostControlUserDevice(userId, dType, isOpen);
        }
    }

    @Override
    public void onNotifyAnswerUpgradeObserverToNormalUser(String userId, SurfaceView remoteVideo) {
        if (mCallListener != null) {
            mCallListener.onNotifyAnswerUpgradeObserverToNormalUser(userId,remoteVideo);
        }
    }

    private static class CallDisconnectedInfo {
        RongCallSession mCallSession;
        RongCallCommon.CallDisconnectedReason mReason;

        public CallDisconnectedInfo(RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason) {
            this.mCallSession = callSession;
            this.mReason = reason;
        }
    }
}
