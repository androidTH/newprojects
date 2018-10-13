package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *
 */
data class DateType(var type: Int = 0):Parcelable {
    var imgUrl: String = ""
    var dateTypeName:String=""

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        imgUrl = parcel.readString()
        dateTypeName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
        parcel.writeString(imgUrl)
        parcel.writeString(dateTypeName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DateType> {
        override fun createFromParcel(parcel: Parcel): DateType {
            return DateType(parcel)
        }

        override fun newArray(size: Int): Array<DateType?> {
            return arrayOfNulls(size)
        }
    }


}