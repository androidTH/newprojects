package com.d6zone.android.app.models.push;

/**
 * author : jinjiarui
 * time   : 2018/11/27
 * desc   :
 * version:
 */
public class PushInfo {
   private String display_type;
   private String msg_id;
   private String random_min;
   private MessageBody body;

    public String getDisplay_type() {
        return display_type == null ? "" : display_type;
    }

    public void setDisplay_type(String display_type) {
        this.display_type = display_type;
    }

    public String getMsg_id() {
        return msg_id == null ? "" : msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getRandom_min() {
        return random_min == null ? "" : random_min;
    }

    public void setRandom_min(String random_min) {
        this.random_min = random_min;
    }

    public MessageBody getBody() {
        return body;
    }

    public void setBody(MessageBody body) {
        this.body = body;
    }
}
