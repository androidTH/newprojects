package com.d6.android.app.rong.bean;

/**
 * author : jinjiarui
 * time   : 2018/12/27
 * desc   :
 * version:
 */
public class ImgTxtMessage {
     private String content;
     private String nums;

    public ImgTxtMessage(String content, String nums) {
        this.content = content;
        this.nums = nums;
    }

    public ImgTxtMessage() {
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNums() {
        return nums == null ? "" : nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }
}
