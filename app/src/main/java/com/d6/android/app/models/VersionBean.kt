package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 *     author : jinjiarui
 *     time   : 2018/10/05
 *     desc   :
 *     version:
 */
data class VersionBean(@SerializedName("sId")var sId:String) :Parcelable{
    @SerializedName("sVersion") var sVersion:String=""
    @SerializedName("iType") var iType:String=""
    @SerializedName("iUpgradetype") var iUpgradetype:Int=-1  //1、强制更新  2、提示更新
    @SerializedName("sDesc") var sDesc:String=""

    constructor(parcel: Parcel) : this(parcel.readString()) {
        sVersion = parcel.readString()
        iType = parcel.readString()
        iUpgradetype = parcel.readInt()
        sDesc = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeString(sVersion)
        parcel.writeString(iType)
        parcel.writeInt(iUpgradetype)
        parcel.writeString(sDesc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VersionBean> {
        override fun createFromParcel(parcel: Parcel): VersionBean {
            return VersionBean(parcel)
        }

        override fun newArray(size: Int): Array<VersionBean?> {
            return arrayOfNulls(size)
        }
    }

}