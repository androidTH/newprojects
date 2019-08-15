package com.d6.android.app.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.d6.android.app.R;
import com.d6.android.app.models.AppMemberPrice;

import java.util.List;

import static com.d6.android.app.utils.UtilKt.getLevelDrawable;

/**
 * author : jinjiarui
 * time   : 2018/12/25
 * desc   :
 * version:
 */
public class AppMemberPriceAdapter extends BaseQuickAdapter<AppMemberPrice,BaseViewHolder> {

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    private int selectedIndex = 0;

    public AppMemberPriceAdapter(int layoutResId, @Nullable List<AppMemberPrice> data) {
        super(layoutResId, data);
    }

    public AppMemberPriceAdapter(@Nullable List<AppMemberPrice> data) {
        this(R.layout.item_appmember_price,data);
    }

    public AppMemberPriceAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, AppMemberPrice item) {
        TextView tvDiscount = helper.getView(R.id.tv_discount);
        if(TextUtils.isEmpty(item.getSAndroidPriceDiscount())){
            tvDiscount.setVisibility(View.INVISIBLE);
        }else{
            tvDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText(item.getSAndroidPriceDiscount());
        }
        helper.setText(R.id.tv_appmember_time, item.getSEnableDateDesc());

        helper.setText(R.id.tv_appmember_price,String.valueOf("¥"+item.getIAndroidPrice()));

        if(TextUtils.isEmpty(item.getSAndroidPriceDiscountDesc())){
            helper.setGone(R.id.tv_discount_desc,false);
        }else{
            helper.setVisible(R.id.tv_discount_desc,true);
            helper.setText(R.id.tv_discount_desc,item.getSAndroidPriceDiscountDesc());
        }

        View mLLAPPMemberItem = helper.getView(R.id.ll_appmember_item);
        if(selectedIndex == helper.getAdapterPosition()){
            mLLAPPMemberItem.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_4stroke_fab));
        }else{
            mLLAPPMemberItem.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_white_ef));
        }
//        helper.addOnClickListener(R.id.tv_vip_price);
    }
}
