package com.d6.android.app.models

import com.google.gson.annotations.SerializedName

/**
 * Created on 2017/12/17.
 */
data class SquareTag(@SerializedName("ids") val id: String, @SerializedName("classesname") val content: String) {
    @SerializedName("coverurl")
    var imgUrl: String? = ""
        get() = field ?: ""
    var isSelected: Boolean = false
}