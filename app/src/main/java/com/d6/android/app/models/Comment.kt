package com.d6.android.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 2017/12/21.
 */
data class Comment(@SerializedName("ids") val id: String? = ""):Serializable {
    @SerializedName("userid")
    val userId: String? = ""
    val picUrl: String? = ""
    val newsId: String? = ""
    val content: String? = ""
    val createTime: Long? = 0
    @SerializedName("replyuserid")
    val replyUserId: String? = ""
    val name: String? = ""
    @SerializedName("replyname")
    val replyName: String? = ""
    val commentUserName:String?=""
    val coverUrl:String?=""
    val suqareUserName:String?=""
    val replyUserName:String?="" //回复某人的评论
    var iIsAnonymous:Int?=0 //1、匿名  2、非匿名状态
    var iIsReplyAnonymous:Int?=0 //回复的那个评论是不是匿名发布的 1 匿名 2 非匿名
    var sourceType:Int?=-1 //动态的类型 1、文字  2、图片 3、视频 4、语音
    var suqareContent:String?=""
    var iType:Int?=-1 //iType 1、评论 2、点赞
}