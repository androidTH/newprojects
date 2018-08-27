package com.d6.android.app.utils

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import java.io.File

/**
 *
 */
class AppUtils {

    companion object {
        var DEBUG = true
        var context: Context? = null
        var SP_NAME :String? = null
        var PICDIR :String? = null

        fun init(context: Context) {
            this.context = context.applicationContext
            initFilePath()
            val config = ImagePipelineConfig.newBuilder(context)
                    .setDownsampleEnabled(true)
                    .build()
            Fresco.initialize(this.context,config)
        }

        fun initFilePath() {
            if (TextUtils.isEmpty(PICDIR)) {
                if (context?.externalCacheDir == null) {
                    AppUtils.PICDIR = Environment.getExternalStorageDirectory().absolutePath + "/Android/data/" + context?.packageName + "/cache/"
                } else {
                    AppUtils.PICDIR = context?.externalCacheDir!!.absolutePath + "/"
                }
            }
            val file: File? = File(PICDIR)
            if (!file!!.exists()) {
                file.mkdirs()
            }
        }
    }
}