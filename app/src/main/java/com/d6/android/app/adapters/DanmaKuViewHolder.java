package com.d6.android.app.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d6.android.app.R;
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

    public ImageView mIcon;
    public TextInlineImage mText;

    public DanmaKuViewHolder(View itemView) {
        super(itemView);
        mIcon = (ImageView) itemView.findViewById(R.id.icon);
        mText = (TextInlineImage) itemView.findViewById(R.id.text);
    }
}
