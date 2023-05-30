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
data class UserGiftBeans(@SerializedName("giftRecordId")var giftId:String):Parcelable{
    @SerializedName("giftIcon")
    var giftIcon:String = ""
    @SerializedName("receiveUserId")
    var receiveUserId:String = ""
    @SerializedName("giveUserId")
    var giveUserId:String = ""
    @SerializedName("giveTypeId")
    var giveTypeId:String = ""
    @SerializedName("name")
    var name:String = ""
    @SerializedName("giftName")
    var giftName:String = ""
    @SerializedName("giftNum")
    var giftNum:Int=0
    @SerializedName("giftLoveNum")
    var giftLoveNum:Int=0
    @SerializedName("giveType")
    var giveType:Int=0
    @SerializedName("totalLoveNum")
    var totalLoveNum:Int=0
    @SerializedName("createDate")
    var createDate:String=""
    @SerializedName("modDate")
    var modDate:String=""
    @SerializedName("nickName")
    var nickName:String=""

    constructor(parcel: Parcel) : this(parcel.readString()) {
        giftIcon = parcel.readString()
        receiveUserId = parcel.readString()
        giveUserId = parcel.readString()
        giveTypeId = parcel.readString()
        name = parcel.readString()
        giftName = parcel.readString()
        giftNum = parcel.readInt()
        giftLoveNum = parcel.readInt()
        giveType = parcel.readInt()
        totalLoveNum = parcel.readInt()
        createDate = parcel.readString()
        modDate = parcel.readString()
        nickName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(giftId)
        parcel.writeString(giftIcon)
        parcel.writeString(receiveUserId)
        parcel.writeString(giveUserId)
        parcel.writeString(giveTypeId)
        parcel.writeString(name)
        parcel.writeString(giftName)
        parcel.writeInt(giftNum)
        parcel.writeInt(giftLoveNum)
        parcel.writeInt(giveType)
        parcel.writeInt(totalLoveNum)
        parcel.writeString(createDate)
        parcel.writeString(modDate)
        parcel.writeString(nickName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserGiftBeans> {
        override fun createFromParcel(parcel: Parcel): UserGiftBeans {
            return UserGiftBeans(parcel)
        }

        override fun newArray(size: Int): Array<UserGiftBeans?> {
            return arrayOfNulls(size)
        }
    }
}