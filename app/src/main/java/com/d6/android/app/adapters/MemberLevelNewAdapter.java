package com.d6.android.app.adapters;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.d6.android.app.R;
import com.d6.android.app.models.MemberBean;
import java.util.List;

/**
 * author : jinjiarui
 * time   : 2018/12/25
 * desc   :
 * version:
 */
public class MemberLevelNewAdapter extends BaseQuickAdapter<MemberBean,BaseViewHolder> {

    public MemberLevelNewAdapter(int layoutResId, @Nullable List<MemberBean> data) {
        super(layoutResId, data);
    }

    public MemberLevelNewAdapter(@Nullable List<MemberBean> data) {
        this(R.layout.item_membertop_infos,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MemberBean item) {
        RelativeLayout mRlTopBg = helper.getView(R.id.rl_topbg);
        TextView mTvVipLevelAddress = helper.getView(R.id.tv_viplevel_address);
        TextView tv_vip_percent = helper.getView(R.id.tv_viplevel_rate);
        TextView mTvVipLevelTime = helper.getView(R.id.tv_viplevel_time);
        ImageView  mIvVipLevel = helper.getView(R.id.iv_viplevel);
        helper.setText(R.id.tv_viplevel_name,item.getClassesname());

        if(TextUtils.isEmpty(item.getSTitle())){
            tv_vip_percent.setVisibility(View.VISIBLE);
        }else{
            tv_vip_percent.setVisibility(View.VISIBLE);
            tv_vip_percent.setText(item.getSTitle());
        }

        helper.setText(R.id.tv_viplevel_price,"¥"+String.valueOf(item.getIAndroidPrice()));

        if(TextUtils.isEmpty(item.getSServiceArea())){
            mTvVipLevelAddress.setText("服务范围：未知");
        }else{
            mTvVipLevelAddress.setText("服务范围："+item.getSServiceArea());
        }

        mTvVipLevelTime.setText("入群时长："+item.getIEnableDate());

        Log.i("MemberLevelAdapter",item.getSServiceArea()+"会员备注,会员id ="+item.getIds());
        if(item.getIds()==22){
            mRlTopBg.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_pt));
            mIvVipLevel.setImageResource(R.mipmap.vip_ordinary);
//            tv_memeber_address.setTextColor(ContextCompat.getColor(mContext,R.color.color_848484));
        }else if(item.getIds()==30){
            mRlTopBg.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_group));
            mIvVipLevel.setImageResource(R.mipmap.ruqun_icon);
//            helper.setText(R.id.tv_vip_level,"入群会员");
        }else if(item.getIds()==23){
            mRlTopBg.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_sliver));
            mIvVipLevel.setImageResource(R.mipmap.vip_silver);
        }else if(item.getIds()==24){
            mRlTopBg.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_gold));
            mIvVipLevel.setImageResource(R.mipmap.vip_gold);
        }else if(item.getIds()==25){
            mRlTopBg.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_diamond));
            mIvVipLevel.setImageResource(R.mipmap.vip_zs);
        }else if(item.getIds()==26){
            mRlTopBg.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_private));
            mIvVipLevel.setImageResource(R.mipmap.vip_private);
        }else if(item.getIds()==31){
            mRlTopBg.setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_5r_appmember));
            mIvVipLevel.setImageResource(R.mipmap.app_vip);
        }
              //R.color.color_848484   R.color.color_888888  R.color.color_A19BB0 R.color.color_C69F60
                                         //R.color.color_8170D2 R.color.color_323432
    }
}
