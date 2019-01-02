package com.d6.android.app.easypay.callback;

import com.d6.android.app.easypay.enums.PayWay;

/**
 * Created by michaelx on 2017/3/11.
 */

public interface OnPayResultListener {
    void onPaySuccess(PayWay payWay,String orderId);

    void onPayCancel(PayWay payWay,String orderId);

    void onPayFailure(PayWay payWay, int errCode);
}
