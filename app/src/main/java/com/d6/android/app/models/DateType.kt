package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *
 */
data class DateType(var type: Int = 0):Parcelable {
    var imgUrl: String = ""
    var dateTypeName:String=""
    var isSelected:Boolean=false;

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        imgUrl = parcel.readString()
        dateTypeName = parcel.readString()
        isSelected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
        parcel.writeString(imgUrl)
        parcel.writeString(dateTypeName)
        parcel.writeByte(if (isSelected) 1 else 0)
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