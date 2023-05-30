package com.d6.android.app.widget;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * author : jinjiarui
 * time   : 2021/08/22
 * desc   :
 * version:
 */
public class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
