package com.d6.android.app.utils

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.widget.TextView
import com.d6.android.app.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import java.io.File
import android.os.Build.VERSION_CODES.KITKAT
import android.provider.Settings


/**
 *
 */
class AppUtils {

    companion object {
        var DEBUG = true
        var context: Context? = null
        var SP_NAME :String? = null
        var PICDIR :String? = null

        fun setUserWallet(context: Context,value:CharSequence , start:Int,end :Int, tv: TextView){
            val ss = SpannableString(value)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_wallet_leftstyle), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_wallet_rightstyle), end,
                    value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            tv.setText(ss, TextView.BufferType.SPANNABLE)
        }


        fun setTvStyle(context: Context,value:CharSequence , start:Int,end :Int, tv: TextView){
            val ss = SpannableString(value)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_auth_style_title), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_auth_style_desc), end,
                    value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            tv.setText(ss, TextView.BufferType.SPANNABLE)
        }

        fun setTvTag(context: Context,value:CharSequence , start:Int,end :Int, tv: TextView){
            val ss = SpannableString(value)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_tags_iconleft), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_tags_iconright), end,
                    value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            tv.setText(ss, TextView.BufferType.SPANNABLE)
        }

        fun setUserInfoTvTag(context: Context,value:CharSequence , start:Int,end :Int, tv: TextView){
            val ss = SpannableString(value)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_userinfotags_iconleft), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_userinfotags_iconright), end,
                    value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            if(value.length>=20){

            }
            tv.setText(ss, TextView.BufferType.SPANNABLE)
        }

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