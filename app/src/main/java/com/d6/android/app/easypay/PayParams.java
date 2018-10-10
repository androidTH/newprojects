package com.d6.android.app.easypay;

import android.app.Activity;

import com.d6.android.app.easypay.enums.HttpType;
import com.d6.android.app.easypay.enums.NetworkClientType;
import com.d6.android.app.easypay.enums.PayWay;

import retrofit2.http.Query;


/**
 * Author: michaelx
 * Create: 17-3-17.
 * <p>
 * Endcode: UTF-8
 * <p>
 * Blog:http://blog.csdn.net/xiong_it | https://xiong-it.github.io
 * github:https://github.com/xiong-it
 * <p>
 * Description: 支付相关参数.
 */

public class PayParams {
    private Activity mActivity;
    private String mWechatAppID;
    private PayWay mPayWay;
    private int mGoodsPrice;
    private int iUserid;
    private int iPoint;
    private String mGoodsName;
    private String mGoodsIntroduction;
    private HttpType mHttpType = HttpType.Post;
    private NetworkClientType mNetworkClientType = NetworkClientType.OkHttp;
    private String mApiUrl;

    public Activity getActivity() {
        return mActivity;
    }

    private void setActivity(Activity activity) {
        mActivity = activity;
    }

    public String getWeChatAppID() {
        return mWechatAppID;
    }

    private void setWechatAppID(String id) {
        mWechatAppID = id;
    }

    public PayWay getPayWay() {
        return mPayWay;
    }

    private void setPayWay(PayWay mPayWay) {
        this.mPayWay = mPayWay;
    }

    public int getGoodsPrice() {
        return mGoodsPrice;
    }

    private void setGoodsPrice(int mGoodsPrice) {
        this.mGoodsPrice = mGoodsPrice;
    }

    public String getGoodsName() {
        return mGoodsName;
    }

    private void setGoodsName(String mGoodsTitle) {
        this.mGoodsName = mGoodsTitle;
    }

    public String getGoodsIntroduction() {
        return mGoodsIntroduction;
    }

    private void setGoodsIntroduction(String mGoodsIntroduction) {
        this.mGoodsIntroduction = mGoodsIntroduction;
    }

    public int getiUserid() {
        return iUserid;
    }

    public void setiUserid(int iUserid) {
        this.iUserid = iUserid;
    }

    public int getiPoint() {
        return iPoint;
    }

    public void setiPoint(int iPoint) {
        this.iPoint = iPoint;
    }

    public HttpType getHttpType() {
        return mHttpType;
    }

    private void setHttpType(HttpType mHttpType) {
        this.mHttpType = mHttpType;
    }

    public NetworkClientType getNetworkClientType() {
        return mNetworkClientType;
    }

    private void setNetworkClientType(NetworkClientType mNetworkClientType) {
        this.mNetworkClientType = mNetworkClientType;
    }

    public String getApiUrl() {
        return mApiUrl;
    }

    private void setApiUrl(String mApiUrl) {
        this.mApiUrl = mApiUrl;
    }

    public static class Builder {
        Activity mActivity;
        String wechatAppId;
        PayWay payWay;
        int goodsPrice;
        int iUserid;
        int iPoint;
        String goodsName;
        String goodsIntroduction;
        HttpType httpType;
        NetworkClientType mNetworkClientType;
        String apiUrl;


        public Builder(Activity activity) {
            mActivity = activity;
        }

        public PayParams.Builder wechatAppID(String appid) {
            wechatAppId = appid;
            return this;
        }

        public PayParams.Builder payWay(PayWay way) {
            payWay = way;
            return this;
        }

        public PayParams.Builder goodsPrice(Integer price) {
            goodsPrice = price;
            return this;
        }

        public PayParams.Builder goodsName(String name) {
            goodsName = name;
            return this;
        }

        public PayParams.Builder goodsIntroduction(String introduction) {
            goodsIntroduction = introduction;
            return this;
        }

        public PayParams.Builder httpType(HttpType type) {
            httpType = type;
            return this;
        }

        public PayParams.Builder httpClientType(NetworkClientType clientType) {
            mNetworkClientType = clientType;
            return this;
        }

        public PayParams.Builder requestBaseUrl(String url) {
            apiUrl = url;
            return this;
        }

        public PayParams.Builder UserId(int userId) {
            iUserid = userId;
            return this;
        }

        public PayParams.Builder iPoint(Integer point) {
            iPoint = point;
            return this;
        }

        public PayParams build() {
            PayParams params = new PayParams();

            params.setActivity(mActivity);
            params.setWechatAppID(wechatAppId);
            params.setPayWay(payWay);
            params.setGoodsPrice(goodsPrice);
            params.setiUserid(iUserid);
            params.setiPoint(iPoint);
            params.setGoodsName(goodsName);
            params.setGoodsIntroduction(goodsIntroduction);
            params.setHttpType(httpType);
            params.setNetworkClientType(mNetworkClientType);
            params.setApiUrl(apiUrl);

            return params;
        }

    }

}