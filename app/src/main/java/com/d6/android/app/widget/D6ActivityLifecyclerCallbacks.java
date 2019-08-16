package com.d6.android.app.widget;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * author : jinjiarui
 * time   : 2019/06/19
 * desc   :
 * version:
 */
public class D6ActivityLifecyclerCallbacks implements Application.ActivityLifecycleCallbacks{

    private int startCount;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        startCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        startCount--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public int getStartCount(){
        return startCount;
    }
}
