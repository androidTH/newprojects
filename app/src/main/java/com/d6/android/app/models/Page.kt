package com.d6.android.app.models

/**
 * Created by 72752 on 2018/1/6.
 */
data class Page<T>(val list:ListBean<T>?){
    val count:Int?=0
    var iAllAppointCount:Int?=0
}

data class ListBean<T>(val pageNum:Int? = 1,
                       val pageSize:Int? = 0,
                       val totalPage:Int? = 0,
                       val totalRecord:Int? = 0,
                       val results:ArrayList<T>? = ArrayList()
)