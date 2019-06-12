package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/06/11
 *     desc   :
 *     version:
 */
data class GroupBean(var sId:String?="") :Parcelable{
    var sGroupPicUrl:String?=""
    var sGroupName:String?=""
    var iIsAnonymous:Int?=0
    var iTalkUserid:Int?=0
    var iCreateUserid:Int?=0
    var lstGroupMember:String?=""

    constructor(parcel: Parcel) : this(parcel.readString()) {
        sGroupPicUrl = parcel.readString()
        sGroupName = parcel.readString()
        iIsAnonymous = parcel.readValue(Int::class.java.classLoader) as? Int
        iTalkUserid = parcel.readValue(Int::class.java.classLoader) as? Int
        iCreateUserid = parcel.readValue(Int::class.java.classLoader) as? Int
        lstGroupMember = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeString(sGroupPicUrl)
        parcel.writeString(sGroupName)
        parcel.writeValue(iIsAnonymous)
        parcel.writeValue(iTalkUserid)
        parcel.writeValue(iCreateUserid)
        parcel.writeString(lstGroupMember)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GroupBean> {
        override fun createFromParcel(parcel: Parcel): GroupBean {
            return GroupBean(parcel)
        }

        override fun newArray(size: Int): Array<GroupBean?> {
            return arrayOfNulls(size)
        }
    }

}