package cn.liaox.cachelib;

import android.content.Context;

/**
 *  需要先初始化上下文,以供存储到数据库或sd卡时使用。
 */

public class CacheDBLib {

    public static Context mContext;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }
}
