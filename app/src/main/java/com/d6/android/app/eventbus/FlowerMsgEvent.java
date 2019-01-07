package com.d6.android.app.eventbus;

import com.d6.android.app.models.Square;

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

    public FlowerMsgEvent(int count, Square mSquare) {
        this.count = count;
        this.mSquare = mSquare;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;
    private Square mSquare;

    public Square getmSquare() {
        return mSquare;
    }

    public void setmSquare(Square mSquare) {
        this.mSquare = mSquare;
    }
}
