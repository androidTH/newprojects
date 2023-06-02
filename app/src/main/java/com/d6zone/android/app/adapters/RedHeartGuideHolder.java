package com.d6zone.android.app.adapters;

import android.view.View;
import android.widget.ImageView;

import com.d6zone.android.app.R;
import com.d6zone.android.app.models.MemberDesc;
import com.d6zone.android.app.widget.convenientbanner.holder.Holder;


/**
 * author : jinjiarui
 * time   : 2019/01/03
 * desc   :
 * version:
 */
public class RedHeartGuideHolder extends Holder<MemberDesc> {

    ImageView simpleDraweeView;

    public RedHeartGuideHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
          simpleDraweeView = itemView.findViewById(R.id.sv_service);
    }

    @Override
    public void updateUI(MemberDesc data,int position,int total) {
        simpleDraweeView.setImageResource(data.getMResId());
    }
}
