package com.d6zone.android.app.models;

/**
 * author : jinjiarui
 * time   : 2020/02/15
 * desc   :
 * version:
 */
public class PhoneBookEntity {
    private String displayName;
    private String mobileNum;

    @Override
    public String toString() {
        return "MyContacts{" +
                "name='" + displayName + '\'' +
                ", phone='" + mobileNum + '\'' +
                '}';
    }


    public PhoneBookEntity() {
    }

    public PhoneBookEntity(String displayName, String mobileNum) {
        this.displayName = displayName;
        mobileNum = mobileNum.replace("-", "");
        mobileNum = mobileNum.replace(" ", "");
        this.mobileNum = mobileNum;
    }

    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMobileNum() {
        return mobileNum == null ? "" : mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }
}
