package cn.liaox.cachelib.cache;

import cn.liaox.cachelib.bean.CacheBean;
import io.reactivex.Flowable;

/**
 *
 */
public abstract class NetworkCache<T extends CacheBean> {

    public abstract Flowable<T> get(String key, final Class<T> cls);
}