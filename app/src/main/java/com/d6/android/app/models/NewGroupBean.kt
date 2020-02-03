package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/06/11
 *     desc   :
 *     version:
 */
data class NewGroupBean(var sId:String?="") :Parcelable{
    var sGroupPic:String?=""
    var sGroupName:String?=""
    var iGroupNum:Int?=-1
    var sGroupDesc:String?=""
    var iGroupStatus:Int?=-1
    var iInGroup:Int?=-1
    var iIsApply:Int?=-1
    var iIsOwner:Int?=-1
    var iIsManager:Int?=-1

    constructor(parcel: Parcel) : this(parcel.readString()) {
        sGroupPic = parcel.readString()
        sGroupName = parcel.readString()
        iGroupNum = parcel.readValue(Int::class.java.classLoader) as? Int
        sGroupDesc = parcel.readString()
        iGroupStatus = parcel.readValue(Int::class.java.classLoader) as? Int
        iInGroup = parcel.readValue(Int::class.java.classLoader) as? Int
        iIsApply = parcel.readValue(Int::class.java.classLoader) as? Int
        iIsOwner = parcel.readValue(Int::class.java.classLoader) as? Int
        iIsManager = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeString(sGroupPic)
        parcel.writeString(sGroupName)
        parcel.writeValue(iGroupNum)
        parcel.writeString(sGroupDesc)
        parcel.writeValue(iGroupStatus)
        parcel.writeValue(iInGroup)
        parcel.writeValue(iIsApply)
        parcel.writeValue(iIsOwner)
        parcel.writeValue(iIsManager)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewGroupBean> {
        override fun createFromParcel(parcel: Parcel): NewGroupBean {
            return NewGroupBean(parcel)
        }

        override fun newArray(size: Int): Array<NewGroupBean?> {
            return arrayOfNulls(size)
        }
    }

}