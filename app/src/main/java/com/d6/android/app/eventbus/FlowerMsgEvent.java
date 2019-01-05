package com.d6.android.app.eventbus;

/**
 * author : jinjiarui
 * time   : 2019/01/05
 * desc   :
 * version:
 */
public class FlowerMsgEvent {
    public FlowerMsgEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;

}
