package com.d6.android.app.dialogs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.widget.popup.BasePopup;

/**
 * Created by jinjiarui on 2017/8/4.
 */

public class AgeSelectedPopup extends BasePopup<AgeSelectedPopup> implements View.OnClickListener {
    private static final String TAG = "AgeSelectedPopup";

    private Context mContext;
    private TextView mTvAllAges;
    private TextView mTvAge02;
    private TextView mTvAge03;
    private TextView mTvAge04;
    private TextView mTvAge05;
    private int position;
    private String str;

    public static AgeSelectedPopup create(Context context) {
        return new AgeSelectedPopup(context);
    }

    protected AgeSelectedPopup(Context context) {
        mContext = context;
        setContext(context);
    }

    @Override
    protected void initAttributes() {
        setContentView(R.layout.popup_date_age_layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0.5f);
    }

    @Override
    protected void initViews(View view, AgeSelectedPopup basePopup) {
         mTvAllAges = findViewById(R.id.tv_allages);
         mTvAge02 = findViewById(R.id.tv_age02);
         mTvAge03 = findViewById(R.id.tv_age03);
         mTvAge04 = findViewById(R.id.tv_age04);
         mTvAge05 = findViewById(R.id.tv_age05);
         mTvAllAges.setOnClickListener(this);
         mTvAge02.setOnClickListener(this);
         mTvAge03.setOnClickListener(this);
         mTvAge04.setOnClickListener(this);
         mTvAge05.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tv_allages){
            position = 0;
            str = "不限";
        }if(v.getId() == R.id.tv_age02){
            position = 2;
            str = "18-25岁";
        }else if(v.getId() == R.id.tv_age03){
            position = 3;
            str = "26-30岁";
        }else if(v.getId() == R.id.tv_age04){
            position = 4;
            str = "31-40岁";
        }else if(v.getId() == R.id.tv_age05){
            position = 5;
            str = "40岁以上";
        }
        if(mOnPopupItemClickListener!=null){
            mOnPopupItemClickListener.onPopupItemClick(this,position,str);
        }
        dismiss();
    }
}
