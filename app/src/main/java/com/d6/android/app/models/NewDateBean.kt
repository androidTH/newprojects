package com.d6.android.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 新的约会数据模型
 */
data class NewDateBean(val ids :String="") :Serializable{
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
    @SerializedName("looktype") val looktype:Int?=-1
    @SerializedName("iType") val iType:Int?=-1
    @SerializedName("userid") val userid:String?=""
    @SerializedName("lookhomepage") val lookhomepage:String?=""
    @SerializedName("coverurl") val coverurl:String?=""
    @SerializedName("createTime") val createTime:String?=""
    @SerializedName("lookpics") val lookpics:String?=""
    @SerializedName("lookstate") val lookstate:String?=""
    @SerializedName("arrayuserclassesid") val arrayuserclassesid:String?=""
    @SerializedName("arraylookstate") val arraylookstate:String?=""
    @SerializedName("loginName") val loginName:String?=""
    @SerializedName("city") val city:String?=""
    @SerializedName("screen") val screen:String?=""
    @SerializedName("zhiye") val zhiye:String?=""
    @SerializedName("shengao") val shengao:String?=""
    @SerializedName("tizhong") val tizhong:String?=""
    @SerializedName("gexingqianming") val gexingqianming:String?=""
    @SerializedName("ziwojieshao") val ziwojieshao:String?=""
    @SerializedName("duifangyaoqiu") val duifangyaoqiu:String?=""
    @SerializedName("zuojia") val zuojia:String?=""
     val speednumber:String?=""
     val speedstate:String?=""
     val speedhomepage:String?=""
     val speedwhere:String?=""
     val speedcontent:String?=""
     val handspeedwhere:String?=""
     val speedtype:String?=""

}