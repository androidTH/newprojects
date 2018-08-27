package cn.liaox.cachelib.bean;

import com.google.gson.Gson;

/**
 *
 */
public abstract class CacheBean {

    /**
     * 默认有效期限是1小时： 60 * 60 * 1000
     */
    private static final long EXPIRE_LIMIT = 60 * 60 * 1000;
    private long mCreateTime;
    private int status = 0;

    public CacheBean() {
        mCreateTime = System.currentTimeMillis();
    }

    public CacheBean(long mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    private boolean isEmpty = false;

    public String toString() {

        return new Gson().toJson(this);
    }

    /**
     * 在{@link #EXPIRE_LIMIT}时间之内有效，过期作废
     *
     * @return true 表示过期
     */
    public boolean isExpire() {

        //当前时间-保存时间如果超过1天，则认为过期
        return System.currentTimeMillis() - mCreateTime > EXPIRE_LIMIT;
    }

    public long getTime() {
        return mCreateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
