package com.d6.android.app.rong.bean;

import com.google.gson.annotations.SerializedName;

/**
 * author : jinjiarui
 * time   : 2018/12/27
 * desc   :
 * version:
 */
public class GroupApplyMessage {
    @SerializedName("sGroupName")
    public String sGroupName;
    @SerializedName("sGroupPic")
    public String sGroupPic;
    @SerializedName("content")
    public String content;
    @SerializedName("sApplyId")
    public String sApplyId;

    public String getsGroupName() {
        return sGroupName == null ? "" : sGroupName;
    }

    public void setsGroupName(String sGroupName) {
        this.sGroupName = sGroupName;
    }

    public String getsGroupPic() {
        return sGroupPic == null ? "" : sGroupPic;
    }

    public void setsGroupPic(String sGroupPic) {
        this.sGroupPic = sGroupPic;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getsApplyId() {
        return sApplyId == null ? "" : sApplyId;
    }

    public void setsApplyId(String sApplyId) {
        this.sApplyId = sApplyId;
    }
}
