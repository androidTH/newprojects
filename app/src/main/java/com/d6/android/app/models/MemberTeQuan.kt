package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/08/10
 *     desc   :
 *     version:
 */
data class MemberTeQuan(var sId:String) :Parcelable {
    var iMemberType:Int =-1 //1 app 2 微信 3 人工
    var sMemberTitle:String="" //会员身份
    var sMemberDesc:String = ""// "显示“会员”专属身份，随处可见",
    var sMemberPic:String = ""// "http://106.14.39.87:8888/JYSystem/static/images/member/1.png",
    var iStatus:Int = -1 // 1 有权限 2无权限
    var iOrder:Int = 0

    constructor(parcel: Parcel) : this(parcel.readString()) {
        iMemberType = parcel.readInt()
        sMemberTitle = parcel.readString()
        sMemberDesc = parcel.readString()
        sMemberPic = parcel.readString()
        iStatus = parcel.readInt()
        iOrder = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeInt(iMemberType)
        parcel.writeString(if(sMemberTitle==null)"" else sMemberTitle)
        parcel.writeString(if(sMemberDesc==null) "" else sMemberDesc)
        parcel.writeString(if(sMemberPic==null)"" else sMemberPic)
        parcel.writeInt(iStatus)
        parcel.writeInt(iOrder)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MemberTeQuan> {
        override fun createFromParcel(parcel: Parcel): MemberTeQuan {
            return MemberTeQuan(parcel)
        }

        override fun newArray(size: Int): Array<MemberTeQuan?> {
            return arrayOfNulls(size)
        }
    }

}