package cn.liaox.cachelib.cache;


import cn.liaox.cachelib.bean.CacheBean;
import io.reactivex.Flowable;

/**
 *
 */
public interface ICache {

    <T extends CacheBean> Flowable<T> get(String key, Class<T> cls);

    <T extends CacheBean> void put(String key, T t);
}