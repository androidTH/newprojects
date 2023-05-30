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
data class PointRule(@SerializedName("sId")var sId:String):Parcelable{
    @SerializedName("iPrice")
    var iPrice:Int?=0
    @SerializedName("iPoint")
    var iPoint:Int?=0
    @SerializedName("iDiscount")
    var iDiscount:Int?=0
    @SerializedName("dDiscountStarttime")
    var dDiscountStarttime:Long?=0
    @SerializedName("dDiscountEndtime")
    var dDiscountEndtime:Long?=0

    constructor(parcel: Parcel) : this(parcel.readString()) {
        iPrice = parcel.readValue(Int::class.java.classLoader) as? Int
        iPoint = parcel.readValue(Int::class.java.classLoader) as? Int
        iDiscount = parcel.readValue(Int::class.java.classLoader) as? Int
        dDiscountStarttime = parcel.readValue(Long::class.java.classLoader) as? Long
        dDiscountEndtime = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeValue(iPrice)
        parcel.writeValue(iPoint)
        parcel.writeValue(iDiscount)
        parcel.writeValue(dDiscountStarttime)
        parcel.writeValue(dDiscountEndtime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PointRule> {
        override fun createFromParcel(parcel: Parcel): PointRule {
            return PointRule(parcel)
        }

        override fun newArray(size: Int): Array<PointRule?> {
            return arrayOfNulls(size)
        }
    }

}