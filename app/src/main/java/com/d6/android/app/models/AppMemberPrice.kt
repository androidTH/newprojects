package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/08/10
 *     desc   :
 *     version:
 */
class AppMemberPrice(var sProductId:String) :Parcelable {

    var sEnableDateDesc:String?=""
    var iAndroidPrice:Int? = -1
    var sAndroidPriceDiscount:String?=""
    var sAndroidPriceDiscountDesc:String?=""

    constructor(parcel: Parcel) : this(parcel.readString()) {
        sEnableDateDesc = parcel.readString()
        iAndroidPrice = parcel.readValue(Int::class.java.classLoader) as? Int
        sAndroidPriceDiscount = parcel.readString()
        sAndroidPriceDiscountDesc = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sProductId)
        parcel.writeString(sEnableDateDesc)
        parcel.writeValue(iAndroidPrice)
        parcel.writeString(sAndroidPriceDiscount)
        parcel.writeString(sAndroidPriceDiscountDesc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppMemberPrice> {
        override fun createFromParcel(parcel: Parcel): AppMemberPrice {
            return AppMemberPrice(parcel)
        }

        override fun newArray(size: Int): Array<AppMemberPrice?> {
            return arrayOfNulls(size)
        }
    }


}