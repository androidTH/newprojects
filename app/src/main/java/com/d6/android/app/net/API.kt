package com.d6.android.app.net

/**
 * Created on 2017/12/27.
 */
object API {

    /**
     * 测试环境
     */
//    @JvmField
//    val LIYU_URL ="JyPhone/"
//
//    @JvmField
//    var URL = "http://test76.d6-zone.com/"
//
//    @JvmField
//    val BASE_URL = URL + LIYU_URL    //JyPhone/
//    val STATIC_BASE_URL = URL + LIYU_URL+"#/"
//
//    const val GETDOMAIN = "http://domain_test.d6-zone.com/getDomain"

    /**
     * 正式环境
     */
    @JvmField
    val LIYU_URL ="JyPhone/"

    @JvmField
    var URL = "https://api-v2.d6-zone.com/"

    @JvmField
    val BASE_URL = URL+ LIYU_URL
    val STATIC_BASE_URL = "https://www.d6-zone.com/JyD6/#/"

    const val GETDOMAIN = "http://domain.d6-zone.com/getDomain"



//    -----------------------------------------------------------
//    const val BASE_URL = "http://47.105.50.76/JyPhone/"
//    const val STATIC_BASE_URL = "http://47.105.50.76/JyPhone/#/"

//    const val BASE_URL = "http://sh1.k9s.run:46515/liyu_new_phone/"
//    const val STATIC_BASE_URL = "http://106.15.0.107/JyPhone/#/"
//    ------------------------------------------------------------

//    @JvmField
//    val LIYU_URL ="JyPhone/"
//
//    @JvmField
//    var URL = "http://sh1.k9s.run:46515/"
//
//    @JvmField
//    val BASE_URL = URL+ LIYU_URL
//    val STATIC_BASE_URL = URL+LIYU_URL+"JyD6/#/"
//
//    const val GETDOMAIN = "http://domain.d6-zone.com/getDomain"

//    ------------------------------------------------------------
     /**
      * 正式环境
      */
//    @JvmField
//    var URL = "https://www.d6-zone.com/"
//    @JvmField
//    val BASE_URL = URL+"JyPhone/"
//    val STATIC_BASE_URL = URL+"JyD6/#/"
//    ------------------------------------------------------------
}