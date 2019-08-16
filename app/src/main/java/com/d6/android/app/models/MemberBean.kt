package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/05/21
 *     desc   :
 *     version:
 */
class MemberBean(var ids:Int?=0):Parcelable{
    var iAddPoint:String?=""
    var sAddPointDesc:String?=""
    var classesname:String?=""
    var sEnClassesname:String?=""
    var describes:String?=""
    var flag:String?=""
    var sex:String?=""
    var talkcount:String?=""
    var iAndroidAPrice:Int?=0
    var iAndroidPrice:Int?=0
    var iEnableDate:Int?=0
    var sEnableDateDesc=""
    var sTitle:String?=""
    var sServiceArea:String?=""
    var iRecommendCount:Int?=0
    var sDesc:String?=""
    var sRemarkTop:String?=""
    var sRemark:String?=""
    var iIsPayapp:Int?=0

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        iAddPoint = parcel.readString()
        sAddPointDesc = parcel.readString()
        classesname = parcel.readString()
        sEnClassesname = parcel.readString()
        describes = parcel.readString()
        flag = parcel.readString()
        sex = parcel.readString()
        talkcount = parcel.readString()
        iAndroidAPrice = parcel.readValue(Int::class.java.classLoader) as? Int
        iAndroidPrice = parcel.readValue(Int::class.java.classLoader) as? Int
        iEnableDate = parcel.readValue(Int::class.java.classLoader) as? Int
        sEnableDateDesc = parcel.readString()
        sTitle = parcel.readString()
        sServiceArea = parcel.readString()
        iRecommendCount = parcel.readValue(Int::class.java.classLoader) as? Int
        sDesc = parcel.readString()
        sRemark = parcel.readString()
        sRemarkTop = parcel.readString()
        iIsPayapp = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    companion object CREATOR : Parcelable.Creator<MemberBean> {
        override fun createFromParcel(parcel: Parcel): MemberBean {
            return MemberBean(parcel)
        }

        override fun newArray(size: Int): Array<MemberBean?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(ids)
        parcel.writeString(iAddPoint)
        parcel.writeString(sAddPointDesc)
        parcel.writeString(classesname)
        parcel.writeString(sEnClassesname)
        parcel.writeString(describes)
        parcel.writeString(flag)
        parcel.writeString(sex)
        parcel.writeString(talkcount)
        parcel.writeValue(iAndroidAPrice)
        parcel.writeValue(iAndroidPrice)
        parcel.writeValue(iEnableDate)
        parcel.writeString(sEnableDateDesc)
        parcel.writeString(sTitle)
        parcel.writeString(sServiceArea)
        parcel.writeValue(iRecommendCount)
        parcel.writeString(sDesc)
        parcel.writeString(sRemark)
        parcel.writeString(sRemarkTop)
        parcel.writeValue(iIsPayapp)
    }

    override fun describeContents(): Int {
        return 0
    }
}