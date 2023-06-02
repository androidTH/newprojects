package com.d6zone.android.app.eventbus;

/**
 * author : jinjiarui
 * time   : 2019/01/05
 * desc   :
 * version:
 */
public class LikeMsgEvent {

    public LikeMsgEvent(int type) {
        this.type = type;
    }

    private int type = 0;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
