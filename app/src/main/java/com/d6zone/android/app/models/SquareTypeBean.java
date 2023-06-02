package com.d6zone.android.app.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author : jinjiarui
 * time   : 2019/09/01
 * desc   :
 * version:
 */
public class SquareTypeBean implements Parcelable {
    private int mResId;
    private String mName;

    public SquareTypeBean(int resid,String name){
        this.mResId = resid;
        this.mName = name;
    }

    protected SquareTypeBean(Parcel in) {
        mResId = in.readInt();
        mName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mResId);
        dest.writeString(mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SquareTypeBean> CREATOR = new Creator<SquareTypeBean>() {
        @Override
        public SquareTypeBean createFromParcel(Parcel in) {
            return new SquareTypeBean(in);
        }

        @Override
        public SquareTypeBean[] newArray(int size) {
            return new SquareTypeBean[size];
        }
    };

    public int getmResId() {
        return mResId;
    }

    public void setmResId(int mResId) {
        this.mResId = mResId;
    }

    public String getmName() {
        return mName == null ? "" : mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
