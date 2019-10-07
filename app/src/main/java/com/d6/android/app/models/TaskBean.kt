package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 新的约会数据模型
 */
data class TaskBean(var ids :String="") :Parcelable{
    @SerializedName("sTitle") var sTitle:String=""
    @SerializedName("sDesc") var sDesc:String=""
    @SerializedName("iIsfinish") var iIsfinish:Int=0 //是否签到 1、完成 2、未签到）
    @SerializedName("iPoint") var iPoint:Int = 0
    @SerializedName("iDay")var iDay:Int = -1
    @SerializedName("iType")var iType:Int = -1 //iType  1、签到 2、约会 3、发布动态

    constructor(parcel: Parcel) : this(parcel.readString()) {
        sTitle = parcel.readString()
        sDesc = parcel.readString()
        iIsfinish = parcel.readInt()
        iPoint = parcel.readInt()
        iDay = parcel.readInt()
        iType = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ids)
        parcel.writeString(sTitle)
        parcel.writeString(sDesc)
        parcel.writeInt(iIsfinish)
        parcel.writeInt(iPoint)
        parcel.writeInt(iDay)
        parcel.writeInt(iType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskBean> {
        override fun createFromParcel(parcel: Parcel): TaskBean {
            return TaskBean(parcel)
        }

        override fun newArray(size: Int): Array<TaskBean?> {
            return arrayOfNulls(size)
        }
    }


}
