package com.d6zone.android.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.d6zone.android.app.fragments.VoiceChatFragment;
import com.d6zone.android.app.models.DateType;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jinjiarui
 * time   : 2019/05/28
 * desc   :
 * version:
 */
public class HomeVoicePageAdapter extends FragmentStatePagerAdapter {

    private List<VoiceChatFragment> mHomeDataFragments;

    private List<DateType> mTitles = new ArrayList<>();
    public HomeVoicePageAdapter(FragmentManager fm, List<VoiceChatFragment> data, List<DateType> titles) {
        super(fm);
        this.mHomeDataFragments = data;
        this.mTitles = titles;
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
        return mTitles.get(position).getDateTypeName();
    }

    public HomeVoicePageAdapter getFragment(int position) {
//        String tag = mFragmentTags.get(position);
//        if (tag == null)
//            return null;
        return (HomeVoicePageAdapter)getFragment(position);
    }

}
