package cn.liaox.cachelib.cache;


import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;

import cn.liaox.cachelib.bean.CacheBean;
import cn.liaox.cachelib.utils.FileUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 */
public class DiskCache implements ICache {

    private String CACHE_PATH;

    public DiskCache() {
        CACHE_PATH = FileUtils.getCacheDir();
    }

    @Override
    public <T extends CacheBean> Flowable<T> get(final String key, final Class<T> cls) {
        return Flowable.create(new FlowableOnSubscribe<T>() {

            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                Log.v("cache", "load from disk: " + key);

                String filename = CACHE_PATH + key;
                String result = FileUtils.readTextFromSDcard(filename);
                if (e.isCancelled()) {
                    return;
                }
                if (TextUtils.isEmpty(result)) {
//                    e.onError(new Throwable("no cache data"));
//                    T t = new Gson().fromJson("", cls);
                    T t = new Gson().fromJson("{\"status:\":0}", cls);
                    e.onNext(t);
                } else {

                    T t = new Gson().fromJson(result, cls);
                    t.setStatus(1);
                    e.onNext(t);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public <T extends CacheBean> void put(final String key, final T t) {

        Flowable.create(new FlowableOnSubscribe<T>() {

            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                Log.v("cache", "save to disk: " + key);

                String filename = CACHE_PATH + key;
                String result = t.toString();
                FileUtils.saveText2Sdcard(filename, result);
                if (!e.isCancelled()) {
                    e.onNext(t);
                    e.onComplete();
                }
            }
        },BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}