package com.d6zone.android.app.interfaces

import io.reactivex.disposables.Disposable

/**
 * Created on 2017/12/27.
 */
interface RequestManager {
    fun showToast(msg:String)
    fun dismissDialog()
    fun onBind(disposable:Disposable)
}