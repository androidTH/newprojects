# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in K:\adt-bundle-64\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontshrink
 -dontoptimize
 -dontwarn com.google.android.maps.**
 -dontwarn android.webkit.WebView
 -dontwarn com.umeng.**
 -dontwarn com.tencent.weibo.sdk.**
 -dontwarn com.facebook.**
 -keep public class javax.**
 -keep public class android.webkit.**
 -dontwarn android.support.v4.**
 -keep enum com.facebook.**
 -keepattributes Exceptions,InnerClasses,Signature
 -keepattributes *Annotation*
 -keepattributes SourceFile,LineNumberTable

 -keep public interface com.facebook.**
 -keep public interface com.tencent.**
 -keep public interface com.umeng.socialize.**
 -keep public interface com.umeng.socialize.sensor.**
 -keep public interface com.umeng.scrshot.**

 -keep public class com.umeng.socialize.* {*;}
 

-keep class com.umeng.commonsdk.** {*;}
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class com.meizu.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}


 -keep class com.facebook.**
 -keep class com.facebook.** { *; }
 -keep class com.umeng.scrshot.**
 -keep public class com.tencent.** {*;}
 -keep class com.umeng.socialize.sensor.**
 -keep class com.umeng.socialize.handler.**
 -keep class com.umeng.socialize.handler.*
 -keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
 -keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

 -keep class im.yixin.sdk.api.YXMessage {*;}
 -keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

 -dontwarn twitter4j.**
 -keep class twitter4j.** { *; }

 -keep class com.tencent.** {*;}
 -dontwarn com.tencent.**
 -keep public class com.umeng.soexample.R$*{
     public static final int *;
 }
 -keep public class com.umeng.soexample.R$*{
     public static final int *;
 }
 -keep class com.tencent.open.TDialog$*
 -keep class com.tencent.open.TDialog$* {*;}
 -keep class com.tencent.open.PKDialog
 -keep class com.tencent.open.PKDialog {*;}
 -keep class com.tencent.open.PKDialog$*
 -keep class com.tencent.open.PKDialog$* {*;}

 -keep class com.sina.** {*;}
 -dontwarn com.sina.**
 -keep class  com.alipay.share.sdk.** {
    *;
 }
 -keepnames class * implements android.os.Parcelable {
     public static final ** CREATOR;
 }

 -keep class com.linkedin.** { *; }
 -keepattributes Signature
 #锛嶏紞锛嶏紞锛嶏紞锛嶏紞锛嶅弸鐩燂紞锛嶏紞锛嶏紞锛嶏紞

 #锛嶏紞锛嶏紞锛嶏紞锛嶏紞retrofit2锛嶏紞锛嶏紞锛嶏紞锛嶏紞
 -dontwarn retrofit2.**
 -keep class retrofit2.** { *; }
 -keepattributes Signature
 -keepattributes Exceptions

 -dontwarn okio.**
 -dontwarn javax.annotation.Nullable
 -dontwarn javax.annotation.ParametersAreNonnullByDefault
 -keep class okio.** { *;}
 -dontwarn okhttp3.**
 -keep class okhttp3.** { *;}

 -dontwarn rx.internal.**
 -keep class rx.internal.** { *;}


 -dontwarn com.alipay.**
 -keep class com.alipay.** { *;}

 -dontwarn io.reactivex.**
 -keep class io.reactivex.** { *;}

##---------------Begin: proguard configuration for Gson  ----------
 # Gson uses generic type information stored in a class file when working with fields. Proguard
 # removes such information by default, so configure it to keep all of it.
 -keepattributes Signature

 # For using GSON @Expose annotation
 -keepattributes *Annotation*

 # Gson specific classes
 -keep class sun.misc.Unsafe { *; }

 #apache
 -keep class org.apache.** {*;}
 -dontwarn  org.apache.**
 -keep class android.net.** {*;}
 -dontwarn  android.net.**
 #鍗庝负
 -keep class com.huawei.android.** {*;}
 -dontwarn  com.huawei.android.**
 #google gcm
 -keep class com.google.android.gms.** {*;}
 -dontwarn  com.google.android.gms.**

 # Application classes that will be serialized/deserialized over Gson
-keep class com.d6.android.app.models.** {*;}
-keep class com.d6.android.app.easypay.pay.BaseModel {*;}
-keep class com.d6.android.app.easypay.pay.PrePayInfo {*;}
-keep class cn.liaox.cachelib.bean.** { *; }

-keepattributes Exceptions,InnerClasses

-keepattributes Signature

# RongCloud SDK
-keep class io.rong.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.push.**
-dontwarn io.rong.imkit.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**

-keep class cn.rongcloud.rtc.*{*;}
-keep class cn.rongcloud.rtc.core.**  { *; }
-keep class cn.rongcloud.rtc.engine.binstack.json.**  { *; }

# VoIP
-keep class io.agora.rtc.** {*;}

# Location
-keep class com.amap.api.**{*;}
-keep class com.amap.api.services.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}
-dontwarn com.amap.api.**

# 绾㈠寘
-keep class com.google.gson.** { *; }
-keep class com.uuhelper.Application.** {*;}
-keep class net.sourceforge.zbar.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.alipay.** {*;}
-keep class com.jrmf360.rylib.** {*;}
-keep class com.melink.bqmmplugin.** {*;}

-keep class com.d6.android.app.models.** {*;}

#green dao
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**

#-ignorewarnings
#-keep class com.lx.simon.utils.SPushMessageReceiver {*;}

#EventBus
-keepclassmembers class ** {
    public void onEvent*(**);
}

#鏈夌洘缁熻
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keep public class com.d6.android.app.R$*{
public static final int *;
}

-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class com.d6.android.app.R$*{
public static final int *;
}

#eventBus混淆
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#华为推送混淆
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

-keep class com.huawei.gamebox.plugin.gameservice.**{*;}

-keep public class com.huawei.android.hms.agent.** extends android.app.Activity { public *; protected *; }
-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}
-keep class me.jessyan.autosize.** { *; }
-keep interface me.jessyan.autosize.** { *; }

#七牛混淆
#-keep class com.pili.pldroid.player.** { *; }
#-keep class com.qiniu.qplayer.mediaEngine.MediaPlayer{*;}

-keep public class cn.jzvd.JZMediaSystem {*; }

-keepattributes LineNumberTable,SourceFile
-keep class com.bugtags.library.** {*;}
-dontwarn com.bugtags.library.**
-keep class io.bugtags.** {*;}
-dontwarn io.bugtags.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient

-keep class com.github.hiteshsondhi88.libffmpeg.*{*;}
-keep class com.zlw.main.recorderlib.**{*;}
-keep class fftlib.*{*;}

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}