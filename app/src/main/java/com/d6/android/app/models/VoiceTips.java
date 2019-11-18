package com.d6.android.app.models;

/**
 * author : jinjiarui
 * time   : 2019/11/18
 * desc   :
 * version:
 */
public class VoiceTips {
    private String sSquareSignupId;
    private String sTitle;

    public String getsSquareSignupId() {
        return sSquareSignupId == null ? "" : sSquareSignupId;
    }

    public void setsSquareSignupId(String sSquareSignupId) {
        this.sSquareSignupId = sSquareSignupId;
    }

    public String getsTitle() {
        return sTitle == null ? "" : sTitle;
    }

    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }
}
