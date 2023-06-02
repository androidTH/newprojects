package com.d6zone.android.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 *     author : jinjiarui
 *     time   : 2019/06/19
 *     desc   :
 *     version:
 */
data class PiecesMarkBean(@SerializedName("sId")var sId:String):Parcelable{

    var piecesMark:String?=""
    var title:String?=""
    var keywork:String?=""
    var description:String?=""
    var userid:String?=""
    var content:String?=""
    var ext1:String?=""
    var ext2:String?=""
    var ext4:String?=""
    var ext5:String?="" //0-不显示 1-不强制更新 2-强制更新

    constructor(parcel: Parcel) : this(parcel.readString()) {
        piecesMark = parcel.readString()
        title = parcel.readString()
        keywork = parcel.readString()
        description = parcel.readString()
        userid = parcel.readString()
        content = parcel.readString()
        ext1 = parcel.readString()
        ext2 = parcel.readString()
        ext4 = parcel.readString()
        ext5 = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sId)
        parcel.writeString(piecesMark)
        parcel.writeString(title)
        parcel.writeString(keywork)
        parcel.writeString(description)
        parcel.writeString(userid)
        parcel.writeString(content)
        parcel.writeString(ext1)
        parcel.writeString(ext2)
        parcel.writeString(ext4)
        parcel.writeString(ext5)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PiecesMarkBean> {
        override fun createFromParcel(parcel: Parcel): PiecesMarkBean {
            return PiecesMarkBean(parcel)
        }

        override fun newArray(size: Int): Array<PiecesMarkBean?> {
            return arrayOfNulls(size)
        }
    }
}