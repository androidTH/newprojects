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
data class TopicBean(@SerializedName("sId")var sId:String = "",var mResId:Int= -1,@SerializedName("sTopicName")var sTopicName:String = ""):Parcelable {

//    "sId": "8ccede7b-038d-462e-b436-e4fbe0ce41df",
//    "sTopicName": "333",
//    "sTopicDesc": "33333",
//    "iCreateUserid": 0,
//    "iIsshow": 1,
//    "dCreateTime": 1567757367000
    @SerializedName("sTopicDesc")var sTopicDesc:String=""
    @SerializedName("iCreateUserid")var iCreateUserid:Int?=0
    @SerializedName("iIsshow")var iIsshow:Int = 0

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readString()) {
        sTopicDesc = parcel.readString()
        iCreateUserid = parcel.readValue(Int::class.java.classLoader) as? Int
        iIsshow = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeInt(mResId)
        parcel.writeString(sTopicName)
        parcel.writeString(sTopicDesc)
        parcel.writeValue(iCreateUserid)
        parcel.writeInt(iIsshow)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TopicBean> {
        override fun createFromParcel(parcel: Parcel): TopicBean {
            return TopicBean(parcel)
        }

        override fun newArray(size: Int): Array<TopicBean?> {
            return arrayOfNulls(size)
        }
    }

}