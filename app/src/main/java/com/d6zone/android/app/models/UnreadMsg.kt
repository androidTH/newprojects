package com.d6zone.android.app.models

import com.google.gson.annotations.SerializedName

/**
 *     author : jinjiarui
 *     time   : 2018/10/16
 *     desc   :
 *     version:
 */
data class UnreadMsg(@SerializedName("unreadCount") var unreadCount:Int?=-1) {
}