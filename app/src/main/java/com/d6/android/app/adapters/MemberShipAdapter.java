package com.d6.android.app.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.d6.android.app.R;
import com.d6.android.app.models.FlowerRule;

import java.util.List;

/**
 * author : jinjiarui
 * time   : 2018/12/25
 * desc   :
 * version:
 */
public class MemberShipAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    private int selectedIndex = -1;

    public MemberShipAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    public MemberShipAdapter(@Nullable List<String> data) {
        this(R.layout.item_membership_pirce,data);
    }

    public MemberShipAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
         TextView tvName = helper.getView(R.id.tv_name);
         tvName.setText(item);
         helper.setText(R.id.tv_vip_price,"¥1888");
         Drawable drawable = ContextCompat.getDrawable(mContext,R.mipmap.vip_ordinary);
         tvName.setCompoundDrawablesWithIntrinsicBounds(null,null,drawable,null);
    }
}
