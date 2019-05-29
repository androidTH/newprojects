package com.d6.android.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jinjiarui
 * time   : 2019/05/28
 * desc   :
 * version:
 */
public class RecommentDatePageAdapter extends FragmentStatePagerAdapter {

    private List<String> mTitles = new ArrayList<>();
    private List<Fragment> mDataFragments;
    public RecommentDatePageAdapter(FragmentManager fm,List<Fragment> data) {
        super(fm);
        this.mDataFragments = data;
        mTitles.add("全部");
        mTitles.add("觅约");
        mTitles.add("救火");
        mTitles.add("征求");
        mTitles.add("急约");
        mTitles.add("旅行约");

    }

    @Override
    public Fragment getItem(int position) {
        return mDataFragments.get(position);
    }

    @Override
    public int getCount() {
        return mDataFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

}
