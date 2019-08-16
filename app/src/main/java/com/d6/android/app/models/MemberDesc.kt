package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/05/24
 *     desc   :
 *     version:
 */
class MemberDesc constructor(var content:String,var title:String, headerurl:String,resId:Int=0){
    var mContent:String=""
    var mHeaderPic:String=""
    var mTitle:String=""
    var mResId:Int=0
    init {
        this.mContent = content
        this.mTitle = title
        this.mHeaderPic = headerurl
        this.mResId = resId
    }
}