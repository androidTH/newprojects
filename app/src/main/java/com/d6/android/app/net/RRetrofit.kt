package com.d6.android.app.net

import com.d6.android.app.net.json.JsonConverterFactory
import com.d6.android.app.utils.Const
import com.d6.android.app.utils.SPUtils
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络请求retrofit初始化。
 */
class RRetrofit private constructor(){
//    val logger = HttpLoggingInterceptor()
//    init {
//        logger.level = HttpLoggingInterceptor.Level.BASIC
//    }

    private val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(API.BASE_URL)
            .addConverterFactory(JsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
                    .addNetworkInterceptor {
                        val token = SPUtils.instance().getString(Const.User.USER_TOKEN)
                        val userId = SPUtils.instance().getString(Const.User.USER_ID)
                        val sex = SPUtils.instance().getString(Const.User.USER_SEX)
                        val origin = it.request()
                        val newRequest = origin.newBuilder()
                                .addHeader("token",token)
                                .addHeader("accountId",userId)
                                .addHeader("sex",sex)
//                                .addHeader("Connection","close")
                                .build()
                        it.proceed(newRequest)
                    }.connectTimeout(5,TimeUnit.MINUTES)//connectTimeout(10, TimeUnit.SECONDS)
                    .connectionPool(ConnectionPool(5,1,TimeUnit.SECONDS)).build())
            .build()

    companion object {
        fun instance() : RRetrofit = RRetrofit()
    }

    fun <T>create(clazz: Class<T>) :T = retrofit.create(clazz)
}