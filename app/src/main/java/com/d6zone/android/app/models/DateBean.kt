package com.d6zone.android.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DateBean(
        @SerializedName("accountId") val accountId: String = "",
        @SerializedName("ids") val ids: String = "",
        @SerializedName("state") val state: String? = "",
        @SerializedName("name") val name: String? = "",
        @SerializedName("sex") val sex: String? = "",
        @SerializedName("picUrl") val picUrl: String? = "",
        @SerializedName("nianling") val nianling: String? = "",
        @SerializedName("userclassesid") val userclassesid: String? = "",
        @SerializedName("classesname") val classesname: String? = "",
        @SerializedName("userpics") val userpics: String? = "",
        @SerializedName("userhandlookwhere") var userhandlookwhere: String? = "",
        @SerializedName("userlookwhere") var userlookwhere: String? = "",
        @SerializedName("egagementtype") val egagementtype: Int? = 0,
        @SerializedName("egagementtext") val egagementtext: String? = "",
        @SerializedName("openEgagementflag") val openEgagementflag: String? = "",
        @SerializedName("phone") val phone: String? = "",
        @SerializedName("egagementwx") val egagementwx: String? = "",
        @SerializedName("seecount") val seecount: String? = "",
        @SerializedName("guoneiarea") val guoneiarea: String? = "",
        @SerializedName("guowaiarea") val guowaiarea: String? = "",
        @SerializedName("tizhong") val tizhong:String?="",
        @SerializedName("zhiye") val zhiye:String?="",
        @SerializedName("xingzuo") val xinzuo:String?="", //星座
        @SerializedName("shengao") val height:String?="",
        val gexingqianming: String? = ""
) : Serializable {
    val screen: Int? = -1
        get() = field ?: -1


}