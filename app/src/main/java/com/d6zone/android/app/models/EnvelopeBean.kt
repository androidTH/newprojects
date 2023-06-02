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
data class EnvelopeBean(@SerializedName("sGuid")var sGuid:String?):Parcelable {

    var sEnvelopeId:String?=""
    var iUserId:Int?=-1
    var iLovePoint:Int?= - 1
    var dCreatetime:Long?=-1
    var name:String?=""
    var picUrl:String?=""
    var sex:String?=""
    var userclassesid:String?=""
    var screen:String?=""
    var zhiye:String?=""
    var nianling:String?=""
    var shengao:String?=""
    var tizhong:String?=""
    var ziwojieshao:String?=""

    constructor(parcel: Parcel) : this(parcel.readString()) {
        sEnvelopeId = parcel.readString()
        iUserId = parcel.readValue(Int::class.java.classLoader) as? Int
        iLovePoint = parcel.readValue(Int::class.java.classLoader) as? Int
        dCreatetime = parcel.readValue(Long::class.java.classLoader) as? Long
        name = parcel.readString()
        picUrl = parcel.readString()
        sex = parcel.readString()
        userclassesid = parcel.readString()
        screen = parcel.readString()
        zhiye = parcel.readString()
        nianling = parcel.readString()
        shengao = parcel.readString()
        tizhong = parcel.readString()
        ziwojieshao = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sGuid)
        parcel.writeString(sEnvelopeId)
        parcel.writeValue(iUserId)
        parcel.writeValue(iLovePoint)
        parcel.writeValue(dCreatetime)
        parcel.writeString(name)
        parcel.writeString(picUrl)
        parcel.writeString(sex)
        parcel.writeString(userclassesid)
        parcel.writeString(screen)
        parcel.writeString(zhiye)
        parcel.writeString(nianling)
        parcel.writeString(shengao)
        parcel.writeString(tizhong)
        parcel.writeString(ziwojieshao)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EnvelopeBean> {
        override fun createFromParcel(parcel: Parcel): EnvelopeBean {
            return EnvelopeBean(parcel)
        }

        override fun newArray(size: Int): Array<EnvelopeBean?> {
            return arrayOfNulls(size)
        }
    }
}