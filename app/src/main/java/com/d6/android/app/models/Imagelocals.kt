package com.d6.android.app.models

/**
 *     author : jinjiarui
 *     time   : 2019/09/05
 *     desc   :
 *     version:
 */
class Imagelocals(urls:ArrayList<String>, type:Int,index:Int) {
    var mUrls = ArrayList<String>()
    var mType:Int = 0
    var position:Int = 0
    init {
        mUrls= urls
        mType = type
        position = index
    }
}