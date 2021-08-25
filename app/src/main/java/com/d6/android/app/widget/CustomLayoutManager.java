package com.d6.android.app.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * author : jinjiarui
 * time   : 2021/08/22
 * desc   :
 * version:
 */
public class CustomLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = false;

    public CustomLayoutManager(Context context) {
        super(context);
    }
    public CustomLayoutManager(Context context,int orientation, boolean reverseLayout) {
        super(context,orientation,reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollHorizontally() {
        //Similarly you can customize "canScrollVertically()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollHorizontally();
    }
}
