package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 *     author : jinjiarui
 *     time   : 2018/09/19
 *     desc   :
 *     version:
 */
data class GroupUserBean(@SerializedName("sId")var sId:String?):Parcelable {
    @SerializedName("iUserid")var iUserid:Int?=0
    @SerializedName("sGroupId")var sGroupId:String?=""
    @SerializedName("iIsOwner")var iIsOwner:Int?=0 // iIsOwner;是否群主 1、是 2、不是
    @SerializedName("iIsManager")var iIsManager:Int?=0 // iIsManager;是否群管理员 1、是 2、不是
    @SerializedName("iIsNotification") var iIsNotification:Int?=-1 //iIsNotification;是否消息提醒 1、提醒 2、不提醒
    @SerializedName("iStatus") var iStatus:Int?=-1
    @SerializedName("name")var name:String?=""
    @SerializedName("sex")var sSex:String?=""
    @SerializedName("picUrl")var picUrl:String?=""
    @SerializedName("userclassesid") var userclassesid:String?=""//24黄金 23白银 22普通 28中级

    constructor(parcel: Parcel) : this(parcel.readString()) {
        iUserid = parcel.readValue(Int::class.java.classLoader) as? Int
        sGroupId = parcel.readString()
        iIsOwner = parcel.readValue(Int::class.java.classLoader) as? Int
        iIsManager = parcel.readValue(Int::class.java.classLoader) as? Int
        iIsNotification = parcel.readValue(Int::class.java.classLoader) as? Int
        iStatus = parcel.readValue(Int::class.java.classLoader) as? Int
        name = parcel.readString()
        sSex = parcel.readString()
        picUrl = parcel.readString()
        userclassesid = parcel.readString()
    }
//    @SerializedName("nianling")var nianling:String?=""
//    @SerializedName("userclassesname") var userclassesname:String?=""

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeValue(iUserid)
        parcel.writeString(sGroupId)
        parcel.writeValue(iIsOwner)
        parcel.writeValue(iIsManager)
        parcel.writeValue(iIsNotification)
        parcel.writeValue(iStatus)
        parcel.writeString(name)
        parcel.writeString(sSex)
        parcel.writeString(picUrl)
        parcel.writeString(userclassesid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GroupUserBean> {
        override fun createFromParcel(parcel: Parcel): GroupUserBean {
            return GroupUserBean(parcel)
        }

        override fun newArray(size: Int): Array<GroupUserBean?> {
            return arrayOfNulls(size)
        }
    }
}