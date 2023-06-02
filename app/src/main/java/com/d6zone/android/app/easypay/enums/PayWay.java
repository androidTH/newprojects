package com.d6zone.android.app.easypay.enums;

/**
 * Created by michaelx on 2017/3/11.
 */

public enum PayWay {
    WechatPay(1),
    ALiPay(2),
    UPPay(3);

    int payway;
    PayWay(int way) {
        payway = way;
    }

    @Override
    public String toString() {
        return String.valueOf(payway);
    }
}
