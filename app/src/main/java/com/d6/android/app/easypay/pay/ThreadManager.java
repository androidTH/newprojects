package com.d6.android.app.easypay.pay;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author : jinjiarui
 * time   : 2019/12/02
 * desc   :
 * version:
 */
public class ThreadManager {
    private static ExecutorService mExecutors = Executors.newSingleThreadExecutor();

    public static void execute(Runnable runnable) {
        if (mExecutors == null) {
            mExecutors = Executors.newSingleThreadExecutor();
        }
        mExecutors.execute(runnable);
    }

    public static void shutdown() {
        if (mExecutors == null) return;
        if (mExecutors.isShutdown()) return;
        mExecutors.shutdownNow();
        mExecutors = null;
    }
}
