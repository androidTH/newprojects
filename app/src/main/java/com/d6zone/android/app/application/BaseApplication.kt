package com.d6zone.android.app.application

import android.app.Application
import com.d6zone.android.app.utils.AppUtils

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