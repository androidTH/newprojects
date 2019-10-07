package com.d6.android.app.application

import android.app.Application
import com.d6.android.app.utils.AppUtils

/**
 *
 */
abstract class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtils.init(this)

    }

    abstract fun getSPName():String

}