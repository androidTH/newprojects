package com.d6.android.app.dialogs;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.d6.android.app.R;
import com.d6.android.app.adapters.CityOfProvinceAdapter;
import com.d6.android.app.adapters.ProvinceAdapter;
import com.d6.android.app.adapters.XinZuoQuickDateAdapter;
import com.d6.android.app.models.City;
import com.d6.android.app.utils.GsonHelper;
import com.d6.android.app.widget.popup.BasePopup;
import com.d6.android.app.widget.test.CategoryBean;
import com.d6.android.app.widget.test.ConvertUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jinjiarui on 2017/8/4.
 */

public class AreaSelectedPopup extends BasePopup<AreaSelectedPopup>  {

    private static final String TAG = AreaSelectedPopup.class.getSimpleName();
    private Context mContext;
    private List<String> mCities =new ArrayList<>();
    private List<CategoryBean.DataBean> mHomeList =new  ArrayList<>();
    private List<Integer> mShowTitles =new ArrayList<>();
    private int currentItem = 0;

    private RecyclerView mRvMenuLeft;
    private RecyclerView mRVMenuRight;

    private ProvinceAdapter mProvinceAdapter;
    private CityOfProvinceAdapter mCityOfProvinceAdapter;

    public static AreaSelectedPopup create(Context context) {
        return new AreaSelectedPopup(context);
    }

    protected AreaSelectedPopup(Context context) {
        mContext = context;
        setContext(context);
        loadData();
    }

    @Override
    protected void initAttributes() {
        setContentView(R.layout.popup_area_choose_layout, ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.height_500));
        setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0.5f);
    }
    @Override
    protected void initViews(View view, AreaSelectedPopup basePopup) {
        mRvMenuLeft = view.findViewById(R.id.rv_menu);
        mRVMenuRight = view.findViewById(R.id.rv_menu_right);
        mRvMenuLeft.setHasFixedSize(true);
        mRvMenuLeft.setLayoutManager(new LinearLayoutManager(mContext));
        mRVMenuRight.setHasFixedSize(true);
        mRVMenuRight.setLayoutManager(new LinearLayoutManager(mContext));

        mRvMenuLeft.setAdapter(mProvinceAdapter = new ProvinceAdapter(mCities));
        mRVMenuRight.setAdapter(mCityOfProvinceAdapter = new CityOfProvinceAdapter(mHomeList));

        mProvinceAdapter.setNewData(mCities);
        mCityOfProvinceAdapter.setNewData(mHomeList);

        mProvinceAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.item_name) {
                    mProvinceAdapter.setSelectItem(position);
                    mProvinceAdapter.notifyDataSetChanged();
                    ((LinearLayoutManager) mRVMenuRight.getLayoutManager()).scrollToPositionWithOffset(mShowTitles.get(position), 0);
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
                    mProvinceAdapter.notifyDataSetChanged();
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

    private void loadData() {
        String json = null;
        try {
            json = ConvertUtils.toString(mContext.getAssets().open("category.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CategoryBean categoryBean = GsonHelper.GsonToBean(json, CategoryBean.class);
        for (int i = 0; i < categoryBean.getData().size(); i++) {
            CategoryBean.DataBean dataBean = categoryBean.getData().get(i);
            mCities.add(dataBean.getModuleTitle());
            mShowTitles.add(i);
            mHomeList.add(dataBean);
        }
    }

    private void onPopupItemClick(int position,String name){
        if (mOnPopupItemClickListener != null) {
            mOnPopupItemClickListener.onPopupItemClick(this, position,name);
        }
        dismiss();
    }
}
