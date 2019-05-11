package com.d6.android.app.utils

import android.annotation.TargetApi
import android.app.Activity
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.d6.android.app.BuildConfig
import com.d6.android.app.R
import com.d6.android.app.activities.DateAuthStateActivity
import com.d6.android.app.activities.MenMemberActivity
import com.d6.android.app.activities.UnAuthUserActivity
import com.d6.android.app.application.D6Application
import com.d6.android.app.base.BaseActivity
import com.d6.android.app.dialogs.DateErrorDialog
import com.d6.android.app.dialogs.DialogUpdateApp
import com.d6.android.app.extentions.request
import com.d6.android.app.interfaces.RequestManager
import com.d6.android.app.models.FriendBean
import com.d6.android.app.models.Square
import com.d6.android.app.models.UserData
import com.d6.android.app.models.VersionBean
import com.d6.android.app.net.Request
import com.d6.android.app.net.http.UpdateAppHttpUtil
import com.d6.android.app.utils.JsonUtil.containsEmoji
import com.d6.android.app.widget.CustomToast
import com.d6.android.app.widget.CustomToast.showToast
import com.d6.android.app.widget.diskcache.DiskLruCacheHelper
import com.google.gson.JsonObject
import com.umeng.analytics.MobclickAgent
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.UpdateCallback
import com.vector.update_app.utils.AppUpdateUtils
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_square_detail.*
import org.jetbrains.anko.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

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


fun getLocalUserId():String{
    return SPUtils.instance().getString(Const.User.USER_ID)
}
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
            .put(Const.User.USER_MESSAGESETTING,"${obj.iMessageSetting}")
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
    if (className == "7") {// 22 普通会员
        var sex = SPUtils.instance().getString(Const.User.USER_SEX)
        if(TextUtils.equals("1",sex)){
              this.startActivity<MenMemberActivity>()
        }else{
             this.startActivity<DateAuthStateActivity>()
        }
    }else{
        next()
    }
}

inline fun Activity.isCheckOnLineAuthUser(requestManager: RequestManager, userId:String, crossinline next: () -> Unit) {
    Request.getUserInfoDetail(userId).request(requestManager,false,success = {msg,data->
            data?.let {
                if (it.userclassesid == "7") {
                    saveUserInfo(it)
//                    SPUtils.instance().put(Const.User.USER_SCREENID,it.screen).apply()
                    if (TextUtils.equals("1", it.sex)) {//1是男
                        this.startActivity<MenMemberActivity>()
                    } else {
                        this.startActivity<DateAuthStateActivity>()
                    }
                }else{
                    next();
                }
            }
    })
}

inline fun Activity.isNoAuthToChat(requestManager: RequestManager, userId:String,crossinline next: () -> Unit) {
    Request.getUserInfoDetail(userId).request(requestManager,false,success = {msg,data->
        data?.let {
            if (it.userclassesid == "7") {
                CustomToast.showToast("联系微信客服开通会员可获得更多聊天机会哦～")
            }else{
                next()
            }
        }
    })
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

/**
 * 检测特殊符号和表情
 */
fun checkLimitEx(str:String):Boolean{
    var limitEx ="[`~!@#\$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"

    var pattern = Pattern.compile(limitEx)
    var m = pattern.matcher(str)
    if(m.find()){
        return true
    }

    if(containsEmoji(str)){
        return true
    }

    return false;
}

fun diyUpdate(activity: BaseActivity,from:String?) {
    val path = Environment.getExternalStorageDirectory().absolutePath

    val params = HashMap<String, String>()

//        params["appKey"] = "ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f"
    params["sVersion"] =AppUpdateUtils.getVersionName(activity) //AppUpdateUtils.getVersionName(activity)//AppUpdateUtils.getVersionName(this)
    params["iType"] = "2"

    UpdateAppManager.Builder()
            //必须设置，当前Activity
            .setActivity(activity)
            //必须设置，实现httpManager接口的对象
            .setHttpManager(UpdateAppHttpUtil())
            //必须设置，更新地址
            .setUpdateUrl(Const.UpdateAppUrl)
            //以下设置，都是可选
            //设置请求方式，默认get
            .setPost(true)
            //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
            .setParams(params)
            //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
            .setTargetPath(path)
            .build()
            //检测是否有新版本
            .checkNewApp(object : UpdateCallback() {
                /**
                 * 解析json,自定义协议
                 *
                 * @param json 服务器返回的json
                 * @return UpdateAppBean
                 */
                override fun parseJson(json: String): UpdateAppBean {
                    val updateAppBean = UpdateAppBean()
                    try {
                        val jsonObject = JSONObject(json)
                        val code = jsonObject.optInt("res", -1)
                        if (code == 1) {
                            var obj = jsonObject.optString("obj")
                            var versionBean = GsonHelper.GsonToBean(obj, VersionBean::class.java)
                            var isNoUpdate = if(!TextUtils.equals(versionBean.sVersion,versionBean.sNewVersion)){
                                "Yes"
                            }else{
                                "No"
                            }
                            updateAppBean
                                    //（必须是否更新Yes,No
                                    .setUpdate(isNoUpdate)//jsonObject.optString("update")
                                    //（必须）新版本号
                                    .setNewVersion(versionBean.sNewVersion)
                                    //（必须）下载地址jsonObject.optString("apk_file_url") "http://test-1251233192.coscd.myqcloud.com/1_1.apk"
                                    .setApkFileUrl(versionBean.sDownloadPath)//http://test-1251233192.coscd.myqcloud.com/1_1.apk
                                    //（必须）更新内容
                                    .setUpdateLog(versionBean.sDesc)
                                    //大小，不设置不显示大小，可以不设置
//                                        .setTargetSize(jsonObject.optString("target_size"))
                                    //是否强制更新，可以不设置
                                    .setConstraint(if (versionBean.iUpgradetype == 1) true else false)
//                                        .newMd5 = jsonObject.optString("new_md5")
                        } else {
                            var msg = jsonObject.optString("resMsg")
//                            showToast(msg)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    return updateAppBean
                }

                /**
                 * 有新版本
                 *
                 * @param updateApp        新版本信息
                 * @param updateAppManager app更新管理器
                 */
                public override fun hasNewApp(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) {
                    var ignoreVersion = SPUtils.instance().getString(Const.IGNORE_VERSION, "")
                    if(TextUtils.equals(activity::class.java.simpleName,from)){
                        var mDialogUpdateApp = DialogUpdateApp()
                        mDialogUpdateApp.arguments = bundleOf("data" to updateApp)
                        mDialogUpdateApp.show((activity).supportFragmentManager, "updateapp")
                        mDialogUpdateApp.setDialogListener { p, s ->
                            updateAppManager.download()
                        }
                    }else if (!TextUtils.equals(updateApp.newVersion, ignoreVersion)) {
                        //自定义对话框
                        var mDialogUpdateApp = DialogUpdateApp()
                        mDialogUpdateApp.arguments = bundleOf("data" to updateApp)
                        mDialogUpdateApp.show((activity).supportFragmentManager, "updateapp")
                        mDialogUpdateApp.setDialogListener { p, s ->
                            updateAppManager.download()
                        }
                    } else if (updateApp.isConstraint) {
                        var mDialogUpdateApp = DialogUpdateApp()
                        mDialogUpdateApp.arguments = bundleOf("data" to updateApp)
                        mDialogUpdateApp.show(activity.supportFragmentManager, "updateapp")
                        mDialogUpdateApp.setDialogListener { p, s ->
                            updateAppManager.download()
                        }
                    }
                }

                /**
                 * 网络请求之前
                 */
                public override fun onBefore() {
                    activity.dialog()
                }

                /**
                 * 网路请求之后
                 */
                public override fun onAfter() {
                    activity.dismissDialog()
                }

                /**
                 * 没有新版本
                 */
                public override fun noNewApp(error: String?) {
                    if(TextUtils.equals(from,activity::class.java.simpleName)){
                        showToast("已是最新版本")
                    }
                }
            })
}

fun getReplace(str:String):String{
    return str.replace("省","").replace("市","")
}

fun hideSoftKeyboard(view:View) {
    var manager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
}

fun showSoftInput(view:View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    //显示软键盘
    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    view.requestFocus()
}

private val CHECK_OP_NO_THROW = "checkOpNoThrow"
private val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"

@TargetApi(Build.VERSION_CODES.KITKAT)
fun isNotificationEnabled(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ///< 8.0手机以上
        if ((context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).getImportance() === NotificationManager.IMPORTANCE_NONE) {
            return false
        }
    }

    val mAppOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val appInfo = context.applicationInfo
    val pkg = context.applicationContext.packageName
    val uid = appInfo.uid

    var appOpsClass: Class<*>? = null
    try {
        appOpsClass = Class.forName(AppOpsManager::class.java!!.getName())
        val checkOpNoThrowMethod = appOpsClass!!.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                String::class.java)
        val opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)

        val value = opPostNotificationValue.get(Int::class.java) as Int
        return checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) as Int === AppOpsManager.MODE_ALLOWED
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun getShareUserId(mChooseFriends:ArrayList<FriendBean>):String{
    if(mChooseFriends!=null&&mChooseFriends.size>0){
        var sb = StringBuilder()
        for(bean in mChooseFriends){
            sb.append("${bean.iUserid}").append(",")
        }
        return sb.deleteCharAt(sb.length-1).toString()
    }
    return ""
}

/**
 * 跳到通知页
 */
fun requestNotify(context: Context){

    var appInfo = context.getApplicationInfo()
    var pkg = context.getApplicationContext().getPackageName()
    var uid = appInfo.uid

//        val intent: Intent = Intent()
//        try {
//            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
//
//            //8.0及以后版本使用这两个extra.  >=API 26
//
//            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//            intent.putExtra(Settings.EXTRA_CHANNEL_ID, uid)
//
//            //5.0-7.1 使用这两个extra.  <= API 25, >=API 21
//            intent.putExtra("app_package", context.packageName)
//            intent.putExtra("app_uid", uid)
//
//            context.startActivity(intent)
//        } catch (e: Exception) {
//            e.printStackTrace()
//
//            //其他低版本或者异常情况，走该节点。进入APP设置界面
//            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//            intent.putExtra("package", context.packageName)
//            //val uri = Uri.fromParts("package", packageName, null)
//            //intent.data = uri
//            context.startActivity(intent)
//        }

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val intent: Intent = Intent()
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, pkg)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, uid)
            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            intent.putExtra("app_package", pkg)
            intent.putExtra("app_uid", uid)
            context.startActivity(intent)
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            val intent: Intent = Intent()
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setData(Uri.parse("package:" + context.getPackageName()))
            context.startActivity(intent)
//                context.startActivityForResult(intent, AppConst.REQUEST_SETTING_NOTIFICATION);
        } else {
            var intent =  Intent(Settings.ACTION_SETTINGS)
            context.startActivity(intent)
//                context.startActivityForResult(intent, AppConst.REQUEST_SETTING_NOTIFICATION);
        }
    } catch (e:Exception) {
        var intent = Intent(Settings.ACTION_SETTINGS)
        context.startActivity(intent)
//            context.startActivityForResult(intent, AppConst.REQUEST_SETTING_NOTIFICATION);
    }
}

/**
 * 分享到聊天
 */
fun shareChat(activity: BaseActivity,type:String,sex:String,userId:String){
    var map = HashMap<String,String>()
    map.put("content_type",type)
    map.put("sex",if(TextUtils.equals("0",sex)) "women" else "man")
    if(TextUtils.equals(userId, Const.CustomerServiceId)||TextUtils.equals(userId, Const.CustomerServiceWomenId)){
        map.put("is_service","Y")
    }else{
        map.put("is_service","n")
    }
    MobclickAgent.onEvent(activity,"share_chat_id",map)
}

/**
 * 发布时同步到聊天
 */
fun syncChat(activity: BaseActivity,type:String,sex:String,userId:String){
    var map = HashMap<String,String>()
    map.put("content_type",type)
    map.put("sex",if(TextUtils.equals("0",sex)) "women" else "man")
    if(TextUtils.equals(userId, Const.CustomerServiceId)||TextUtils.equals(userId, Const.CustomerServiceWomenId)){
        map.put("is_service","Y")
    }else{
        map.put("is_service","n")
    }
    MobclickAgent.onEvent(activity,"sync_chat_id",map)
}

fun chatService(activity: BaseActivity){
    var sex = SPUtils.instance().getString(Const.User.USER_SEX)
    if(TextUtils.equals("0",sex)){
        //女客服号
        RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, Const.CustomerServiceId, "D6客服")
    }else{
        RongIM.getInstance().startConversation(activity, Conversation.ConversationType.PRIVATE, Const.CustomerServiceWomenId, "D6客服")
    }
}

inline fun Activity.pushCustomerMessage(requestManager: RequestManager, userId:String,iType:Int,sSourceId:String, crossinline next: () -> Unit) {
    Request.pushCustomerMessage(userId,iType,sSourceId).request(requestManager,false,success = {msg,data->
        data?.let {
              Log.i("pushCustomerMessage","消息内容="+it)
              next()
//            if (it.userclassesid == "7") {
//
//            }else{
//            }
        }
    }){code,msg->
        toast(msg)
    }
}