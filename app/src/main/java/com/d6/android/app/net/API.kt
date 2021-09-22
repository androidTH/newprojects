package com.d6.android.app.net

import com.d6.android.app.BuildConfig
import com.d6.android.app.utils.getDebugMode

/**
 * Created on 2017/12/27.
 */
object API {

    /**
     * 测试环境
     */
//    const val BASE_URL = "http://47.105.50.76/JyPhone/"
//    const val STATIC_BASE_URL = "http://47.105.50.76/JyPhone/#/"

    @JvmField
    var URL = "http://api_test.d6-zone.com/"

    @JvmField
    val BASE_URL = URL +"JyPhone/"
    val STATIC_BASE_URL = URL +"JyPhone/#/"

//    const val BASE_URL = "http://sh1.k9s.run:46515/liyu_new_phone/"
//    const val STATIC_BASE_URL = "http://106.15.0.107/JyPhone/#/"
    /**
     * 正式环境
     */
//    const val BASE_URL = "https://www.d6-zone.com/JyPhone/"
//    const val STATIC_BASE_URL = "https://www.d6-zone.com/JyD6/#/"

//    val BASE_URL = if(getDebugMode()) "http://47.105.50.76/JyPhone/" else "https://www.d6-zone.com/JyPhone/"
}