package com.d6.android.app.rong.bean;

/**
 * author : jinjiarui
 * time   : 2018/12/27
 * desc   :
 * version:
 */
public class GroupApplyMessage {
   private String sGroupName;
   private String sGroupPic;
   private String content;
   private String sApplyId;
   private String iUserid;
   private String status;//2、加入群聊 3、踢出群聊

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

    public String getiUserid() {
        return iUserid == null ? "" : iUserid;
    }

    public void setiUserid(String iUserid) {
        this.iUserid = iUserid;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
