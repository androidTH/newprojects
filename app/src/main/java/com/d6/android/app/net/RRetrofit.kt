package com.d6.android.app.net

import android.util.Log
import cn.liaox.cachelib.CacheDBLib
import com.d6.android.app.R
import com.d6.android.app.net.HttpsFactroy.getSSLSocketFactory
import com.d6.android.app.net.json.JsonConverterFactory
import com.d6.android.app.utils.*
import okhttp3.CacheControl
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * 网络请求retrofit初始化。
 */
class RRetrofit private constructor(){
//    val logger = HttpLoggingInterceptor()
//    init {
//        logger.level = HttpLoggingInterceptor.Level.BASIC
//    }

    val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
        if (message.startsWith("<-- 200 OK")) {
            Log.d("HttpLoggingInterceptor", "--------------------------------------------------------------------")

        }
        Log.d("HttpLoggingInterceptor", JsonUtil.decodeUnicode(message))
        if (message.startsWith("<-- END HTTP")) {
            Log.d("HttpLoggingInterceptor", "----------------------------------------------------------")
        }
    })

    private val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(API.BASE_URL)
            .addConverterFactory(JsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor.apply { level = HttpLoggingInterceptor.Level.BODY })
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
                    }.connectTimeout(30,TimeUnit.SECONDS)
                    .readTimeout(30,TimeUnit.SECONDS)
                    .writeTimeout(30,TimeUnit.SECONDS)//connectTimeout(10, TimeUnit.SECONDS)
                    .connectionPool(ConnectionPool(5,1,TimeUnit.SECONDS)).build())
            .build()

    companion object {
        fun instance() : RRetrofit = RRetrofit()
    }

    fun <T>create(clazz: Class<T>) :T = retrofit.create(clazz)

}