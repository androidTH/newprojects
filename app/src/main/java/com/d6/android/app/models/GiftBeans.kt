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
data class GiftBeans(@SerializedName("giftId")var giftId:Int):Parcelable{
    @SerializedName("icon")
    var icon:String = ""
    @SerializedName("name")
    var name:String = ""
    @SerializedName("loveNum")
    var loveNum:Int?=0
    @SerializedName("createDate")
    var createDate:String=""
    @SerializedName("modDate")
    var modDate:String=""
    @SerializedName("status")
    var status:Int?=0

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        icon = parcel.readString()
        name = parcel.readString()
        loveNum = parcel.readValue(Int::class.java.classLoader) as? Int
        createDate = parcel.readString()
        modDate = parcel.readString()
        status = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(giftId)
        parcel.writeString(icon)
        parcel.writeString(name)
        parcel.writeValue(loveNum)
        parcel.writeString(createDate)
        parcel.writeString(modDate)
        parcel.writeValue(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GiftBeans> {
        override fun createFromParcel(parcel: Parcel): GiftBeans {
            return GiftBeans(parcel)
        }

        override fun newArray(size: Int): Array<GiftBeans?> {
            return arrayOfNulls(size)
        }
    }

}