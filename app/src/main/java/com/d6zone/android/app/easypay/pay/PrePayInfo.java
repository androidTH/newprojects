package com.d6zone.android.app.easypay.pay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author: michaelx
 * Create: 17-3-20.
 * <p>
 * Endcode: UTF-8
 * <p>
 * Blog:http://blog.csdn.net/xiong_it | https://xiong-it.github.io
 * github:https://github.com/xiong-it
 * <p>
 * Description: 用于微信支付.// TODO 集成时请按照自身需求修改此类
 */
public class PrePayInfo implements Serializable {

    private String appid;
    private String partnerid;//商户号
    private String prepayid;//微信返回的支付交易会话ID
    @SerializedName("package")
    private String packageValue;
    private String noncestr;//随机字符串
    private String timestamp;
    private String sign;
    private String pre_pay_order_status;
    private String sOrderid;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPre_pay_order_status() {
        return pre_pay_order_status == null ? "" : pre_pay_order_status;
    }

    public void setPre_pay_order_status(String pre_pay_order_status) {
        this.pre_pay_order_status = pre_pay_order_status;
    }

    public String getsOrderid() {
        return sOrderid == null ? "" : sOrderid;
    }

    public void setsOrderid(String sOrderid) {
        this.sOrderid = sOrderid;
    }
}
