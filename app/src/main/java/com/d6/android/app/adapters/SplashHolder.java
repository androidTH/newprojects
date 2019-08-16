package com.d6.android.app.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;
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
public class SplashHolder extends Holder<MemberDesc> {

    RelativeLayout mRLSplash;
    SimpleDraweeView simpleDraweeView;
    TextView splash_title;
    TextView splash_smalltitle;
    Context mContext;

    public SplashHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
    }

    @Override
    protected void initView(View itemView) {
        mRLSplash = itemView.findViewById(R.id.rl_splash);
        simpleDraweeView = itemView.findViewById(R.id.imageView);
        splash_title = itemView.findViewById(R.id.splash_title);
        splash_smalltitle = itemView.findViewById(R.id.splash_smalltitle);
    }

    @Override
    public void updateUI(MemberDesc data, int position, int total) {
        splash_title.setText(data.getTitle());
        splash_smalltitle.setText(data.getMContent());
        simpleDraweeView.setImageURI(data.getMHeaderPic());
        mRLSplash.setBackground(ContextCompat.getDrawable(mContext,data.getMResId()));
    }
}
