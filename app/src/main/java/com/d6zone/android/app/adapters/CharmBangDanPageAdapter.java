package com.d6zone.android.app.adapters;

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
public class CharmBangDanPageAdapter extends FragmentStatePagerAdapter {

    private List<String> mTitles = new ArrayList<>();
    private List<Fragment> mDataFragments;
    public CharmBangDanPageAdapter(FragmentManager fm, List<Fragment> data, List<String> titles) {
        super(fm);
        this.mDataFragments = data;
        this.mTitles = titles;
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
