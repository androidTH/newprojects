package com.d6.android.app.rong.bean;

import com.google.gson.annotations.SerializedName;

/**
 * author : jinjiarui
 * time   : 2019/05/04
 * desc   :
 * version:
 */
public class CommentMessage {
    private String newsId;
    private String createTime;
    private String commentUserName;
    private String suqareContent;
    private int   ids;
    private int  iStatus;
    private int  iState;
    private String userid;
    private String content;
    private String isShow;
    private String coverUrl;

    public String getNewsId() {
        return newsId == null ? "" : newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getCreateTime() {
        return createTime == null ? "" : createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCommentUserName() {
        return commentUserName == null ? "" : commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }

    public String getSuqareContent() {
        return suqareContent == null ? "" : suqareContent;
    }

    public void setSuqareContent(String suqareContent) {
        this.suqareContent = suqareContent;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public int getiStatus() {
        return iStatus;
    }

    public void setiStatus(int iStatus) {
        this.iStatus = iStatus;
    }

    public int getiState() {
        return iState;
    }

    public void setiState(int iState) {
        this.iState = iState;
    }

    public String getUserid() {
        return userid == null ? "" : userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsShow() {
        return isShow == null ? "" : isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public String getCoverUrl() {
        return coverUrl == null ? "" : coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
