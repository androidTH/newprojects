package com.d6zone.android.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;

/**
 * author : jinjiarui
 * time   : 2019/05/28
 * desc   :
 * version:
 */
public class HomeFindDateAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mHomeDataFragments;

    public HomeFindDateAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        this.mHomeDataFragments = data;
    }

    @Override
    public Fragment getItem(int position) {
        return mHomeDataFragments.get(position);
    }

    @Override
    public int getCount() {
        return mHomeDataFragments.size();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return mTitles.get(position).getDateTypeName();
//    }

    public HomeFindDateAdapter getFragment(int position) {
        return (HomeFindDateAdapter)getFragment(position);
    }

}
