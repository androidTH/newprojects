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
data class Fans(@SerializedName("iUserid")var iUserid:Int?):Parcelable {
    @SerializedName("iFollowUserid")var iFollowUserid:Int?=0
    @SerializedName("iVistorid")var iVistorid:Int?=0
    @SerializedName("dJointime")var dJointime:Long? = 0
    @SerializedName("sUserName")var sUserName:String?=""
    @SerializedName("sSex")var sSex:String?=""
    @SerializedName("sPicUrl")var sPicUrl:String?=""
    @SerializedName("userclassesid") var userclassesid:String?=""
    @SerializedName("nianling")var nianling:String?=""
    @SerializedName("ziwojieshao")var gexingqianming:String?=""
    @SerializedName("iIsFollow")var iIsFollow:Int?=0
    @SerializedName("userclassesname") var userclassesname:String?=""
//    screen

    constructor(parcel: Parcel) : this(parcel.readValue(Int::class.java.classLoader) as? Int) {
        iFollowUserid = parcel.readValue(Int::class.java.classLoader) as? Int
        iVistorid = parcel.readValue(Int::class.java.classLoader) as? Int
        dJointime = parcel.readValue(Long::class.java.classLoader) as? Long
        sUserName = parcel.readString()
        sSex = parcel.readString()
        sPicUrl = parcel.readString()
        userclassesid = parcel.readString()
        nianling = parcel.readString()
        gexingqianming = parcel.readString()
        iIsFollow = parcel.readValue(Int::class.java.classLoader) as? Int
        userclassesname = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(iUserid)
        parcel.writeValue(iFollowUserid)
        parcel.writeValue(iVistorid)
        parcel.writeValue(dJointime)
        parcel.writeString(sUserName)
        parcel.writeString(sSex)
        parcel.writeString(sPicUrl)
        parcel.writeString(userclassesid)
        parcel.writeString(nianling)
        parcel.writeString(gexingqianming)
        parcel.writeValue(iIsFollow)
        parcel.writeString(userclassesname)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Fans> {
        override fun createFromParcel(parcel: Parcel): Fans {
            return Fans(parcel)
        }

        override fun newArray(size: Int): Array<Fans?> {
            return arrayOfNulls(size)
        }
    }

}