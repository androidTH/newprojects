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
data class BlackListBean(@SerializedName("sId")var sId:String?=""):Parcelable {
    @SerializedName("iUserid") var iUserid:Int? = -1 //登录用户id
    @SerializedName("iBlackUserid") var iBlackUserid:Int?=-1
    @SerializedName("sPicUrl") var sPicUrl:String?=""
    @SerializedName("sUserName") var sUserName:String?=""
    @SerializedName("sSex")var sSex:String?=""
    @SerializedName("userclassesid") var userclassesid:String?=""
    @SerializedName("nianling")var nianling:String?=""
    @SerializedName("ziwojieshao")var ziwojieshao:String?=""
    @SerializedName("gexingqianming")var gexingqianming:String?=""
    @SerializedName("userclassesname") var userclassesname:String?=""
    var iIsAnonymous:Int?=0 //1、匿名  2、非匿名状态

    constructor(parcel: Parcel) : this(parcel.readString()) {
        iUserid = parcel.readValue(Int::class.java.classLoader) as? Int
        iBlackUserid = parcel.readValue(Int::class.java.classLoader) as? Int
        sPicUrl = parcel.readString()
        sUserName = parcel.readString()
        sSex = parcel.readString()
        userclassesid = parcel.readString()
        nianling = parcel.readString()
        ziwojieshao = parcel.readString()
        gexingqianming = parcel.readString()
        userclassesname = parcel.readString()
        iIsAnonymous = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeValue(iUserid)
        parcel.writeValue(iIsAnonymous)
        parcel.writeValue(iBlackUserid)
        parcel.writeString(sPicUrl)
        parcel.writeString(sUserName)
        parcel.writeString(sSex)
        parcel.writeString(userclassesid)
        parcel.writeString(nianling)
        parcel.writeString(ziwojieshao)
        parcel.writeString(gexingqianming)
        parcel.writeString(userclassesname)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlackListBean> {
        override fun createFromParcel(parcel: Parcel): BlackListBean {
            return BlackListBean(parcel)
        }

        override fun newArray(size: Int): Array<BlackListBean?> {
            return arrayOfNulls(size)
        }
    }
}