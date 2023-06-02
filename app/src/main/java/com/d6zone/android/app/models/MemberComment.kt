package com.d6zone.android.app.models

/**
 *     author : jinjiarui
 *     time   : 2019/05/24
 *     desc   :
 *     version:
 */
class MemberComment constructor(var content:String,headerurl:String){
    var mContent:String=""
    var mHeaderPic:String=""
    var title:String=""

    init {
        this.mContent = content
        this.mHeaderPic = headerurl
    }
}