package com.d6zone.android.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.d6zone.android.app.fragments.RecommendDateQuickFragment;
import com.d6zone.android.app.rong.bean.RecommentType;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jinjiarui
 * time   : 2019/05/28
 * desc   :
 * version:
 */
public class RecommentDatePageAdapter extends FragmentStatePagerAdapter {

    private List<RecommentType> mTitles = new ArrayList<>();
    private List<RecommendDateQuickFragment> mDataFragments;
    public RecommentDatePageAdapter(FragmentManager fm,List<RecommendDateQuickFragment> data,List<RecommentType> titles) {
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
        return mTitles.get(position).getMName();
    }

}
