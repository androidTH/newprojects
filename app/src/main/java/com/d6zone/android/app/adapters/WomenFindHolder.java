package com.d6zone.android.app.adapters;

import android.view.View;
import android.widget.TextView;

import com.d6zone.android.app.R;
import com.d6zone.android.app.widget.convenientbanner.holder.Holder;
import com.facebook.drawee.view.SimpleDraweeView;


/**
 * author : jinjiarui
 * time   : 2019/01/03
 * desc   :
 * version:
 */
public class WomenFindHolder extends Holder<String> {

    SimpleDraweeView simpleDraweeView;
    TextView mTvMemeberComments;

    private String[] mMenHeaderPics = new String[]{
            "https://tva1.sinaimg.cn/crop.188.1135.1843.1843.180/574421cfgw1ep2mr2retuj21kw2dcnnc.jpg",
            "https://tvax2.sinaimg.cn/crop.0.0.1080.1080.180/006lz966ly8g2vdezyk2aj30u00u0ac7.jpg",
            "https://tvax1.sinaimg.cn/crop.0.0.996.996.180/006koYhFly8g2u7m94y4oj30ro0rotai.jpg "
    };

    private String[] mWomenHeaderPics = new String[]{
            "https://tvax1.sinaimg.cn/crop.0.0.996.996.180/0074V8z6ly8g1v3pxqs6jj30ro0rojte.jpg",
            "https://tvax4.sinaimg.cn/crop.0.0.1080.1080.180/700a69f8ly8g0fj1kcfdbj20u00u00vy.jpg",
            "https://tva1.sinaimg.cn/crop.10.0.492.492.180/9ba8d31djw8f9ocv5yysfj20e80doaar.jpg"
    };

    public WomenFindHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
          simpleDraweeView = itemView.findViewById(R.id.imageView);
    }

    @Override
    public void updateUI(String data,int position,int total) {
        simpleDraweeView.setImageURI(data);
    }
}
