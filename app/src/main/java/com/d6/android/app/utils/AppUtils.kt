package com.d6.android.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.support.annotation.NonNull
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.widget.TextView
import com.d6.android.app.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import java.io.File
import android.util.Log
import com.facebook.common.internal.Preconditions
import java.math.BigDecimal


/**
 *
 */
class AppUtils {

    companion object {
        var DEBUG = true
        var context: Context? = null
        var SP_NAME :String? = null
        var PICDIR :String? = null

        fun setMemberNums(context: Context,type:Int=1,value:CharSequence , start:Int,end :Int, tv: TextView){
            val ss = SpannableString(value)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_member_leftstyle), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            if(type == 1){
                ss.setSpan(TextAppearanceSpan(context, R.style.tv_member_rightstyle), end,
                        value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }else if(type==2){
                ss.setSpan(TextAppearanceSpan(context, R.style.tv_member_rightf7astyle), end,
                        value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            tv.setText(ss, TextView.BufferType.SPANNABLE)
        }

        fun setUserWallet(context: Context,value:CharSequence , start:Int,end :Int, tv: TextView){
            val ss = SpannableString(value)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_wallet_leftstyle), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_wallet_rightstyle), end,
                    value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            tv.setText(ss, TextView.BufferType.SPANNABLE)
        }

        fun setDateDialog(context: Context,value:CharSequence , start:Int,end :Int, tv: TextView){
            val ss = SpannableString(value)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_date_leftstyle), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_date_rightstyle), end,
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

        fun setTvNewTag(context: Context,value:CharSequence , start:Int,end :Int, tv: TextView){
            val ss = SpannableString(value)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_tags_iconbfffffff), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_tags_iconbwhite), end,
                    value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            tv.setText(ss, TextView.BufferType.SPANNABLE)
        }

        fun setUserInfoTvTag(context: Context,value:CharSequence , start:Int,end :Int, tv: TextView){
            val ss = SpannableString(value)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_userinfotags_iconleft), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(TextAppearanceSpan(context, R.style.tv_userinfotags_iconright), end,
                    value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tv.setText(ss, TextView.BufferType.SPANNABLE)
        }

        fun setTextViewSpannable(context: Context,value:CharSequence , start:Int,end :Int, tv: TextView,appbefore:Int,appafter:Int) {
            val ss = SpannableString(value)
            ss.setSpan(TextAppearanceSpan(context, appbefore), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(TextAppearanceSpan(context, appafter), end,
                    value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tv.setText(ss, TextView.BufferType.SPANNABLE)
        }

        fun init(context: Context) {
            this.context = context.applicationContext
            initFilePath()

            var imagePipelineConfig = ImagePipelineConfig.newBuilder(Preconditions.checkNotNull(context))
                    .setBitmapsConfig(Bitmap.Config.ARGB_8888) // 若不是要求忒高清显示应用，就用使用RGB_565吧（默认是ARGB_8888)
                    .setDownsampleEnabled(true)
                    .build()
            Fresco.initialize(context,imagePipelineConfig)
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

        //0代表相等，1代表version1大于version2，-1代表version1小于version2
        fun compareVersion(version1:String,version2:String):Int{
            if (version1.equals(version2)) {
                return 0
            }
            val version1Array = version1.split("\\.")
            val version2Array = version2.split("\\.")
            Log.d("HomePageActivity", "version1Array==" + version1Array.size)
            Log.d("HomePageActivity", "version2Array==" + version2Array.size)
            var index = 0
            // 获取最小长度值
            val minLen = Math.min(version1Array.size, version2Array.size)

            // 循环判断每位的大小
            var diff =version1Array[index].toInt() - version2Array[index].toInt()
            while (index < minLen && diff == 0) {
                index++
                diff = version1Array[index].toInt() - version2Array[index].toInt()
            }
            if (diff == 0) {
                // 如果位数不一致，比较多余位数
                for (i in index until version1Array.size) {
                    if (Integer.parseInt(version1Array[i]) > 0) {
                        return 1
                    }
                }

                for (i in index until version2Array.size) {
                    if (Integer.parseInt(version2Array[i]) > 0) {
                        return -1
                    }
                }
                return 0
            } else {
                return if (diff > 0) 1 else -1
            }
        }

        private var HWRatio = 1.0f
        fun setHWRatio(context:Context){
            if(HWRatio==1.0f){
                HWRatio = AppScreenUtils.getPhoneRatio(context)
            }
        }

        fun setRealHWRatio(context:Context){
            if(HWRatio==1.0f){
                HWRatio = AppScreenUtils.getPhoneRealRatio(context)
            }
        }

        fun setHWRatio(context:Context,isShow:Boolean,navHeight:Int){
            if(HWRatio==1.0f){
                HWRatio = AppScreenUtils.getPhoneRealRatio(context,isShow,navHeight)
            }
        }

        fun getWHRatio():Float{
            var bigDecimal =BigDecimal(HWRatio.toDouble())
            Log.i("mLayoutNormal","${HWRatio}")
            return bigDecimal.setScale(1,BigDecimal.ROUND_HALF_UP).toFloat()
        }
    }
}