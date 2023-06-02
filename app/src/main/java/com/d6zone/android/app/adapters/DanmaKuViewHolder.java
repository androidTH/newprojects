package com.d6zone.android.app.adapters;

import android.util.Log;
import android.view.View;

import com.d6zone.android.app.R;
import com.d6zone.android.app.widget.CircleImageView;
import com.d6zone.android.app.widget.textinlineimage.TextInlineImage;

import master.flame.danmaku.danmaku.model.android.ViewCacheStuffer;

/**
 * author : jinjiarui
 * time   : 2019/10/20
 * desc   :
 * version:
 */
public class DanmaKuViewHolder  extends ViewCacheStuffer.ViewHolder {

    public CircleImageView mIcon;
//    public SimpleDraweeView mIcon;
    public TextInlineImage mText;

    public DanmaKuViewHolder(View itemView) {
        super(itemView);
        mIcon = (CircleImageView) itemView.findViewById(R.id.icon);
//        mIcon = (SimpleDraweeView) itemView.findViewById(R.id.icon);
        mText = itemView.findViewById(R.id.text);
    }

    @Override
    public void measure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.measure(widthMeasureSpec, heightMeasureSpec);
        } catch (Exception e) {
            Log.d("DanmaKuViewHolder", "MyViewHolder.measure: " + e.getMessage());
        }
    }
}
