package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created on 2017/12/21.
 */
data class IPBean(var ip:String = "") : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ip)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IPBean> {
        override fun createFromParcel(parcel: Parcel): IPBean {
            return IPBean(parcel)
        }

        override fun newArray(size: Int): Array<IPBean?> {
            return arrayOfNulls(size)
        }
    }
}