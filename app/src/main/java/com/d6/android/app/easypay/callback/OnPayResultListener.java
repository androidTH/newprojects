package com.d6.android.app.easypay.callback;

import com.d6.android.app.easypay.enums.PayWay;

/**
 * Created by michaelx on 2017/3/11.
 */

public interface OnPayResultListener {
    void onPaySuccess(PayWay payWay);

    void onPayCancel(PayWay payWay);

    void onPayFailure(PayWay payWay, int errCode);
}
