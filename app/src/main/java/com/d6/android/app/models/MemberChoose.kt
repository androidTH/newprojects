package com.d6.android.app.models

import android.os.Parcel
import android.os.Parcelable

/**
 *     author : jinjiarui
 *     time   : 2019/05/24
 *     desc   :
 *     version:
 */
class MemberChoose constructor(var content:String, var title:String,var id:Int){
    var mContent:String=""
    var mTitle:String=""
    var mSId:Int=0
    init {
        this.mContent = content
        this.mTitle = title
        this.mSId = id
    }
}