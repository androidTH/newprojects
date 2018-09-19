package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 *     author : jinjiarui
 *     time   : 2018/09/19
 *     desc   :
 *     version:
 */
data class FollowFansVistor(val iUserid:Int?):Parcelable {
    @SerializedName("iVistorCount")var iVistorCount:Int?=0
    @SerializedName("iVistorCountAll")var iVistorCountAll:Int?=0
    @SerializedName("iFansCount")var iFansCount:Int?=0
    @SerializedName("iFansCountAll")var iFansCountAll:Int?=0
    @SerializedName("iFollowCount")var iFollowCount:Int?=0

    constructor(parcel: Parcel) : this(parcel.readValue(Int::class.java.classLoader) as? Int) {
        iVistorCount = parcel.readValue(Int::class.java.classLoader) as? Int
        iVistorCountAll = parcel.readValue(Int::class.java.classLoader) as? Int
        iFansCount = parcel.readValue(Int::class.java.classLoader) as? Int
        iFansCountAll = parcel.readValue(Int::class.java.classLoader) as? Int
        iFollowCount = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(iUserid)
        parcel.writeValue(iVistorCount)
        parcel.writeValue(iVistorCountAll)
        parcel.writeValue(iFansCount)
        parcel.writeValue(iFansCountAll)
        parcel.writeValue(iFollowCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FollowFansVistor> {
        override fun createFromParcel(parcel: Parcel): FollowFansVistor {
            return FollowFansVistor(parcel)
        }

        override fun newArray(size: Int): Array<FollowFansVistor?> {
            return arrayOfNulls(size)
        }
    }


}