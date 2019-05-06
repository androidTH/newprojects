package com.d6.android.app.rong.bean;

import com.d6.android.app.models.Comment;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jinjiarui
 * time   : 2019/05/04
 * desc   :
 * version:
 */
public class SquareMessage {
    private String ids;
    private String name;
    private String picUrl;
    private String title;
    private String content;
    private Long updatetime;
    private String userid;
    private String classesName;
    private String userclassesname;
    private String coverurl;
    private String city;
    private int commentcount;
    @SerializedName("upvote")
    private int appraiseCount;
    private String isupvote;
    private String sex;
    private String age;
    private int iFlowerCount;
    private int iIsSendFlower; //iIsSendFlower 大于0送过花，等于0没送过
    private String sSourceSquarePics;
    private String userclassesid;


    public String getIds() {
        return ids == null ? "" : ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl == null ? "" : picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Long updatetime) {
        this.updatetime = updatetime;
    }

    public String getUserid() {
        return userid == null ? "" : userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getClassesName() {
        return classesName == null ? "" : classesName;
    }

    public void setClassesName(String classesName) {
        this.classesName = classesName;
    }

    public String getUserclassesname() {
        return userclassesname == null ? "" : userclassesname;
    }

    public void setUserclassesname(String userclassesname) {
        this.userclassesname = userclassesname;
    }

    public String getCoverurl() {
        return coverurl == null ? "" : coverurl;
    }

    public void setCoverurl(String coverurl) {
        this.coverurl = coverurl;
    }

    public String getCity() {
        return city == null ? "" : city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
    }

    public int getAppraiseCount() {
        return appraiseCount;
    }

    public void setAppraiseCount(int appraiseCount) {
        this.appraiseCount = appraiseCount;
    }

    public String getIsupvote() {
        return isupvote == null ? "" : isupvote;
    }

    public void setIsupvote(String isupvote) {
        this.isupvote = isupvote;
    }

    public String getSex() {
        return sex == null ? "" : sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age == null ? "" : age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getiFlowerCount() {
        return iFlowerCount;
    }

    public void setiFlowerCount(int iFlowerCount) {
        this.iFlowerCount = iFlowerCount;
    }

    public int getiIsSendFlower() {
        return iIsSendFlower;
    }

    public void setiIsSendFlower(int iIsSendFlower) {
        this.iIsSendFlower = iIsSendFlower;
    }

    public String getsSourceSquarePics() {
        return sSourceSquarePics == null ? "" : sSourceSquarePics;
    }

    public void setsSourceSquarePics(String sSourceSquarePics) {
        this.sSourceSquarePics = sSourceSquarePics;
    }

    public String getUserclassesid() {
        return userclassesid == null ? "" : userclassesid;
    }

    public void setUserclassesid(String userclassesid) {
        this.userclassesid = userclassesid;
    }
}
