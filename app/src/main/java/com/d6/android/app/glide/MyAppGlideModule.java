//package com.d6.android.app.glide;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//
//import java.io.InputStream;
//
//import cc.shinichi.library.glide.sunfusheng.progress.ProgressManager;
//
//@GlideModule public class MyAppGlideModule extends AppGlideModule {
//    @Override
//    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
//        super.registerComponents(context, glide, registry);
//
//        // 替换底层网络框架为okhttp3
//        registry.replace(GlideUrl.class, InputStream.class,
//            new OkHttpUrlLoader.Factory(ProgressManager.getOkHttpClient()));
//    }
//}