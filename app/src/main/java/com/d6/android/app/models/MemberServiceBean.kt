package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/09/05
 *     desc   :
 *     version:
 */
class MemberServiceBean(var sId:String?="") :Parcelable{

    var mResId:Int = 0
    var mClassName:String=""
    var mClassDesc:String=""
    var mClassTag:String = ""
    var mClassType:Int = -1;

    constructor(parcel: Parcel) : this(parcel.readString()) {
        mResId = parcel.readInt()
        mClassName = parcel.readString()
        mClassDesc = parcel.readString()
        mClassTag = parcel.readString()
        mClassType = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeInt(mResId)
        parcel.writeString(mClassName)
        parcel.writeString(mClassDesc)
        parcel.writeString(mClassTag)
        parcel.writeInt(mClassType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MemberServiceBean> {
        override fun createFromParcel(parcel: Parcel): MemberServiceBean {
            return MemberServiceBean(parcel)
        }

        override fun newArray(size: Int): Array<MemberServiceBean?> {
            return arrayOfNulls(size)
        }
    }


}