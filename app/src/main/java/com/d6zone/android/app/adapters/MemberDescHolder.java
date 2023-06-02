package com.d6zone.android.app.adapters;

import android.view.View;
import android.widget.TextView;

import com.d6zone.android.app.R;
import com.d6zone.android.app.models.MemberDesc;
import com.d6zone.android.app.widget.convenientbanner.holder.Holder;
import com.facebook.drawee.view.SimpleDraweeView;


/**
 * author : jinjiarui
 * time   : 2019/01/03
 * desc   :
 * version:
 */
public class MemberDescHolder extends Holder<MemberDesc> {

    SimpleDraweeView simpleDraweeView;
    TextView mTvMemeberComments;
    TextView mTvTitle1;

    public MemberDescHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
          simpleDraweeView = itemView.findViewById(R.id.sv_service);
          mTvMemeberComments = itemView.findViewById(R.id.tv_title);
          mTvTitle1 = itemView.findViewById(R.id.tv_title1);
    }

    @Override
    public void updateUI(MemberDesc data,int position,int total) {
        mTvTitle1.setText(data.getMContent());
        mTvMemeberComments.setText(data.getTitle());
        simpleDraweeView.setImageURI(data.getMHeaderPic());
    }
}
