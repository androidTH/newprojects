package com.d6.android.app.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.d6.android.app.R;
import com.d6.android.app.models.FlowerRule;
import com.d6.android.app.models.MemberBean;

import java.util.List;

import static com.d6.android.app.utils.UtilKt.getLevelDrawable;

/**
 * author : jinjiarui
 * time   : 2018/12/25
 * desc   :
 * version:
 */
public class MemberShipAdapter extends BaseQuickAdapter<MemberBean,BaseViewHolder> {

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    private int selectedIndex = -1;

    public MemberShipAdapter(int layoutResId, @Nullable List<MemberBean> data) {
        super(layoutResId, data);
    }

    public MemberShipAdapter(@Nullable List<MemberBean> data) {
        this(R.layout.item_membership_pirce,data);
    }

    public MemberShipAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MemberBean item) {
        TextView tvName = helper.getView(R.id.tv_name);
        tvName.setText(item.getClassesname());
        helper.setText(R.id.tv_vip_price, "¥"+String.valueOf(item.getIAndroidPrice()));
        helper.setText(R.id.tv_vipinfo, item.getSTitle());
        Drawable drawable = getLevelDrawable(String.valueOf(item.getIds()),mContext);
        tvName.setCompoundDrawablesWithIntrinsicBounds(null,null,drawable,null);
        helper.addOnClickListener(R.id.tv_vip_price);
    }
}
