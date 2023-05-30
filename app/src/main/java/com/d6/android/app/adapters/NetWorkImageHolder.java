package com.d6.android.app.adapters;

import android.view.View;
import android.view.ViewGroup;

import com.d6.android.app.R;
import com.d6.android.app.models.Banner;
import com.d6.android.app.widget.convenientbanner.holder.Holder;
import com.d6.android.app.widget.convenientbanner.utils.ScreenUtil;
import com.facebook.drawee.view.SimpleDraweeView;


/**
 * author : jinjiarui
 * time   : 2019/01/03
 * desc   :
 * version:
 */
public class NetWorkImageHolder extends Holder<Banner> {

    SimpleDraweeView simpleDraweeView;

    float width = 0;
    float space = 0;
    public NetWorkImageHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
          simpleDraweeView = itemView.findViewById(R.id.imageView);
//        width =(ScreenUtil.getScreenWidth(itemView.getContext())*0.95f);
//        space = (ScreenUtil.getScreenWidth(itemView.getContext())-width)/2;
//        simpleDraweeView.getLayoutParams().width = (int)width;
//        simpleDraweeView.requestLayout();
    }

    @Override
    public void updateUI(Banner data,int position,int total) {
        ViewGroup.LayoutParams params = simpleDraweeView.getLayoutParams();
//        if (position == 0) {
//            if (params instanceof ViewGroup.MarginLayoutParams) {
//                ((ViewGroup.MarginLayoutParams) params).leftMargin = (int)space;
//                ((ViewGroup.MarginLayoutParams) params).rightMargin = ScreenUtil.dip2px(simpleDraweeView.getContext(),2.5f);
//            }
//        }else if (position == (total-1) && position > 0) {
//            if (params instanceof ViewGroup.MarginLayoutParams) {
//                ((ViewGroup.MarginLayoutParams) params).rightMargin = (int)space;
//                        ((ViewGroup.MarginLayoutParams) params).leftMargin = ScreenUtil.dip2px(simpleDraweeView.getContext(),2.5f);
//            }
//        } else {
//            if (params instanceof ViewGroup.MarginLayoutParams) {
//                ((ViewGroup.MarginLayoutParams) params).leftMargin = ScreenUtil.dip2px(simpleDraweeView.getContext(),2.5f);
//                ((ViewGroup.MarginLayoutParams) params).rightMargin = ScreenUtil.dip2px(simpleDraweeView.getContext(),2.5f);
//            }
//        }
//        simpleDraweeView.setLayoutParams(params);
        simpleDraweeView.setImageURI(data.getPicurl());
    }
}
