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
}