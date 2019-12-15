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
    // 1、充值  2、消费  3、充值购买小红花 4、提现小红花 5、收到小红花 6、赠送小红花 7、充值红心 8、消费红心 9、收到红心 10、提现红心
    @SerializedName("dCreatetime") var dCreatetime:Long?=0
    @SerializedName("iPointtype") var iPointtype:Int?=1
     @SerializedName("sResourceId") var sResourceId:String?=""
     @SerializedName("iSenduserid") var iSenduserid:Int=0
     @SerializedName("iType") var iType:Int = 0 //16、好友注册奖励 17、邀请认证会员奖励小红花 18、邀请够买会员奖励小红花
     @SerializedName("sSendUserName") var sSendUserName:String=""
     @SerializedName("iIsShowPoint") var iIsShowPoint:Int = 0 //1、显示  2、不显示

    constructor(parcel: Parcel) : this(parcel.readValue(Int::class.java.classLoader) as? Int) {
        sId = parcel.readString()
        sPointdesc = parcel.readString()
        iPoint = parcel.readValue(Int::class.java.classLoader) as? Int
        iPointtype = parcel.readValue(Int::class.java.classLoader) as? Int
        dCreatetime = parcel.readValue(Long::class.java.classLoader) as? Long
        sResourceId = parcel.readString()
        iSenduserid = parcel.readInt()
        iType = parcel.readInt()
        sSendUserName = parcel.readString()
        iIsShowPoint = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(iUserid)
        parcel.writeString(sId)
        parcel.writeString(sPointdesc)
        parcel.writeValue(iPoint)
        parcel.writeValue(iPointtype)
        parcel.writeValue(dCreatetime)
        parcel.writeString(sResourceId)
        parcel.writeInt(iSenduserid)
        parcel.writeInt(iType)
        parcel.writeString(sSendUserName)
        parcel.writeInt(iIsShowPoint)
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