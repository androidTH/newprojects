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

    constructor(parcel: Parcel) : this() {
        iInviteFlower = parcel.readInt()
        iInviteCount = parcel.readInt()
        sInviteLinkPic = parcel.readString()
        sInviteDesc = parcel.readString()
        iInvitePoint = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(iInviteFlower)
        parcel.writeInt(iInviteCount)
        parcel.writeString(sInviteLinkPic)
        parcel.writeString(sInviteDesc)
        parcel.writeInt(iInvitePoint)
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