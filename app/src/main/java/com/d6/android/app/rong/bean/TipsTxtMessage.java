package com.d6.android.app.rong.bean;

/**
 * author : jinjiarui
 * time   : 2018/12/27
 * desc   :
 * version:
 */
public class TipsTxtMessage {
    private String content;
    private String type;//0 申请 1 同意 2 拒绝

    public TipsTxtMessage(String content, String type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
