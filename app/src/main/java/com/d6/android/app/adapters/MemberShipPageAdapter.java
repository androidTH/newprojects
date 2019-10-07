package com.d6.android.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.d6.android.app.fragments.MemberShipQuickFragment;
import com.d6.android.app.fragments.MemberShipQuickFragmentKt;
import com.d6.android.app.fragments.RecommendDateQuickFragment;
import com.d6.android.app.models.MemberBean;
import com.d6.android.app.rong.bean.RecommentType;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jinjiarui
 * time   : 2019/05/28
 * desc   :
 * version:
 */
public class MemberShipPageAdapter extends FragmentStatePagerAdapter {

    private List<MemberBean> mTitles = new ArrayList<>();
    private List<MemberShipQuickFragment> mDataFragments;
    public MemberShipPageAdapter(FragmentManager fm, List<MemberShipQuickFragment> data, List<MemberBean> titles) {
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
        return mTitles.get(position).getClassesname();
    }

}
