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
data class FlowerRule(@SerializedName("sId")var sId:String):Parcelable{
    @SerializedName("iPrice")
    var iPrice:Int?=0
    @SerializedName("iFlowerCount")
    var iFlowerCount:Int?=0

    constructor(parcel: Parcel) : this(parcel.readString()) {
        iPrice = parcel.readValue(Int::class.java.classLoader) as? Int
        iFlowerCount = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeValue(iPrice)
        parcel.writeValue(iFlowerCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FlowerRule> {
        override fun createFromParcel(parcel: Parcel): FlowerRule {
            return FlowerRule(parcel)
        }

        override fun newArray(size: Int): Array<FlowerRule?> {
            return arrayOfNulls(size)
        }
    }

}