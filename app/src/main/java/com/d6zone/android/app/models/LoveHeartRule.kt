package com.d6zone.android.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 *     author : jinjiarui
 *     time   : 2018/10/05
 *     desc   :
 *     version:
 */
data class LoveHeartRule(@SerializedName("sId")var sId:String):Parcelable{
    @SerializedName("iPrice")
    var iPrice:Int?=0
    @SerializedName("iLoveCount")
    var iLoveCount:Int?=0
    @SerializedName("iType")
    var iType:Int?=0
    @SerializedName("sProductId")
    var sProductId:Long?=0
    var sLoveCount:String=""
    var sDesc:String = ""

    constructor(parcel: Parcel) : this(parcel.readString()) {
        iPrice = parcel.readValue(Int::class.java.classLoader) as? Int
        iLoveCount = parcel.readValue(Int::class.java.classLoader) as? Int
        iType = parcel.readValue(Int::class.java.classLoader) as? Int
        sProductId = parcel.readValue(Long::class.java.classLoader) as? Long
        sLoveCount = parcel.readString()
        sDesc = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeValue(iPrice)
        parcel.writeValue(iLoveCount)
        parcel.writeValue(iType)
        parcel.writeValue(sProductId)
        parcel.writeString(sLoveCount)
        parcel.writeString(sDesc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoveHeartRule> {
        override fun createFromParcel(parcel: Parcel): LoveHeartRule {
            return LoveHeartRule(parcel)
        }

        override fun newArray(size: Int): Array<LoveHeartRule?> {
            return arrayOfNulls(size)
        }
    }

}