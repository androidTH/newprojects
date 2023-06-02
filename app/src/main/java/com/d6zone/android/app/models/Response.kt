package com.d6zone.android.app.models

import com.google.gson.annotations.SerializedName

/**
 * 基础响应数据
 */
data class Response<T>(var res: Int) {
    @SerializedName("obj")
    var data: T? = null
    var resMsg: String? = ""
        get() = field ?: ""
}