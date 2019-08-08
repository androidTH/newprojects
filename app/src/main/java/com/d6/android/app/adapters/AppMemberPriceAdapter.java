package com.d6.android.app.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.d6.android.app.R;
import com.d6.android.app.models.MemberBean;

import java.util.List;

import static com.d6.android.app.utils.UtilKt.getLevelDrawable;

/**
 * author : jinjiarui
 * time   : 2018/12/25
 * desc   :
 * version:
 */
public class AppMemberPriceAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    private int selectedIndex = -1;

    public AppMemberPriceAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    public AppMemberPriceAdapter(@Nullable List<String> data) {
        this(R.layout.item_appmember_price,data);
    }

    public AppMemberPriceAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView tvDiscount = helper.getView(R.id.tv_discount);
        tvDiscount.setText("8.5折");
        helper.setText(R.id.tv_appmember_time, "3个月");
        helper.setText(R.id.tv_appmember_price,"¥505");
        helper.setText(R.id.tv_discount_desc,"折合¥168/月");
        View mLLAPPMemberItem = helper.getView(R.id.ll_appmember_item);
        if(selectedIndex == helper.getAdapterPosition()){
            mLLAPPMemberItem.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_4stroke_fab));
        }else{
            mLLAPPMemberItem.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_white_ef));
        }
        helper.addOnClickListener(R.id.tv_vip_price);

    }
}
