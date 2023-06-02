package com.d6zone.android.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 *     author : jinjiarui
 *     time   : 2018/09/19
 *     desc   :
 *     version:
 */
data class EnvelopeStatus(@SerializedName("sGuid")var sGuid:String?):Parcelable {

    var iUserId:Int?=-1
    var iLovePoint:Int?= - 1
    var dCreatetime:Long=-1
    var iLoveCount:Int?=-1
    var iType:Int?=-1
    var sResourceId:String?=""
    var sSendUserName:String?=""
    var sEnvelopeDesc:String?=""
    var iIsGet:Int?= 0
    var iGetLovePoint:Int? = 0
    var iRemainCount:Int? = 0
    var iRemainPoint:Int?= 0
    var iStatus:Int?=0

    constructor(parcel: Parcel) : this(parcel.readString()) {
        iUserId = parcel.readValue(Int::class.java.classLoader) as? Int
        iLovePoint = parcel.readValue(Int::class.java.classLoader) as? Int
        dCreatetime = parcel.readLong()
        iLoveCount = parcel.readValue(Int::class.java.classLoader) as? Int
        iType = parcel.readValue(Int::class.java.classLoader) as? Int
        sResourceId = parcel.readString()
        sSendUserName = parcel.readString()
        sEnvelopeDesc = parcel.readString()
        iIsGet = parcel.readValue(Int::class.java.classLoader) as? Int
        iGetLovePoint = parcel.readValue(Int::class.java.classLoader) as? Int
        iRemainCount = parcel.readValue(Int::class.java.classLoader) as? Int
        iRemainPoint = parcel.readValue(Int::class.java.classLoader) as? Int
        iStatus = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sGuid)
        parcel.writeValue(iUserId)
        parcel.writeValue(iLovePoint)
        parcel.writeLong(dCreatetime)
        parcel.writeValue(iLoveCount)
        parcel.writeValue(iType)
        parcel.writeString(sResourceId)
        parcel.writeString(sSendUserName)
        parcel.writeString(sEnvelopeDesc)
        parcel.writeValue(iIsGet)
        parcel.writeValue(iGetLovePoint)
        parcel.writeValue(iRemainCount)
        parcel.writeValue(iRemainPoint)
        parcel.writeValue(iStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EnvelopeStatus> {
        override fun createFromParcel(parcel: Parcel): EnvelopeStatus {
            return EnvelopeStatus(parcel)
        }

        override fun newArray(size: Int): Array<EnvelopeStatus?> {
            return arrayOfNulls(size)
        }
    }


}