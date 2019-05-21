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
        helper.setText(R.id.tv_vip_price, String.valueOf(item.getIAndroidPrice()));
        helper.setText(R.id.tv_vipinfo, item.getSTitle());
        Drawable drawable;
        if (TextUtils.equals(String.valueOf(item.getIds()), "27")) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.gril_cj);
        } else if (TextUtils.equals(String.valueOf(item.getIds()), "28")) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.gril_zj);
        } else if (TextUtils.equals(String.valueOf(item.getIds()), "29")) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.gril_gj);
        } else if (TextUtils.equals(String.valueOf(item.getIds()), "22")) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_ordinary);
        } else if (TextUtils.equals(String.valueOf(item.getIds()), "23")) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_silver);
        } else if (TextUtils.equals(String.valueOf(item.getIds()), "24")) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_gold);
        } else if (TextUtils.equals(String.valueOf(item.getIds()), "25")) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_zs);
        } else if (TextUtils.equals(String.valueOf(item.getIds()), "26")) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.vip_private);
        } else if (TextUtils.equals(String.valueOf(item.getIds()), "7")) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.youke_icon);
        } else if(TextUtils.equals(String.valueOf(item.getIds()), "30")){
            drawable = null;
        }else {
            drawable = null;
        }
//         Drawable drawable = ContextCompat.getDrawable(mContext,R.mipmap.vip_ordinary);

        tvName.setCompoundDrawablesWithIntrinsicBounds(null,null,drawable,null);
    }
}
