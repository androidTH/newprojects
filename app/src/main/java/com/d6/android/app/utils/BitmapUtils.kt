package com.d6.android.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.media.ExifInterface
import android.os.Build
import android.os.Environment
import android.view.WindowManager
import java.io.*
import java.math.BigDecimal



/**
 *
 */
object BitmapUtils {

    var MINWIDTH = 480
    var MINHEIGHT = 720

    data class FileInfo(val path:String){
        var width:Int=0
        var height:Int=0
    }

    fun compressImageFileWithSize(filePath:String): FileInfo {
        var fos: FileOutputStream? = null
        var tempBitmap: Bitmap? = null
        val path = AppUtils.PICDIR + filePath.hashCode() + ".jpg"
        val fileInfo = FileInfo(path)
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            var scale = 1f
            val min = Math.min(options.outWidth, options.outHeight)
            val max = Math.max(options.outWidth, options.outHeight)
            if (min >= 800) {
                scale = max / 800f
            } else if (max >= 1200) {
                scale = max / 1200f
            }
            val sampleSize = BigDecimal(scale.toDouble()).setScale(0, BigDecimal.ROUND_HALF_UP).toInt()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            options.inJustDecodeBounds = false
            options.inSampleSize = sampleSize
            options.inPurgeable = true
            options.inInputShareable = true
            tempBitmap = BitmapFactory.decodeFile(filePath,
                    options)
            val width = options.outWidth
            val height = options.outHeight
            fileInfo.width = width
            fileInfo.height = height

            val degree = readPictureDegree(filePath)
            tempBitmap = adjustPhotoRotation(tempBitmap, degree)
            val baos = ByteArrayOutputStream()
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val file = File(AppUtils.PICDIR)
            if (!file.exists()) {
                file.mkdirs()
            }
            fos = FileOutputStream(path)
            fos.write(baos.toByteArray())

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            java.lang.System.gc()
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (tempBitmap != null) {
                tempBitmap.recycle()
            }
        }
        return fileInfo
    }
    fun compressImageFile(file: File):File = compressImageFile(file.absolutePath)
    fun compressImageFile(filePath: String): File {
        val info = compressImageFileWithSize(filePath)
        return File(info.path)
    }

    fun decodeBitmapFromPath(context: Context,path:String):Bitmap?{
        val maxSize = calculateMaxBitmapSize(context)
        return decodeBitmapFromPath(path,maxSize[0],maxSize[1])
    }

    fun decodeBitmapFromPath(filePath: String, maxWidth: Int, maxHeight: Int): Bitmap? {
        var inputStream: InputStream? = null
        var bitmap: Bitmap? = null
        try {
            val file = File(filePath)
            val sampleSize = calculateBitmapSampleSize(file, maxHeight, maxWidth)
            inputStream = FileInputStream(file)
            val option = BitmapFactory.Options()
            option.inSampleSize = sampleSize
            option.inPreferredConfig = Bitmap.Config.ARGB_8888
            option.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeStream(inputStream, null, option)
            val degree = readPictureDegree(filePath)
            bitmap = adjustPhotoRotation(bitmap, degree)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e : OutOfMemoryError) {
            //            java.lang.System.gc();
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return bitmap
    }

    private fun adjustPhotoRotation(bm: Bitmap, orientationDegree: Int): Bitmap {

        val matrix = Matrix()
        matrix.postRotate(orientationDegree.toFloat())
        return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
    }

    /**
     * 读取照片exif信息中的旋转角度

     * @param path
     * *            照片路径
     * *
     * @return 角度
     */
    private fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    private fun calculateMaxBitmapSize(context: Context): IntArray {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        val width: Int
        val height: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size)
            width = size.x
            height = size.y
        } else {
            width = display.width
            height = display.height
        }
        return intArrayOf(width, height)
    }

    @Throws(IOException::class)
    private fun calculateBitmapSampleSize(file: File, maxHeight: Int, maxWidth: Int): Int {
        var inputStream: InputStream? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            inputStream = FileInputStream(file)
            BitmapFactory.decodeStream(inputStream, null, options) // Just get image size
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (t: Throwable) {
                    // Do nothing
                }
            }
        }
        var sampleSize = 1
        while (options.outHeight / sampleSize > maxHeight || options.outWidth / sampleSize > maxWidth) {
            sampleSize = sampleSize shl 1
        }
        return sampleSize
    }

    @Throws(IOException::class)
    public fun getcalculateBitmapSampleSize(file: File): List<Int> {
        var inputStream: InputStream? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            inputStream = FileInputStream(file)
            BitmapFactory.decodeStream(inputStream, null, options) // Just get image size
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (t: Throwable) {
                    // Do nothing
                }
            }
        }
        val sampleSize: List<Int> = listOf(options.outWidth,options.outHeight)
        return sampleSize
    }

    public fun saveImageToFile(bmp:Bitmap):String{
        val appDir = File(Environment.getExternalStorageDirectory(), "d6")
        // 测试由此抽象路径名表示的文件或目录是否存在
        if (!appDir.exists()) {
            //如果不存在，则创建由此抽象路径名命名的目录

            appDir.mkdir()
        }
        // 然后自定义图片的文件名称
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        // 创建file对象
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
       return file.path
    }
}