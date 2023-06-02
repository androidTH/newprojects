package com.d6zone.android.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 新的约会数据模型
 */
data class NewDateBean(@SerializedName("ids") var ids :String="") :Serializable{
    @SerializedName("userid") var userid:String?=""
//    var userlookwhere: String? = ""
//    var lookhomepage:String?=""
//    var userhandlookwhere: String? = ""
//    var coverurl:String?=""
//    var looktype:Int?=-1
//    var createTime:String?=""
    @SerializedName("lookpics")var lookpics:String?=""
//    var lookstate:String?=""
//    var guoneiarea: String? = ""
//    var guowaiarea: String? = ""
//    var arrayuserclassesid:String?=""
//    var arraylookstate:String?=""
    var name: String? = ""
//    var loginName:String?=""
//    var picUrl: String? = ""
//    var phone: String? = ""
//    var userclassesid: String? = ""
//    var classesname: String? = ""
    @SerializedName("sex") var sex: String? = ""
    @SerializedName("city") var city:String?=""
//    var screen:String?=""
//    var zhiye:String?=""
    @SerializedName("nianling") var nianling: String? = ""
    @SerializedName("shengao") var shengao:String?=""
    @SerializedName("tizhong") var tizhong:String?=""
//    var xingzuo:String?=""
//    var gexingqianming:String?=""
//    var ziwojieshao:String?=""
    @SerializedName("duifangyaoqiu") var duifangyaoqiu:String?=""
//    var zuojia:String?=""
    @SerializedName("iType") var iType:Int?=-1
}