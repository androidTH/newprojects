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
data class InviteUserBean(@SerializedName("sId")var sId:String?=""):Parcelable {
    var iInviteUserId:Int?=0
    var iEventType:Int?=0//事件类型 //1 注册  2成为会员  3升级会员  4续费会员
    var iUserid:Int?=0
    var sEventBefor:String? = ""
     var sEventAfter:String?=""
    var dCreatetime:Long? = 0
    var sMsgContent:String?=""
    var iEnableMonth:Int?=0
    var sSex:String?=""
    var sPicUrl:String?=""
    var userclassesid:String?=""//24黄金 23白银 22普通 28中级
    var nianling:String?=""
    var screen:String?=""
    var gexingqianming:String?=""
    var userclassesname:String?=""
    var sUserName:String?=""

    constructor(parcel: Parcel) : this(parcel.readString()) {
        iInviteUserId = parcel.readValue(Int::class.java.classLoader) as? Int
        iEventType = parcel.readValue(Int::class.java.classLoader) as? Int
        iUserid = parcel.readValue(Int::class.java.classLoader) as? Int
        sEventBefor = parcel.readString()
        sEventAfter = parcel.readString()
        dCreatetime = parcel.readValue(Long::class.java.classLoader) as? Long
        sMsgContent = parcel.readString()
        iEnableMonth = parcel.readValue(Int::class.java.classLoader) as? Int
        sSex = parcel.readString()
        sPicUrl = parcel.readString()
        userclassesid = parcel.readString()
        nianling = parcel.readString()
        screen = parcel.readString()
        gexingqianming = parcel.readString()
        userclassesname = parcel.readString()
        sUserName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeValue(iInviteUserId)
        parcel.writeValue(iEventType)
        parcel.writeValue(iUserid)
        parcel.writeString(sEventBefor)
        parcel.writeString(sEventAfter)
        parcel.writeValue(dCreatetime)
        parcel.writeString(sMsgContent)
        parcel.writeValue(iEnableMonth)
        parcel.writeString(sSex)
        parcel.writeString(sPicUrl)
        parcel.writeString(userclassesid)
        parcel.writeString(nianling)
        parcel.writeString(screen)
        parcel.writeString(gexingqianming)
        parcel.writeString(userclassesname)
        parcel.writeString(sUserName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InviteUserBean> {
        override fun createFromParcel(parcel: Parcel): InviteUserBean {
            return InviteUserBean(parcel)
        }

        override fun newArray(size: Int): Array<InviteUserBean?> {
            return arrayOfNulls(size)
        }
    }

}