package com.d6.android.app.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.d6.android.app.R;

import java.util.List;

/**
 * author : jinjiarui
 * time   : 2018/12/25
 * desc   :
 * version:
 */
public class MemberLevelAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    private int selectedIndex = -1;

    public MemberLevelAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    public MemberLevelAdapter(@Nullable List<String> data) {
        this(R.layout.item_card_pt,data);
    }

    public MemberLevelAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView tv_vip_level = helper.getView(R.id.tv_vip_level);
        tv_vip_level.setText(item);
        RelativeLayout rl_vip_top = helper.getView(R.id.rl_vip_top);
    }
}
