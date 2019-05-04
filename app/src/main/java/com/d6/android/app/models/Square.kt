package com.d6.android.app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 2017/12/17.
 */
data class Square(@SerializedName("ids") val id: String? = ""):Serializable {
    var name: String? = ""
    var picUrl: String? = ""//头像
    var title: String? = ""
    var content: String? = ""
    var updatetime: Long? = 0
    var userid: String? = ""
    @SerializedName("classesname")
    var classesName: String? = ""
    @SerializedName("userclassesname")
    var userclassesname:String?=""
    @SerializedName("coverurl")
    var imgUrl: String? = ""//图片集
    var city: String? = ""
    @SerializedName("commentcount")
    var commentCount: Int? = 0
    @SerializedName("upvote")
    var appraiseCount: Int? = 0
    var comments: ArrayList<Comment> = ArrayList()
    var isupvote: String? = ""
    var sex: String? = ""
    var age: String? = ""
    var iFlowerCount:Int?=0
    var iIsSendFlower:Int?=0 //iIsSendFlower 大于0送过花，等于0没送过
    var sSourceSquarePics:String?=""
    var userclassesid:String?=""
}