package cn.liaox.cachelib.cache;

import android.content.Context;

import com.google.gson.Gson;

import cn.liaox.cachelib.CacheDBLib;
import cn.liaox.cachelib.bean.CacheBean;
import cn.liaox.cachelib.bean.UserBean;
import cn.liaox.cachelib.utils.UserHelper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 */

public class DBCache implements ICache {

    private Context context;

    public DBCache() {
        context = CacheDBLib.mContext;
        if (context == null) {
            throw new NullPointerException("请先init cacheLib，初始化");
        }
    }

    @Override
    public <T extends CacheBean> Flowable<T> get(final String key, final Class<T> cls) {

        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                UserBean userBean = UserHelper.getInstance(context).getUser(key);
                if (e.isCancelled()) {
                    return;
                }
                if (userBean == null) {
                    T t = (T) new UserBean(0L);
                    e.onNext(t);
                } else {
                    T t = (T) userBean;
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
                if(t instanceof  UserBean){
                    UserBean bean = (UserBean) t;
                    UserHelper.getInstance(context).saveUser(key, bean.getNickName(), bean.getHeadUrl(), bean.getTime());
                }
                if (!e.isCancelled()) {
                    e.onNext(t);
                    e.onComplete();
                }
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
