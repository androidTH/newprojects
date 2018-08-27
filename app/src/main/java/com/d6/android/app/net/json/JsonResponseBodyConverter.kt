package com.d6.android.app.net.json

import com.d6.android.app.application.D6Application
import com.d6.android.app.models.Response
import com.d6.android.app.utils.sysErr
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import java.io.IOException

/**
 *
 */
class JsonResponseBodyConverter<T>(private val mGson: Gson, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {

    /**
     * 转换
     *
     * @param responseBody
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    @Suppress("UNCHECKED_CAST")
    override fun convert(responseBody: ResponseBody): T {
        val s = responseBody.string()
        sysErr("--响应数据-->" + s)
        try {
            val json = JSONObject(s)
            val code = json.optInt("res", -1)
            val time = json.optLong("systime",0)
            if (time > 0) {
                D6Application.systemTime = time
            }

            when (code) {
                1 -> {
                    if (json.optString("obj") == null || json.optString("obj") == "null") {
                        val resultData = Response<Any>(code)
                        resultData.resMsg = json.optString("resMsg")
                        resultData.data = null
                        return resultData as T
                    } else {
                        responseBody.use { _ ->
                            return adapter.fromJson(s)
                        }
                    }
                }
//                10000 -> {
//                    val resultData = Response<Any>(code)
//                    resultData.resMsg = json.optString("resMsg")
//                    return resultData as T
//                }
                else -> {
                    val resultData = Response<Any>(code)
                    resultData.resMsg = json.optString("resMsg")
                    return resultData as T
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        responseBody.use { _ ->
            return adapter.fromJson(s)
        }
    }
}