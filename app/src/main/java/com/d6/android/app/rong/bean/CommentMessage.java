package com.d6.android.app.rong.bean;

import com.google.gson.annotations.SerializedName;

/**
 * author : jinjiarui
 * time   : 2019/05/04
 * desc   :
 * version:
 */
public class CommentMessage {

    private String ids;
    private String path;
    private String newsId;
    private String picUrl;
    private String replypicUrl;
    @SerializedName("replyname")
    private String replyContent;
    private String content;
    private Long createTime;
    private String replyuserid;
    private String name;
    private String replyname;
    private String title;
    private String userid;
    private String url;
    private String urltype;

    public String getIds() {
        return ids == null ? "" : ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getPath() {
        return path == null ? "" : path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNewsId() {
        return newsId == null ? "" : newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getPicUrl() {
        return picUrl == null ? "" : picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getReplypicUrl() {
        return replypicUrl == null ? "" : replypicUrl;
    }

    public void setReplypicUrl(String replypicUrl) {
        this.replypicUrl = replypicUrl;
    }

    public String getReplyContent() {
        return replyContent == null ? "" : replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getReplyuserid() {
        return replyuserid == null ? "" : replyuserid;
    }

    public void setReplyuserid(String replyuserid) {
        this.replyuserid = replyuserid;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReplyname() {
        return replyname == null ? "" : replyname;
    }

    public void setReplyname(String replyname) {
        this.replyname = replyname;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserid() {
        return userid == null ? "" : userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrltype() {
        return urltype == null ? "" : urltype;
    }

    public void setUrltype(String urltype) {
        this.urltype = urltype;
    }
}
