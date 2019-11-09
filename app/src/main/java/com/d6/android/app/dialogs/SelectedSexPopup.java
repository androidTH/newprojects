package com.d6.android.app.dialogs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.d6.android.app.R;
import com.d6.android.app.widget.popup.BasePopup;

/**
 * Created by jinjiarui on 2017/8/4.
 */

public class SelectedSexPopup extends BasePopup<SelectedSexPopup> implements View.OnClickListener {
    private static final String TAG = "AgeSelectedPopup";

    private Context mContext;
    private TextView mTvAll;
    private TextView mTvMen;
    private TextView mTvWomen;
    private int position;
    private String str;

    public static SelectedSexPopup create(Context context) {
        return new SelectedSexPopup(context);
    }

    protected SelectedSexPopup(Context context) {
        mContext = context;
        setContext(context);
    }

    @Override
    protected void initAttributes() {
        setContentView(R.layout.popup_selectedsex_layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0.5f);
    }

    @Override
    protected void initViews(View view, SelectedSexPopup basePopup) {
        mTvAll = findViewById(R.id.tv_all);
        mTvMen = findViewById(R.id.tv_men);
        mTvWomen = findViewById(R.id.tv_women);
        mTvAll.setOnClickListener(this);
        mTvMen.setOnClickListener(this);
        mTvWomen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tv_men){
            position = 1;
            str = "男生";
        }else if(v.getId() == R.id.tv_women){
            position = 0;
            str = "女生";
        }else if(v.getId()==R.id.tv_all){
            position = -1;
            str = "全部";
        }
        if(mOnPopupItemClickListener!=null){
            mOnPopupItemClickListener.onPopupItemClick(this,position,str);
        }
        dismiss();
    }
}
