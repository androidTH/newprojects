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
    var iAndroidYPrice:Int?=0
    var iEnableDate:Int?=0
    var sEnableDateDesc=""
    var sTitle:String?=""
    var sServiceArea:String?=""
    var iRecommendCount:Int?=0
    var sDesc:String?=""
    var sRemarkTop:String?=""
    var sAndroidPriceDiscount:String?=""
    var sAndroidAPriceDiscount:String?=""
    var sAndroidYPriceDiscount:String?=""
    var sAndroidPriceDiscountDesc:String?=""
    var sAndroidAPriceDiscountDesc:String?=""
    var sAndroidYPriceDiscountDesc:String?=""
    var sRemark:String?=""
    var iIsPayapp:Int?=0
    var lstPrice:List<AppMemberPrice>?=null
    var lstMembers:List<MemberTeQuan>?=null

    constructor(parcel: Parcel) : this(parcel.readValue(Int::class.java.classLoader) as? Int) {
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
        iAndroidYPrice = parcel.readValue(Int::class.java.classLoader) as? Int
        iEnableDate = parcel.readValue(Int::class.java.classLoader) as? Int
        sEnableDateDesc = parcel.readString()
        sTitle = parcel.readString()
        sServiceArea = parcel.readString()
        iRecommendCount = parcel.readValue(Int::class.java.classLoader) as? Int
        sDesc = parcel.readString()
        sRemarkTop = parcel.readString()
        sAndroidPriceDiscount = parcel.readString()
        sAndroidAPriceDiscount = parcel.readString()
        sAndroidYPriceDiscount = parcel.readString()
        sAndroidPriceDiscountDesc = parcel.readString()
        sAndroidAPriceDiscountDesc = parcel.readString()
        sAndroidYPriceDiscountDesc = parcel.readString()
        sRemark = parcel.readString()
        iIsPayapp = parcel.readValue(Int::class.java.classLoader) as? Int
        lstPrice = parcel.createTypedArrayList(AppMemberPrice)
        lstMembers = parcel.createTypedArrayList(MemberTeQuan)
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
        parcel.writeValue(iAndroidYPrice)
        parcel.writeValue(iEnableDate)
        parcel.writeString(sEnableDateDesc)
        parcel.writeString(sTitle)
        parcel.writeString(sServiceArea)
        parcel.writeValue(iRecommendCount)
        parcel.writeString(sDesc)
        parcel.writeString(sRemarkTop)
        parcel.writeString(sAndroidPriceDiscount)
        parcel.writeString(sAndroidAPriceDiscount)
        parcel.writeString(sAndroidYPriceDiscount)
        parcel.writeString(sAndroidPriceDiscountDesc)
        parcel.writeString(sAndroidAPriceDiscountDesc)
        parcel.writeString(sAndroidYPriceDiscountDesc)
        parcel.writeString(sRemark)
        parcel.writeValue(iIsPayapp)
        parcel.writeTypedList(lstPrice)
        parcel.writeTypedList(lstMembers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MemberBean> {
        override fun createFromParcel(parcel: Parcel): MemberBean {
            return MemberBean(parcel)
        }

        override fun newArray(size: Int): Array<MemberBean?> {
            return arrayOfNulls(size)
        }
    }

}