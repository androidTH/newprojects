package com.d6.android.app.adapters;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.d6.android.app.R;
import com.d6.android.app.models.FlowerRule;
import com.d6.android.app.models.LoveHeartRule;

import java.util.List;

/**
 * author : jinjiarui
 * time   : 2018/12/25
 * desc   :
 * version:
 */
public class BuyRedHeartAdapter extends BaseQuickAdapter<LoveHeartRule,BaseViewHolder> {

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    private int selectedIndex = -1;

    public BuyRedHeartAdapter(int layoutResId, @Nullable List<LoveHeartRule> data) {
        super(layoutResId, data);
    }

    public BuyRedHeartAdapter(@Nullable List<LoveHeartRule> data) {
        this(R.layout.item_red_heart,data);
    }

    public BuyRedHeartAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, LoveHeartRule item) {
         View view = helper.getView(R.id.ll_redheart_item);
         if(selectedIndex == helper.getAdapterPosition()){
             view.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_1stroke_1a));
         }else{
             view.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.shape_white_ef));
         }
         helper.setText(R.id.tv_redheartnums,item.getILoveCount()+"");
         if(item.getILoveCount()==200){
             helper.setText(R.id.tv_redheartinfo,"爱你哦");
         }else if(item.getILoveCount()==520){
            helper.setText(R.id.tv_redheartinfo,"爱你哦");
        }else if(item.getILoveCount()==1314){
            helper.setText(R.id.tv_redheartinfo,"一生一世");
        }else if(item.getILoveCount()==3399){
            helper.setText(R.id.tv_redheartinfo,"长长久久");
        }else if(item.getILoveCount()==9420){
            helper.setText(R.id.tv_redheartinfo,"就是爱你");
        }else if(item.getILoveCount()==20100){
             helper.setText(R.id.tv_redheartinfo,"爱你一万年");
        }
//         helper.setText(R.id.tv_redheartinfo,item.getSProductId());
    }
}
