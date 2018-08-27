package cn.liaox.cachelib;

import android.util.Log;

import cn.liaox.cachelib.bean.CacheBean;
import cn.liaox.cachelib.cache.DiskCache;
import cn.liaox.cachelib.cache.ICache;
import cn.liaox.cachelib.cache.MemoryCache;
import cn.liaox.cachelib.cache.NetworkCache;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 *
 */
public class CacheManager {

    private ICache mMemoryCache, mDiskCache;

    private CacheManager() {

        mMemoryCache = new MemoryCache();
        mDiskCache = new DiskCache();
    }

    public static final CacheManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public <T extends CacheBean> Flowable<T> load(String key, Class<T> cls, NetworkCache<T> networkCache) {

        return Flowable.concat(
                loadFromMemory(key, cls),
                loadFromDisk(key, cls),
                loadFromNetwork(key, cls, networkCache))
                .filter(new Predicate<T>() {
                    @Override
                    public boolean test(T t) throws Exception {
                        String result = t == null ? "not exist" :
                                t.isExpire() ? "exist but expired" : "exist and not expired";
                        Log.v("cache", "result: " + result);
                        return t != null && !t.isExpire();
                    }
                })
                .firstElement()
                .toFlowable();
    }

    private <T extends CacheBean> Flowable<T> loadFromMemory(String key, Class<T> cls) {

        return mMemoryCache.get(key, cls);
    }

    private <T extends CacheBean> Flowable<T> loadFromDisk(final String key, Class<T> cls) {

//        Observable.Transformer<T, T> transformer = log("load from disk: " + key);

        return mDiskCache.get(key, cls)
//                .compose(transformer)
                .doOnNext(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        if (null != t) {
                            mMemoryCache.put(key, t);
                        }
                    }
                });
    }

    private <T extends CacheBean> Flowable<T> loadFromNetwork(final String key, Class<T> cls
            , NetworkCache<T> networkCache) {

//        Observable.Transformer<T, T> transformer = log("load from network: " + key);

        return networkCache.get(key, cls)
//                .compose(transformer)
                .doOnNext(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        if (null != t) {
                            mDiskCache.put(key, t);
                            mMemoryCache.put(key, t);
                        }
                    }
                });
    }

//    private <T extends CacheBean> Observable.Transformer<T, T> log(final String msg) {
//        return new Observable.Transformer<T, T>() {
//            @Override
//            public Observable<T> call(Observable<T> observable) {
//                return observable.doOnNext(new Action1<T>() {
//                    @Override
//                    public void call(T t) {
//
//                        //MemoryCache、DiskCache中已经打印过log了，这里只是为了演示transformer、和compose的使用
//                        Log.v("cache", msg);
//                    }
//                });
//            }
//        };
//    }

    private static final class LazyHolder {
        public static final CacheManager INSTANCE = new CacheManager();
    }
}