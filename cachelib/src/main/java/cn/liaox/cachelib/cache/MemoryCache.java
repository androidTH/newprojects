package cn.liaox.cachelib.cache;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import cn.liaox.cachelib.bean.CacheBean;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 *
 */
public class MemoryCache implements ICache {

    private LruCache<String, String> mCache;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public MemoryCache() {
        //获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        //给LruCache分配1/8 4M
        mCache = new LruCache<String, String>(mCacheSize) {
            @Override
            protected int sizeOf(String key, String value) {
                try {
                    return value.getBytes("UTF-8").length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return value.getBytes().length;
                }
            }
        };
    }

    @Override
    public <T extends CacheBean> Flowable<T> get(final String key, final Class<T> cls) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                Log.v("cache", "load from memory: " + key);
                String result = mCache.get(key);
                if (e.isCancelled()) {
                    return;
                }
                if (TextUtils.isEmpty(result)) {
                    T t = new Gson().fromJson("{\"status:\":0}", cls);
                    e.onNext(t);
                } else {
                    T t = new Gson().fromJson(result, cls);
                    t.setStatus(1);
                    e.onNext(t);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.DROP);
    }

    @Override
    public <T extends CacheBean> void put(String key, T t) {

        if (null != t) {
            Log.v("cache", "save to memory: " + key);

            mCache.put(key, t.toString());
        }
    }
}