package com.d6.android.app.widget.convenientbanner.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Sai on 2018/4/25.
 */

public interface OnPageChangeListener {
    void onScrollStateChanged(RecyclerView recyclerView, int newState);
    void onScrolled(RecyclerView recyclerView, int dx, int dy);
    void onPageSelected(int index);
}
