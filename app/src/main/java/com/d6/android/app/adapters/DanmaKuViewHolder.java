package com.d6.android.app.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.widget.CircleImageView;
import com.d6.android.app.widget.textinlineimage.TextInlineImage;
import com.facebook.drawee.view.SimpleDraweeView;

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
    public TextView mText;

    public DanmaKuViewHolder(View itemView) {
        super(itemView);
        mIcon = (CircleImageView) itemView.findViewById(R.id.icon);
//        mIcon = (SimpleDraweeView) itemView.findViewById(R.id.icon);
        mText = (TextView) itemView.findViewById(R.id.text);
    }
}
