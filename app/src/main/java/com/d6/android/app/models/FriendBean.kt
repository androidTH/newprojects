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
data class FriendBean(@SerializedName("sId")var sId:String?=""):Parcelable {
    @SerializedName("iUserid") var iUserid:Int? = -1
    @SerializedName("iBlackUserid") var iBlackUserid:Int?=-1
    @SerializedName("sPicUrl") var sPicUrl:String?=""
    @SerializedName("sUserName") var sUserName:String?=""
    @SerializedName("sSex")var sSex:String?=""
    @SerializedName("userclassesid") var userclassesid:String?=""
    @SerializedName("nianling")var nianling:String?=""
    @SerializedName("ziwojieshao")var ziwojieshao:String?=""
    @SerializedName("gexingqianming")var gexingqianming:String?=""
    @SerializedName("userclassesname") var userclassesname:String?=""
    @SerializedName("iFriendid") var iFriendid:Int?=-1
    @SerializedName("iIsFollow") var iIsFollow:Int?=-1
    @SerializedName("screen") var screen:String?=""
    var iIsChecked:Int?=0

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
        iFriendid = parcel.readValue(Int::class.java.classLoader) as? Int
        iIsFollow = parcel.readValue(Int::class.java.classLoader) as? Int
        screen = parcel.readString()
    }

//    "iAddPoint": null,
//    "sAddPointDesc": null,
//    "sId": "236c75a4-589e-4ba0-a4fb-8a12f183af82",
//    "iUserid": 61003,
//    "iFriendid": 0,
//    "dJointime": 1556442107000,
//    "sUserName": "测试006",
//    "sSex": "0",
//    "sPicUrl": "http://p22l7xdxa.bkt.clouddn.com/1556074563704.jpg",
//    "userclassesid": "28",
//    "nianling": null,
//    "gexingqianming": null,
//    "iIsFollow": null,
//    "screen": "0",
//    "userclassesname": "中级会员",
//    "ziwojieshao": ""
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeValue(iUserid)
        parcel.writeValue(iBlackUserid)
        parcel.writeString(sPicUrl)
        parcel.writeString(sUserName)
        parcel.writeString(sSex)
        parcel.writeString(userclassesid)
        parcel.writeString(nianling)
        parcel.writeString(ziwojieshao)
        parcel.writeString(gexingqianming)
        parcel.writeString(userclassesname)
        parcel.writeValue(iFriendid)
        parcel.writeValue(iIsFollow)
        parcel.writeString(screen)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FriendBean> {
        override fun createFromParcel(parcel: Parcel): FriendBean {
            return FriendBean(parcel)
        }

        override fun newArray(size: Int): Array<FriendBean?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        var ob =other as FriendBean
        if(this.iUserid==ob.iUserid){
            return true
        }else{
            return false
        }
    }
}