package com.d6zone.android.app.models;

/**
 * author : jinjiarui
 * time   : 2020/02/15
 * desc   :
 * version:
 */
public class PhoneNumEntity {
    private String mobileNum;

    @Override
    public String toString() {
        return "MyContacts{" +
                ", phone='" + mobileNum + '\'' +
                '}';
    }

    public PhoneNumEntity(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public PhoneNumEntity() {
    }

    public String getMobileNum() {
        return mobileNum == null ? "" : mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }
}
