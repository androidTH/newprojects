package com.d6.android.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 2017/12/17.
 */
data class Square(@SerializedName("ids") val id: String? = ""):Serializable {
    var name: String? = ""
    var picUrl: String? = ""//头像
    var title: String? = ""
    var content: String? = ""
    var updatetime: Long? = 0
    var userid: String? = ""
    @SerializedName("classesname")
    var classesName: String? = ""
    @SerializedName("userclassesname")
    var userclassesname:String?=""
    @SerializedName("squarepics")
    var imgUrl: String? = ""//图片集
    @SerializedName("squarecity")
    var city: String? = ""
    @SerializedName("commentcount")
    var commentCount: Int? = 0
    @SerializedName("upvote")
    var appraiseCount: Int? = 0
    var comments: ArrayList<Comment> = ArrayList()
    var isupvote: String? = ""
    var sex: String? = ""
    var age: String? = ""
    var iFlowerCount:Int?=0
    var iIsSendFlower:Int?=0 //iIsSendFlower 大于0送过花，等于0没送过
    var sSourceSquarePics:String?="" //原图链接
    var userclassesid:String?=""
    var screen:String?=""
    var iIsAnonymous:Int?=0 //1、匿名  2、非匿名状态
    var iIsCommentTop:Int?=1 //1、否  2、是
    var isPlaying:Boolean = false
    var sVideoUrl:String = "" //（视频的链接url）
    var sVideoPicUrl:String = "" //视频首页图片链接地址
    var sVoiceUrl:String = ""
    var sTopicName:String = "" //话题名字
    var sTopicId:String="" //话题id
    var iResourceType:Int = -1 // 1、文字  2、图片 3、视频 4、语音 ，新发布的这样区分，之前的为0
    var orders:Int= 1// orders <=0 置顶
    var sVoiceLength:String = ""
    var sVideoWidth:String =""
    var sVideoHeight:String = ""
    var iLovePoint:Int? = -1
    var iSendLovePoint:Int?=0 //0表示已赠送

    var classesid:Int?=-1 //66约会
    var sAppointmentId:String?=""
    var iAppointType:Int?=-1 //约会类型
    var dEndtime:Long =0  //"dEndtime":1539855678000,//约会截至时间
    var dStarttime:Long = 0
    var iAppointmentSignupCount:Int?=-1
}