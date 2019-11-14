package com.d6.android.app.adapters;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.d6.android.app.R;
import com.d6.android.app.models.LoveHeartRule;

import java.util.List;

/**
 * author : jinjiarui
 * time   : 2018/12/25
 * desc   :
 * version:
 */
public class BuyRedHeartVoiceChatAdapter extends BaseQuickAdapter<LoveHeartRule,BaseViewHolder> {

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    private int selectedIndex = -1;
    public int showModel = 0;

    public int getShowModel() {
        return showModel;
    }

    public void setShowModel(int showModel) {
        this.showModel = showModel;
    }

    public BuyRedHeartVoiceChatAdapter(int layoutResId, @Nullable List<LoveHeartRule> data) {
        super(layoutResId, data);
    }

    public BuyRedHeartVoiceChatAdapter(@Nullable List<LoveHeartRule> data) {
        this(R.layout.item_redheart_voicechat,data);
    }

    public BuyRedHeartVoiceChatAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, LoveHeartRule item) {
         View view = helper.getView(R.id.ll_redheart_item);
         if(selectedIndex == helper.getAdapterPosition()){
             view.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_1stroke_1a));
         }else{
             view.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_4r_ef));
         }
         if(showModel==0){
             helper.setText(R.id.tv_redheartnums,"66 [img src=redheart_small/] X"+item.getILoveCount());
         }else{
             helper.setText(R.id.tv_redheartnums,String.valueOf(item.getILoveCount())+" [img src=redheart_small/]");
         }
    }
}
