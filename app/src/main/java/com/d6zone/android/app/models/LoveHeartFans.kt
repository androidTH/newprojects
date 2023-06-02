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
data class LoveHeartFans(@SerializedName("iUserid")var iUserid:Int?):Parcelable {

    @SerializedName("iSenduserid") var iSenduserid: Int=-1
    @SerializedName("sId") var sId:String=""
    @SerializedName("sPointdesc") var sPointdesc:String="" //"给客服六妹赠送了10颗❤",
    @SerializedName("iPoint") var iPoint:Int=-1
//    "iPointtype": 8,
//    "iType": 1,
//    "iIsread": null,
//    "sResourceId": "",
//    "iIsCheck": null,
//    "iIsCode": null,
//    "iIsPay": null,
    @SerializedName("sSendUserName") var sSendUserName:String="" //"客服六妹",
//    "iIsSend": null,
    @SerializedName("sSex") var sSex:String="" //: "0",
    @SerializedName("sPicUrl") var sPicUrl:String = ""// "http://p22l7xdxa.bkt.clouddn.com/1556507697336.jpg",
//    "iAllLovePoint": null,
    @SerializedName("iAllLovePoint") var iAllLovePoint:Int = -1
    @SerializedName("userclassesid") var userclassesid:String="" //: "29",
    @SerializedName("nianling") var nianling:String="" //"26",
    @SerializedName("gexingqianming") var gexingqianming:String = ""
    @SerializedName("screen") var screen:String="" //: "1",
    @SerializedName("userclassesname") var userclassesname:String="" //"userclassesname": "优质会员",
    @SerializedName("ziwojieshao") var ziwojieshao:String = ""// "ziwojieshao": "人工客服在线时间早9点－晚1点，官方客服Vx:dkn042"
    @SerializedName("iIsCode") var iIsCode:Int = -1 // 2、允许显示用户头像  1、头像和名称打码
    var iListSetting:Int = -1
    @SerializedName("shengao")var shengao:String?=""
    @SerializedName("zhiye")var zhiye:String?=""
    @SerializedName("sPosition")var sPosition:String?=""
    var giftName:String =""
    var giftLoveNum:Int=-1
    var giftNum:Int=-1

    constructor(parcel: Parcel) : this(parcel.readValue(Int::class.java.classLoader) as? Int) {
        iSenduserid = parcel.readInt()
        sId = parcel.readString()
        sPointdesc = parcel.readString()
        iPoint = parcel.readInt()
        sSendUserName = parcel.readString()
        sSex = parcel.readString()
        sPicUrl = parcel.readString()
        iAllLovePoint = parcel.readInt()
        userclassesid = parcel.readString()
        nianling = parcel.readString()
        gexingqianming = parcel.readString()
        screen = parcel.readString()
        userclassesname = parcel.readString()
        ziwojieshao = parcel.readString()
        iIsCode = parcel.readInt()
        iListSetting = parcel.readInt()
        shengao = parcel.readString()
        zhiye = parcel.readString()
        sPosition = parcel.readString()
        giftName = parcel.readString()
        giftNum = parcel.readInt()
        giftLoveNum = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(iUserid)
        parcel.writeInt(iSenduserid)
        parcel.writeString(sId)
        parcel.writeString(sPointdesc)
        parcel.writeInt(iPoint)
        parcel.writeString(sSendUserName)
        parcel.writeString(sSex)
        parcel.writeString(sPicUrl)
        parcel.writeInt(iAllLovePoint)
        parcel.writeString(userclassesid)
        parcel.writeString(nianling)
        parcel.writeString(gexingqianming)
        parcel.writeString(screen)
        parcel.writeString(userclassesname)
        parcel.writeString(ziwojieshao)
        parcel.writeInt(iIsCode)
        parcel.writeInt(iListSetting)
        parcel.writeString(shengao)
        parcel.writeString(zhiye)
        parcel.writeString(sPosition)
        parcel.writeString(giftName)
        parcel.writeInt(giftNum)
        parcel.writeInt(giftLoveNum)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoveHeartFans> {
        override fun createFromParcel(parcel: Parcel): LoveHeartFans {
            return LoveHeartFans(parcel)
        }

        override fun newArray(size: Int): Array<LoveHeartFans?> {
            return arrayOfNulls(size)
        }
    }


}