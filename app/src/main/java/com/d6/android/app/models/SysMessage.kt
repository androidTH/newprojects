package com.d6.android.app.models

import java.io.Serializable

/**
 *
 */
data class SysMessage(val ids: Int = 0) : Serializable {
    val content: String? = ""
    val title: String? = ""
    val createTime: Long? = 0
//    val urltype: Int? = -1
//        get() = field ?: -1
    val urltype: String? = "-1"
        get() = field ?: "-1"
    val count: Int? = 0
    val url: String? = ""
    val userid: String? = ""
    val arrayuserids:String?=""
}