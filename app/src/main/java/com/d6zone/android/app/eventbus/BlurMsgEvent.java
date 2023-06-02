package com.d6zone.android.app.eventbus;

import com.d6zone.android.app.models.Square;

/**
 * author : jinjiarui
 * time   : 2019/12/02
 * desc   :
 * version:
 */
public class BlurMsgEvent {
    private Square mSquare;

    public Square getmSquare() {
        return mSquare;
    }

    public void setmSquare(Square mSquare) {
        this.mSquare = mSquare;
    }

    public BlurMsgEvent(Square square) {
        this.mSquare = square;
    }
}
