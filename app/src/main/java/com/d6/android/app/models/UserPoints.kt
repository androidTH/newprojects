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
data class UserPoints(@SerializedName("iUserid")var iUserid:Int?) :Parcelable{
     @SerializedName("sId") var sId:String?=""
     @SerializedName("sPointdesc") var sPointdesc:String?=""
     @SerializedName("iPoint") var iPoint:Int?=0
     @SerializedName("iPointtype") var iPointtype:Int?=1 // 1、充值  2、消费  3、充值购买小红花 4、提现小红花 5、收到小红花 6、赠送小红花
     @SerializedName("dCreatetime") var dCreatetime:Long?=0

    constructor(parcel: Parcel) : this(parcel.readValue(Int::class.java.classLoader) as? Int) {
        sId = parcel.readString()
        sPointdesc = parcel.readString()
        iPoint = parcel.readValue(Int::class.java.classLoader) as? Int
        iPointtype = parcel.readValue(Int::class.java.classLoader) as? Int
        dCreatetime = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(iUserid)
        parcel.writeString(sId)
        parcel.writeString(sPointdesc)
        parcel.writeValue(iPoint)
        parcel.writeValue(iPointtype)
        parcel.writeValue(dCreatetime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserPoints> {
        override fun createFromParcel(parcel: Parcel): UserPoints {
            return UserPoints(parcel)
        }

        override fun newArray(size: Int): Array<UserPoints?> {
            return arrayOfNulls(size)
        }
    }
}