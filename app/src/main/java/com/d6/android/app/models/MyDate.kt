package com.d6.android.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 2017/12/16.
 */
data class MyDate(@SerializedName("ids") val id: String?) : Serializable {
    @SerializedName("nianling")
    val age: String? = ""
        get() = field ?: "0"
    @SerializedName("shengao")
    val height: String? = "0"
        get() = field ?: "0"
    @SerializedName("tizhong")
    val weight: String? = "0"
        get() = field ?: "0"
    val city: String? = ""
    @SerializedName("zhiye")
    val job: String? = ""
    @SerializedName("xingquaihao")
    val hobbit: String? = ""
    val speednumber: String? = ""
    val selfnumber: String? = ""//自主发布使用
    @SerializedName("loginName")
    val looknumber: String? = ""//觅约使用
    val content: String? = ""//自主发布使用
    val lookcontent: String? = ""//觅约内容
    val userhandlookwhere: String? = ""//觅约地点
    val userlookwhere: String? = ""//觅约地点
    val speedwhere: String? = ""
    val speedcity: String? = ""
    val speedfriendstand: String? = ""
    @SerializedName("duifangyaoqiu")
    val lookfriendstand: String? = ""
    val speedcontent: String? = ""
    val handspeedwhere: String? = ""
    val coverurl: String? = ""
    val speedpics: String? = ""
    val lookpics: String? = ""
    val speedtype: String? = ""
    val speedstate: String? = ""
    val name: String? = ""
    val picUrl: String? = ""
    @SerializedName("userid")
    val userId: String? = ""
    val sex: String? = ""
    val beginTime: String? = ""
    val endTime: String? = ""
    val createTime: Long? = 0
    val classesname: String? = ""
    val lookstate: String? = ""
    val zuojia: String? = ""
    val screen: String? = ""
    val selfpicurl: String? = ""//自助发布图片
    @SerializedName("iType")
    var iType:Int?=-1  // 1你约  2 速约
    fun getSpeedStateStr(): String {
        return when ( lookstate?:speedstate) {
            "1" -> "救火"
            "2" -> "征求"
            "3" -> "急约"
            "4" -> "旅行约"
            else -> "救火"
        }
    }

//    override fun toString(): String {
//        val s = "MyDate(id=$id, age=$age,  weight=$weight,  height=$height,  city=$city, job=$job, hobbit=$hobbit, speednumber=$speednumber, selfnumber=$selfnumber, looknumber=$looknumber, content=$content, lookcontent=$lookcontent, handlookwhere=$handlookwhere, speedwhere=$speedwhere, speedfriendstand=$speedfriendstand, lookfriendstand=$lookfriendstand, speedcontent=$speedcontent, handspeedwhere=$handspeedwhere, coverurl=$coverurl, speedpics=$speedpics, speedtype=$speedtype, speedstate=$speedstate, name=$name, picUrl=$picUrl, userId=$userId, sex=$sex, beginTime=$beginTime, endTime=$endTime, createTime=$createTime, classesname=$classesname, screen=$screen, selfpicurl=$selfpicurl)"
//        System.err.println(s)
//        return s
//    }


}