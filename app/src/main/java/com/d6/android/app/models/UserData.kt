package com.d6.android.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 */
data class UserData(val accountId: String? = "") : Serializable {
    val loginName: String? = ""
    var name: String? = ""
    var classesname: String? = ""
    var userclassesid: String? = ""
    var picUrl: String? = ""
    val email: String? = ""
    val apptoken: String? = ""
    val phone: String? = ""
    var sex: String? = "" //0 是女 1是男
    var city: String? = ""
    var area: String? = ""
    @SerializedName("xingquaihao")
    var hobbit: String? = ""
    @SerializedName("zhiye")
    var job: String? = ""
    @SerializedName("nianling")
    var age: String? = ""
    @SerializedName("shengao")
    var height: String? = ""
    @SerializedName("tizhong")
    var weight: String? = ""
    @SerializedName("gexingqianming")
    var signature: String? = ""
    @SerializedName("xingzuo")
    var constellation: String? = ""
    @SerializedName("ziwojieshao")
    var intro: String? = ""
    @SerializedName("screen")
    var screen:String?=""
    var invitecode: String? = ""
    var birthday: String? = ""
    var userpics: String? = ""
    var userId: String? = ""
    var egagementtext: String? = ""
    var openEgagementflag: String? = ""
    var egagementwx: String? = ""
    var userhandlookwhere: String? = ""
    var userlookwhere: String? = ""
    var egagementtype: Int? = 0
    var iIsFollow:Int? = 0
    var iPoint:Int? = 0
    @SerializedName("zuojia")
    var zuojia:String?=""
    var iDatacompletion:Int=-1
    var iFansCountAll:Int= -1
    var iVistorCountAll:Int= -1
    var appointment:MyAppointment?=null
    var wxid:String?=""
    var sUnionid:String?=""
    var iFlowerCount:Int?=-1
    var wxname:String?=""
    var wxpic:String?=""
    var iTalkSetting:Int?=-1 //1、直接私聊  2、同意后私聊
    var isValid:String?=""
    var iIsInBlackList:Int?=-1
    var iMessageSetting:Int?=-1
    var iSquareCount:Int?=-1
    var sSquarePicList:String?="" //动态图片
    var sSourceSquarePicList:String?=""
    var sLoginToken:String?=""
    var sServicePicUrl:String?=""
    var dUserClassEndTime:Long?=0
    var iAnonymousCount:Int?=-1
    var iWeekTaskPoint:Int = -1
    var iTaskFlower:Int=-1
    var iLovePoint:Int = -1 //红心数量
    var iSendLovePoint:Int = -1//发送的红心数量
    var iReceiveLovePoint:Int = -1 //收到的红心数量
    var iReceiveNewLovePoint:Int = -1 //收到的红心数量


    override fun toString(): String {

        return "{\"accountId\":\"$accountId\"," +
                "\"name\":\"$name\"," +
                "\"picUrl\":\"$picUrl\"," +
                "\"sex\":\"$sex\"," +
                "\"city\":\"$city\"," +
                "\"area\":\"$area\"," +
                "\"xingquaihao\":\"$hobbit\"," +
                "\"zhiye\":\"$job\"," +
                "\"zuojia\":\"$zuojia\"," +
                "\"shengao\":\"$height\"," +
                "\"tizhong\":\"$weight\"," +
                "\"gexingqianming\":\"$signature\"," +
                "\"xingzuo\":\"$constellation\"," +
                "\"ziwojieshao\":\"$intro\"," +
                "\"invitecode\":\"$invitecode\"," +
                "\"birthday\":\"$birthday\"," +
                "\"userId\":\"$userId\"," +
                "\"userpics\":\"$userpics\"," +
                "\"sUnionid\":\"$sUnionid\"," +
                "\"wxid\":\"$wxid\"" +
                "}"
    }
}