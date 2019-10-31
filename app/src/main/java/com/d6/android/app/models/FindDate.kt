package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class FindDate(var accountId:String?="") :Parcelable {
        var id=null
        var wxid=null
        var loginName:String?=""
        var password = null
        var egagementuseridlist = null
        var iIsFollow:Int?=-1
        var loginuserid = null
        var egagementwx = null
        var zizhuhoutai:Int=-1
        var invitecode = null
        var name:String=""
        var picUrl:String=""
        var email:String=""
        var createTime:String=""
        var updateTime= null
        var apptoken=null
        var deviceToken = null
        var logintype = null
        var guoneiguowai:String=""
        var duifangyaoqiu:String=""
        var egagementtext:String=""
        var phone:String=""
        var userclassesid = ""
        var classesname:String=""
        var sex:String=""
        var city:String=""
        var screen:String=""
        var xingquaihao:String=""
        var zhiye:String=""
        var nianling:String=""
        var shengao:String=""
        var tizhong:String=""
        var gexingqianming:String=""
        var xingzuo:String=""
        var ziwojieshao:String=""
        var devicetype=null
        var isValid = null
        var canpublishsquare:String=""
        var imessageids=null
        var onlineflag=null
        var zuojia:String=""
        var userhandlookwhere:String=""
        var userlookwhere:String=""
        var userpics:String=""
        var birthday:String=""
        var openEgagementflag= null
        var egagementcount=null
        var iPoint:Int=-1
        var sPosition:String=""
        var iFansCountAll:Int=-1
        var iVistorCountAll:Int=-1
        var lstUserid =null
        var iIsFans:Int=-1 //0代表没有喜欢 1代表已喜欢
        var iReceiveLovePoint:Int = -1 //添加收到的喜欢总数
        var sOnlineMsg:String?=""
        var iOnline:Int =-1
        var iPositionType:Int = -1

    constructor(parcel: Parcel) : this(parcel.readString()) {
        loginName = parcel.readString()
        iIsFollow = parcel.readValue(Int::class.java.classLoader) as? Int
        zizhuhoutai = parcel.readInt()
        name = parcel.readString()
        picUrl = parcel.readString()
        email = parcel.readString()
        createTime = parcel.readString()
        guoneiguowai = parcel.readString()
        duifangyaoqiu = parcel.readString()
        egagementtext = parcel.readString()
        phone = parcel.readString()
        userclassesid = parcel.readString()
        classesname = parcel.readString()
        sex = parcel.readString()
        city = parcel.readString()
        screen = parcel.readString()
        xingquaihao = parcel.readString()
        zhiye = parcel.readString()
        nianling = parcel.readString()
        shengao = parcel.readString()
        tizhong = parcel.readString()
        gexingqianming = parcel.readString()
        xingzuo = parcel.readString()
        ziwojieshao = parcel.readString()
        canpublishsquare = parcel.readString()
        zuojia = parcel.readString()
        userhandlookwhere = parcel.readString()
        userlookwhere = parcel.readString()
        userpics = parcel.readString()
        birthday = parcel.readString()
        iPoint = parcel.readInt()
        sPosition = parcel.readString()
        iFansCountAll = parcel.readInt()
        iVistorCountAll = parcel.readInt()
        iIsFans = parcel.readInt()
        iReceiveLovePoint = parcel.readInt()
        sOnlineMsg = parcel.readString()
        iPositionType = parcel.readInt()
        iOnline = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountId)
        parcel.writeString(loginName)
        parcel.writeValue(iIsFollow)
        parcel.writeInt(zizhuhoutai)
        parcel.writeString(name)
        parcel.writeString(picUrl)
        parcel.writeString(email)
        parcel.writeString(createTime)
        parcel.writeString(guoneiguowai)
        parcel.writeString(duifangyaoqiu)
        parcel.writeString(egagementtext)
        parcel.writeString(phone)
        parcel.writeString(userclassesid)
        parcel.writeString(classesname)
        parcel.writeString(sex)
        parcel.writeString(city)
        parcel.writeString(screen)
        parcel.writeString(xingquaihao)
        parcel.writeString(zhiye)
        parcel.writeString(nianling)
        parcel.writeString(shengao)
        parcel.writeString(tizhong)
        parcel.writeString(gexingqianming)
        parcel.writeString(xingzuo)
        parcel.writeString(ziwojieshao)
        parcel.writeString(canpublishsquare)
        parcel.writeString(zuojia)
        parcel.writeString(userhandlookwhere)
        parcel.writeString(userlookwhere)
        parcel.writeString(userpics)
        parcel.writeString(birthday)
        parcel.writeInt(iPoint)
        parcel.writeString(sPosition)
        parcel.writeInt(iFansCountAll)
        parcel.writeInt(iVistorCountAll)
        parcel.writeInt(iIsFans)
        parcel.writeInt(iReceiveLovePoint)
        parcel.writeString(if(sOnlineMsg!=null) sOnlineMsg else "")
        parcel.writeInt(iPositionType)
        parcel.writeInt(iOnline)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FindDate> {
        override fun createFromParcel(parcel: Parcel): FindDate {
            return FindDate(parcel)
        }

        override fun newArray(size: Int): Array<FindDate?> {
            return arrayOfNulls(size)
        }
    }
}