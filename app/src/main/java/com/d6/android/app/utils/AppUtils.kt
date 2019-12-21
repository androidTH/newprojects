package com.d6.android.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
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
            if(version1.isNullOrEmpty()||version2.isNullOrEmpty()){
                return 3
            }
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

//        fun getPhoneType():Boolean{
//            var model = android.os.Build.MODEL
//            var  manufacturer = Build.MANUFACTURER;
//            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
//                return true;
//            }
//            Log.i("phoneTyep","${model}---${manufacturer}")
//            return false;
//        }

    fun fastblur(sentBitmap: Bitmap, radius: Int): Bitmap? {


        val bitmap = sentBitmap.copy(sentBitmap.config, true)

        if (radius < 1) {
            return null
        }

        val w = bitmap.width
        val h = bitmap.height

        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))

        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }

        yi = 0
        yw = yi

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius

            x = 0
            while (x < w) {

                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]

                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x

                sir = stack[i + radius]

                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]

                rbs = r1 - Math.abs(i)

                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs

                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }

                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
                y++
            }
            x++
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h)

        return bitmap
    }
    }
}