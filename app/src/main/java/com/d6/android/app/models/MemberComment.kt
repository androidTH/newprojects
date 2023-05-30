package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

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