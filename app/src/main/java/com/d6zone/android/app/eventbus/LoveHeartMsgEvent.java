package com.d6zone.android.app.eventbus;

/**
 * author : jinjiarui
 * time   : 2019/01/05
 * desc   :
 * version:
 */
public class LoveHeartMsgEvent {
    public LoveHeartMsgEvent(String userId,int lovepoint) {
        this.mTargetId = userId;
        this.count = lovepoint;
    }

    private String mTargetId;
    private int count;

    public String getmTargetId() {
        return mTargetId == null ? "" : mTargetId;
    }

    public void setmTargetId(String mTargetId) {
        this.mTargetId = mTargetId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
