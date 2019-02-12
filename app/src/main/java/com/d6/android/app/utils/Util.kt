package com.d6.android.app.utils

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.d6.android.app.BuildConfig
import com.d6.android.app.R
import com.d6.android.app.activities.DateAuthStateActivity
import com.d6.android.app.activities.UnAuthUserActivity
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.DateErrorDialog
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.Square
import com.d6.android.app.models.UserData
import com.d6.android.app.net.Request
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.CustomToast.showToast
import com.d6.android.app.widget.diskcache.DiskLruCacheHelper
import com.google.gson.JsonObject
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import org.jetbrains.anko.*
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.regex.Pattern

/**
 * 打印日志
 */
fun Any?.sysErr(msg: Any?) {
//    if (BuildConfig.DEBUG)
    Log.e("my_log", "--------$msg")
}

fun <T> Flowable<T>.ioScheduler(): Flowable<T> = this.subscribeOn(Schedulers.io())
fun <T> Flowable<T>.defaultScheduler(): Flowable<T> = this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
fun <T> Observable<T>.defaultScheduler(): Observable<T> = this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

/**
 * 屏幕尺寸工具
 */
fun Context.screenWidth(): Int {
    val dm = this.displayMetrics
    this.windowManager.defaultDisplay.getMetrics(dm)
    return dm.widthPixels
}

fun Fragment.screenWidth(): Int = activity.screenWidth()

fun View.screenWidth(): Int = context.screenWidth()

fun Context.screenHeight(): Int {
    val dm = this.displayMetrics
    this.windowManager.defaultDisplay.getMetrics(dm)
    return dm.heightPixels
}

fun Fragment.screenHeight(): Int = context.screenHeight()

fun View.screenHeight(): Int = context.screenHeight()

fun Activity.callPhone(phone: String?) {
//    val isAllow = permission(Manifest.permission.CALL_PHONE,"拨号权限",15)
//    if (isAllow) {
//        val p = phone ?: ""
//        val intent = Intent(Intent.ACTION_DIAL)
//        intent.data = Uri.parse("tel:" + p)
//        this.startActivity(intent)
//    } else {
//        toast("没有拨号权限")
//    }

}

fun Activity?.saveUserInfo(obj: UserData?) {
    if (obj == null) {
        return
    }
    SPUtils.instance().put(Const.User.USER_ID, obj.accountId)
            .put(Const.User.USER_TOKEN, obj.apptoken)
            .put(Const.User.USER_PHONE, obj.phone)
            .put(Const.User.USER_NICK, obj.name)
            .put(Const.User.USER_HEAD, obj.picUrl)
            .put(Const.User.USER_CLASS_ID, obj.userclassesid)
            .put(Const.User.USER_SEX, obj.sex)
            .put(Const.User.USER_SCREENID, obj.screen)
            .put(Const.User.USER_DATACOMPLETION, obj.iDatacompletion)
//            .put(Const.User.IS_LOGIN, true)
            .apply()
}

fun View?.visible() {
    this?.let {
        if (it.visibility != View.VISIBLE) {
            it.visibility = View.VISIBLE
        }
    }
}

fun View?.gone() {
    this?.let {
        if (it.visibility != View.GONE) {
            it.visibility = View.GONE
        }
    }
}

fun View?.invisible() {
    this?.let {
        if (it.visibility != View.INVISIBLE) {
            it.visibility = View.INVISIBLE
        }
    }
}

fun Fragment.callPhone(phone: String?) {
    activity.callPhone(phone)
}

fun Activity.getUrlPath(imageUri: Uri?): String? {
    if (imageUri == null) {
        return null
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(this, imageUri)) {
        if (isExternalStorageDocument(imageUri)) {
            val docId = DocumentsContract.getDocumentId(imageUri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(imageUri)) {
            val id = DocumentsContract.getDocumentId(imageUri)
            val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)
            return getDataColumn(this, contentUri, null, null)
        } else if (isMediaDocument(imageUri)) {
            val docId = DocumentsContract.getDocumentId(imageUri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = MediaStore.Images.Media._ID + "=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(this, contentUri, selection, selectionArgs)
        }
    } // MediaStore (and general)
    else if ("content".equals(imageUri.scheme, ignoreCase = true)) {
        // Return the remote address
        if (isGooglePhotosUri(imageUri))
            return imageUri.lastPathSegment
        return getDataColumn(this, imageUri, null, null)
    } else if ("file".equals(imageUri.scheme, ignoreCase = true)) {
        return imageUri.path
    }// File
    return null
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
fun isGooglePhotosUri(uri: Uri): Boolean = "com.google.android.apps.photos.content" == uri.authority

/**
 * @param uri The Uri to check.
 * *
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun isExternalStorageDocument(uri: Uri): Boolean =
        "com.android.externalstorage.documents" == uri.authority

/**
 * @param uri The Uri to check.
 * *
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean =
        "com.android.providers.downloads.documents" == uri.authority

/**
 * @param uri The Uri to check.
 * *
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean = "com.android.providers.media.documents" == uri.authority

fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    if (uri == null) {
        return null
    }
    var cursor: Cursor? = null
    val column = MediaStore.Images.Media.DATA
    val projection = arrayOf(column)
    try {
        cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        if (cursor != null)
            cursor.close()
    }
    return null
}

/**
 * alert弹窗
 */
fun Activity.alertDialog(title: String = "请注意", message: String, outCancel: Boolean = true, positive: String = "确定", negative: String? = null,
                         pListener: DialogInterface.OnClickListener? = null, nListener: DialogInterface.OnClickListener? = null) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setCancelable(outCancel)
    if (!TextUtils.isEmpty(positive)) {
        builder.setPositiveButton(positive, pListener)
    }
    if (!TextUtils.isEmpty(negative)) {
        builder.setNegativeButton(negative, nListener)
    }
    builder.create().show()
}

fun Any.getSDFreeSize(): Long {
    //取得SD卡文件路径
    val path = Environment.getExternalStorageDirectory()
    val sf = StatFs(path.path)
    //获取单个数据块的大小(Byte)
    val blockSize: Long
    //空闲的数据块的数量
    val freeBlocks: Long
    if (Build.VERSION.SDK_INT >= 18) {
        blockSize = sf.blockSizeLong
        freeBlocks = sf.availableBlocksLong
    } else {
        blockSize = sf.blockSize.toLong()
        freeBlocks = sf.availableBlocks.toLong()
    }
    //返回SD卡空闲大小
    //return freeBlocks * blockSize;  //单位Byte
    //return (freeBlocks * blockSize)/1024;   //单位KB
    return freeBlocks * blockSize / 1024 / 1024 //单位MB
}

/**
 * 获取文件后缀
 * @param file 文件
 * @return 文件后缀
 */
fun File?.getFileSuffix(): String {
    if (this == null) {
        return ""
    }
    if (!this.isFile) {
        return ""
    }
    val fileName = this.name
    return if (fileName.contains(".")) {
        fileName.substring(fileName.lastIndexOf(".") + 1)
    } else ""
}

inline fun Activity.isAuthUser(next: () -> Unit) {
    val className = SPUtils.instance().getString(Const.User.USER_CLASS_ID)
    val screen = SPUtils.instance().getString(Const.User.USER_SCREENID)
    if (className == "7"&&screen == "0") {// 22 普通会员
        this.startActivity<DateAuthStateActivity>()
    } else {
        next()
    }
}

inline fun Activity.isCheckOnLineAuthUser(requestManager: RequestManager, userId:String, crossinline next: () -> Unit) {
    Request.getUserInfoDetail(userId).request(requestManager,false,success = {msg,data->
            data?.let {
                if(it.screen=="0"&&it.userclassesid=="7"){
//                    SPUtils.instance().put(Const.User.USER_CLASS_ID,it.userclassesid).apply()
//                    SPUtils.instance().put(Const.User.USER_SCREENID,it.screen).apply()
                    this.startActivity<DateAuthStateActivity>()
                }else{
                    next()
                }
            }
    })
}

inline fun Activity.isNoAuthToChat(id:String?,next: () -> Unit) {
    val className = SPUtils.instance().getString(Const.User.USER_CLASS_ID)
    if (className == "7") {
        CustomToast.showToast("联系微信客服开通会员可获得更多聊天机会哦～")
//        RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, id, "D6客服")
    } else {
        next()
    }
}

inline fun Activity.doAuthUser(next: () -> Unit) {
    val className = SPUtils.instance().getString(Const.User.USER_CLASS_ID)
    if (className == "7") {
        this.startActivity<DateAuthStateActivity>()
    } else {
        next()
    }
}


inline fun Activity.checkUserAuthUser(userclassId:String,next: () -> Unit) {
    if (userclassId == "7") {
        this.startActivity<UnAuthUserActivity>()
    } else {
        next()
    }
}


/**
 * 是否白银以上
 */
inline fun Activity.isVipSilver(next: () -> Unit) {
    val className = SPUtils.instance().getString(Const.User.USER_CLASS_ID)
    if ((className == "7" || className=="22" )) {//游客或普通
        if ( this is AppCompatActivity) {
            val dateErrorDialog = DateErrorDialog()
            dateErrorDialog.arguments = bundleOf("msg" to "必须达到白银及以上会员才能约哦")
            dateErrorDialog.show(this.supportFragmentManager, "d")
        }
    } else {
        next()
    }
}

/**
 * md5字符串
 */
fun String?.md5(): String {
    if (this == null) {
        return ""
    }
    try {
        val bmd5 = MessageDigest.getInstance("MD5")
        bmd5.update(this.toByteArray())
        var i: Int
        val buf = StringBuffer()
        val b = bmd5.digest()
        for (offset in b.indices) {
            i = b[offset].toInt()
            if (i < 0) {
                i += 256
            }
            if (i < 16) {
                buf.append("0")
            }
            buf.append(Integer.toHexString(i))
        }
        return buf.toString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    } catch (e: NullPointerException) {
        e.printStackTrace()
    }
    return ""
}

fun String.isValidPwd(): Boolean {
    val i = if (this.matches(".*\\d+.*".toRegex())) 1 else 0
    val j = if (this.matches(".*[a-zA-Z]+.*".toRegex())) 1 else 0
    return i == 1 && j == 1
}

fun String?.isValidPhone(): Boolean {
    if (isNullOrEmpty()) {
        return false
    }
    if (this!!.length != 11) {
        return false
    }
    val expression = "(^(13|14|15|16|17|18|19)[0-9]{9}$)"
    val pattern = Pattern.compile(expression)
    val matcher = pattern.matcher(this)
    if (matcher.matches()) {
        return true
    }
    return false
}

inline fun BaseActivity.checkChatCount(to: String, crossinline next: () -> Unit) {
    val from = SPUtils.instance().getString(Const.User.USER_ID)
    val date = D6Application.systemTime.toYMDTime()
    this.dialog()
    Request.getTalkDetails(from, to, date).request(this,false ,success = {code, data->
        data?.let {
            // next()
            val talkcount = data.optInt("talkcount")
            val userIds = data.optString("touserid")
            if (userIds.isNotEmpty()) {
                val list = userIds.split(",")
                if (list.size <= talkcount) {
                    next()
                }else{
                    this.toast("聊天次数已达上限")
                }
            } else {
                next()
            }
        }
    }) { _, msg ->
        this.toast(msg)
    }
}

inline fun BaseActivity.getTrendDetail(id:String,crossinline next:(square:Square)->Unit){
    val userId = SPUtils.instance().getString(Const.User.USER_ID)
    dialog()
    Request.getSquareDetail(userId,id).request(this){_,data->
        data?.let {
            next(it)
        }
    }
}

//fun converTime(timestamp: Double): String {
//    val currentSeconds = System.currentTimeMillis() / 1000
//    val timeGap = currentSeconds - timestamp// 与现在时间相差秒数
//    var timeStr: String? = null
//    if (timeGap > 24 * 60 * 60) {// 1天以上
//        timeStr = (timeGap / (24 * 60 * 60)).toString() + "天"
//    } else if (timeGap > 60 * 60) {// 1小时-24小时
//        timeStr = (timeGap / (60 * 60)).toString() + "小时"
//    } else if (timeGap > 60) {// 1分钟-59分钟
//        timeStr = (timeGap / 60).toString() + "分钟"
//    } else {// 1秒钟-59秒钟
//        timeStr = "0"
//    }
//    return timeStr

    fun stampToTime(stamp: Long): String {
        val now = System.currentTimeMillis()
        val intervals = (now - stamp)/1000
        return if (intervals / 60 <= 1) {
            "刚刚"
        } else if (intervals / 60 < 60) {
            (intervals / 60).toString() + "分钟前"
        } else if (intervals / 3600 < 24) {
            (intervals / 3600).toString() + "小时前"
        } else if (intervals / (3600 * 24) < 30) {
            (intervals / (3600 * 24)).toString() + "天前"
        } else if (intervals / (3600 * 24 * 30) < 12) {
            (intervals / (3600 * 24 * 30)).toString() + "月前"
        } else {
           stamp.toTime("yyyy-MM-dd")
        }
    }

fun showTips(jsonObject:JsonObject?,desc:String,iAddPoint:String){
    if(jsonObject!=null){
        var pointDesc = jsonObject.optString("sAddPointDesc")
        var iAddPoint = jsonObject.optString("iAddPoint")
        if(!TextUtils.isEmpty(pointDesc)){
            CustomToast.success("$pointDesc", R.mipmap.popup_money_icon, Toast.LENGTH_LONG, true).show()
        }
    } else if(!TextUtils.isEmpty(desc)){
        CustomToast.success("$desc+$iAddPoint", R.mipmap.popup_money_icon, Toast.LENGTH_LONG, true).show()
    }
}

/**
 * 获得版本名称
 *
 * @return
 */
fun getD6VersionName(context: Context): String {
    var versionName = "1.6.1"
    try {
        val info = context.packageManager.getPackageInfo(
                context.packageName, 0)
        versionName = info.versionName

    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return versionName
}

/**
 * 日期匹配星座
 */
 fun getConstellations(time:String):String{
    val astrologyArray = arrayOf("魔羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座")
   //星座分割时间
    var date= intArrayOf(20,19,21,20,21,22,23,23,23,24,23,22)
    var data = time.split("-")
    var month = Integer.parseInt(data[1])
    var compareDay = date[month - 1]
    if (Integer.parseInt(data[2]) >= compareDay) {
        return astrologyArray[month]
    } else {
        return astrologyArray[month - 1]
    }
}

fun getDiskLruCacheHelper(context: Context): DiskLruCacheHelper? {
    var mDiskLruCacheHelper: DiskLruCacheHelper?=null
    try {
        mDiskLruCacheHelper = DiskLruCacheHelper(context)
        mDiskLruCacheHelper = mDiskLruCacheHelper
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return mDiskLruCacheHelper
}

private val MIN_DELAY_TIME= 1000  // 两次点击间隔不能少于1000ms
private var lastClickTime:Long = 0

/**
 * 防止多次点击
 */
fun isFastClick():Boolean {
    var flag = true
    var currentClickTime = System.currentTimeMillis()
    if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
        flag = false
    }
    lastClickTime = currentClickTime
    return flag
}