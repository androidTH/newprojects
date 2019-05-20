package com.d6.android.app.adapters;

import android.view.View;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.models.Banner;
import com.d6.android.app.widget.convenientbanner.holder.Holder;
import com.facebook.drawee.view.SimpleDraweeView;


/**
 * author : jinjiarui
 * time   : 2019/01/03
 * desc   :
 * version:
 */
public class MemberCommentHolder extends Holder<String> {

    SimpleDraweeView simpleDraweeView;
    TextView mTvMemeberComments;
    public MemberCommentHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
          simpleDraweeView = itemView.findViewById(R.id.sv_service);
          mTvMemeberComments = itemView.findViewById(R.id.tv_member_comment);
    }

    @Override
    public void updateUI(String data,int position,int total) {
//        simpleDraweeView.setImageURI(data.getPicurl());
        mTvMemeberComments.setText(data);
    }
}
