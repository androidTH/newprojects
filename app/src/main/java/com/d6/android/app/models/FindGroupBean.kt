package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/06/11
 *     desc   :
 *     version:
 */
data class FindGroupBean(var sId:String?="") :Parcelable{
    var sGroupPic:String?=""
    var sGroupName:String?=""
    var iGroupNum:Int?=-1
    var sGroupDesc:String?=""
    var iGroupStatus:Int?=-1 //1、可用 2、停用 3、解散
    var iInGroup:Int?=-1 //1、不在  2、在
    var iIsApply:Int?=-1 //1、申请中  2、未申请
    var iIsOwner:Int?=-1
    var iIsManager:Int?=-1
    var iMemberCount:Int?=-1;//群成员数量
    var iType:Int?=-1 //iType 1、群  2、好友
    var sEnviroment:String?=""
    var sLastJoinMember:String?=""
    var iCreateUserid:Int?=-1
    var sMemberId:String?=""
    var iJoinGroup:Int=-1

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
        iMemberCount = parcel.readValue(Int::class.java.classLoader) as? Int
        iType = parcel.readValue(Int::class.java.classLoader) as? Int
        sEnviroment = parcel.readString()
        sLastJoinMember = parcel.readString()
        iCreateUserid = parcel.readValue(Int::class.java.classLoader) as? Int
        sMemberId = parcel.readString()
        iJoinGroup = parcel.readValue(Int::class.java.classLoader) as Int
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
        parcel.writeValue(iMemberCount)
        parcel.writeValue(iType)
        parcel.writeString(sEnviroment)
        parcel.writeString(sLastJoinMember)
        parcel.writeValue(iCreateUserid)
        parcel.writeString(sMemberId)
        parcel.writeValue(iJoinGroup)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FindGroupBean> {
        override fun createFromParcel(parcel: Parcel): FindGroupBean {
            return FindGroupBean(parcel)
        }

        override fun newArray(size: Int): Array<FindGroupBean?> {
            return arrayOfNulls(size)
        }
    }

}