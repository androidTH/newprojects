package com.share.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

import com.umeng.sdk.R
import com.umeng.socialize.ShareAction
import com.umeng.socialize.ShareContent
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.UmengTool
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb

/**
 *
 */
object ShareUtils {

    fun checkQQState(context: Context): Boolean {
        val packageName = "com.tencent.mobileqq"
        val pi: PackageInfo
        try {
            pi = context.packageManager.getPackageInfo(packageName, 0)
            val resolveIntent = Intent(Intent.ACTION_MAIN, null)
            resolveIntent.`package` = pi.packageName
            val pManager = context.packageManager
            val apps = pManager.queryIntentActivities(
                    resolveIntent, 0)

            val ri = apps.iterator().next() as ResolveInfo
            return ri != null
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return false
    }

    fun checkSinaState(context: Context): Boolean {
        val packageName = "com.sina.weibo"
        val pi: PackageInfo
        try {
            pi = context.packageManager.getPackageInfo(packageName, 0)
            val resolveIntent = Intent(Intent.ACTION_MAIN, null)
            resolveIntent.`package` = pi.packageName
            val pManager = context.packageManager
            val apps = pManager.queryIntentActivities(
                    resolveIntent, 0)

            val ri = apps.iterator().next() as ResolveInfo
            return ri != null
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return false
    }

    fun share(context: Activity, content: String, title: String, tagUrl: String) {
        val displaylist = arrayOf(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
        val image = UMImage(context, R.mipmap.icon)
        val web = UMWeb(tagUrl, title, content, image)
        val shareContent = ShareContent()
        shareContent.mMedia = web
        ShareAction(context).setDisplayList(*displaylist)
                .setShareContent(shareContent)
                .open()
    }

    fun share(context: Activity, content: String, title: String, tagUrl: String, image: UMImage, listener: UMShareListener) {
        val displaylist = arrayOf(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
        val web = UMWeb(tagUrl, title, content, image)
        val shareContent = ShareContent()
        shareContent.mMedia = web
        ShareAction(context).setDisplayList(*displaylist)
                .setShareContent(shareContent)
                .setCallback(listener)
                .open()
    }

    fun share(context: Activity, platform: SHARE_MEDIA, content: String, title: String, tagUrl: String, listener: UMShareListener) {
        share(context, platform, content, title, tagUrl, null, listener)
    }

    fun share(context: Activity, platform: SHARE_MEDIA, content: String, title: String, tagUrl: String, image: UMImage?, listener: UMShareListener) {
        var img = image
        if (img == null) {
            img = UMImage(context, R.mipmap.icon)
        }
        val web = UMWeb(tagUrl, title, content, img)
        val shareContent = ShareContent()
        shareContent.mMedia = web
        ShareAction(context).setPlatform(platform)
                .setShareContent(shareContent)
                .setCallback(listener)
                .share()
    }
}
