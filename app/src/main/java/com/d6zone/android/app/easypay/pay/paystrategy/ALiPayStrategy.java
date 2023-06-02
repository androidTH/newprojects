package com.d6zone.android.app.easypay.pay.paystrategy;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.d6zone.android.app.easypay.EasyPay;
import com.d6zone.android.app.easypay.PayParams;
import com.d6zone.android.app.easypay.pay.ALiPayResult;
import com.d6zone.android.app.easypay.pay.BaseModel;
import com.d6zone.android.app.easypay.pay.PrePayInfo;
import com.d6zone.android.app.easypay.pay.ThreadManager;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Author: michaelx
 * Create: 17-3-13.
 * <p>
 * Endcode: UTF-8
 * <p>
 * Blog:http://blog.csdn.net/xiong_it | https://xiong-it.github.io
 * github:https://github.com/xiong-it
 * <p>
 * Description: 支付宝策略.
 */


public class ALiPayStrategy extends BasePayStrategy {
    private static final int PAY_RESULT_MSG = 0;
    private static String Out_trade_no= "";
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != PAY_RESULT_MSG) {
                return;
            }
            ThreadManager.shutdown();
            ALiPayResult result = new ALiPayResult((Map<String, String>) msg.obj);
//            Gson gson = new Gson();
//            try {
//                JSONObject jsonObject = new JSONObject(result.getResult());
//                String str = jsonObject.getString("alipay_trade_app_pay_response");
//                ResultStatus resultStatus = gson.fromJson(str, ResultStatus.class);
//                Out_trade_no = resultStatus.getTrade_no();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            switch (result.getResultStatus()) {
                case ALiPayResult.PAY_OK_STATUS:
                    mOnPayResultListener.onPayCallBack(EasyPay.COMMON_PAY_OK,Out_trade_no);
                    break;
                case ALiPayResult.PAY_CANCLE_STATUS:
                    mOnPayResultListener.onPayCallBack(EasyPay.COMMON_USER_CACELED_ERR,Out_trade_no);
                    break;

                case ALiPayResult.PAY_FAILED_STATUS:
                    mOnPayResultListener.onPayCallBack(EasyPay.COMMON_PAY_ERR,Out_trade_no);
                    break;

                case ALiPayResult.PAY_WAIT_CONFIRM_STATUS:
                    mOnPayResultListener.onPayCallBack(EasyPay.ALI_PAY_WAIT_CONFIRM_ERR,Out_trade_no);
                    break;

                case ALiPayResult.PAY_NET_ERR_STATUS:
                    mOnPayResultListener.onPayCallBack(EasyPay.ALI_PAY_NET_ERR,Out_trade_no);
                    break;

                case ALiPayResult.PAY_UNKNOWN_ERR_STATUS:
                    mOnPayResultListener.onPayCallBack(EasyPay.ALI_PAY_UNKNOW_ERR,Out_trade_no);
                    break;

                default:
                    mOnPayResultListener.onPayCallBack(EasyPay.ALI_PAY_OTHER_ERR,Out_trade_no);
                    break;
            }
            mHandler.removeCallbacksAndMessages(null);
        }
    };

    public ALiPayStrategy(PayParams params, String prePayInfo, EasyPay.PayCallBack resultListener) {
        super(params, prePayInfo, resultListener);
    }

    @Override
    public void doPay() {
        Runnable payRun = new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                BaseModel baseModel = gson.fromJson(mPrePayInfo, BaseModel.class);
                PrePayInfo payInfo = baseModel.getObj();
                if(payInfo!=null){
                    PayTask task = new PayTask(mPayParams.getActivity());
                    // TODO 请根据自身需求解析mPrePayinfo，最终的字符串值应该为一连串key=value形式
                    Out_trade_no = payInfo.getsOrderid();
                    Log.i("ALiPayStrategy",Out_trade_no+"orderId,orderInfo"+payInfo.getPrepayid());
                    Map<String, String> result = task.payV2(payInfo.getPrepayid(), true);
                    Message message = mHandler.obtainMessage();
                    message.what = PAY_RESULT_MSG;
                    message.obj = result;
                    mHandler.sendMessage(message);
                }
            }
        };
        ThreadManager.execute(payRun);
    }
}

