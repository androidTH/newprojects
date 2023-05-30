package com.d6.android.app.models

import com.google.gson.annotations.SerializedName

/**
 *
 */
data class SquareMessage(@SerializedName("ids")val id:String?="") {
    val path: String = ""
    val newsId: String = ""
    val replypicUrl: String? = ""
    @SerializedName("replyname")
    val squareContent: String? = ""
    val name: String? = ""
    val content: String? = ""
    @SerializedName("picUrl")
    val userPic: String? = ""
    val createTime: Long? = 0
    val count: Int? = 0
    var title:String?=""
    var userid:String?=""
    var url:String=""
    var urltype:String=""
    var iIsAnonymous:Int?=0 //1、匿名  2、非匿名状态
    var status:Int?=1  //1正常 2删除 3过期
}