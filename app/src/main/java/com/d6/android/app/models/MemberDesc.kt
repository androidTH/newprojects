package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/05/24
 *     desc   :
 *     version:
 */
class MemberDesc constructor(var content:String,var title:String, headerurl:String){
    var mContent:String=""
    var mHeaderPic:String=""
    var mTitle:String=""

    init {
        this.mContent = content
        this.mTitle = title
        this.mHeaderPic = headerurl
    }
}