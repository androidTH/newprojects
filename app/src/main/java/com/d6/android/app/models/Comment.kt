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
}