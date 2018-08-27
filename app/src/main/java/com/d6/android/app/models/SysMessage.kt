package com.d6.android.app.models

import java.io.Serializable

/**
 *
 */
data class SysMessage(val id: String? = "") : Serializable {
    val content: String? = ""
    val title: String? = ""
    val createTime: Long? = 0
    val urltype: Int? = -1
        get() = field ?: -1
    val count: Int? = 0
    val url: String? = ""
    val userid: String? = ""
}