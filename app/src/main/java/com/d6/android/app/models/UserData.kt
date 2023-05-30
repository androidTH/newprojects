package com.d6.android.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 */
data class UserData(val accountId: String? = "") : Serializable {
    val loginName: String? = ""
    var name: String? = null
    var classesname: String? = ""
    var userclassesid: String? = ""
    var picUrl: String? = ""
    val email: String? = ""
    val apptoken: String? = ""
    val phone: String? = ""
    var sex: String? = "" //0 是女 1是男
    var city: String? = ""
    @SerializedName("sPosition")
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
    var userhandlookwhere: String? = ""//国外区域
    var userlookwhere: String? = ""//国内区域
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
    var iLastDayExposureCount:Int = 0 //昨天卡片曝光量
    var iAllExposureCount:Int = 0 //总得卡片曝光量
    var iLastDayReceiveLovePoint:Int = 0 //昨日收到的喜欢数量
    var iIsFind:Int = -1 //1、显示 2、不显示
    var sOnlineMsg:String = ""
    var iOnline:Int = -1
    var iListSetting:Int = -1 //是否显示 1、显示 2、不显示
    var iSendPointShow:Int = -1 //发送的爱心是否隐藏 1、不隐藏 2、隐藏
    var iShowInviteMessage:Int = -1 //1、推送 2、不推送
    var iIsCreateGroup:Int = -1 //1、不允许  2、允许
    var iMySendAllLovePoint:Int= -1 //当前登录用户送给这个用户的总的爱心数量
    var iLovePointShow:Int = -1 //超级喜欢的爱心数量
    var iPhonePrivacy:Int = -1 //手机通讯录隐私保护是否开启 1、开启 2、不开启
    var isShowGift:Int = -1  //0展示礼物 1不展示礼物
    var orderNum:Int = -1 //排名
    var iAllLovePoint:Int = -1 //收到的爱心数
    var orderType:Int = -1 //榜单类型 1月榜 2年榜 3总榜

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