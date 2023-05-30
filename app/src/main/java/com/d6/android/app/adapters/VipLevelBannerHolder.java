package com.d6.android.app.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.models.MemberBean;
import com.d6.android.app.models.MemberDesc;
import com.d6.android.app.widget.convenientbanner.holder.Holder;


/**
 * author : jinjiarui
 * time   : 2019/01/03
 * desc   :
 * version:
 */
public class VipLevelBannerHolder extends Holder<MemberBean> {

    private TextView mTvVipLevelName;
    private TextView mTvVipLevelPrice;
    private ImageView mIvVipLevel;
    private TextView mTvVipLevelTime;
    private TextView mTvVipLevelAddress;
    private TextView mVipLevelPoints;
    private TextView mTvVipLevelRate;
    public VipLevelBannerHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
          mTvVipLevelName = itemView.findViewById(R.id.tv_viplevel_name);
          mTvVipLevelPrice = itemView.findViewById(R.id.tv_viplevel_price);
          mIvVipLevel = itemView.findViewById(R.id.iv_viplevel);
          mTvVipLevelTime = itemView.findViewById(R.id.tv_viplevel_time);
          mTvVipLevelAddress = itemView.findViewById(R.id.tv_viplevel_address);
          mVipLevelPoints = itemView.findViewById(R.id.tv_viplevel_points);
          mTvVipLevelRate = itemView.findViewById(R.id.tv_viplevel_rate);
    }

    @Override
    public void updateUI(MemberBean data, int position, int total) {
           mTvVipLevelName.setText(data.getClassesname());
           mTvVipLevelTime.setText("入群时长："+data.getIEnableDate());
           mTvVipLevelPrice.setText("¥"+data.getIAndroidAPrice()+"");
//           mTvVipLevelRate.setText("入群时长："+data.getIEnableDate());
           mTvVipLevelAddress.setText("服务范围："+data.getSServiceArea());
    }
}
