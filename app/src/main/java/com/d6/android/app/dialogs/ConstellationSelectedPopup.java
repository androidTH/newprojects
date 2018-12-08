package com.d6.android.app.dialogs;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.d6.android.app.R;
import com.d6.android.app.adapters.XinZuoQuickDateAdapter;
import com.d6.android.app.widget.popup.BasePopup;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by jinjiarui on 2017/8/4.
 */

public class ConstellationSelectedPopup extends BasePopup<ConstellationSelectedPopup>  {
    private static final String TAG = ConstellationSelectedPopup.class.getSimpleName();

    private String constellations[] = {"不限", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"};

    private Context mContext;
    private RecyclerView mRvConstellation;
    private XinZuoQuickDateAdapter mAdapter;
    private int position;

    public static ConstellationSelectedPopup create(Context context) {
        return new ConstellationSelectedPopup(context);
    }

    protected ConstellationSelectedPopup(Context context) {
        mContext = context;
        setContext(context);
    }

    @Override
    protected void initAttributes() {
        setContentView(R.layout.popup_date_constellation_layout, ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.height_400));
        setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0.5f);
    }

    @Override
    protected void initViews(View view, ConstellationSelectedPopup basePopup) {
        mRvConstellation = findViewById(R.id.rv_date_constelation);
        mRvConstellation.setHasFixedSize(true);
        mRvConstellation.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        mRvConstellation.setAdapter(mAdapter = new XinZuoQuickDateAdapter(Arrays.asList(constellations)));
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                onPopupItemClick(view, position);
            }
        });
    }

    private void onPopupItemClick(View view,int position){
        if (mOnPopupItemClickListener != null) {
            mOnPopupItemClickListener.onPopupItemClick(this, position, constellations[position]);
        }
        dismiss();
    }
}
