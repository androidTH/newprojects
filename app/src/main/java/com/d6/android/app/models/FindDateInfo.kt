package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/06/11
 *     desc   :
 *     version:
 */
data class FindDateInfo(var lookabout_looktype:String=""):Parcelable{

    var rongGroup_count:Int=-1
    var lookabout_picurl:String=""
    var userpoint_picUrl:String=""
    var userpoint_allLovePoint:Int=-1
    var lookabout_type:Int=-1
    var userpoint_name:String=""
    var appointment_picurl:String=""
    var rongGroup_name:String=""
    var appointment_sendusername:String=""

    constructor(parcel: Parcel) : this() {
        lookabout_looktype = parcel.readString()
        rongGroup_count = parcel.readInt()
        lookabout_picurl = parcel.readString()
        userpoint_picUrl = parcel.readString()
        userpoint_allLovePoint = parcel.readInt()
        lookabout_type = parcel.readInt()
        userpoint_name = parcel.readString()
        appointment_picurl = parcel.readString()
        rongGroup_name = parcel.readString()
        appointment_sendusername = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lookabout_looktype)
        parcel.writeInt(rongGroup_count)
        parcel.writeString(lookabout_picurl)
        parcel.writeString(userpoint_picUrl)
        parcel.writeInt(userpoint_allLovePoint)
        parcel.writeInt(lookabout_type)
        parcel.writeString(userpoint_name)
        parcel.writeString(appointment_picurl)
        parcel.writeString(rongGroup_name)
        parcel.writeString(appointment_sendusername)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FindDateInfo> {
        override fun createFromParcel(parcel: Parcel): FindDateInfo {
            return FindDateInfo(parcel)
        }

        override fun newArray(size: Int): Array<FindDateInfo?> {
            return arrayOfNulls(size)
        }
    }
}