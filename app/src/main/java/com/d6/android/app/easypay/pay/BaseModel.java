package com.d6.android.app.easypay.pay;

import java.io.Serializable;

/**
 * author : jinjiarui
 * time   : 2018/10/18
 * desc   :
 * version:
 */
public class BaseModel implements Serializable {
    private String resMsg;
    private int res;
    private String systime;
    private PrePayInfo obj;

    public String getResMsg() {
        return resMsg == null ? "" : resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public PrePayInfo getObj() {
        return obj;
    }

    public void setObj(PrePayInfo obj) {
        this.obj = obj;
    }

    public String getSystime() {
        return systime == null ? "" : systime;
    }

    public void setSystime(String systime) {
        this.systime = systime;
    }
}
