package com.d6.android.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.d6.android.app.fragments.RecommendDateQuickFragment;
import com.d6.android.app.fragments.SelfPullDateFragment;
import com.d6.android.app.rong.bean.RecommentType;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jinjiarui
 * time   : 2019/05/28
 * desc   :
 * version:
 */
public class HomeDatePageAdapter extends FragmentStatePagerAdapter {

    private List<SelfPullDateFragment> mHomeDataFragments;

    private List<String> mTitles = new ArrayList<>();
    public HomeDatePageAdapter(FragmentManager fm, List<SelfPullDateFragment> data) {
        super(fm);
        this.mHomeDataFragments = data;
        mTitles.add("官方推荐");
    }

    @Override
    public Fragment getItem(int position) {
        return mHomeDataFragments.get(position);
    }

    @Override
    public int getCount() {
        return mHomeDataFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

}
