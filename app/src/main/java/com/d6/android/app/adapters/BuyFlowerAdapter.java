package com.d6.android.app.adapters;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

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
public class BuyFlowerAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    private int selectedIndex = -1;

    public BuyFlowerAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    public BuyFlowerAdapter(@Nullable List<String> data) {
        this(R.layout.item_red_flower,data);
    }

    public BuyFlowerAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
         View view = helper.getView(R.id.ll_flower_item);
         if(selectedIndex == helper.getAdapterPosition()){
             view.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_1stroke_1a));
         }else{
             view.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_white_ef));
         }
         helper.setText(R.id.tv_flowernums,item);
    }
}
