package com.d6.android.app.dialogs;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.d6.android.app.R;
import com.d6.android.app.adapters.CityOfProvinceAdapter;
import com.d6.android.app.adapters.ProvinceAdapter;
import com.d6.android.app.models.Province;
import com.d6.android.app.utils.Const;
import com.d6.android.app.widget.popup.BasePopup;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinjiarui on 2017/8/4.
 */

public class AreaSelectedPopup extends BasePopup<AreaSelectedPopup>  {

    private static final String TAG = AreaSelectedPopup.class.getSimpleName();
    private Context mContext;
    private List<Province> mCities =new ArrayList<>();
    private List<Province> mHomeList =new  ArrayList<>();
    private int currentItem = 0;

    private RecyclerView mRvMenuLeft;
    private RecyclerView mRVMenuRight;
    private TextView mTvMenuTopTitle;

    private ProvinceAdapter mProvinceAdapter;
    private CityOfProvinceAdapter mCityOfProvinceAdapter;

    public static AreaSelectedPopup create(Context context) {
        return new AreaSelectedPopup(context);
    }

    protected AreaSelectedPopup(Context context) {
        mContext = context;
        setContext(context);
    }

    @Override
    protected void initAttributes() {
        if(mContext!=null){
            setContentView(R.layout.popup_area_choose_layout, ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.height_500));
            setFocusAndOutsideEnable(true)
                    .setBackgroundDimEnable(true)
                    .setDimValue(0.5f);
        }
    }
    @Override
    protected void initViews(View view, AreaSelectedPopup basePopup) {
        mRvMenuLeft = view.findViewById(R.id.rv_menu);
        mRVMenuRight = view.findViewById(R.id.rv_menu_right);
        mTvMenuTopTitle = view.findViewById(R.id.tv_menu_toptitle);
        mRvMenuLeft.setHasFixedSize(true);
        mRvMenuLeft.setLayoutManager(new LinearLayoutManager(mContext));
        mRVMenuRight.setHasFixedSize(true);
        mRVMenuRight.setLayoutManager(new LinearLayoutManager(mContext));

        mRvMenuLeft.setAdapter(mProvinceAdapter = new ProvinceAdapter(mCities));
        mRVMenuRight.setAdapter(mCityOfProvinceAdapter = new CityOfProvinceAdapter(mHomeList));

        mProvinceAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.item_name) {
                    mProvinceAdapter.setSelectItem(position);
                    mTvMenuTopTitle.setText(mCities.get(position).getName());
                    mProvinceAdapter.notifyDataSetChanged();
                    ((LinearLayoutManager) mRVMenuRight.getLayoutManager()).scrollToPositionWithOffset(position, 0);
                }
            }
        });

        mRVMenuRight.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentPos = ((LinearLayoutManager) mRVMenuRight.getLayoutManager()).findFirstVisibleItemPosition();
                if (currentPos != currentItem && currentPos >= 0) {
                    currentItem = currentPos;
                    mProvinceAdapter.setSelectItem(currentPos);
                    mTvMenuTopTitle.setText(mCities.get(currentPos).getName());
                    mProvinceAdapter.notifyDataSetChanged();
                    mRvMenuLeft.getLayoutManager().scrollToPosition(currentPos);
                }
            }
        });

        mCityOfProvinceAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(view.getId() == R.id.tv_arealocation){
                    TextView mTv = (TextView) view;
                    if(TextUtils.equals(mTv.getTag().toString(),Const.LOCATIONSUCCESS)){
                        onPopupItemClick(-1,mTv.getText().toString().trim());
                    }else{
                        onPopupItemClick(-2,mTv.getText().toString().trim());
                    }
                }else if(view.getId() == R.id.tv_no_limit_area){
                    TextView mTv = (TextView) view;
                    Const.selectCategoryType = mTv.getTag().toString();
                    onPopupItemClick(-3,mTv.getText().toString().trim());
                    mCityOfProvinceAdapter.notifyDataSetChanged();
                }
            }
        });

        mCityOfProvinceAdapter.setOnSelectCityOfProvince(new CityOfProvinceAdapter.onSelectCityOfProvinceListenerInterface() {
            @Override
            public void onSelectedCityListener(int pos, @NotNull String name) {
                onPopupItemClick(pos,name);
                mCityOfProvinceAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setData(List<Province> cities){
        this.mCities = cities;
        this.mHomeList = cities;
        mProvinceAdapter.setNewData(mCities);
        mCityOfProvinceAdapter.setNewData(mHomeList);

        mTvMenuTopTitle.setText(mCities.get(0).getName());
    }


    private void onPopupItemClick(int position,String name){
        if (mOnPopupItemClickListener != null) {
            mOnPopupItemClickListener.onPopupItemClick(this, position,name);
        }
        dismiss();
    }
}
