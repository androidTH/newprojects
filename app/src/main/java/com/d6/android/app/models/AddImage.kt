package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *
 */
data class AddImage(val imgUrl: String, val type: Int = 0):Parcelable {
    var path: String = ""

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt()) {
        path = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imgUrl)
        parcel.writeInt(type)
        parcel.writeString(path)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddImage> {
        override fun createFromParcel(parcel: Parcel): AddImage {
            return AddImage(parcel)
        }

        override fun newArray(size: Int): Array<AddImage?> {
            return arrayOfNulls(size)
        }
    }
}