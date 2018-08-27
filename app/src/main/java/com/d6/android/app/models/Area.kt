package com.d6.android.app.models

/**
 *
 */
data class Area(val id: String? = "") {
    val paramKey: String? = "" //paramKey 1:海外地市 0：国内地市
    val isValid: String? = ""//isValid 1 热门地市，0 普通地市
    var paramName: String? = ""
}