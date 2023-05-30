package com.d6.android.app.application

import android.app.Application
import com.d6.android.app.utils.AppUtils

/**
 *
 */
abstract class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtils.initContent(this)

    }

    abstract fun getSPName():String

}