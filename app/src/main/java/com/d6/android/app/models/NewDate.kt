package com.d6.android.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 新的约会数据模型
 */
data class NewDate(val ids :String="") :Serializable{
    @SerializedName("accountId") val accountId: String = ""
    @SerializedName("state") var state: Int? = 0
    @SerializedName("name") val name: String? = ""
    @SerializedName("sex") val sex: String? = ""
    @SerializedName("picUrl") val picUrl: String? = ""
    @SerializedName("nianling") val nianling: String? = ""
    @SerializedName("userclassesid") val userclassesid: String? = ""
    @SerializedName("classesname") val classesname: String? = ""
    @SerializedName("userpics") val userpics: String? = ""
    @SerializedName("userhandlookwhere") val userhandlookwhere: String? = ""
    @SerializedName("userlookwhere") val userlookwhere: String? = ""
    @SerializedName("egagementtype") val egagementtype: String? = ""
    @SerializedName("egagementtext") val egagementtext: String? = ""
    @SerializedName("openEgagementflag") val openEgagementflag: String? = ""
    @SerializedName("phone") val phone: String? = ""
    @SerializedName("egagementwx") val egagementwx: String? = ""
    @SerializedName("seecount") val seecount: String? = ""
    @SerializedName("guoneiarea") val guoneiarea: String? = ""
    @SerializedName("guowaiarea") val guowaiarea: String? = ""
}