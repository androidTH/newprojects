package com.d6.android.app.adapters;

import android.view.View;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.models.MemberDesc;
import com.d6.android.app.widget.convenientbanner.holder.Holder;
import com.facebook.drawee.view.SimpleDraweeView;


/**
 * author : jinjiarui
 * time   : 2019/01/03
 * desc   :
 * version:
 */
public class InviateBannerHolder extends Holder<MemberDesc> {

    SimpleDraweeView simpleDraweeView;

    public InviateBannerHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
          simpleDraweeView = itemView.findViewById(R.id.sv_service);
    }

    @Override
    public void updateUI(MemberDesc data,int position,int total) {
        simpleDraweeView.setImageURI(data.getMHeaderPic());
    }
}
