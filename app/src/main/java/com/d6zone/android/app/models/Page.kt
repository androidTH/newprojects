package com.d6zone.android.app.models

/**
 * Created by 72752 on 2018/1/6.
 */
data class Page<T>(val list:ListBean<T>?){
    val count:Int=0
    var iAllAppointCount:Int?=0
    var iFollowCount:Int=-1
    var iAllReceiveLovePoint:Int = -1
    var unreadlist:ArrayList<LoveHeartFans>?=null
    var iMyOrder:Int=-1
}

data class ListBean<T>(val pageNum:Int? = 1,
                       val pageSize:Int? = 0,
                       val totalPage:Int? = 0,
                       val totalRecord:Int? = 0,
                       val results:ArrayList<T>? = ArrayList()
)