package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 新的约会数据模型
 */
data class InviteLinkBean(var ids :String="") :Parcelable{
    @SerializedName("iInviteFlower")var iInviteFlower:Int = -1
    @SerializedName("iInviteCount")var iInviteCount:Int = -1
    @SerializedName("sInviteLinkPic") var sInviteLinkPic:String=""
    @SerializedName("sInviteDesc") var sInviteDesc:String=""
    @SerializedName("iInvitePoint") var iInvitePoint:Int = -1
    @SerializedName("sInviteUserName") var sInviteUserName:String = ""
    @SerializedName("sInviteUserId") var sInviteUserId:String=""

    constructor(parcel: Parcel) : this(parcel.readString()) {
        iInviteFlower = parcel.readInt()
        iInviteCount = parcel.readInt()
        sInviteLinkPic = parcel.readString()
        sInviteDesc = parcel.readString()
        iInvitePoint = parcel.readInt()
        sInviteUserName = parcel.readString()
        sInviteUserId = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ids)
        parcel.writeInt(iInviteFlower)
        parcel.writeInt(iInviteCount)
        parcel.writeString(sInviteLinkPic)
        parcel.writeString(sInviteDesc)
        parcel.writeInt(iInvitePoint)
        parcel.writeString(sInviteUserName)
        parcel.writeString(sInviteUserId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InviteLinkBean> {
        override fun createFromParcel(parcel: Parcel): InviteLinkBean {
            return InviteLinkBean(parcel)
        }

        override fun newArray(size: Int): Array<InviteLinkBean?> {
            return arrayOfNulls(size)
        }
    }


}
